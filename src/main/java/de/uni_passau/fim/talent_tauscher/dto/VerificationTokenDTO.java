package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a verification token used for password resetting and email confirmation. Contains the
 * email address of the user and the actual verification token.
 */
public class VerificationTokenDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String emailAddress;
  private String token;

  /** Constructs a new {@link VerificationTokenDTO}. */
  public VerificationTokenDTO() {
    // Default constructor
  }

  /**
   * Constructs a new {@link VerificationTokenDTO} with the specified email address and token.
   *
   * @param emailAddress The email address.
   * @param token The random verification token.
   */
  public VerificationTokenDTO(String emailAddress, String token) {
    this.emailAddress = emailAddress;
    this.token = token;
  }

  /**
   * Gets the email address with which the token can be used.
   *
   * @return The email address.
   */
  public String getEmailAddress() {
    return emailAddress;
  }

  /**
   * Sets the email address with which the token can be used.
   *
   * @param emailAddress The email address.
   */
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  /**
   * Gets the verification token string.
   *
   * @return The verification token string.
   */
  public String getToken() {
    return token;
  }

  /**
   * Sets the verification token string.
   *
   * @param token The verification token string.
   */
  public void setToken(String token) {
    this.token = token;
  }
}
