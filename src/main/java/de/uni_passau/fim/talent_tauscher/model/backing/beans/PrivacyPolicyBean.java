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

/**
 * Backing bean for the privacy policy page.
 *
 * @author Jakob Edmaier
 */
@Named
@ViewScoped
public class PrivacyPolicyBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private boolean previewShown;
  private SystemDataDTO systemData;

  @Inject private SystemDataService systemDataService;

  @Inject private JFUtils jfUtils;

  /** Default constructor. */
  public PrivacyPolicyBean() {
    // Default constructor
  }

  /** Loads the system data. */
  @PostConstruct
  public void init() {
    systemData = systemDataService.get();
  }

  /**
   * Gets the system data containing the privacy policy.
   *
   * @return The system data.
   */
  public SystemDataDTO getSystemData() {
    return systemData;
  }

  /** Saves the changes made to the privacy policy. */
  public void submitNewPrivacyPolicy() {
    systemDataService.update(systemData);

    FacesContext fctx = FacesContext.getCurrentInstance();
    String message = jfUtils.getMessageFromResourceBundle("m_updateSuccessful_privacyPolicy");
    fctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
  }

  /** Toggles the visibility of the preview of the new imprint. */
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
