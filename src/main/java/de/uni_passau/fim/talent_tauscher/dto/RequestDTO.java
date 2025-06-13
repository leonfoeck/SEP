package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/** Represents a request and, if present, the associated response. */
public class RequestDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private long id;
  private String request;
  private String response;
  private long advertisementId;
  private long senderId;
  private Boolean accepted;
  private LocalDateTime requestTimestamp;
  private LocalDateTime responseTimeStamp;

  /** Default constructor. */
  public RequestDTO() {
    // Default constructor
  }

  /**
   * Gets the id of the request.
   *
   * @return The id of the request.
   */
  public long getId() {
    return id;
  }

  /**
   * Sets the id of the request.
   *
   * @param id The id of the request.
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Gets the request message.
   *
   * @return The request message.
   */
  public String getRequest() {
    return request;
  }

  /**
   * Sets the request message.
   *
   * @param request The request message.
   */
  public void setRequest(String request) {
    this.request = request;
  }

  /**
   * Gets the response message.
   *
   * @return The response message.
   */
  public String getResponse() {
    return response;
  }

  /**
   * Sets the response message.
   *
   * @param response The response message.
   */
  public void setResponse(String response) {
    this.response = response;
  }

  /**
   * Gets the id of the advertisement.
   *
   * @return The id of the advertisement.
   */
  public long getAdvertisementId() {
    return advertisementId;
  }

  /**
   * Sets the id of the advertisement.
   *
   * @param advertisementId The id of the advertisement.
   */
  public void setAdvertisementId(long advertisementId) {
    this.advertisementId = advertisementId;
  }

  /**
   * Gets the user id of the request sender.
   *
   * @return The user id of the request sender.
   */
  public long getSenderId() {
    return senderId;
  }

  /**
   * Sets the user id of the request sender.
   *
   * @param senderId The user id of the request sender.
   */
  public void setSenderId(long senderId) {
    this.senderId = senderId;
  }

  /**
   * Checks if the request was accepted. If {@code accepted} is {@code null}, the request has
   * neither been accepted nor rejected.
   *
   * @return True if the request was accepted, false otherwise.
   */
  public Boolean getAccepted() {
    return accepted;
  }

  /**
   * Sets whether the request was accepted. If {@code accepted} is {@code null}, the request has
   * neither been accepted nor rejected.
   *
   * @param accepted Whether the request was accepted.
   */
  public void setAccepted(Boolean accepted) {
    this.accepted = accepted;
  }

  /**
   * Gets the timestamp of the request.
   *
   * @return The timestamp of the request.
   */
  public LocalDateTime getRequestTimestamp() {
    return requestTimestamp;
  }

  /**
   * Sets the timestamp of the request.
   *
   * @param requestTimestamp The timestamp of the request.
   */
  public void setRequestTimestamp(LocalDateTime requestTimestamp) {
    this.requestTimestamp = requestTimestamp;
  }

  /**
   * Gets the timestamp of the response.
   *
   * @return The timestamp of the response.
   */
  public LocalDateTime getResponseTimeStamp() {
    return responseTimeStamp;
  }

  /**
   * Sets the timestamp of the response.
   *
   * @param responseTimeStamp The timestamp of the response.
   */
  public void setResponseTimeStamp(LocalDateTime responseTimeStamp) {
    this.responseTimeStamp = responseTimeStamp;
  }

  /**
   * Returns a string representation of the request.
   *
   * @return A string representation of the request.
   */
  @Override
  public String toString() {
    return "RequestDTO{"
        + "id="
        + id
        + ", request='"
        + request
        + '\''
        + ", response='"
        + response
        + '\''
        + ", advertisementId="
        + advertisementId
        + ", senderId="
        + senderId
        + ", accepted="
        + accepted
        + ", requestTimestamp="
        + requestTimestamp
        + ", responseTimeStamp="
        + responseTimeStamp
        + '}';
  }
}
