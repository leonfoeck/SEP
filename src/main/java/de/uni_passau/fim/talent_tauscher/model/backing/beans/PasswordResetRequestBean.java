package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.EmailDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.dto.VerificationTokenDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.util.EmailBuilder;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import de.uni_passau.fim.talent_tauscher.model.business.util.EmailDispatcher;
import de.uni_passau.fim.talent_tauscher.model.business.util.VerificationTokenGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Optional;

/** Backing bean for the password forgotten page. */
@Named
@RequestScoped
public class PasswordResetRequestBean {

  private VerificationTokenDTO verification;

  @Inject private UserService userService;

  @Inject private VerificationTokenGenerator tokenGenerator;

  @Inject private EmailDispatcher emailDispatcher;

  @Inject private EmailBuilder emailBuilder;

  @Inject private JFUtils jfUtils;

  /** Default constructor. */
  public PasswordResetRequestBean() {
    // Default constructor
  }

  /** Initializes the bean by creating an instance of {@link VerificationTokenDTO}. */
  @PostConstruct
  public void init() {
    verification = new VerificationTokenDTO();
  }

  /** Submits the request to reset the password. */
  public void submit() {
    UserAuthenticationDTO auth = new UserAuthenticationDTO();
    auth.setEmail(verification.getEmailAddress());
    Optional<UserAuthenticationDTO> user = userService.findUserByEmail(auth);
    if (user.isEmpty()) {
      showEmailAddressNotFoundMessage();
    } else {
      verification.setToken(tokenGenerator.generateToken());
      userService.putPasswordResetToken(verification);
      EmailDTO email = emailBuilder.buildPasswordResetEmail(user.get(), verification);
      emailDispatcher.send(email);
    }
  }

  /**
   * Gets the verification object consisting of the email and the verification token.
   *
   * @return The verification object.
   */
  public VerificationTokenDTO getVerification() {
    return verification;
  }

  private void showEmailAddressNotFoundMessage() {
    FacesContext fctx = FacesContext.getCurrentInstance();
    String message = jfUtils.getMessageFromResourceBundle("v_emailNotFound_passwordResetRequest");
    fctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
  }
}
