package de.uni_passau.fim.talent_tauscher.model.backing.exceptions;

/**
 * Unchecked exception thrown when a user tries to access a page without the necessary permissions.
 */
public class UnauthorizedAccessException extends RuntimeException {

  /** Constructs a new {@link UnauthorizedAccessException} without a detail message. */
  public UnauthorizedAccessException() {
    super();
  }

  /**
   * Constructs a new {@link UnauthorizedAccessException} with the given detail message.
   *
   * @param message The detail message.
   */
  public UnauthorizedAccessException(String message) {
    super(message);
  }
}
