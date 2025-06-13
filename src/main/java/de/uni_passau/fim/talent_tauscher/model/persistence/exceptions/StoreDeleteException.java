package de.uni_passau.fim.talent_tauscher.model.persistence.exceptions;

/** Unchecked exception thrown when an error occurs while deleting data from the data store. */
public class StoreDeleteException extends StoreAccessException {

  /** Constructs a new {@link StoreDeleteException} without a detail message. */
  public StoreDeleteException() {
    super();
  }

  /**
   * Constructs a new {@link StoreDeleteException} with the given detail message.
   *
   * @param message The detail message.
   */
  public StoreDeleteException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@link StoreDeleteException} with the given detail message and cause.
   *
   * @param message The detail message.
   * @param cause The exception cause.
   */
  public StoreDeleteException(String message, Throwable cause) {
    super(message, cause);
  }
}
