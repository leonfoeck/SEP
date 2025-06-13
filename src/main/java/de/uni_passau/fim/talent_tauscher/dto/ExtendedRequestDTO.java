package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a request and, if present, the associated response. Extends the normal {@link
 * RequestDTO} by including the title of the advertisement as well as the nicknames of the creator
 * and the recipient of the request. These additional fields are displayed in the lists of incoming
 * and outgoing requests.
 */
public class ExtendedRequestDTO extends RequestDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String requestCreatorUsername;
  private String recipientUsername;
  private String advertisementTitle;

  /** Default constructor. */
  public ExtendedRequestDTO() {
    // Default constructor
  }

  /**
   * Gets the username of the request creator.
   *
   * @return The username of the request creator.
   */
  public String getRequestCreatorUsername() {
    return requestCreatorUsername;
  }

  /**
   * Sets the username of the request creator.
   *
   * @param requestCreatorUsername The username of the request creator.
   */
  public void setRequestCreatorUsername(String requestCreatorUsername) {
    this.requestCreatorUsername = requestCreatorUsername;
  }

  /**
   * Gets the username of the recipient of the request.
   *
   * @return The username of the recipient of the request.
   */
  public String getRecipientUsername() {
    return recipientUsername;
  }

  /**
   * Sets the username of the recipient of the request.
   *
   * @param recipientUsername The username of the recipient of the request.
   */
  public void setRecipientUsername(String recipientUsername) {
    this.recipientUsername = recipientUsername;
  }

  /**
   * Gets the title of the advertisement.
   *
   * @return The title of the advertisement.
   */
  public String getAdvertisementTitle() {
    return advertisementTitle;
  }

  /**
   * Sets the title of the advertisement.
   *
   * @param advertisementTitle The title of the advertisement.
   */
  public void setAdvertisementTitle(String advertisementTitle) {
    this.advertisementTitle = advertisementTitle;
  }

  /**
   * Returns a string representation of the ExtendedRequestDTO.
   *
   * @return A string representation of the ExtendedRequestDTO.
   */
  @Override
  public String toString() {
    return "ExtendedRequestDTO{"
        + "requestCreatorUsername='"
        + requestCreatorUsername
        + '\''
        + ", recipientUsername='"
        + recipientUsername
        + '\''
        + ", advertisementTitle='"
        + advertisementTitle
        + '\''
        + '}';
  }
}
