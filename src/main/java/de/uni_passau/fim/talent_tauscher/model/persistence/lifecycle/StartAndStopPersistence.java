package de.uni_passau.fim.talent_tauscher.model.persistence.lifecycle;

import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.persistence.config.AppConfig;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.ConnectionPool;
import de.uni_passau.fim.talent_tauscher.model.persistence.util.ResourceReader;
import java.io.InputStream;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * This utility class is responsible for managing the startup and shutdown processes of the
 * persistence layer in the application.
 *
 * @author Leon Föckersperger
 */
public final class StartAndStopPersistence {

  private static final Logger LOGGER = LoggerProducer.get(StartAndStopPersistence.class);

  private StartAndStopPersistence() {
    // Utility class should not be instantiated
  }

  /**
   * Initializes the persistence layer, including logging, reading configuration files, setting up
   * database connections, and initializing the database schema if required.
   *
   * @param contextStreamProvider A function providing InputStreams for context resources.
   */
  public static void init(Function<String, InputStream> contextStreamProvider) {
    LoggerProducer.init(contextStreamProvider);
    LOGGER.info("Initializing persistence layer.");
    AppConfig.getInstance().init(contextStreamProvider);
    ConnectionPool.getInstance();
    StoreSetup setup =
        StoreSetupFactory.createStoreSetup(
            new ResourceReader(contextStreamProvider, "WEB-INF/classes/"));
    if (!setup.schemaExists()) {
      LOGGER.info("Setting up DB Schema.");
      setup.createSchema();
      setup.initializeData();
    }
  }

  /** Destroys the connection pool to clean up resources. */
  public static void destroy() {
    ConnectionPool.getInstance().destroy();
    LOGGER.info("Persistence layer destroyed.");
  }
}
