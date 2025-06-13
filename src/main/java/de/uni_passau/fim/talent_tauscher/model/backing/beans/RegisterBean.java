package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.PasswordInputDTO;
import de.uni_passau.fim.talent_tauscher.dto.SystemDataDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.VerificationTokenDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.util.EmailBuilder;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.ResourceService;
import de.uni_passau.fim.talent_tauscher.model.business.services.SystemDataService;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import de.uni_passau.fim.talent_tauscher.model.business.util.EmailDispatcher;
import de.uni_passau.fim.talent_tauscher.model.business.util.VerificationTokenGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;

/**
 * Backing bean for the register page.
 *
 * @author Sturm
 */
@Named
@ViewScoped
public class RegisterBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private static final int DEFAULT_TALENT_POINTS = 50;

  private static final long NEGATIVE_1_LONG = -1L;

  private UserDetailsDTO user;

  private PasswordInputDTO passwordInput;

  private boolean emailDispatchedForMail;

  private String oldEmail;

  private SystemDataDTO systemdata;

  @Inject private UserService userService;

  @Inject private VerificationTokenGenerator tokenGenerator;

  @Inject private JFUtils jfUtils;

  @Inject private EmailBuilder emailBuilder;

  @Inject private EmailDispatcher emailDispatcher;

  @Inject private ResourceService resourceService;

  @Inject private SystemDataService systemDataService;

  /** Default constructor. */
  public RegisterBean() {
    // Default constructor
  }

  /** Initializes the UserDetailsDTO and the PasswordInputDTO for the facelet. */
  @PostConstruct
  public void init() {
    user = new UserDetailsDTO();
    passwordInput = new PasswordInputDTO();
    emailDispatchedForMail = false;
    oldEmail = "";
    systemdata = systemDataService.get();
  }

  /**
   * Returns if the form has been successfully submitted.
   *
   * @return If the form has been successfully submitted.
   */
  public boolean isEmailDispatchedForMail() {
    return emailDispatchedForMail;
  }

  /**
   * Gets the user.
   *
   * @return The user.
   */
  public UserDetailsDTO getUser() {
    return user;
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
   * Submits the form data. If the registration was successful, the user is redirected to the
   * all-advertisements page.
   *
   * @return The link to the all-advertisements page.
   */
  public String submit() {
    if (!passwordInput.getPassword().equals(passwordInput.getRepetition())) {
      sendFacesMessage("userInput:password", "v_passwords_dont_match");
      return "";
    }

    VerificationTokenDTO token =
        new VerificationTokenDTO(user.getEmail(), tokenGenerator.generateToken());
    user.setAvatar(
        resourceService.getDefaultAvatarImage(
            FacesContext.getCurrentInstance().getExternalContext()::getResourceAsStream));
    user.setAdminVerified(!systemdata.isAdminConfirmationRequiredForRegistration());
    user.setTalentPoints(DEFAULT_TALENT_POINTS);

    emailDispatchedForMail = user.getEmail().equals(oldEmail);
    if (!emailDispatchedForMail) {
      oldEmail = user.getEmail();
      emailDispatchedForMail = true;
      emailDispatcher.send(emailBuilder.buildVerificationEmail(user, token));
    }

    if (userService.create(user, passwordInput) == NEGATIVE_1_LONG) {
      sendFacesMessage(null, "v_validation_fail");
      return null;
    }
    userService.putEmailVerificationToken(token);

    FacesContext fctx = FacesContext.getCurrentInstance();
    String message = jfUtils.getMessageFromResourceBundle("f_registered_successful");
    fctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    fctx.getExternalContext().getFlash().setKeepMessages(true);

    return "/view/anonymous/allAdvertisements?faces-redirect=true";
  }

  private void sendFacesMessage(String target, String messageKey) {
    FacesContext fctx = FacesContext.getCurrentInstance();
    String message = jfUtils.getMessageFromResourceBundle(messageKey);
    fctx.addMessage(target, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    fctx.getExternalContext().getFlash().setKeepMessages(true);
  }

  /** Notify the backing Bean that the email input has been updated. */
  public void emailInputUpdated() {
    emailDispatchedForMail = oldEmail.equals(user.getEmail());
  }
}
