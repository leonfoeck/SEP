package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the user input necessary to set a new password, i.e., the plain-text password and a
 * repetition of the password.
 */
public class PasswordInputDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String password;
  private String repetition;

  /** Default constructor. */
  public PasswordInputDTO() {
    // Default constructor
  }

  /**
   * Gets the password.
   *
   * @return The password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password.
   *
   * @param password The password.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the password repetition.
   *
   * @return The password repetition.
   */
  public String getRepetition() {
    return repetition;
  }

  /**
   * Sets the password repetition.
   *
   * @param repetition The password repetition.
   */
  public void setRepetition(String repetition) {
    this.repetition = repetition;
  }
}
