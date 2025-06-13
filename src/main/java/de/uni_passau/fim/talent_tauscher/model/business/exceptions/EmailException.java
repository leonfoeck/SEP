package de.uni_passau.fim.talent_tauscher.model.business.exceptions;

/** Checked exception thrown when sending an email fails. */
public class EmailException extends RuntimeException {

  /** Constructs a new {@link EmailException} without a detail message. */
  public EmailException() {
    super();
  }

  /**
   * Constructs a new {@link EmailException} with the given detail message.
   *
   * @param message The detail message.
   */
  public EmailException(String message) {
    super(message);
  }
}
