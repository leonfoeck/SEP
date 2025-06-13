package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;

/** Transports the data necessary to login. */
public class LoginDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String nickname;
  private String password;

  /** Default constructor. */
  public LoginDTO() {
    // Default constructor
  }

  /**
   * Gets the nickname or the email address used to log in.
   *
   * @return The nickname or the email address used to log in.
   */
  public String getNickname() {
    return nickname;
  }

  /**
   * Sets the nickname or the email address used to log in.
   *
   * @param nickname The nickname or the email address used to log in.
   */
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  /**
   * Gets the plain-text password.
   *
   * @return The plain-text password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the plain-text password.
   *
   * @param password The plain-text password.
   */
  public void setPassword(String password) {
    this.password = password;
  }
}
