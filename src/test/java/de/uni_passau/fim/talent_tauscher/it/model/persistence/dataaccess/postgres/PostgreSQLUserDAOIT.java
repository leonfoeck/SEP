package de.uni_passau.fim.talent_tauscher.it.model.persistence.dataaccess.postgres;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uni_passau.fim.talent_tauscher.dto.AuthTokenDTO;
import de.uni_passau.fim.talent_tauscher.dto.PasswordInputDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.business.util.PasswordAuthentication;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.TransactionFactory;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.Transaction;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.UserDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreValidationException;
import de.uni_passau.fim.talent_tauscher.it.util.TestUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.logging.Logger;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Unit test for PostgreSQLUserDAO. */
@EnableWeld
@SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:VisibilityModifier"})
public class PostgreSQLUserDAOIT {

  @WeldSetup
  WeldInitiator weld =
      WeldInitiator.from(PasswordAuthentication.class, Logger.class, LoggerProducer.class)
          .activate(RequestScoped.class, SessionScoped.class, ApplicationScoped.class)
          .build();

  @Inject private PasswordAuthentication passwordAuthentication;

  /** Load the necessary resources before all tests. */
  @BeforeAll
  public static void load() {
    TestUtils.load();
  }

  /** Destroy resources after all tests. */
  @AfterAll
  public static void destroy() {
    TestUtils.destroy();
  }

  /** Test to cause a serialization error by writing to the same entry before committing. */
  @Test
  void serializationFailureTest() {
    UserDetailsDTO user1 = new UserDetailsDTO();
    UserDetailsDTO user2 = new UserDetailsDTO();
    createUsers(user1, user2);
    long id = 0;
    try (Transaction transaction1 = TransactionFactory.createTransaction();
        Transaction transaction2 = TransactionFactory.createTransaction(); ) {
      UserDAO userDAO1 = transaction1.getDAOFactory().createUserDAO();
      UserDAO userDAO2 = transaction2.getDAOFactory().createUserDAO();

      id = assertDoesNotThrow(() -> userDAO1.createUser(user1));
      user1.setId(id);
      assertThrows(StoreValidationException.class, () -> userDAO2.createUser(user2));

      transaction1.commit();
      transaction2.abort();
    }

    try (Transaction transaction =
        TransactionFactory.createTransaction(); ) { // remove created user
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      // check the created user
      UserAuthenticationDTO userAuthenticationDTO = new UserAuthenticationDTO();
      userAuthenticationDTO.setNickname("mmustermann");
      Optional<UserAuthenticationDTO> foundUser = userDAO.findUserByNickname(userAuthenticationDTO);
      assertTrue(foundUser.isPresent());
      assertTrue(compareUsers(user1, foundUser.get()));

      // delete created user again
      UserDTO user = new UserDTO();
      user.setId(id);
      userDAO.deleteUser(user);
      transaction.commit();
    }
  }

  /**
   * Compare two users to check if they are equal.
   *
   * @param user1 the first user
   * @param user2 the second user
   * @return true if users are equal, false otherwise
   */
  private boolean compareUsers(UserDetailsDTO user1, UserAuthenticationDTO user2) {
    return user1.getEmail().equals(user2.getEmail())
        && user1.getId() == user2.getId()
        && user1.getAuthToken().getPasswordHash().equals(user2.getAuthToken().getPasswordHash())
        && user1.getAuthToken().getAlgorithm().equals(user2.getAuthToken().getAlgorithm())
        && user1.getAuthToken().getSalt().equals(user2.getAuthToken().getSalt());
  }

  /**
   * Create users for testing.
   *
   * @param user1 the first user
   * @param user2 the second user
   */
  private void createUsers(UserDetailsDTO user1, UserDetailsDTO user2) {
    user1.setNickname("mmustermann");
    user1.setEmail("mustermann@example.com");
    user1.setPostalCode("12345");
    user1.setCity("musterstadt");
    user1.setStreet("musterstraße");
    user1.setHouseNumber("1");
    user1.setAddressAddition("a");
    user1.setCountry("land");
    user1.setFirstname("max");
    user1.setLastname("mustermann");
    user1.setTalentPoints(50);
    user1.setAdminVerified(false);
    user1.setEmailVerified(false);
    user1.setAdmin(false);
    user1.setAvatar(new byte[255]);
    user1.setPhone("1234567");

    PasswordInputDTO passwordInput1 = new PasswordInputDTO();
    passwordInput1.setPassword("passWord1");
    passwordInput1.setRepetition("passWord1");

    AuthTokenDTO authToken1 = passwordAuthentication.hash(passwordInput1.getPassword());
    user1.setAuthToken(authToken1);

    user2.setNickname("mmustermann");
    user2.setEmail("secondmustermann@example.com");
    user1.setPostalCode("12346");
    user2.setCity("musterdorf");
    user2.setStreet("musterstraße");
    user2.setHouseNumber("1");
    user2.setAddressAddition("a");
    user2.setCountry("land");
    user2.setFirstname("marie");
    user2.setLastname("mustermann");
    user2.setTalentPoints(50);
    user2.setAdminVerified(false);
    user2.setEmailVerified(false);
    user2.setAdmin(false);
    user2.setAvatar(new byte[255]);
    user2.setPhone("1234567");

    PasswordInputDTO passwordInput2 = new PasswordInputDTO();
    passwordInput2.setPassword("1droWssap");
    passwordInput2.setRepetition("1droWssap");

    AuthTokenDTO authToken2 = passwordAuthentication.hash(passwordInput2.getPassword());
    user2.setAuthToken(authToken2);
  }
}
