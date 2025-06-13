package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.SystemDataDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.SystemDataService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;

/** Backing bean for the contact information of the administrators. */
@Named
@ViewScoped
public class ContactUsBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private SystemDataDTO systemData;

  private boolean previewShown;

  @Inject private SystemDataService systemDataService;

  @Inject private JFUtils jfUtils;

  /** Default constructor. */
  public ContactUsBean() {
    // Default constructor
  }

  /** Initializes the bean by loading the contact data. */
  @PostConstruct
  public void init() {
    systemData = systemDataService.get();
  }

  /**
   * Returns the system data containing the contact information.
   *
   * @return The system data.
   */
  public SystemDataDTO getSystemData() {
    return systemData;
  }

  /** Saves the changes made to the contact data to the data store. */
  public void submitNewContactInformation() {
    systemDataService.update(systemData);
    FacesContext fctx = FacesContext.getCurrentInstance();
    String message = jfUtils.getMessageFromResourceBundle("m_updateSuccessful_contact");
    fctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
  }

  /** Toggles the visibility of the preview of the new Contact Information. */
  public void togglePreview() {
    previewShown = !previewShown;
  }

  /**
   * Gets whether the preview is shown.
   *
   * @return Whether the preview is shown.
   */
  public boolean isPreviewShown() {
    return previewShown;
  }
}
