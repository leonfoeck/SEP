package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.AuthTokenDTO;
import de.uni_passau.fim.talent_tauscher.dto.PasswordInputDTO;
import de.uni_passau.fim.talent_tauscher.dto.PasswordResetDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import de.uni_passau.fim.talent_tauscher.model.business.util.PasswordAuthentication;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/** Backing bean for the reset password form. */
@Named
@RequestScoped
public class PasswordResetInputBean {

  @Inject private UserService userService;

  @Inject private PasswordAuthentication passwordAuthentication;

  @Inject private JFUtils jfUtils;

  private PasswordResetDTO passwordReset;
  private PasswordInputDTO passwordInput;
  private UserAuthenticationDTO user;

  /** Default constructor. */
  public PasswordResetInputBean() {
    // Default constructor
  }

  /** Initializes the bean by creating instances of all used DTOs. */
  @PostConstruct
  public void init() {
    passwordReset = new PasswordResetDTO();
    passwordInput = new PasswordInputDTO();
    user = new UserAuthenticationDTO();
  }

  /** Loads the user identified by the verification token given as a view param. */
  public void loadUser() {
    user = userService.getUserByPasswordResetSecret(user, passwordReset);
  }

  /**
   * Gets the password input.
   *
   * @return The password input.
   */
  public PasswordInputDTO getPasswordInput() {
    return passwordInput;
  }

  /**
   * Saves the new password and redirects the user to the login page.
   *
   * @return The link to the login page.
   */
  public String submit() {
    if (!passwordInput.getPassword().equals(passwordInput.getRepetition())) {
      showPasswordsDontMatchMessage();
      return null;
    }
    AuthTokenDTO authToken = passwordAuthentication.hash(passwordInput.getPassword());
    passwordReset.setAuthToken(authToken);
    userService.resetPassword(passwordReset);
    return "/view/anonymous/login";
  }

  /**
   * Gets the password reset data consisting of the verification token and the hashed password.
   *
   * @return The password reset data.
   */
  public PasswordResetDTO getPasswordReset() {
    return passwordReset;
  }

  /**
   * Gets the user whose password is to be reset.
   *
   * @return The user whose password is to be reset.
   */
  public UserAuthenticationDTO getUser() {
    return user;
  }

  private void showPasswordsDontMatchMessage() {
    FacesContext fctx = FacesContext.getCurrentInstance();
    String message = jfUtils.getMessageFromResourceBundle("v_passwords_dont_match");
    fctx.addMessage(
        "passwordResetInput:password",
        new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
  }
}
