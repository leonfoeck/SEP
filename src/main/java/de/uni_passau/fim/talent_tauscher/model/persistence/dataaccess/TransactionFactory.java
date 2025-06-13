package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess;

import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.Transaction;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.postgres.PostgreSQLTransaction;

/**
 * Factory class for creating instances of the {@link Transaction} interface.
 *
 * @author Jakob Edmaier
 */
public final class TransactionFactory {

  private TransactionFactory() {
    // utility class
  }

  /**
   * Creates an instance of the {@link Transaction} interface.
   *
   * @return An instance of the {@link Transaction} interface.
   */
  public static Transaction createTransaction() {
    return new PostgreSQLTransaction();
  }
}
