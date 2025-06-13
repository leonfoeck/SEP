package de.uni_passau.fim.talent_tauscher.st.load_test;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.st.StandardTestSuite;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.uni_passau.fim.talent_tauscher.st.util.STDatabaseWriter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

/**
 * A load test for the TalentTauscher application.
 *
 * @author Jakob Edmaier
 */
@SuppressWarnings({"checkstyle:MagicNumber", "PMD.ClassNamingConventions"})
public class LoadST {

  private static final Logger LOGGER = LoggerProducer.get(LoadST.class);
  private static final int REPETITIONS = 5;

  private final STDatabaseWriter dbWriter = new STDatabaseWriter();
  private final SummaryGeneratingListener listener = new SummaryGeneratingListener();

  /**
   * Runs the load test.
   */
  //@Test
  void run() {
    LOGGER.info("Run with 1 client");
    for (int i = 0; i < REPETITIONS; i++) {
      runWithParallelClients(1);
    }

    LOGGER.info("Run with 20 clients");
    for (int i = 0; i < REPETITIONS; i++) {
      runWithParallelClients(20);
    }
  }

  private void runWithParallelClients(int parallelClients) {
    long start = System.currentTimeMillis();
    List<Thread> threads = new ArrayList<>();

    for (int i = 0; i < parallelClients; i++) {
      Thread t =
          new Thread(
              () -> {
                LauncherDiscoveryRequest request =
                    LauncherDiscoveryRequestBuilder.request()
                        .selectors(selectClass(StandardTestSuite.class))
                        .build();

                Launcher launcher = LauncherFactory.create();
                TestPlan testPlan = launcher.discover(request);
                launcher.registerTestExecutionListeners(listener);
                launcher.execute(request);
              });
      threads.add(t);
      t.start();
    }

    try {
      for (Thread t : threads) {
        t.join();
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    long end = System.currentTimeMillis();
    long elapsed = end - start;

    try {
      dbWriter.writePerformanceReport(parallelClients, elapsed);
    } catch (SQLException e) {
      fail("Could not write result of load test to the database.");
    }
  }
}
