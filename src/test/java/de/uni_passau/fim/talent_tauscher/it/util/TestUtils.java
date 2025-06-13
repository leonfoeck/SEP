package de.uni_passau.fim.talent_tauscher.it.util;

import de.uni_passau.fim.talent_tauscher.model.persistence.config.AppConfig;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.ConnectionPool;
import de.uni_passau.fim.talent_tauscher.model.persistence.lifecycle.StoreSetup;
import de.uni_passau.fim.talent_tauscher.model.persistence.lifecycle.StoreSetupFactory;
import de.uni_passau.fim.talent_tauscher.model.persistence.util.ResourceReader;

/** Utility class for setting up and tearing down the persistence store during tests. */
public final class TestUtils {

  /** Flag indicating whether the persistence store has been initialized. */
  private static boolean initialized = false;

  /** Private constructor to prevent instantiation of this utility class. */
  private TestUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Loads the application configuration, initializes the connection pool, and sets up the database
   * schema and initial data. This method ensures the setup is performed only once.
   */
  public static void load() {
    if (!initialized) {
      AppConfig.getInstance()
          .init(Thread.currentThread().getContextClassLoader()::getResourceAsStream);
      ConnectionPool.getInstance();
      StoreSetup setup =
          StoreSetupFactory.createStoreSetup(
              new ResourceReader(
                  Thread.currentThread().getContextClassLoader()::getResourceAsStream, ""));
      if (!setup.schemaExists()) {
        setup.createSchema();
        setup.initializeData();
      }
      initialized = true;
    }
  }

  /** Destroys the connection pool. */
  public static void destroy() {
    ConnectionPool.getInstance().releaseAllConnections();
  }
}
