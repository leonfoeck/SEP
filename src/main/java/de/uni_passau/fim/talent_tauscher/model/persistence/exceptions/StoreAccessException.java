package de.uni_passau.fim.talent_tauscher.model.persistence.exceptions;

/**
 * Unchecked exception thrown when an error occurs during a data store access. Only should be used
 * when no more specific exception type matches.
 */
public class StoreAccessException extends RuntimeException {

  /** Constructs a new {@link StoreAccessException} without a detail message. */
  public StoreAccessException() {
    super();
  }

  /**
   * Constructs a new {@link StoreAccessException} with the given detail message.
   *
   * @param message The detail message.
   */
  public StoreAccessException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@link StoreAccessException} with the given detail message and cause.
   *
   * @param message The detail message.
   * @param cause The exception cause.
   */
  public StoreAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
