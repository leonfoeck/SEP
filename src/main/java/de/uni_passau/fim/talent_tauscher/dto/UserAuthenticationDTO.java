package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;

/** Represents the information about a user necessary for authentication and authorization. */
public class UserAuthenticationDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private long id;
  private String email;
  private String nickname;
  private AuthTokenDTO authToken;
  private boolean adminVerified;
  private boolean emailVerified;
  private boolean admin;

  /** Default constructor. */
  public UserAuthenticationDTO() {
    // Default constructor
  }

  /**
   * Gets the id of the user.
   *
   * @return The id of the user.
   */
  public long getId() {
    return id;
  }

  /**
   * Sets the id of the user.
   *
   * @param id The id of the user.
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Gets the email address of the user.
   *
   * @return The email address of the user.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email address of the user.
   *
   * @param email The email address of the user.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the nickname of the user.
   *
   * @return The nickname of the user.
   */
  public String getNickname() {
    return nickname;
  }

  /**
   * Sets the nickname of the user.
   *
   * @param nickname The nickname of the user.
   */
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  /**
   * Gets the authentication token of the user.
   *
   * @return The authentication token of the user.
   */
  public AuthTokenDTO getAuthToken() {
    return authToken;
  }

  /**
   * Sets the authentication token of the user.
   *
   * @param authToken The authentication token of the user.
   */
  public void setAuthToken(AuthTokenDTO authToken) {
    this.authToken = authToken;
  }

  /**
   * Checks if the user is verified by an administrator.
   *
   * @return True if the user is verified by an administrator, false otherwise.
   */
  public boolean isAdminVerified() {
    return adminVerified;
  }

  /**
   * Sets the admin verification status.
   *
   * @param adminVerified Whether the user is verified by an administrator.
   */
  public void setAdminVerified(boolean adminVerified) {
    this.adminVerified = adminVerified;
  }

  /**
   * Checks if the user's email is verified.
   *
   * @return True if the user's email is verified, false otherwise.
   */
  public boolean isEmailVerified() {
    return emailVerified;
  }

  /**
   * Sets the email verification status.
   *
   * @param emailVerified Whether the user's email is verified.
   */
  public void setEmailVerified(boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  /**
   * Checks if the user is an administrator.
   *
   * @return True if the user is an administrator, false otherwise.
   */
  public boolean isAdmin() {
    return admin;
  }

  /**
   * Sets the administrator status of the user.
   *
   * @param admin Whether the user is an administrator.
   */
  public void setAdmin(boolean admin) {
    this.admin = admin;
  }
}
