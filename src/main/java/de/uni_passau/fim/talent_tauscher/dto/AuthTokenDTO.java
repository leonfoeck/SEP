package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an authentication token of a user, consisting of the hashed password, a salt, and the
 * algorithm used for hashing.
 */
public class AuthTokenDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String salt;
  private String passwordHash;
  private String algorithm;

  /** Default constructor. */
  public AuthTokenDTO() {
    // Default constructor
  }

  /**
   * Gets the salt used in the hashing process.
   *
   * @return The salt used in the hashing process.
   */
  public String getSalt() {
    return salt;
  }

  /**
   * Sets the salt used in the hashing process.
   *
   * @param salt The salt used in the hashing process.
   */
  public void setSalt(String salt) {
    this.salt = salt;
  }

  /**
   * Gets the hash value of the password.
   *
   * @return The hash value of the password.
   */
  public String getPasswordHash() {
    return passwordHash;
  }

  /**
   * Sets the hash value of the password.
   *
   * @param passwordHash The hash value of the password.
   */
  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  /**
   * Gets the hashing algorithm used.
   *
   * @return The hashing algorithm used.
   */
  public String getAlgorithm() {
    return algorithm;
  }

  /**
   * Sets the hashing algorithm used.
   *
   * @param algorithm The hashing algorithm used.
   */
  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * Returns a string representation of the authentication token.
   *
   * @return A string representation of the authentication token.
   */
  @Override
  public String toString() {
    return "AuthTokenDTO{"
        + "salt='"
        + salt
        + '\''
        + ", passwordHash='"
        + passwordHash
        + '\''
        + ", algorithm='"
        + algorithm
        + '\''
        + '}';
  }
}
