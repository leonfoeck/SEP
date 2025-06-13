package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;

/** Represents an email with recipient, subject and message body. */
public class EmailDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String recipientAddress;
  private String subject;
  private String body;

  /** Default constructor. */
  public EmailDTO() {
    // Default constructor
  }

  /**
   * Gets the email address of the recipient.
   *
   * @return The email address of the recipient.
   */
  public String getRecipientAddress() {
    return recipientAddress;
  }

  /**
   * Sets the email address of the recipient.
   *
   * @param recipientAddress The new email address.
   */
  public void setRecipientAddress(String recipientAddress) {
    this.recipientAddress = recipientAddress;
  }

  /**
   * Gets the subject of the email.
   *
   * @return The subject of the email.
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Sets the subject of the email.
   *
   * @param subject The new subject.
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Gets the body of the email.
   *
   * @return The body of the email.
   */
  public String getBody() {
    return body;
  }

  /**
   * Sets the body of the email.
   *
   * @param body The new body.
   */
  public void setBody(String body) {
    this.body = body;
  }
}
