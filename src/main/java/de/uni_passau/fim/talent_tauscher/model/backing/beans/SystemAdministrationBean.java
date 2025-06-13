package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.SystemDataDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.ResourceService;
import de.uni_passau.fim.talent_tauscher.model.business.services.SystemDataService;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import org.omnifaces.util.Utils;

/**
 * Backing bean for the system administration page.
 *
 * @author Sturm
 */
@Named
@ViewScoped
public class SystemAdministrationBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private SystemDataDTO systemData;

  private transient Part logoInputPart;

  private List<String> cssFiles;

  @Inject private SystemDataService systemDataService;

  @Inject private UserService userService;

  @Inject private JFUtils jfUtils;

  @Inject private ResourceService resourceService;

  /** Default constructor. */
  public SystemAdministrationBean() {
    // Default constructor
  }

  /** Initializes the bean by retrieving the system data and filling the form. */
  @PostConstruct
  public void init() {
    this.systemData = systemDataService.get();
    cssFiles = systemDataService.getStylesheetNames();
  }

  /**
   * Gets the system configuration data.
   *
   * @return The system configuration data.
   */
  public SystemDataDTO getSystemData() {
    return systemData;
  }

  /**
   * Returns a Part Object for the logo file input.
   *
   * @return A Part Object for the logo file input.
   */
  public Part getLogoInputPart() {
    return logoInputPart;
  }

  /**
   * Takes the Part object from the file input, converts it to a byte array and sets it in the
   * SystemDataDTO.
   *
   * @param inputFile The uploaded logo image.
   */
  public void setLogoInputPart(Part inputFile) {
    if (inputFile != null && inputFile.getSubmittedFileName() != null) {
      try {
        systemData.setLogo(Utils.toByteArray(inputFile.getInputStream()));
      } catch (IOException e) {
        String message = jfUtils.getMessageFromResourceBundle("v_other_fail");
        FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
      }
    }
  }

  /**
   * Saves the current input for the system settings form.
   *
   * @return The link to the all-advertisements page.
   */
  public String submit() {
    systemDataService.update(systemData);
    if (!systemData.isAdminConfirmationRequiredForRegistration()) {
      userService.setAllUsersAdminVerified();
    }

    String message = jfUtils.getMessageFromResourceBundle("f_changes_saved");
    FacesContext.getCurrentInstance()
        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    return "/view/anonymous/allAdvertisements.xhtml?faces-redirect=true";
  }

  /** Reverts the logo to the default logo. */
  public void revertLogo() {
    systemData.setLogo(
        resourceService.getDefaultLogoImage(
            FacesContext.getCurrentInstance().getExternalContext()::getResourceAsStream));
    logoInputPart = null;
  }

  /**
   * Returns a List of all .css files in the webapp/resources/css folder.
   *
   * @return A List of all .css files in the webapp/resources/css folder.
   */
  public List<String> getCssFiles() {
    return cssFiles;
  }
}
