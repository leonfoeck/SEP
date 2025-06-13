package de.uni_passau.fim.talent_tauscher.logging;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * This class is responsible for the logging configuration and for providing instances of the {@link
 * Logger} class. Contains a static method to obtain {@code Logger} instances. Also produces {@code
 * Logger} instances for CDI, whereby the target class is automatically recognized.
 */
public class LoggerProducer {

  /**
   * Initializes logging by reading the corresponding configuration file.
   *
   * @param contextStreamProvider The context stream provider of the servlet event.
   */
  public static void init(Function<String, InputStream> contextStreamProvider) {
    try (InputStream inputStream =
        contextStreamProvider.apply("WEB-INF/config/logging.properties")) {
      LogManager.getLogManager().readConfiguration(inputStream);
    } catch (IOException e) {
      Logger logger = Logger.getAnonymousLogger();
      logger.warning(e.getMessage());
    }
  }

  /**
   * Returns a logger instance for the given class. Wraps the {@link Logger#getLogger(String)}
   * method.
   *
   * @param clazz The class using the logger.
   * @return A logger instance.
   */
  public static Logger get(Class<?> clazz) {
    Logger logger = Logger.getLogger(clazz.getName());
    logger.setLevel(Level.ALL);
    return logger;
  }

  /**
   * Produces {@link Logger} instances for CDI. The target class is automatically inferred from the
   * injection point.
   *
   * @param injectionPoint The injection point.
   * @return A logger instance.
   */
  @Produces
  @Dependent
  public Logger produceLogger(final InjectionPoint injectionPoint) {
    return get(injectionPoint.getMember().getDeclaringClass());
  }
}
