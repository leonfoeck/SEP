package de.uni_passau.fim.talent_tauscher.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uni_passau.fim.talent_tauscher.model.persistence.util.ResourceReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Order(3)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings({"checkstyle:MagicNumber", "PMD.ClassNamingConventions"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchDetailViewAdST extends BaseST {

  private static final String DB_DRIVER = "org.postgresql.Driver";
  private static final String DB_HOST = "bueno.fim.uni-passau.de";
  private static final String DB_NAME = "sep24g01t";
  private static final String DB_USER = "sep24g01";
  private static final String DB_PASSWORD = "aitui5aiSee2";

  private WebDriver driver;
  private WebDriverWait wait;

  private static final Logger LOGGER = Logger.getLogger(SearchDetailViewAdST.class.getName());

  private byte[] defaultAvatarImage;

  @BeforeAll
  public void init() {
    initializeWebDriver();
    preloadResources();
    setupDatabase();
  }

  private void initializeWebDriver() {
    driver = super.getDriver();
    wait = super.getWait();
  }

  private void preloadResources() {
    ResourceReader resourceReader =
        new ResourceReader(Thread.currentThread().getContextClassLoader()::getResourceAsStream, "");
    defaultAvatarImage = resourceReader.fetchDefaultAvatarImage();
  }

  private int insertUserData(Connection connection) throws SQLException {
    String insertUsersSql =
        "INSERT INTO talent_tauscher.user "
            + "(first_name, last_name, avatar, nickname, user_role, email_address, password_hash, password_salt, hash_method, "
            + "has_admin_verified, is_email_verified, country, city, postal_code, street, house_number, created_at) "
            + "VALUES (?, ?, ?, ?, CAST(? AS talent_tauscher.user_role), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW()) RETURNING id;";

    try (PreparedStatement preparedStatement = connection.prepareStatement(insertUsersSql)) {
      preparedStatement.setString(1, "Martin");
      preparedStatement.setString(2, "Mustermann");
      preparedStatement.setBytes(3, defaultAvatarImage);
      preparedStatement.setString(4, "Weiterernutzer");
      preparedStatement.setString(5, "USER");
      preparedStatement.setString(6, "mustermann@example.com");
      preparedStatement.setString(7, "jlB2VbnQ2nfJaOr5NHgsUNe6Bv1YgA9/bwPLw+ic45w=");
      preparedStatement.setString(8, "+Vp2uw4oKr9jXWZmMvJrqw==");
      preparedStatement.setString(9, "PBKDF2WithHmacSHA512");
      preparedStatement.setBoolean(10, true);
      preparedStatement.setBoolean(11, true);
      preparedStatement.setString(12, "Germany");
      preparedStatement.setString(13, "Musterdorf");
      preparedStatement.setString(14, "23456");
      preparedStatement.setString(15, "Musterstraße");
      preparedStatement.setInt(16, 5);

      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt(1); // Return the user ID
      } else {
        throw new SQLException("Inserting user failed, no ID obtained.");
      }
    }
  }

  private void insertAdData(Connection connection, int creatorId) throws SQLException {
    ResourceReader resourceReader =
        new ResourceReader(Thread.currentThread().getContextClassLoader()::getResourceAsStream, "");
    byte[] defaultLogoImage = resourceReader.fetchDefaultLogoImage();

    String insertAdSql =
        "INSERT INTO talent_tauscher.ad (creator_id, image, title, free_text, cost_in_talent_points, date_of_publication, "
            + "country, city, postal_code, email_address) "
            + "VALUES (?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?)";

    try (PreparedStatement preparedStatement = connection.prepareStatement(insertAdSql)) {
      preparedStatement.setInt(1, creatorId);
      preparedStatement.setBytes(2, defaultLogoImage);
      preparedStatement.setString(3, "Streiche ihre Fassade");
      preparedStatement.setString(4, "Ich komme und streiche ihre Fassade.");
      preparedStatement.setInt(5, 6);
      preparedStatement.setString(6, "Deutschland");
      preparedStatement.setString(7, "Musterdorf");
      preparedStatement.setString(8, "23456");
      preparedStatement.setString(9, "mustermann@example.com");
      preparedStatement.executeUpdate();
    }
  }

  private void setupDatabase() {
    try {
      Class.forName(DB_DRIVER);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }

    Properties props = new Properties();
    props.setProperty("user", DB_USER);
    props.setProperty("password", DB_PASSWORD);
    props.setProperty("ssl", "true");
    props.setProperty("sslfactory", "org.postgresql.ssl.DefaultJavaSSLFactory");

    try (Connection connection =
        DriverManager.getConnection("jdbc:postgresql://" + DB_HOST + "/" + DB_NAME, props)) {
      connection.setAutoCommit(false);
      int userId = insertUserData(connection);
      insertAdData(connection, userId);
      connection.commit();
    } catch (SQLException e) {
      handleSqlException(e);
    }
  }

  private void handleSqlException(SQLException e) {
    System.err.println("SQL error during database initialization: " + e.getMessage());
    e.printStackTrace();
  }

  /** Sets up the initial state for each test by navigating to the base URL of the application. */
  @BeforeEach
  public void setup() {
    driver.get(BASE_URL);
  }

  /** Closes the WebDriver after all tests in this class have been executed. */
  @AfterAll
  public void shutdown() {
    super.closeDriver();
  }

  @Test
  @Order(1)
  @DisplayName("Detail View of an Advertisement Without Authentication")
  void detailViewWithoutAuthentication() {
    navigateTo("view/anonymous/allAdvertisements.xhtml");
    searchAllAdvertisementsForTitle("Streiche ihre Fassade");
    waitAndClick(By.xpath("//a[contains(.,'Streiche ihre Fassade')]"));
    // Additional checks for data visibility without authentication
    assertAdvertisementDetails("Streiche ihre Fassade", "Musterdorf", "23456", "6", false);
  }

  @Test
  @Order(2)
  @DisplayName("Detail View of an Advertisement With Authentication")
  void detailViewWithAuthentication() {
    login("Weiterernutzer", "Passwort2");
    searchAllAdvertisementsForTitle("Streiche ihre Fassade");
    waitAndClick(By.xpath("//a[contains(.,'Streiche ihre Fassade')]"));
    wait.until(ExpectedConditions.urlContains("view/anonymous/advertisementDetails.xhtml"));
    assertEquals("view/anonymous/advertisementDetails.xhtml", getCurrentPage());
    // Additional checks for data visibility with authentication
    assertAdvertisementDetails("Streiche ihre Fassade", "Musterdorf", "23456", "6", true);
  }

  @Test
  @Order(3)
  @DisplayName("Search by Title")
  void searchByTitle() {
    searchAllAdvertisementsForTitle("Streiche%");
    assertEquals(3, countDataTableChildren("all-advertisements:table:dataTable"));
    assertEquals("Streiche Ihr Haus", getTitleOfRow(1, "all-advertisements:table:dataTable"));
    assertEquals("Streiche ihre Fassade", getTitleOfRow(2, "all-advertisements:table:dataTable"));
  }

  @Test
  @Order(4)
  @DisplayName("Search by Postal Code")
  void searchByPostalCode() {
    navigateTo("view/anonymous/allAdvertisements.xhtml");
    searchAllAdvertisementsForPostalCode("123%");
    assertEquals("Streiche Ihr Haus", getTitleOfRow(1, "all-advertisements:table:dataTable"));
  }

  private void assertAdvertisementDetails(
      String expectedTitle,
      String expectedCity,
      String expectedPostalCode,
      String costs,
      boolean withContactInfo) {
    String actualTitle = driver.findElement(By.id("ad_title")).getText();

    assertEquals(
        expectedTitle + " (" + costs + " Punkte)",
        actualTitle,
        "Advertisement title does not match.");
    String location = driver.findElement(By.id("ad_location")).getText();
    assertEquals(
        expectedPostalCode + " " + expectedCity,
        location,
        "Advertisement location does not match.");
    List<WebElement> contactInfoElements = driver.findElements(By.id("contact-info"));
    if (withContactInfo) {
      assertFalse(contactInfoElements.isEmpty(), "Contact info should be visible.");
      assertTrue(contactInfoElements.getFirst().isDisplayed(), "Contact info should be visible.");
    } else {
      assertTrue(contactInfoElements.isEmpty(), "Contact info should not be visible.");
    }
  }

  private void searchAllAdvertisementsForTitle(String title) {
    waitAndSendKeys(By.id("all-advertisements:table:dataTable:ad-title:title"), title);
    waitAndClick(By.id("all-advertisements:table:filterButton"));
  }

  private void searchAllAdvertisementsForPostalCode(String postalCode) {
    waitAndSendKeys(
        By.id("all-advertisements:table:dataTable:ad-postal-code:postal_code"), postalCode);
    waitAndClick(By.id("all-advertisements:table:filterButton"));
  }
}
