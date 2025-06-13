package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Transports the information necessary to reset a password, i.e., a verification token and the new
 * authentication token.
 */
public class PasswordResetDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String verificationToken;
  private AuthTokenDTO authToken;

  /** Default constructor. */
  public PasswordResetDTO() {
    // Default constructor
  }

  /**
   * Gets the authentication token containing the hashed password.
   *
   * @return A copy of the authentication token containing the hashed password.
   */
  public AuthTokenDTO getAuthToken() {
    return authToken;
  }

  /**
   * Sets the authentication token containing the hashed password.
   *
   * @param authToken The authentication token containing the hashed password.
   */
  public void setAuthToken(AuthTokenDTO authToken) {
    this.authToken = authToken;
  }

  /**
   * Gets the verification token used for resetting the password.
   *
   * @return The verification token.
   */
  public String getVerificationToken() {
    return verificationToken;
  }

  /**
   * Sets the verification token used for resetting the password.
   *
   * @param verificationToken The verification token.
   */
  public void setVerificationToken(String verificationToken) {
    this.verificationToken = verificationToken;
  }
}
