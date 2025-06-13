package de.uni_passau.fim.talent_tauscher.st;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

/**
 * System test using Selenium to interact with the web application. Tests creating, managing and
 * deleting own advertisements.
 *
 * @author Jakob Edmaier
 */
@Order(2)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings({"checkstyle:MagicNumber"})
final class OwnAdvertisementsST extends BaseST {

  private static final String AD_TITLE = "Streiche Ihre Wohnung";
  private static final String NEW_AD_TITLE = "Streiche Ihr Haus";
  private static final String AD_DESCRIPTION = "Ich komme und streiche Ihnen ihre Wohnung";
  private static final String USER_EMAIL = "maxmustermann@example.com";
  private static final String USER_CITY = "Musterstadt";
  private static final String USER_POSTAL_CODE = "12345";
  private static final String AD_COST = "4";

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
    loginAsRegularUser();
    driver.get(BASE_URL);
  }

  /** Quits the web driver after the last test. */
  @AfterAll
  public void afterAll() {
    super.closeDriver();
  }

  @DisplayName("Test navigation to edit-advertisements page")
  @Test
  @Order(1)
  void goToCreateAdvertisement() {
    waitAndClick(By.id("headerButtonsLeft:createAd"));
    assertEquals("view/registered/editAdvertisement.xhtml", getCurrentPage());
  }

  @DisplayName("Test the successful creation of a new advertisement")
  @Test
  @Order(2)
  void successfullyCreateAd() {
    wait.until(ExpectedConditions.urlContains("view/registered/editAdvertisement.xhtml"));
    waitAndSendKeys(By.id("advertisement-form:title"), AD_TITLE);
    waitAndSendKeys(By.id("advertisement-form:cost"), AD_COST);
    waitAndSendKeys(By.id("advertisement-form:description"), AD_DESCRIPTION);
    submitAdvertisementForm();

    assertEquals("view/registered/createdAdvertisements.xhtml", getCurrentPage());
    assertTrue(isDisplayedInCreatedAdvertisements(AD_TITLE));
  }

  @DisplayName("Test inputting invalid data.")
  @Test
  @Order(3)
  void createAdvertisementFailing() {
    goToPage("view/registered/editAdvertisement.xhtml");
    waitAndSendKeys(By.id("advertisement-form:cost"), AD_COST);
    waitAndSendKeys(By.id("advertisement-form:description"), AD_DESCRIPTION);
    submitAdvertisementForm();
    assertEquals("view/registered/editAdvertisement.xhtml", getCurrentPage());
    String errorMessage = driver.findElement(By.id("advertisement-form:title-message")).getText();
    assertFalse(errorMessage.isEmpty());
  }

  @DisplayName("Test opening form with existing advertisement")
  @Test
  @Order(4)
  void testShowExistingAdvertisement() {
    goToPage("view/registered/createdAdvertisements.xhtml");
    searchCreatedAdvertisementsForTitle(AD_TITLE);
    waitAndClick(By.xpath("//a[contains(.,'" + AD_TITLE + "')]"));
    assertEquals("view/registered/editAdvertisement.xhtml", getCurrentPage());

    String displayedTitle =
        driver.findElement(By.id("advertisement-form:title")).getAttribute("value");
    String displayedCost =
        driver.findElement(By.id("advertisement-form:cost")).getAttribute("value");
    String displayedDescription =
        driver.findElement(By.id("advertisement-form:description")).getAttribute("value");
    String displayedEmail =
        driver.findElement(By.id("advertisement-form:email")).getAttribute("value");
    String displayedCity =
        driver.findElement(By.id("advertisement-form:city")).getAttribute("value");
    String displayedPostalCode =
        driver.findElement(By.id("advertisement-form:postalCode")).getAttribute("value");
    assertAll(
        () -> {
          assertEquals(AD_TITLE, displayedTitle);
          assertEquals(AD_COST, displayedCost);
          assertEquals(AD_DESCRIPTION, displayedDescription);
          assertEquals(USER_EMAIL, displayedEmail);
          assertEquals(USER_CITY, displayedCity);
          assertEquals(USER_POSTAL_CODE, displayedPostalCode);
        });
  }

  @DisplayName("Test successfully editing an existing advertisement.")
  @Test
  @Order(5)
  void successfullyEditAdvertisement() {
    waitAndSendKeys(By.id("advertisement-form:title"), NEW_AD_TITLE);
    submitAdvertisementForm();

    assertEquals("view/registered/createdAdvertisements.xhtml", getCurrentPage());
    assertTrue(isDisplayedInCreatedAdvertisements(NEW_AD_TITLE));
  }

  @DisplayName("Test inputting an input value when editing an existing advertisement.")
  @Test
  @Order(6)
  void failEditAdvertisement() {
    goToPage("view/registered/createdAdvertisements.xhtml");
    searchCreatedAdvertisementsForTitle(NEW_AD_TITLE);
    waitAndClick(By.xpath("//a[contains(.,'" + NEW_AD_TITLE + "')]"));
    assertEquals("view/registered/editAdvertisement.xhtml", getCurrentPage());

    waitAndSendKeys(By.id("advertisement-form:cost"), "");
    submitAdvertisementForm();

    assertEquals("view/registered/editAdvertisement.xhtml", getCurrentPage());
    String errorMessage = driver.findElement(By.id("advertisement-form:cost-message")).getText();
    assertFalse(errorMessage.isEmpty());
  }

  private void searchCreatedAdvertisementsForTitle(String title) {
    waitAndSendKeys(By.id("created-advertisements:table:dataTable:ad-title:title"), title);
    waitAndClick(By.id("created-advertisements:table:filterButton"));
  }

  private boolean isDisplayedInCreatedAdvertisements(String title) {
    searchCreatedAdvertisementsForTitle(title);
    return !driver
        .findElements(
            By.xpath(
                "//table[@id='created-advertisements:table:dataTable']//a[contains(.,'"
                    + title
                    + "')]"))
        .isEmpty();
  }

  private void submitAdvertisementForm() {
    scrollToBottom(3000);
    waitAndClick(By.id("advertisement-form:submit"));
  }
}
