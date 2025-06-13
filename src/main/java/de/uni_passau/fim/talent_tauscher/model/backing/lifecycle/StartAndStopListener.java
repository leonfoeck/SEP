package de.uni_passau.fim.talent_tauscher.model.backing.lifecycle;

import de.uni_passau.fim.talent_tauscher.model.business.lifecycle.StartAndStopBusiness;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import java.util.logging.Logger;

/**
 * This class is responsible for managing the system startup and shutdown processes for a Jakarta
 * Faces and CDI based web application.
 *
 * @author Leon Föckersperger
 */
@ApplicationScoped
public class StartAndStopListener {

  @Inject private Logger logger;

  @Inject private StartAndStopBusiness startAndStopBusiness;

  /**
   * Initializes the lower layers of the application upon startup.
   *
   * @param context The servlet context.
   */
  public void init(@Observes @Initialized(ApplicationScoped.class) ServletContext context) {
    startAndStopBusiness.init(context::getResourceAsStream);
    logger.info("Application set up successfully");
  }

  /**
   * Cleans up the application before it is shut down.
   *
   * @param context The servlet context.
   */
  public void destroy(@Observes @Destroyed(ApplicationScoped.class) ServletContext context) {
    startAndStopBusiness.destroy();
    logger.info("Application shut down successfully");
  }
}
