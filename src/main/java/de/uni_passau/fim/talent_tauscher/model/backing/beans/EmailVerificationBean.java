package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.VerificationTokenDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.exceptions.UnauthorizedAccessException;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing bean for the email verification landing page.
 *
 * @author Jakob Edmaier
 */
@Named
@RequestScoped
public class EmailVerificationBean {
  private VerificationTokenDTO verification;

  @Inject private UserService userService;

  /** Default constructor. */
  public EmailVerificationBean() {
    // Default constructor
  }

  /** Initializes the bean by constructing a {@link VerificationTokenDTO}. */
  @PostConstruct
  public void init() {
    verification = new VerificationTokenDTO();
  }

  /** Attempts to verify the email associated with that token. */
  public void attemptVerification() {
    if (verification.getToken() == null) {
      throw new UnauthorizedAccessException();
    }
    userService.verifyEmail(verification);
  }

  /**
   * Gets the verification object.
   *
   * @return The verification object.
   */
  public VerificationTokenDTO getVerification() {
    return verification;
  }
}
