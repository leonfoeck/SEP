package de.uni_passau.fim.talent_tauscher.model.persistence.exceptions;

/** Unchecked exception thrown when an entity with a certain id does not exist in the data store. */
public class EntityNotFoundException extends RuntimeException {

  /** Constructs a new {@link EntityNotFoundException} without a detail message. */
  public EntityNotFoundException() {
    super();
  }

  /**
   * Constructs a new {@link EntityNotFoundException} with the given detail message.
   *
   * @param message The detail message.
   */
  public EntityNotFoundException(String message) {
    super(message);
  }
}
