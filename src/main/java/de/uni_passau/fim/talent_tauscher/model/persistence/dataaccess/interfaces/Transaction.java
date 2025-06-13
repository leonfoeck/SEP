package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces;

/** Represents a data store transaction. */
public interface Transaction extends AutoCloseable {

  /** Commits this transaction. */
  void commit();

  /** Aborts this transaction. */
  void abort();

  /**
   * Returns an instance of the {@link DAOFactory} interface.
   *
   * @return An instance of the {@link DAOFactory} interface.
   */
  DAOFactory getDAOFactory();

  /**
   * Closes this transaction, relinquishing all underlying resources. If the transaction has not
   * been committed, this method calls {@link Transaction#abort()}. This method is invoked
   * automatically on objects managed by the try-with-resources statement. Overrides the {@link
   * AutoCloseable#close()} method so that it does not throw anymore.
   */
  @Override
  default void close() {
    abort();
  }
}
