package de.uni_passau.fim.talent_tauscher.it.model.business.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uni_passau.fim.talent_tauscher.dto.AuthTokenDTO;
import de.uni_passau.fim.talent_tauscher.model.business.util.PasswordAuthentication;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PasswordAuthentication} class.
 *
 * <p>This class tests the password hashing and authentication functionalities.
 *
 * @author Jakob Edmaier
 */
class PasswordAuthenticationTest {

  /**
   * Tests the authentication functionality. Verifies that a hashed password can be correctly
   * authenticated and that an incorrect password fails authentication.
   */
  @Test
  void testAuthenticate() {
    PasswordAuthentication passwordAuthentication = new PasswordAuthentication();
    AuthTokenDTO token = passwordAuthentication.hash("password");
    assertTrue(passwordAuthentication.authenticate("password", token));
    assertFalse(passwordAuthentication.authenticate("passwort", token));
  }
}
