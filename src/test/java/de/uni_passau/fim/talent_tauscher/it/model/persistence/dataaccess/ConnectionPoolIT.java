package de.uni_passau.fim.talent_tauscher.it.model.persistence.dataaccess;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.uni_passau.fim.talent_tauscher.it.util.TestUtils;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.ConnectionPool;
import java.sql.Connection;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ConnectionPool} class.
 *
 * <p>This class tests the functionality of the connection pool, including acquiring, releasing, and
 * handling connections when the pool is empty.
 */
@EnableAutoWeld
@SuppressWarnings("checkstyle:MagicNumber")
class ConnectionPoolIT {

  /** Sets up the test environment before all tests. */
  @BeforeAll
  public static void setup() {
    TestUtils.load();
  }

  /** Cleans up the test environment after all tests. */
  @AfterAll
  public static void teardown() {
    TestUtils.destroy();
  }

  /** Releases all connections in the pool after each test. */
  @AfterEach
  public void releaseAllConnections() {
    ConnectionPool.getInstance().releaseAllConnections();
  }

  /** Tests acquiring a connection from the pool. */
  @Test
  void testGetConnection() {
    Connection connection = ConnectionPool.getInstance().getConnection();
    assertNotNull(connection, "Connection should not be null");
    ConnectionPool.getInstance().releaseConnection(connection);
  }

  /** Tests releasing a connection back to the pool and then re-acquiring it. */
  @Test
  void testReleaseAndReacquireConnection() {
    Connection connection = ConnectionPool.getInstance().getConnection();
    assertNotNull(connection, "Connection should not be null");
    ConnectionPool.getInstance().releaseConnection(connection);

    Connection newConnection = ConnectionPool.getInstance().getConnection();
    assertNotNull(newConnection, "New connection should not be null");
    ConnectionPool.getInstance().releaseConnection(newConnection);
  }

  /** Tests acquiring and releasing multiple connections. */
  @Test
  void testMultipleConnections() {
    Connection connection1 = ConnectionPool.getInstance().getConnection();
    Connection connection2 = ConnectionPool.getInstance().getConnection();

    assertNotNull(connection1, "Connection1 should not be null");
    assertNotNull(connection2, "Connection2 should not be null");

    ConnectionPool.getInstance().releaseConnection(connection1);
    ConnectionPool.getInstance().releaseConnection(connection2);
  }

  /** Tests releasing an invalid (null) connection. */
  @Test
  void testInvalidReleaseConnection() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          ConnectionPool pool = ConnectionPool.getInstance();
          pool.releaseConnection(null);
        },
        "Should throw an exception when releasing a null connection");
  }
}
