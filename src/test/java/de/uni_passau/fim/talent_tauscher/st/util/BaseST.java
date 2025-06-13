package de.uni_passau.fim.talent_tauscher.st.util;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Superclass for system tests. Provides common setup and utility methods for Selenium tests.
 *
 * @author Jakob Edmaier
 */
public abstract class BaseST {

  protected static final String BASE_URL = "http://localhost:8080/talent_tauscher/";

  private static final Pattern CURRENT_PAGE_PATTERN =
    Pattern.compile("^" + BASE_URL + "(view/[^;]+\\.xhtml)(;.*)?");


  private final WebDriver driver;
  private final WebDriverWait wait;

  private static final Logger LOGGER = Logger.getLogger(BaseST.class.getName());

  /** Initializes the WebDriverFactory instance before all tests. */
  public BaseST() {
    WebDriverFactory factory = WebDriverFactory.getInstance();
    this.driver = factory.getDriver();
    this.wait = factory.getWait(driver);
  }

  /**
   * Waits for an element to be clickable and then clicks it.
   *
   * @param by The selector to locate the element.
   */
  protected void waitAndClick(By by) {
    WebElement element = null;
    try {
      element = wait.until(ExpectedConditions.elementToBeClickable(by));
      element.click();
      LOGGER.info("Clicked on element successfully: " + by);
    } catch (TimeoutException e) {
      LOGGER.severe("Element located by " + by + " was not clickable within the timeout period.");
      throw new AssertionError("Element not clickable: " + by, e);
    }
  }

  /**
   * Waits until an element is clickable, clears any existing text, and sends the specified input.
   *
   * @param by The selector to locate the element.
   * @param input The text to input into the element.
   */
  protected void waitAndSendKeys(By by, String input) {
    WebElement element = null;
    try {
      element = wait.until(ExpectedConditions.elementToBeClickable(by));
    } catch (TimeoutException e) {
      LOGGER.severe("Element located by " + by + " was not clickable within the timeout period.");
      throw new AssertionError("Element not clickable: " + by, e);
    }
    element.clear();
    element.sendKeys(input);
  }

  /**
   * Navigates to the specified page.
   *
   * @param pageName The page name, e.g. "view/anonymous/login.xhtml".
   */
  protected void goToPage(String pageName) {
    if (pageName.startsWith("/")) {
      pageName = pageName.substring(1);
    }
    driver.get(BASE_URL + pageName);
  }

  /**
   * Retrieves the current page path based on the URL.
   *
   * @return The current page path.
   * @throws RuntimeException If the current page URL does not match the expected pattern.
   */
  protected String getCurrentPage() {
    Matcher matcher = CURRENT_PAGE_PATTERN.matcher(driver.getCurrentUrl());
    if (matcher.find()) {
      return matcher.group(1); // group(1) gibt den Teil der URL zurück, der auf das Muster "view/[^;]+\\.xhtml" passt.
    } else {
      throw new RuntimeException(
        "Current page URL does not match the expected pattern: " + driver.getCurrentUrl());
    }
  }


  /** Logs in as an admin user using predefined credentials. */
  protected void loginAsAdmin() {
    login("Admin1", "Pa55wort");
  }

  /**
   * Logs in a user using the given credentials.
   *
   * @param username The username.
   * @param password The password.
   */
  protected void login(String username, String password) {
    driver.get(BASE_URL + "view/anonymous/login.xhtml");
    navigateTo("view/anonymous/login.xhtml");
    driver.findElement(By.id("userInput:nick")).sendKeys(username);
    driver.findElement(By.id("userInput:password")).sendKeys(password);
    waitAndClick(By.id("userInput:submit"));
    wait.until(ExpectedConditions.urlContains(BASE_URL + "view/anonymous/allAdvertisements.xhtml"));
  }

  /** Closes the WebDriver instance. */
  public void closeDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  /**
   * Provides the Selenium {@link WebDriver} instance to the user.
   *
   * @return The Selenium {@link WebDriver} instance
   */
  public WebDriver getDriver() {
    return driver;
  }

  /**
   * Provides the Selenium {@link WebDriverWait} instance used by the {@link WebDriver} to the user.
   *
   * @return The Selenium {@link WebDriverWait} instance
   */
  public WebDriverWait getWait() {
    return wait;
  }

  /** Logs in a regular user (i.e., not an admin) using predefined credentials. */
  protected void loginAsRegularUser() {
    login("Testnutzer", "Passwort1");
  }

  /**
   * Scrolls to the bottom of the current page using JavaScript.
   *
   * @param waitMillis The number of milliseconds to wait after executing the JavaScript command.
   */
  protected void scrollToBottom(int waitMillis) {
    ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    try {
      Thread.sleep(waitMillis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted unexpectedly.", e);
    }
  }

  /**
   * Scrolls to the top of the current page using JavaScript.
   *
   * @param waitMillis The number of milliseconds to wait after executing the JavaScript command.
   */
  protected void scrollToTop(int waitMillis) {
    ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");

    // Wait until javascript has finished executing.
    // Are there working alternatives to Thread.sleep()?
    try {
      Thread.sleep(waitMillis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted unexpectedly.", e);
    }
  }

  /**
   * Counts the rows of a data table.
   *
   * @param id The ID of the data table.
   * @return Amount of rows.
   */
  protected int countDataTableChildren(String id) {
    WebElement dataTable = driver.findElement(By.id(id));
    return dataTable.findElements(By.tagName("tr")).size();
  }

  /**
   * Gets the title of an ad in a certain row of a data table.
   *
   * @param index Index of the wanted row.
   * @param dataTableID ID of the data table.
   * @return The title of the ad in a certain row
   */
  protected String getTitleOfRow(int index, String dataTableID) {
    String xpath = "//*[@id=\"" + dataTableID + "\"]/tbody/tr[" + index + "]/td[2]";
    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
    return driver.findElement(By.xpath(xpath)).getText();
  }

  /**
   * Navigates to the specified file path.
   *
   * @param filePath The file path to navigate to.
   */
  protected void navigateTo(String filePath) {
    if (filePath.startsWith("/")) {
      filePath = filePath.substring(1);
    }
    driver.get(BASE_URL + filePath);
    wait.until(ExpectedConditions.urlContains(filePath));
  }

  /**
   * Helper method to check if an element is present on the page.
   *
   * @param by The locator used to find the element.
   * @return true if the element is present, false otherwise.
   */
  protected boolean isElementPresent(By by) {
    try {
      wait.until(ExpectedConditions.presenceOfElementLocated(by));
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  /** Logs out the user by clicking the logout button and waits for the redirection. */
  protected void logout() {
    waitAndClick(By.id("headerButtonsRight:logout"));
    wait.until(ExpectedConditions.urlContains(BASE_URL + "view/anonymous/allAdvertisements.xhtml"));
  }

  /**
   * Inputs a query into a search box and initiates a search.
   *
   * @param query The search query.
   * @param columnId The ID of the search column.
   */
  protected void inputAndSearch(String query, int columnId) {
    driver.findElement(By.xpath("")).sendKeys(query);
    driver.findElement(By.id("filterButton")).click();
  }
}
