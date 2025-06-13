package de.uni_passau.fim.talent_tauscher.it.model.backing.beans;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uni_passau.fim.talent_tauscher.dto.PasswordInputDTO;
import de.uni_passau.fim.talent_tauscher.dto.PasswordResetDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.VerificationTokenDTO;
import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.backing.beans.PasswordResetInputBean;
import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import de.uni_passau.fim.talent_tauscher.model.business.util.PasswordAuthentication;
import de.uni_passau.fim.talent_tauscher.it.util.TestUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import java.util.logging.Logger;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/** Unit tests for {@link PasswordResetInputBean} class. */
@EnableWeld
@TestInstance(Lifecycle.PER_CLASS)
@SuppressWarnings("checkstyle:VisibilityModifier")
class PasswordResetInputBeanIT {

  private static final String NEW_PASSWORD = "new_password1";

  private static final String USER_EMAIL = "password.reset@example.com";

  private static final String VERIFICATION_TOKEN = "c4e94744-5233-4499-a896-c4fbeee1227d";

  /** Weld initiator, which initializes the Weld container and injects all necessary beans. */
  @WeldSetup
  WeldInitiator weld =
      WeldInitiator.from(
              PasswordAuthentication.class,
              UserService.class,
              PasswordResetInputBean.class,
              JFUtils.class,
              UserSession.class,
              Logger.class,
              LoggerProducer.class)
          .activate(
              RequestScoped.class, SessionScoped.class, ApplicationScoped.class, ViewScoped.class)
          .build();

  @Inject private PasswordAuthentication passwordAuthentication;

  @Inject private PasswordResetInputBean passwordResetInputBean;

  @Inject private UserService userService;

  /** Loads the test data and initializes the user before each test. */
  @BeforeAll
  public void load() {
    TestUtils.load();
    initUser(userService);
  }

  /**
   * Tests the password reset functionality to ensure a user can reset their password and
   * authenticate with the new password.
   */
  @Test
  void testPasswordReset() {
    PasswordInputDTO passwordInput = passwordResetInputBean.getPasswordInput();
    PasswordResetDTO passwordReset = passwordResetInputBean.getPasswordReset();
    passwordInput.setPassword(NEW_PASSWORD);
    passwordInput.setRepetition(NEW_PASSWORD);
    passwordReset.setVerificationToken(VERIFICATION_TOKEN);
    passwordResetInputBean.submit();

    UserAuthenticationDTO user = new UserAuthenticationDTO();
    user.setEmail(USER_EMAIL);
    user = userService.findUserByEmail(user).orElseThrow();

    assertTrue(
        passwordAuthentication.authenticate(NEW_PASSWORD, user.getAuthToken()),
        "Expected to be able to authenticate with the new password");
  }

  /**
   * Initializes a user with default details and sets a password reset token.
   *
   * @param userService The user service to use for creating the user.
   */
  private static void initUser(UserService userService) {
    UserDetailsDTO user = new UserDetailsDTO();
    user.setEmail(USER_EMAIL);
    user.setNickname("Nickname");
    user.setCity("Test City");
    user.setPostalCode("12345");
    user.setFirstname("Firstname");
    user.setLastname("Lastname");
    user.setCountry("DE");

    PasswordInputDTO passwordInput = new PasswordInputDTO();
    passwordInput.setPassword("oldPassword");
    passwordInput.setRepetition("oldPassword");
    userService.create(user, passwordInput);

    VerificationTokenDTO verificationToken = new VerificationTokenDTO();
    verificationToken.setEmailAddress(USER_EMAIL);
    verificationToken.setToken(VERIFICATION_TOKEN);
    userService.putPasswordResetToken(verificationToken);
  }

  /** Cleans up the test data after each test. */
  @AfterAll
  public void destroy() {
    TestUtils.destroy();
  }
}
