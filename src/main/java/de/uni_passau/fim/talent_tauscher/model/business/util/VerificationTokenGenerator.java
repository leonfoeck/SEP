package de.uni_passau.fim.talent_tauscher.model.business.util;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/** Generates random verification tokens. */
@ApplicationScoped
public class VerificationTokenGenerator implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Generates a random verification token.
   *
   * @return A random verification token.
   */
  public String generateToken() {
    return UUID.randomUUID().toString();
  }
}
