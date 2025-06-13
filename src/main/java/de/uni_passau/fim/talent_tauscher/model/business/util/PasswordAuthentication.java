package de.uni_passau.fim.talent_tauscher.model.business.util;

import de.uni_passau.fim.talent_tauscher.dto.AuthTokenDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serial;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utility class for hashing and salting passwords. Uses the PBKDF2WithHmacSHA512 by default, but
 * can be extended to support other hashing algorithms while still being compatible with the
 * previously used algorithms.
 *
 * @author Jakob Edmaier
 */
@ApplicationScoped
public class PasswordAuthentication implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
  private static final int COST = 16;
  private static final int HASH_SIZE = 256;
  private static final int SALT_SIZE = 16;

  @Inject private transient Logger logger;

  private final SecureRandom random = new SecureRandom();

  /**
   * Hashes a password using the PBKDF2WithHmacSHA512 algorithm.
   *
   * @param password The password to hash.
   * @return An authentication token consisting of a salt, the hash value of the password and the
   *     algorithm used for hashing.
   */
  public AuthTokenDTO hash(String password) {
    byte[] salt = new byte[SALT_SIZE];
    random.nextBytes(salt);
    byte[] passwordHash = pbkdf2(password.toCharArray(), salt);

    AuthTokenDTO authToken = new AuthTokenDTO();
    authToken.setSalt(encode(salt));
    authToken.setPasswordHash(encode(passwordHash));
    authToken.setAlgorithm(ALGORITHM);
    return authToken;
  }

  /**
   * Checks if the hash value of a password matches an authentication token, using the hashing
   * algorithm specified by the authentication token.
   *
   * @param password The password in plain text.
   * @param token The authentication token consisting of a salt, a hash value and the algorithm used
   *     for hashing.
   * @return {@code true} if and only if the hash value of the password matches the hash value of
   *     the token.
   */
  public boolean authenticate(String password, AuthTokenDTO token) {
    byte[] salt = decode(token.getSalt());
    byte[] passwordHash = decode(token.getPasswordHash());
    byte[] check = pbkdf2(password.toCharArray(), salt);
    return Arrays.equals(passwordHash, check);
  }

  private byte[] pbkdf2(char[] password, byte[] salt) {
    int iterations = 1 << COST;
    KeySpec keySpec = new PBEKeySpec(password, salt, iterations, HASH_SIZE);

    try {
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
      return secretKeyFactory.generateSecret(keySpec).getEncoded();
    } catch (NoSuchAlgorithmException e) {
      logger.severe("Missing algorithm: " + ALGORITHM);
      throw new IllegalStateException(e);
    } catch (InvalidKeySpecException e) {
      logger.severe("Invalid key specification: " + ALGORITHM);
      throw new IllegalStateException(e);
    }
  }

  private String encode(byte[] input) {
    return Base64.getEncoder().encodeToString(input);
  }

  private byte[] decode(String input) {
    return Base64.getDecoder().decode(input);
  }
}
