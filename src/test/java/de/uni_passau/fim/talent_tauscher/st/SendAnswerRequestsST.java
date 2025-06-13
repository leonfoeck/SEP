package de.uni_passau.fim.talent_tauscher.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

@Order(4)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings({"checkstyle:MagicNumber", "PMD.ClassNamingConventions"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SendAnswerRequestsST extends BaseST {

  private WebDriver driver;
  private WebDriverWait wait;

  /** Initializes the WebDriver and WebDriverWait before all tests run. */
  @BeforeAll
  public void init() {
    driver = super.getDriver();
    wait = super.getWait();
    driver.get(BASE_URL);
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.titleIs("Übersicht"), ExpectedConditions.titleIs("Overview")));
  }

  /** Closes the WebDriver after all tests in this class have been executed. */
  @AfterAll
  public void shutdown() {
    super.closeDriver();
  }

  @Test
  @Order(1)
  @DisplayName("Detail View of an Advertisement Without Authentication")
  void siteForWritingRequests() {
    login("Weiterernutzer", "Passwort2");
    searchAllAdvertisementsForTitle("Streiche Ihr Haus");
    waitAndClick(By.xpath("//a[contains(.,'Streiche Ihr Haus')]"));
    waitAndClick(By.id("writeRequestButton"));
    wait.until(ExpectedConditions.urlContains("writeRequest"));
    assertEquals("view/registered/writeRequest.xhtml", getCurrentPage());
  }

  @Test
  @Order(2)
  @DisplayName("Send Request")
  void sendRequest() {
    scrollToBottom(300);
    waitAndClick(By.id("writeRequest:submit"));
    wait.until(ExpectedConditions.urlContains("outgoingRequests.xhtml"));
    assertEquals("view/registered/outgoingRequests.xhtml", getCurrentPage());
  }

  @Test
  @Order(3)
  @DisplayName("Request received")
  void requestReceived() {
    logout();
    login("Testnutzer", "Passwort1");
    navigateTo("view/registered/createdAdvertisements.xhtml");
    waitAndClick(By.id("incomingRequestsLink"));
    assertEquals(
        "Streiche Ihr Haus", getTitleOfRow(1, "incoming-Requests:incomingRequestsTable:dataTable"));
  }

  @Test
  @Order(4)
  @DisplayName("Site for Answering Requests")
  void siteForAnsweringRequests() {
    waitAndClick(By.id("incoming-Requests:incomingRequestsTable:dataTable:0:answerButton"));
    wait.until(ExpectedConditions.urlContains("writeResponse"));
    assertEquals("view/registered/writeResponse.xhtml", getCurrentPage());
  }

  @Test
  @Order(5)
  @DisplayName("Reject Request")
  void rejectRequest() {
    waitAndSendKeys(By.id("writeResponse:response"), "Leider muss ich ihre Anfrage ablehnen");
    waitAndClick(By.id("writeResponse:rejectButton"));
    wait.until(ExpectedConditions.urlContains("incomingRequests.xhtml"));
    assertEquals("view/registered/incomingRequests.xhtml", getCurrentPage());
    for (int i = 1;
        i < countDataTableChildren("incoming-Requests:incomingRequestsTable:dataTable");
        i++) {
      assertNotEquals(
          "Streiche ihr Haus",
          getTitleOfRow(i, "incoming-Requests:incomingRequestsTable:dataTable"));
    }
  }

  @Test
  @Order(6)
  @DisplayName("Accept Request")
  void acceptRequest() {
    logout();
    siteForWritingRequests();
    sendRequest();
    requestReceived();
    siteForAnsweringRequests();
    waitAndSendKeys(By.id("writeResponse:response"), "Sehr gerne nehme ich ihre Anzeige an");
    waitAndClick(By.id("writeResponse:acceptButton"));
    wait.until(ExpectedConditions.urlContains("incomingRequests.xhtml"));
    assertEquals("view/registered/incomingRequests.xhtml", getCurrentPage());
    for (int i = 1;
        i < countDataTableChildren("incoming-Requests:incomingRequestsTable:dataTable");
        i++) {
      assertNotEquals(
          "Streiche ihr Haus",
          getTitleOfRow(i, "incoming-Requests:incomingRequestsTable:dataTable"));
    }
  }

  @Test
  @Order(7)
  @DisplayName("View Claimed Ads")
  void viewClaimedAds() {
    logout();
    login("Weiterernutzer", "Passwort2");
    assertEquals("view/anonymous/allAdvertisements.xhtml", getCurrentPage());
    waitAndClick(By.id("claimedAdsLink"));
    assertEquals("view/registered/claimedAdvertisements.xhtml", getCurrentPage());
    assertEquals("Streiche Ihr Haus", getTitleOfRow(1, "claimedAds:dataTable:dataTable"));
  }

  private void searchAllAdvertisementsForTitle(String title) {
    waitAndSendKeys(By.id("all-advertisements:table:dataTable:ad-title:title"), title);
    waitAndClick(By.id("all-advertisements:table:filterButton"));
  }
}
