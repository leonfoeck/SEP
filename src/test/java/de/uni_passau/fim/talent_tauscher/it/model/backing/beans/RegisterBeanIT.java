package de.uni_passau.fim.talent_tauscher.it.model.backing.beans;

import static org.junit.jupiter.api.Assertions.assertThrows;

import de.uni_passau.fim.talent_tauscher.dto.AuthTokenDTO;
import de.uni_passau.fim.talent_tauscher.dto.PasswordInputDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.backing.util.EmailBuilder;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import de.uni_passau.fim.talent_tauscher.model.business.util.EmailDispatcher;
import de.uni_passau.fim.talent_tauscher.model.business.util.PasswordAuthentication;
import de.uni_passau.fim.talent_tauscher.model.business.util.VerificationTokenGenerator;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.TransactionFactory;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.Transaction;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.UserDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreValidationException;
import de.uni_passau.fim.talent_tauscher.it.util.TestUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.util.logging.Logger;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * This class contains tests for the registration process in the Talent Tauscher application. It
 * uses JUnit 5 and Weld for dependency injection and testing.
 */
@ExtendWith(WeldJunit5Extension.class)
@SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:VisibilityModifier"})
public class RegisterBeanIT {

  /** Weld setup for initializing dependencies for the test class. */
  @WeldSetup
  WeldInitiator weld =
      WeldInitiator.from(
              UserService.class,
              VerificationTokenGenerator.class,
              JFUtils.class,
              PasswordAuthentication.class,
              Logger.class,
              LoggerProducer.class,
              EmailDispatcher.class,
              EmailBuilder.class)
          .activate(RequestScoped.class, ApplicationScoped.class)
          .build();

  @Inject private PasswordAuthentication passwordAuthentication;

  /** Loads necessary resources before all tests. */
  @BeforeAll
  public static void load() {
    TestUtils.load();
  }

  /** Cleans up resources after all tests. */
  @AfterAll
  public static void destroy() {
    TestUtils.destroy();
  }

  /** Tries provoking a race condition by writing into the database with multiple threads. */
  @Test
  void raceCondition() {
    UserDetailsDTO user1 = new UserDetailsDTO();
    UserDetailsDTO user2 = new UserDetailsDTO();

    createUsers(user1, user2);

    DBUserDummy userDummy1 = new DBUserDummy(user1);
    AsyncTester tester1 = new AsyncTester(userDummy1);
    DBUserDummy userDummy2 = new DBUserDummy(user1);
    AsyncTester tester2 = new AsyncTester(userDummy2);

    tester1.start();
    tester2.start();

    assertThrows(
        StoreValidationException.class,
        () -> {
          tester1.testIfThrown();
          tester2.testIfThrown();
        });
  }

  /**
   * Creates two users with necessary details for testing.
   *
   * @param user1 The first user details.
   * @param user2 The second user details.
   */
  private void createUsers(UserDetailsDTO user1, UserDetailsDTO user2) {
    user1.setNickname("mmustermann");
    user1.setEmail("mustermann@example.com");
    user1.setCity("musterstadt");
    user1.setStreet("musterstraße");
    user1.setHouseNumber("1");
    user1.setCountry("land");
    user1.setFirstname("max");
    user1.setLastname("mustermann");
    user1.setAdminVerified(true);
    user1.setAvatar(new byte[255]);

    PasswordInputDTO passwordInput1 = new PasswordInputDTO();
    passwordInput1.setPassword("passWord1");
    passwordInput1.setRepetition("passWord1");

    AuthTokenDTO authToken1 = passwordAuthentication.hash(passwordInput1.getPassword());
    user1.setAuthToken(authToken1);

    user2.setNickname("mmustermann");
    user2.setEmail("secondmustermann@example.com");
    user2.setCity("musterdorf");
    user2.setStreet("musterstraße");
    user2.setHouseNumber("1");
    user2.setCountry("land");
    user2.setFirstname("marie");
    user2.setLastname("mustermann");
    user2.setAdminVerified(true);
    user2.setAvatar(new byte[255]);

    PasswordInputDTO passwordInput2 = new PasswordInputDTO();
    passwordInput2.setPassword("1droWssap");
    passwordInput2.setRepetition("1droWssap");

    AuthTokenDTO authToken2 = passwordAuthentication.hash(passwordInput2.getPassword());
    user2.setAuthToken(authToken2);
  }

  /** A helper class for running tasks asynchronously. */
  private class AsyncTester {
    private final Thread thread;
    private StoreValidationException exception;

    /**
     * Constructs an AsyncTester with a specified runnable task.
     *
     * @param runnable The task to run asynchronously.
     */
    AsyncTester(final Runnable runnable) {
      thread =
          new Thread(
              () -> {
                try {
                  runnable.run();
                } catch (StoreValidationException e) {
                  exception = e;
                }
              });
    }

    /** Starts the asynchronous task. */
    public void start() {
      thread.start();
    }

    /**
     * Tests if the task has thrown an exception.
     *
     * @throws InterruptedException If the thread is interrupted.
     * @throws StoreValidationException If a StoreValidationException is thrown by the task.
     */
    public void testIfThrown() throws InterruptedException {
      thread.join();
      if (exception != null) {
        throw exception;
      }
    }
  }

  /** A dummy implementation for simulating database user actions. */
  private class DBUserDummy implements Runnable {
    final UserDetailsDTO user;

    /**
     * Constructs a DBUserDummy with specified user details.
     *
     * @param user The user details.
     */
    DBUserDummy(UserDetailsDTO user) {
      this.user = user;
    }

    /** Runs the database operations for the user. */
    public void run() {
      try (Transaction transaction = TransactionFactory.createTransaction()) {
        UserDAO userDAO = transaction.getDAOFactory().createUserDAO();

        userDAO.createUser(user);
        transaction.commit();
      }
    }
  }
}
