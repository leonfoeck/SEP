package de.uni_passau.fim.talent_tauscher.model.persistence.exceptions;

/** Exception thrown when an error occurs while writing an update to the data store. */
public class StoreUpdateException extends StoreAccessException {

  /** Constructs a new {@link StoreUpdateException} without a detail message. */
  public StoreUpdateException() {
    super();
  }

  /**
   * Constructs a new {@link StoreUpdateException} with the given detail message.
   *
   * @param message The detail message.
   */
  public StoreUpdateException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@link StoreUpdateException} with the given detail message and cause.
   *
   * @param message The detail message.
   * @param cause The exception cause.
   */
  public StoreUpdateException(String message, Throwable cause) {
    super(message, cause);
  }
}
