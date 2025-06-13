package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.postgres;

import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.ConnectionPool;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.DAOFactory;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.Transaction;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.TransactionException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** PostgreSQL's implementation of the {@link Transaction} interface. */
public class PostgreSQLTransaction implements Transaction {

  private final Logger logger = LoggerProducer.get(PostgreSQLTransaction.class);

  private final Connection connection;
  private boolean committed;
  private boolean isConnectionReleased;

  /** Constructs a new {@code PostgreSQLTransaction} instance. */
  public PostgreSQLTransaction() {
    Connection tempConnection = null;
    boolean initSuccess = false;

    try {
      tempConnection = ConnectionPool.getInstance().getConnection();
      tempConnection.setAutoCommit(false);
      initSuccess = true;
    } catch (SQLException e) {
      ConnectionPool.getInstance()
          .releaseConnection(tempConnection); // Ensure connection is released
      logger.log(Level.SEVERE, "Failed to initialize transaction.", e);
      throw new TransactionException("Failed to initialize transaction.", e);
    } finally {
      if (!initSuccess) {
        this.connection = null;
        this.committed = false;
        this.isConnectionReleased = true;
      } else {
        this.connection = tempConnection;
        this.committed = false;
        this.isConnectionReleased = false;
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public void commit() throws TransactionException {
    if (!isConnectionReleased) {
      try {
        connection.commit();
        committed = true;
      } catch (SQLException e) {
        throw new TransactionException("Committing the transaction failed", e);
      } finally {
        releaseConnection();
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public void abort() {
    if (!committed && !isConnectionReleased) {
      try {
        connection.rollback();
      } catch (SQLException e) {
        logger.log(Level.WARNING, "Failed to roll back transaction.", e);
      } finally {
        releaseConnection();
      }
    }
  }

  /** Ensures the connection is released to the pool properly. */
  private void releaseConnection() {
    if (!isConnectionReleased) {
      ConnectionPool.getInstance().releaseConnection(connection);
      isConnectionReleased = true;
    }
  }

  /** {@inheritDoc} */
  @Override
  public DAOFactory getDAOFactory() {
    return new PostgreSQLDAOFactory(this.connection);
  }

  /** {@inheritDoc} */
  @Override
  public void close() {
    if (!committed && !isConnectionReleased) {
      abort(); // Ensure rollback and release if not committed or released
    } else {
      releaseConnection();
    }
  }
}
