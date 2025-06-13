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
@Order(5)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings({"checkstyle:MagicNumber", "PMD.ClassNamingConventions"})
class UserAdministrationST extends BaseST {

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
    loginAsAdmin();
    driver.get(BASE_URL);
  }

  /** Quits the web driver after the last test. */
  @AfterAll
  public void afterAll() {
    super.closeDriver();
  }

  @DisplayName("Tests navigation to the edit user page")
  @Test
  @Order(1)
  void goToEditUser() {
    driver.get(BASE_URL + "view/admin/userAdministration.xhtml");
    waitAndClick(By.id("newUser"));
    assertEquals("view/admin/editUser.xhtml", getCurrentPage());
  }

  @DisplayName("Tests creation of a new user")
  @Test
  @Order(2)
  void createNewUser() {
    wait.until(ExpectedConditions.urlContains("view/admin/editUser.xhtml"));
    waitAndSendKeys(By.id("userInput:nick"), "NeuerNutzer");
    waitAndSendKeys(By.id("userInput:email"), "hmustermann@example.com");
    waitAndSendKeys(By.id("userInput:password"), "Passwort3");
    waitAndSendKeys(By.id("userInput:repetition"), "Passwort3");
    waitAndSendKeys(By.id("userInput:firstname"), "Hans");
    waitAndSendKeys(By.id("userInput:lastname"), "Mustermann");
    waitAndSendKeys(By.id("userInput:street"), "Musterstraße");
    waitAndSendKeys(By.id("userInput:houseNumber"), "1");
    scrollToBottom(2000);
    waitAndSendKeys(By.id("userInput:postalCode"), "23456");
    waitAndSendKeys(By.id("userInput:city"), "Musterdorf");
    waitAndSendKeys(By.id("userInput:country"), "Deutschland");
    waitAndSendKeys(By.id("userInput:phone"), "01189998819991197253");
    scrollToBottom(1000);
    waitAndClick(By.id("userInput:emailVerified"));
    waitAndClick(By.id("userInput:submit"));
    try {
      Thread.sleep(2500);
    } catch (InterruptedException e) {
    }
    assertEquals("view/admin/userAdministration.xhtml", getCurrentPage());
  }

  @DisplayName("Tests searching for a user")
  @Test
  @Order(3)
  void searchForUser() {
    wait.until(ExpectedConditions.urlContains("view/admin/userAdministration.xhtml"));
    waitAndSendKeys(By.id("users:table:dataTable:nick:nickname"), "NeuerNutzer");
    waitAndClick(By.id("users:table:filterButton"));
    assertFalse(
        driver
            .findElements(
                By.xpath(
                    "//table[@id='users:table:dataTable']//a[contains(.,'" + "NeuerNutzer" + "')]"))
            .isEmpty());
  }

  @DisplayName("Tests filling of the user form")
  @Test
  @Order(4)
  void testLoadUserData() {
    wait.until(ExpectedConditions.urlContains("view/admin/userAdministration.xhtml"));
    waitAndClick(By.id("users:table:dataTable:0:id"));
    wait.until(ExpectedConditions.urlContains("view/admin/editUser.xhtml"));

    assertEquals("NeuerNutzer", driver.findElement(By.id("userInput:nick")).getAttribute("value"));
    assertEquals("Hans", driver.findElement(By.id("userInput:firstname")).getAttribute("value"));
    assertEquals(
        "Mustermann", driver.findElement(By.id("userInput:lastname")).getAttribute("value"));
    assertEquals(
        "hmustermann@example.com",
        driver.findElement(By.id("userInput:email")).getAttribute("value"));
    assertEquals(
        "01189998819991197253", driver.findElement(By.id("userInput:phone")).getAttribute("value"));
    assertEquals(
        "Musterstraße", driver.findElement(By.id("userInput:street")).getAttribute("value"));
    assertEquals("1", driver.findElement(By.id("userInput:houseNumber")).getAttribute("value"));
    assertEquals("23456", driver.findElement(By.id("userInput:postalCode")).getAttribute("value"));
    assertEquals("Musterdorf", driver.findElement(By.id("userInput:city")).getAttribute("value"));
  }

  @DisplayName("Tests editing a user")
  @Test
  @Order(5)
  void editUser() {
    final String newNick = "AlterNutzer";
    wait.until(ExpectedConditions.urlContains("view/admin/editUser.xhtml"));
    waitAndSendKeys(By.id("userInput:nick"), newNick);
    scrollToBottom(400);
    waitAndClick(By.id("userInput:submit"));
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
    }
    assertEquals("view/admin/userAdministration.xhtml", getCurrentPage());
  }
}
