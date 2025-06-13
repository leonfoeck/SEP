package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess;

import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.persistence.config.AppConfig;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreConnectionException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code ConnectionPool} class manages and pools database connections for reuse in an
 * application. It implements a singleton pattern using the Initialization-on-demand Holder idiom to
 * ensure a single instance globally. This class provides methods to borrow and release connections,
 * and to manage the lifecycle of the pool.
 *
 * @author Leon Föckersperger
 */
public final class ConnectionPool {

  private final Logger logger = LoggerProducer.get(Connection.class);
  private final AppConfig config = AppConfig.getInstance();
  private final BlockingQueue<Connection> connections;
  private final Set<Connection> borrowedConnections = ConcurrentHashMap.newKeySet();

  /** Holder class to implement the singleton pattern with lazy initialization and thread safety. */
  private static final class Holder {
    static final ConnectionPool INSTANCE = new ConnectionPool();
  }

  /**
   * Retrieves the singleton instance of {@code ConnectionPool}.
   *
   * @return The singleton instance.
   */
  public static ConnectionPool getInstance() {
    return Holder.INSTANCE;
  }

  /**
   * Constructs a new ConnectionPool. Initializes the pool with a number of connections defined in
   * {@code AppConfig}.
   */
  private ConnectionPool() {
    connections = new LinkedBlockingQueue<>(config.getDBConnectionCount());
    setup();
  }

  /**
   * Sets up the connection pool by loading the database driver and creating initial connections.
   */
  private void setup() {
    loadDriver();
    for (int i = 0; i < config.getDBConnectionCount(); i++) {
      Connection conn = createConnection();
      if (!connections.offer(conn)) {
        logger.warning("Queue is full, failed to add new connection to the pool.");
        closeConnection(conn);
      } else {
        logger.info("Connection added to pool. Current pool size: " + connections.size());
      }
    }
  }

  /**
   * Loads the database driver specified in the application configuration.
   *
   * @throws StoreConnectionException If the database driver cannot be loaded.
   */
  private void loadDriver() {
    try {
      Class.forName(config.getDBDriver());
      logger.config("Database driver loaded successfully.");
    } catch (ClassNotFoundException e) {
      logger.log(Level.SEVERE, "Database driver not found.", e);
      throw new StoreConnectionException("Database driver not found.", e);
    }
  }

  /**
   * Creates a new database connection using the configuration settings.
   *
   * @return A new database connection.
   * @throws StoreConnectionException if a connection cannot be established.
   */
  private Connection createConnection() {
    if (!configurationsAreValid()) {
      logger.severe("Database connection configurations are invalid or incomplete.");
      throw new StoreConnectionException("Invalid or incomplete configuration settings.");
    }

    Properties connectionProperties = new Properties();
    connectionProperties.put("user", config.getDBUsername());
    connectionProperties.put("password", config.getDBPassword());
    connectionProperties.put("ssl", config.isDBUseSSL());

    String connectionString = buildConnectionString();

    try {
      Connection conn = DriverManager.getConnection(connectionString, connectionProperties);
      logger.finer("Successfully created a new connection to the database.");
      return conn;
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "Failed to create database connection: " + e.getMessage(), e);
      throw new StoreConnectionException("Database connection could not be established.", e);
    }
  }

  /**
   * Builds the connection string using the configuration settings.
   *
   * @return The connection string.
   */
  private String buildConnectionString() {
    return String.format(
        "%s://%s/%s", config.getDBConnectionPrefix(), config.getDBHost(), config.getDBName());
  }

  /**
   * Validates the configuration settings required for database connections.
   *
   * @return true if the configuration settings are valid, false otherwise.
   */
  private boolean configurationsAreValid() {
    // Add specific checks for configuration validity, e.g., non-null and non-empty values
    return config.getDBUsername() != null
        && !config.getDBUsername().isEmpty()
        && config.getDBPassword() != null
        && !config.getDBPassword().isEmpty()
        && config.getDBHost() != null
        && !config.getDBHost().isEmpty()
        && config.getDBName() != null
        && !config.getDBName().isEmpty();
  }

  /**
   * Retrieves a connection from the pool. If no valid connection is available, it throws an
   * exception.
   *
   * @return A valid database connection.
   * @throws StoreConnectionException if no valid connection is available.
   */
  public synchronized Connection getConnection() {
    Connection conn;
    try {
      conn = connections.poll(config.getDBConnectionTimeout(), TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // Restore the interrupted status
      logger.log(Level.SEVERE, "Connection retrieval was interrupted.", e);
      throw new StoreConnectionException("Connection retrieval was interrupted.", e);
    }
    if (conn == null) {
      logger.warning("No available connection retrieved from the pool.");
      throw new StoreConnectionException("No available connection.");
    } else if (!isConnectionValid(conn)) {
      logger.warning("No valid connection retrieved from the pool.");
      closeConnection(conn);
      return getConnection(); // Attempt to retrieve another connection
    }
    borrowedConnections.add(conn);
    logger.fine("Connection borrowed from pool.");
    return conn;
  }

  /**
   * Checks if the provided connection is valid and can be used.
   *
   * @param conn The connection to validate.
   * @return true if the connection is valid, false otherwise.
   */
  private boolean isConnectionValid(Connection conn) {
    try {
      return conn != null && !conn.isClosed();
    } catch (SQLException e) {
      logger.log(Level.WARNING, "Failed to validate connection.", e);
      return false;
    }
  }

  /**
   * Releases a connection back to the pool or closes it if it cannot be added back to the pool.
   *
   * @param connection The connection to release.
   */
  public synchronized void releaseConnection(Connection connection) {
    if (connection == null) {
      throw new IllegalArgumentException("Connection cannot be null.");
    }
    if (borrowedConnections.remove(connection)) {
      cleanupConnection(connection);
      boolean added = connections.offer(connection);
      if (!added) {
        logger.warning("Failed to return the connection to the pool: Queue is full");
        closeConnection(connection);
      } else {
        logger.fine("Connection returned to pool.");
      }
    } else {
      logger.warning("Attempted to release an untracked or already released connection.");
      closeConnection(connection);
    }
  }

  /**
   * Performs any necessary cleanup on the connection, such as rolling back uncommitted
   * transactions.
   *
   * @param connection The connection to clean up.
   */
  private void cleanupConnection(Connection connection) {
    try {
      if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
        connection.rollback();
        logger.fine("Connection rollback completed during cleanup.");
      }
    } catch (SQLException e) {
      logger.log(Level.WARNING, "Failed to clean up connection properly.", e);
    }
  }

  /**
   * Closes the specified connection.
   *
   * @param connection The connection to close.
   */
  private void closeConnection(Connection connection) {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
        logger.fine("Connection closed successfully.");
      }
    } catch (SQLException e) {
      logger.log(Level.WARNING, "Failed to close connection.", e);
    }
  }

  /** Destroys the connection pool, closing all connections, both idle and borrowed. */
  public synchronized void destroy() {
    logger.info("Destroying connection pool and closing all connections.");
    Connection conn;
    while ((conn = connections.poll()) != null) {
      closeConnection(conn);
    }
    for (Connection borrowedConn : borrowedConnections) {
      cleanupConnection(borrowedConn);
      closeConnection(borrowedConn);
    }
    borrowedConnections.clear();
    connections.clear();
    logger.info("Connection pool successfully destroyed.");
  }

  /** Releases all borrowed connections back to the pool. Mainly for testing purposes. */
  public void releaseAllConnections() {
    for (Connection borrowedConn : borrowedConnections) {
      releaseConnection(borrowedConn);
    }
    logger.info("All borrowed connections have been released back to the pool.");
  }
}
