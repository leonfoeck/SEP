package de.uni_passau.fim.talent_tauscher.model.persistence.exceptions;

/** Unchecked exception thrown when an error occurs while reading from the data store. */
public class StoreReadException extends StoreAccessException {

  /** Constructs a new {@link StoreReadException} without a detail message. */
  public StoreReadException() {
    super();
  }

  /**
   * Constructs a new {@link StoreReadException} with the given detail message.
   *
   * @param message The detail message.
   */
  public StoreReadException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@link StoreReadException} with the given detail message and cause.
   *
   * @param message The detail message.
   * @param cause The exception cause.
   */
  public StoreReadException(String message, Throwable cause) {
    super(message, cause);
  }
}
