package de.uni_passau.fim.talent_tauscher.model.business.lifecycle;

import de.uni_passau.fim.talent_tauscher.model.persistence.lifecycle.StartAndStopPersistence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.InputStream;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * This class is responsible for managing the startup and shutdown processes of the business layer
 * in the application.
 *
 * @author Leon Föckersperger
 */
@ApplicationScoped
public class StartAndStopBusiness {

  @Inject private Logger logger;

  /**
   * Initializes the business layer of the application. Sets up a shutdown hook and starts the
   * initialization of the persistence layer.
   *
   * @param contextStreamProvider A function providing InputStreams for context resources.
   */
  public void init(Function<String, InputStream> contextStreamProvider) {
    StartAndStopPersistence.init(contextStreamProvider);
    initShutdownHook();
  }

  /**
   * Cleans up the business layer before the application is shut down and starts the destruction of
   * the persistence layer.
   */
  public void destroy() {
    StartAndStopPersistence.destroy();
  }

  /** Sets up a shutdown hook to ensure proper cleanup during JVM shutdown. */
  private void initShutdownHook() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  logger.info("Shutting down application inside shutdown hook.");
                  StartAndStopPersistence.destroy();
                  logger.info("Application shut down successfully.");
                }));
  }
}
