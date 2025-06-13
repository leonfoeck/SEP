package de.uni_passau.fim.talent_tauscher.model.persistence.exceptions;

/** Unchecked exception indicating an invalid input. */
public class StoreValidationException extends StoreAccessException {

  /** Constructs a new {@link StoreValidationException} without a detail message. */
  public StoreValidationException() {
    super();
  }

  /**
   * Constructs a new {@link StoreValidationException} with the given detail message.
   *
   * @param message The detail message.
   */
  public StoreValidationException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@link StoreValidationException} with the specified detail message and cause.
   *
   * @param message The detail message.
   * @param cause The cause.
   */
  public StoreValidationException(String message, Throwable cause) {
    super(message);
  }
}
