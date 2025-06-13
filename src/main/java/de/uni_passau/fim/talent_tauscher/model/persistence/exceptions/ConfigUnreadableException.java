package de.uni_passau.fim.talent_tauscher.model.persistence.exceptions;

/** Unchecked exception thrown if the database configuration cannot be loaded. */
public class ConfigUnreadableException extends RuntimeException {

  /** Constructs a new {@link ConfigUnreadableException} without a detail message. */
  public ConfigUnreadableException() {
    super();
  }

  /**
   * Constructs a new {@link ConfigUnreadableException} with the given detail message.
   *
   * @param message The detail message.
   */
  public ConfigUnreadableException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@link ConfigUnreadableException} with the given detail message and cause.
   *
   * @param message The detail message.
   * @param cause The cause.
   */
  public ConfigUnreadableException(String message, Throwable cause) {
    super(message, cause);
  }
}
