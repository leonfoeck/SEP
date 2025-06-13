package de.uni_passau.fim.talent_tauscher.it.model.backing.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for verifying the URL extraction using regular expressions in {@link
 * de.uni_passau.fim.talent_tauscher.model.backing.util.EmailBuilder}.
 */
class EmailBuilderTest {

  /**
   * Tests the URL extraction regex with a specific input. Verifies that the regex correctly
   * extracts the base URL.
   */
  @Test
  void testRegex() {
    String input =
        "http://localhost:8001/talent_tauscher_war_exploded/view/anonymous/register.xhtml?sessionId=123FAD393D§C252FEB1";
    final Pattern uriPattern = Pattern.compile("^(.*?/)view/.*");
    Matcher matcher = uriPattern.matcher(input);
    assertTrue(matcher.matches());
    assertEquals("http://localhost:8001/talent_tauscher_war_exploded/", matcher.group(1));
  }

  /**
   * Tests the URL extraction regex with a different input. Verifies that the regex correctly
   * extracts the base URL.
   */
  @Test
  void testRegex2() {
    String input =
        "http://localhost:80/view/anonymous/register.xhtml?sessionId=123FAD393D§C252FEB1";
    final Pattern uriPattern = Pattern.compile("^(.*?/)view/.*");
    Matcher matcher = uriPattern.matcher(input);
    assertTrue(matcher.matches());
    assertEquals("http://localhost:80/", matcher.group(1));
  }

  /**
   * Tests the URL extraction regex with an HTTPS input. Verifies that the regex correctly extracts
   * the base URL.
   */
  @Test
  void testRegex3() {
    String input =
        "https://127.0.0.1:443/talent_tauscher_war_exploded/view/anonymous/view/register.xhtml?sessionId=123FAD393D§C252FEB1";
    final Pattern uriPattern = Pattern.compile("^(.*?/)view/.*");
    Matcher matcher = uriPattern.matcher(input);
    assertTrue(matcher.matches());
    assertEquals("https://127.0.0.1:443/talent_tauscher_war_exploded/", matcher.group(1));
  }
}
