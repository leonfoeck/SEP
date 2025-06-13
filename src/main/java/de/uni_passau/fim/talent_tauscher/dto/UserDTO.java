package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

/** Represents a user. */
public class UserDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private long id;
  private String email = "";
  private String nickname;
  private int talentPoints;
  private boolean adminVerified;
  private boolean emailVerified;
  private byte[] avatar;

  /** Default constructor. */
  public UserDTO() {
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
   * Gets the number of talent points the user has.
   *
   * @return The number of talent points.
   */
  public int getTalentPoints() {
    return talentPoints;
  }

  /**
   * Sets the number of talent points the user has.
   *
   * @param talentPoints The number of talent points.
   */
  public void setTalentPoints(int talentPoints) {
    this.talentPoints = talentPoints;
  }

  /**
   * Gets the avatar image of the user.
   *
   * @return The avatar image of the user.
   */
  public byte[] getAvatar() {
    return avatar == null ? null : avatar.clone();
  }

  /**
   * Sets the avatar image of the user.
   *
   * @param avatar The avatar image of the user.
   */
  public void setAvatar(byte[] avatar) {
    this.avatar = avatar == null ? null : avatar.clone();
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
   * Sets the email verification status of the user.
   *
   * @param emailVerified Whether the user's email is verified.
   */
  public void setEmailVerified(boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  /**
   * Returns a string representation of the user.
   *
   * @return A string representation of the user.
   */
  @Override
  public String toString() {
    return "UserDTO{"
        + "id="
        + id
        + ", email='"
        + email
        + '\''
        + ", nickname='"
        + nickname
        + '\''
        + ", talentPoints="
        + talentPoints
        + ", adminVerified="
        + adminVerified
        + ", emailVerified="
        + emailVerified
        + ", avatar="
        + Arrays.toString(avatar)
        + '}';
  }
}
