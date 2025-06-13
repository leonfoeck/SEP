package de.uni_passau.fim.talent_tauscher.model.persistence.exceptions;

/** Unchecked exception thrown if the application cannot connect to the data store. */
public class StoreConnectionException extends StoreAccessException {

  /** Constructs a new {@link StoreConnectionException} without a detail message. */
  public StoreConnectionException() {
    super();
  }

  /**
   * Constructs a new {@link StoreConnectionException} with the given detail message.
   *
   * @param message The detail message.
   */
  public StoreConnectionException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@link StoreConnectionException} with the given detail message and cause.
   *
   * @param message The detail message.
   * @param cause The cause.
   */
  public StoreConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
