package de.uni_passau.fim.talent_tauscher.model.persistence.exceptions;

/** Unchecked exception thrown when a transaction commit fails. */
public class TransactionException extends RuntimeException {

  /** Constructs a new {@link TransactionException} without a detail message. */
  public TransactionException() {
    super();
  }

  /**
   * Constructs a new {@link TransactionException} with the given detail message.
   *
   * @param message The detail message.
   */
  public TransactionException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@link TransactionException} with the given detail message and cause.
   *
   * @param message The detail message.
   * @param cause The cause.
   */
  public TransactionException(String message, Throwable cause) {
    super(message, cause);
  }
}
