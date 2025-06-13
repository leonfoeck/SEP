package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.SystemDataDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import de.uni_passau.fim.talent_tauscher.model.business.services.SystemDataService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;

/**
 * Backing bean for the application header.
 *
 * <p>This bean manages the header of the application, including displaying system configuration
 * data such as the application logo and handling user session actions like logout.
 *
 * @author Leon Föckersperger
 */
@Named
@ViewScoped
public class HeaderBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private SystemDataDTO systemData;

  @Inject private SystemDataService systemDataService;

  @Inject private UserSession userSession;

  /** Default constructor. */
  public HeaderBean() {
    // Default constructor
  }

  /**
   * Initializes the bean by retrieving system configuration data such as the application logo.
   *
   * <p>This method is called after the bean's construction and dependency injection to set up
   * initial state.
   */
  @PostConstruct
  public void init() {
    systemData = systemDataService.get();
  }

  /**
   * Gets the system configuration data.
   *
   * @return The {@link SystemDataDTO} containing the system configuration data.
   */
  public SystemDataDTO getSystemData() {
    return systemData;
  }

  /**
   * Logs out the user and navigates to the login page.
   *
   * <p>This method invalidates the current user session and redirects the user to the login page.
   *
   * @return A string representing the navigation outcome to the login page.
   */
  public String logout() {
    userSession.logout();
    return "/view/anonymous/allAdvertisements?faces-redirect=true";
  }
}
