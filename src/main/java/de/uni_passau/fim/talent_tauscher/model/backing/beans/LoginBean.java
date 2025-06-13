package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.LoginDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Optional;

/**
 * Backing bean for the login page.
 *
 * @author Sturm
 */
@Named
@RequestScoped
public class LoginBean {

  private LoginDTO login;

  @Inject private JFUtils jfUtils;

  @Inject private UserService userService;

  @Inject private UserSession session;

  /** Default constructor. */
  public LoginBean() {
    // Default constructor
  }

  /** Initializes this bean. */
  @PostConstruct
  public void init() {
    login = new LoginDTO();
  }

  /**
   * Gets the login data.
   *
   * @return The login data.
   */
  public LoginDTO getLogin() {
    return login;
  }

  /**
   * Sets the login data.
   *
   * @param login The login data.
   */
  public void setLogin(LoginDTO login) {
    this.login = login;
  }

  /**
   * Attempts to log in the user with the supplied login credentials. If the login is successful,
   * the user is redirected to the created-advertisements page.
   *
   * @return The link to the created-advertisements page, or {@code null} if the login credentials
   *     are invalid.
   */
  public String submit() {
    Optional<UserAuthenticationDTO> result = userService.authenticate(login);
    FacesContext fctx = FacesContext.getCurrentInstance();

    if (result.isPresent()) {
      boolean verificationNeeded = false;
      if (!result.get().isEmailVerified()) {
        String message = jfUtils.getMessageFromResourceBundle("f_email_verification_missing");
        fctx.addMessage(
            "userInput:nick", new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        fctx.getExternalContext().getFlash().setKeepMessages(true);
        verificationNeeded = true;
      }
      if (!result.get().isAdminVerified()) {
        String message = jfUtils.getMessageFromResourceBundle("f_admin_verification_missing");
        fctx.addMessage(
            "userInput:nick", new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        fctx.getExternalContext().getFlash().setKeepMessages(true);
        verificationNeeded = true;
      }

      if (verificationNeeded) {
        return null;
      }

      session.login(result.get());
      return "/view/anonymous/allAdvertisements?faces-redirect=true";
    } else {
      String message = jfUtils.getMessageFromResourceBundle("f_login_fail");
      fctx.addMessage(
          "userInput:password", new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
      return null;
    }
  }
}
