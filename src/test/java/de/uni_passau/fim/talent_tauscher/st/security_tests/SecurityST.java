package de.uni_passau.fim.talent_tauscher.st.security_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


import de.uni_passau.fim.talent_tauscher.st.util.BaseST;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * System test using Selenium to interact with the web application. Tests security-relevant aspects
 * of the application, such as the protection against malicious code injection.
 */
@Order(7)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings({"checkstyle:MagicNumber"})
class SecurityST extends BaseST {

  private WebDriver driver;

  /** Sets the web driver before executing the first test. */
  @BeforeAll
  public void beforeAll() {
    driver = super.getDriver();
  }

  /** Navigates to the application's base url before each test. */
  @BeforeEach
  public void setup() {
    driver.get(BASE_URL);
  }

  /** Quits the web driver after the last test. */
  @AfterAll
  public void afterAll() {
    super.closeDriver();
  }

  @DisplayName("Test protection against data deletion through SQL injection")
  @Test
  @Order(1)
  void testDataDeletionSQLInjection() throws InterruptedException {
    waitAndClick(By.id("headerButtonsRight:login"));
    waitAndSendKeys(By.id("userInput:nick"), "' OR 1 = 1; DROP TABLE \"user\";--");
    waitAndSendKeys(By.id("userInput:password"), "Pa55wort");
    waitAndClick(By.id("userInput:submit"));
    Thread.sleep(1000);
    assertEquals("view/anonymous/login.xhtml", getCurrentPage());
    String message = driver.findElement(By.id("userInput:password-message")).getText();
    assertFalse(message.isEmpty());
  }

  @DisplayName("Test protection against compromising of the system through SQL injection")
  @Test
  @Order(2)
  void testSystemCompromisingSQLInjection() throws InterruptedException {
    waitAndClick(By.id("headerButtonsRight:login"));
    waitAndSendKeys(By.id("userInput:nick"), "Admin1");
    waitAndSendKeys(By.id("userInput:password"), "' OR 1 = 1;--");
    waitAndClick(By.id("userInput:submit"));
    Thread.sleep(1000);
    assertEquals("view/anonymous/login.xhtml", getCurrentPage());
    String message = driver.findElement(By.id("userInput:password-message")).getText();
    assertFalse(message.isEmpty());
  }

  @DisplayName("Test protection against unauthorized access")
  @Test
  @Order(3)
  void testUnauthorizedAccess() throws InterruptedException {
    goToPage("view/admin/userAdministration.xhtml");
    Thread.sleep(1000);
    String message = driver.findElement(By.id("error-code")).getText();
    assertEquals("404", message);
  }
}
