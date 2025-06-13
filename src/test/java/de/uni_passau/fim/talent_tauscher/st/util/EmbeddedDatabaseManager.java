package de.uni_passau.fim.talent_tauscher.st.util;

import de.uni_passau.fim.talent_tauscher.model.persistence.util.ResourceReader;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Provides utility methods for managing an embedded PostgreSQL database used for testing. Includes
 * functionalities to start the database, initialize the schema, insert initial data, and manage
 * database connections.
 *
 * <p>This class is designed as a utility class and should not be instantiated. Instead, it provides
 * static methods to manage the lifecycle of the embedded database.
 *
 * @author Leon Föckersperger
 */
public final class EmbeddedDatabaseManager {

  private static final Logger LOGGER = Logger.getLogger(EmbeddedDatabaseManager.class.getName());
  private static final String CONFIG_FILE_PATH = "WEB-INF/config/application.properties";
  private static final String DB_PORT_KEY = "DB_PORT";
  private static final int SLEEP_TIME = 100;

  private EmbeddedDatabaseManager() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Main method to start the embedded database and initialize schema and data.
   *
   * @param args Command line arguments (not used).
   * @throws InterruptedException if the thread is interrupted while waiting for the database port
   *     to become available.
   * @throws IOException if there is an I/O error starting the database or loading configuration.
   * @throws SQLException if there is an error initializing the database schema or inserting data.
   */
  public static void main(String[] args) throws InterruptedException, IOException, SQLException {
    int databasePort = loadDatabaseConfiguration();
    waitForAvailablePort(databasePort);
    EmbeddedPostgres postgres = startDatabase(databasePort);
    initSchema(postgres);
  }

  /**
   * Initializes the database schema including the creation of the base schema and insertion of
   * initial data, and updating the default logo image.
   *
   * @param postgres The embedded PostgreSQL instance.
   * @throws SQLException if there is an error executing the SQL statements to initialize the
   *     schema.
   */
  private static void initSchema(EmbeddedPostgres postgres) throws SQLException {
    // Create a new ResourceReader instance for accessing SQL scripts
    ResourceReader resourceReader =
        new ResourceReader(Thread.currentThread().getContextClassLoader()::getResourceAsStream, "");

    // Create the base schema
    executeSql(postgres, resourceReader.fetchSQLSchema(), "Base schema created successfully.");

    // Insert initial data
    executeSql(
        postgres,
        resourceReader.fetchInitialDataDefinition(),
        "Initial data inserted successfully.");

    insertImage(
        postgres, resourceReader.fetchDefaultAvatarImage(), "\"user\"", "avatar");

    // Update default logo image in the system_settings table
    insertImage(
        postgres,
        resourceReader.fetchDefaultLogoImage(),
        "system_settings",
        "logo");
  }

  /**
   * Executes a SQL statement using the provided database connection from the embedded PostgreSQL
   * instance.
   *
   * @param postgres The embedded PostgreSQL database instance.
   * @param sql The SQL statement to execute.
   * @param successMessage A message to log upon successful execution of the SQL statement.
   * @throws SQLException if there is an error executing the SQL statement.
   */
  private static void executeSql(EmbeddedPostgres postgres, String sql, String successMessage)
      throws SQLException {
    try (Connection connection = getConnection(postgres);
        PreparedStatement statement = connection.prepareStatement(sql)) {
      connection.setAutoCommit(false);
      statement.executeUpdate();
      connection.commit();
      LOGGER.info(successMessage);
    } catch (SQLException e) {
      LOGGER.severe("Failed to execute SQL: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Inserts the default logo image into the system settings table of the database.
   *
   * @param postgres The embedded PostgreSQL database instance.
   * @param imageBytes The byte array of the image to insert as the default logo.
   * @param column The column name in the database table to update.
   * @param table The table name in the database to update.
   * @throws SQLException if there is an error updating the image in the database.
   */
  private static void insertImage(
      EmbeddedPostgres postgres, byte[] imageBytes, String table, String column)
      throws SQLException {
    String sql = String.format("UPDATE %s SET %s = ? WHERE id = 1", table, column);
    try (Connection connection = getConnection(postgres);
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setBytes(1, imageBytes);
      statement.executeUpdate();
      LOGGER.info("Default image updated successfully in " + table + ".");
    } catch (SQLException e) {
      LOGGER.severe("Failed to update image: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Loads the database configuration from a properties file.
   *
   * @return The database port number configured in the properties file.
   * @throws IOException if there is an error reading the configuration file.
   */
  private static int loadDatabaseConfiguration() throws IOException {
    Properties config = new Properties();
    try (InputStream input =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE_PATH)) {
      if (input == null) {
        throw new IOException("Configuration file not found at " + CONFIG_FILE_PATH);
      }
      try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
        config.load(reader);
        return Integer.parseInt(config.getProperty(DB_PORT_KEY));
      }
    }
  }

  /**
   * Waits for a specified port to become available.
   *
   * @param databasePort The port number to check.
   * @throws InterruptedException if the thread waiting for the port to become available is
   *     interrupted.
   */
  private static void waitForAvailablePort(int databasePort) throws InterruptedException {
    while (isPortInUse(databasePort)) {
      LOGGER.fine("Waiting for database port " + databasePort + " to become available.");
      Thread.sleep(SLEEP_TIME);
    }
  }

  /**
   * Checks if a specified port is currently in use.
   *
   * @param databasePort The port to check.
   * @return {@code true} if the port is in use, otherwise {@code false}.
   */
  private static boolean isPortInUse(int databasePort) {
    try (Socket ignored = new Socket("localhost", databasePort)) {
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * Starts the embedded PostgreSQL database on a specified port.
   *
   * @param databasePort The port to start the database on.
   * @return An instance of {@link EmbeddedPostgres} representing the started database.
   * @throws IOException if there is an error starting the database.
   */
  private static EmbeddedPostgres startDatabase(int databasePort) throws IOException {
    EmbeddedPostgres pg = EmbeddedPostgres.builder().setPort(databasePort).start();
    LOGGER.info("EmbeddedPostgres started on port " + databasePort);
    try (Connection connection = pg.getPostgresDatabase().getConnection();
         PreparedStatement statement =
           connection.prepareStatement("CREATE DATABASE \"talent_tauscher\""); ) {
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return pg;
  }

  /**
   * Shuts down the embedded PostgreSQL database.
   *
   * @param postgres The instance of {@link EmbeddedPostgres} to shut down.
   * @throws IOException if there is an error during the shutdown process.
   */
  public static void shutdown(EmbeddedPostgres postgres) throws IOException {
    if (postgres != null) {
      postgres.close();
      LOGGER.info("EmbeddedPostgres stopped.");
    }
  }

  /**
   * Retrieves a database connection from the embedded PostgreSQL instance.
   *
   * @param postgres The embedded PostgreSQL database instance.
   * @return A database connection.
   * @throws SQLException if the database has not been initialized or there is an error obtaining
   *     the connection.
   */
  private static Connection getConnection(EmbeddedPostgres postgres) throws SQLException {
    if (postgres == null) {
      throw new SQLException("Database has not been initialized.");
    }
    return postgres.getDatabase("postgres", "talent_tauscher").getConnection();
  }
}
