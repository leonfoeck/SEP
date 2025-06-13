package de.uni_passau.fim.talent_tauscher.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

@Order(6)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings({"checkstyle:MagicNumber", "PMD.ClassNamingConventions"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeleteST extends BaseST {

  private WebDriver driver;
  private WebDriverWait wait;

  /**
   * Sets up database entries for testing purposes. Inserts test data for users and advertisements
   * into the database.
   */
  @BeforeAll
  public void init() {
    driver = super.getDriver();
    wait = super.getWait();
    driver.get(BASE_URL);
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.titleIs("Übersicht"), ExpectedConditions.titleIs("Overview")));
  }

  /** Cleans up by closing the WebDriver after all tests have run. */
  @AfterAll
  public void shutdown() {
    super.closeDriver();
  }

  @Test
  @Order(1)
  @DisplayName("Warning when deleting an advertisement")
  void warningWhenDeletingAdvertisement() {
    login("Weiterernutzer", "Passwort2");
    navigateTo("view/registered/createdAdvertisements.xhtml");
    searchCreatedAdvertisementsForTitle("Streiche ihre Fassade");
    waitAndClick(By.xpath("//a[contains(.,'Streiche ihre Fassade')]"));
    scrollToBottom(300);
    waitAndClick(By.id("deleteButton"));
    assertTrue(isElementPresent(By.id("deleteDialog")));
  }

  @Test
  @Order(2)
  @DisplayName("Delete an advertisement")
  void deleteAdvertisement() {
    waitAndClick(By.id("deleteDialog:deleteForm:confirmDeleteButton"));
    wait.until(ExpectedConditions.urlContains("view/registered/createdAdvertisements.xhtml"));
    assertEquals("view/registered/createdAdvertisements.xhtml", getCurrentPage());
    for (int i = 1; i < countDataTableChildren("created-advertisements:table:dataTable"); i++) {
      assertNotEquals(
          "Streiche ihre Fassade", getTitleOfRow(i, "created-advertisements:table:dataTable"));
    }
  }

  @Test
  @Order(3)
  @DisplayName("Warning when deleting Profile")
  void warningWhenDeletingProfile() {
    logout();
    login("AlterNutzer", "Passwort3");
    navigateTo("view/registered/profile.xhtml");
    scrollToBottom(300);
    waitAndClick(By.id("deleteButton"));
    assertTrue(isElementPresent(By.id("deleteDialog")));
  }

  @Test
  @Order(4)
  @DisplayName("Delete a Profile")
  void deleteProfile() {
    waitAndClick(By.id("deleteDialog:deleteForm:confirmDeleteButton"));
    wait.until(ExpectedConditions.urlContains("view/anonymous/allAdvertisements.xhtml"));
    assertEquals("view/anonymous/allAdvertisements.xhtml", getCurrentPage());
  }

  @Test
  @Order(5)
  @DisplayName("Delete user account by admin")
  void deleteUserAccountByAdmin() {
    loginAsAdmin();
    wait.until(ExpectedConditions.urlContains("view/anonymous/allAdvertisements.xhtml"));
    navigateTo("view/admin/userAdministration.xhtml");
    searchUserForNick("Weiterernutzer");
    waitAndClick(By.xpath("//a[contains(.,'Weiterernutzer')]"));
    scrollToBottom(300);
    waitAndClick(By.id("deleteButton"));
    waitAndClick(By.id("deleteDialog:deleteForm:confirmDeleteButton"));
    wait.until(ExpectedConditions.urlContains("view/admin/userAdministration.xhtml"));
    assertEquals("view/admin/userAdministration.xhtml", getCurrentPage());
    searchUserForNick("Weiterernutzer");
    assertEquals(2, countDataTableChildren("users:table:dataTable"));
  }

  private void searchCreatedAdvertisementsForTitle(String title) {
    waitAndSendKeys(By.id("created-advertisements:table:dataTable:ad-title:title"), title);
    waitAndClick(By.id("created-advertisements:table:filterButton"));
  }

  private void searchUserForNick(String nickname) {
    waitAndSendKeys(By.id("users:table:dataTable:nick:nickname"), nickname);
    waitAndClick(By.id("users:table:filterButton"));
  }
}
