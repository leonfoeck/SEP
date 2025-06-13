package de.uni_passau.fim.talent_tauscher.st.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Responsible for writing the times measured by the {@link LoadST} to the database.
 */
public class STDatabaseWriter {
  private static final String DB_DRIVER = "org.postgresql.Driver";
  private static final String DB_HOST = "bueno.fim.uni-passau.de";
  private static final String DB_NAME = "sep24g01t";
  private static final String DB_USER = "sep24g01";
  private static final String DB_PASSWORD = "aitui5aiSee2";

  /**
   * Stores a new record containing the number of parallel clients and the elapsed time in milliseconds.
   *
   * @param parallelClients The number of parallel clients.
   * @param elapsedMillis The number of elapsed milliseconds.
   * @throws SQLException If an error occurs when writing to the database.
   */
  public void writePerformanceReport(int parallelClients, long elapsedMillis) throws SQLException {
    String query = """
        INSERT INTO test_time (elapsed_millis, number_of_clients)
        VALUES (?, ?);
        """;

    try (Connection connection = getConnection();
        PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.setLong(1, elapsedMillis);
      stmt.setInt(2, parallelClients);
      stmt.executeUpdate();
    }
  }

  private Connection getConnection() throws SQLException {
    try {
      Class.forName(DB_DRIVER);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }

    Properties props = new Properties();
    props.setProperty("user", DB_USER);
    props.setProperty("password", DB_PASSWORD);
    props.setProperty("ssl", "true");
    props.setProperty("sslfactory", "org.postgresql.ssl.DefaultJavaSSLFactory");

    return DriverManager.getConnection("jdbc:postgresql://" + DB_HOST + "/" + DB_NAME, props);
  }
}
