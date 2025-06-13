package de.uni_passau.fim.talent_tauscher.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import de.uni_passau.fim.talent_tauscher.st.util.BaseST;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** A system test using Selenium to interact with the web application. */
@Order(1)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings({"checkstyle:MagicNumber", "PMD.ClassNamingConventions"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginST extends BaseST {

  private WebDriver driver;
  private WebDriverWait wait;

  /**
   * Sets the {@link WebDriver} before executing the first test, and ensures a regular user is
   * logged in. Navigates to the application's base URL.
   */
  @BeforeAll
  public void beforeAll() {
    driver = super.getDriver();
    wait = super.getWait();
    driver.get(BASE_URL);
  }

  /** Quits the web driver after the last test. */
  @AfterAll
  public void afterAll() {
    super.closeDriver();
  }

  @DisplayName("Tests navigation to the login page")
  @Test
  @Order(1)
  void goToLogin() {
    waitAndClick(By.id("headerButtonsRight:login"));
    wait.until(ExpectedConditions.urlContains("view/anonymous/login.xhtml"));
    assertEquals("view/anonymous/login.xhtml", getCurrentPage());
  }

  @DisplayName("Test a failed login")
  @Test
  @Order(2)
  void failedLogin() {
    wait.until(ExpectedConditions.urlContains("view/anonymous/login.xhtml"));
    waitAndSendKeys(By.id("userInput:nick"), "Admin1");
    waitAndSendKeys(By.id("userInput:password"), "123456");
    waitAndClick(By.id("userInput:submit"));
    assertFalse(driver.findElements(By.id("userInput:password-message")).isEmpty());
  }

  @DisplayName("Test a successful login")
  @Test
  @Order(3)
  void successfulLogin() {
    loginAsAdmin();
    wait.until(ExpectedConditions.urlContains("view/anonymous/allAdvertisements.xhtml"));
    assertEquals("view/anonymous/allAdvertisements.xhtml", getCurrentPage());
  }

  @DisplayName("Test the logout")
  @Test
  @Order(4)
  void testLogout() {
    logout();
    wait.until(ExpectedConditions.urlContains("view/anonymous/allAdvertisements.xhtml"));
    assertEquals("view/anonymous/allAdvertisements.xhtml", getCurrentPage());
  }

  @DisplayName("Tests navigation to the register page")
  @Test
  @Order(5)
  void goToRegister() {
    waitAndClick(By.id("headerButtonsRight:register"));
    assertEquals("view/anonymous/register.xhtml", getCurrentPage());
  }

  @DisplayName("Tests a successful registration")
  @Test
  @Order(6)
  void successfulRegistration() {
    navigateTo("view/anonymous/register.xhtml");
    wait.until(ExpectedConditions.urlContains("view/anonymous/register.xhtml"));
    waitAndSendKeys(By.id("userInput:nick"), "Testnutzer");
    waitAndSendKeys(By.id("userInput:email"), "maxmustermann@example.com");
    waitAndSendKeys(By.id("userInput:password"), "Passwort1");
    waitAndSendKeys(By.id("userInput:repetition"), "Passwort1");
    waitAndSendKeys(By.id("userInput:firstname"), "Max");
    waitAndSendKeys(By.id("userInput:lastname"), "Mustermann");
    waitAndSendKeys(By.id("userInput:street"), "Musterplatz");
    waitAndSendKeys(By.id("userInput:houseNumber"), "2");
    waitAndSendKeys(By.id("userInput:postalCode"), "12345");
    waitAndSendKeys(By.id("userInput:city"), "Musterstadt");
    waitAndSendKeys(By.id("userInput:country"), "Deutschland");
    waitAndSendKeys(By.id("userInput:phone"), "987654321");
    scrollToBottom(3000);
    waitAndClick(By.id("userInput:submit"));

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
    }
    waitAndClick(By.id("userInput:submit"));
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
    }
    assertEquals("view/anonymous/allAdvertisements.xhtml", getCurrentPage());
    verifyTestnutzer();
  }

  @DisplayName("Tests a failed registration")
  @Test
  @Order(7)
  void failedRegistration() {
    waitAndClick(By.id("headerButtonsRight:register"));
    wait.until(ExpectedConditions.urlContains("view/anonymous/register.xhtml"));
    waitAndSendKeys(By.id("userInput:nick"), "Testnutzer");
    waitAndSendKeys(By.id("userInput:email"), "mustermail@e@mail.com");
    waitAndSendKeys(By.id("userInput:password"), "abcd1234");
    waitAndSendKeys(By.id("userInput:repetition"), "abcd1234");
    waitAndSendKeys(By.id("userInput:firstname"), "Adam");
    waitAndSendKeys(By.id("userInput:lastname"), "Riese");
    waitAndSendKeys(By.id("userInput:street"), "Musterberg");
    waitAndSendKeys(By.id("userInput:houseNumber"), "6");
    waitAndSendKeys(By.id("userInput:postalCode"), "12345");
    waitAndSendKeys(By.id("userInput:city"), "Musterstadt");
    waitAndSendKeys(By.id("userInput:country"), "Deutschland");
    waitAndSendKeys(By.id("userInput:phone"), "11235813");
    scrollToBottom(400);
    waitAndClick(By.id("userInput:submit"));
    scrollToTop(1000);
    assertFalse(driver.findElements(By.id("userInput:nick-message")).isEmpty());
    assertFalse(driver.findElements(By.id("userInput:email-message")).isEmpty());
  }

  @DisplayName("Tests navigation to the profile page")
  @Test
  @Order(8)
  void goToProfile() {
    navigateTo("view/anonymous/allAdvertisements.xhtml");
    loginAsRegularUser();
    waitAndClick(By.id("headerButtonsRight:profile"));
    assertEquals("view/registered/profile.xhtml", getCurrentPage());
  }

  @DisplayName("Tests successfully updating your profile")
  @Test
  @Order(9)
  void successfullyUpdateProfile() {
    final String newStreet = "Musterplatz";
    final String newHouseNumber = "1";
    final String newPostalCode = "12345";
    final String newCity = "Musterstadt";

    wait.until(ExpectedConditions.urlContains("view/registered/profile.xhtml"));
    waitAndSendKeys(By.id("userInput:street"), newStreet);
    waitAndSendKeys(By.id("userInput:houseNumber"), newHouseNumber);
    scrollToBottom(2000);
    waitAndSendKeys(By.id("userInput:postalCode"), newPostalCode);
    waitAndSendKeys(By.id("userInput:city"), newCity);
    scrollToBottom(200);
    waitAndClick(By.id("userInput:submit"));
    try {
      Thread.sleep(2500);
    } catch (InterruptedException e) {
    }
    assertEquals("view/registered/createdAdvertisements.xhtml", getCurrentPage());

    waitAndClick(By.id("headerButtonsRight:profile"));
    wait.until(ExpectedConditions.urlContains("view/registered/profile.xhtml"));
    assertEquals(newStreet, driver.findElement(By.id("userInput:street")).getAttribute("value"));
    assertEquals(
        newHouseNumber, driver.findElement(By.id("userInput:houseNumber")).getAttribute("value"));
    assertEquals(
        newPostalCode, driver.findElement(By.id("userInput:postalCode")).getAttribute("value"));
    assertEquals(newCity, driver.findElement(By.id("userInput:city")).getAttribute("value"));
  }

  @DisplayName("Tests fail updating your profile")
  @Test
  @Order(10)
  void failUpdateProfile() {
    waitAndClick(By.id("headerButtonsRight:profile"));
    wait.until(ExpectedConditions.urlContains("view/registered/profile.xhtml"));
    waitAndSendKeys(By.id("userInput:nick"), "Admin1");
    scrollToBottom(200);
    waitAndClick(By.id("userInput:submit"));
    scrollToTop(400);
    assertFalse(driver.findElements(By.id("userInput:nick-message")).isEmpty());
  }

  private void verifyTestnutzer() {
    driver.get(BASE_URL);
    loginAsAdmin();
    navigateTo("view/admin/userAdministration.xhtml");
    wait.until(ExpectedConditions.urlContains("view/admin/userAdministration.xhtml"));
    waitAndSendKeys(By.id("users:table:dataTable:nick:nickname"), "Testnutzer");
    waitAndClick(By.id("users:table:filterButton"));
    waitAndClick(By.id("users:table:dataTable:0:id"));
    scrollToBottom(200);
    waitAndClick(By.id("userInput:emailVerified"));
    waitAndClick(By.id("userInput:submit"));
    wait.until(ExpectedConditions.urlContains("view/admin/userAdministration.xhtml"));
    waitAndClick(By.id("headerButtonsRight:logout"));
    wait.until(ExpectedConditions.urlContains("view/anonymous/allAdvertisements.xhtml"));
  }
}
