package de.uni_passau.fim.talent_tauscher.st.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** A factory to set up the Firefox Web Driver necessary for Selenium testing. */
public final class WebDriverFactory {

  private static final int TIMEOUT = 10;

  private static final int SCREEN_WIDTH = 1920;

  private static final int SCREEN_HEIGHT = 1080;

  /** Use the Initialization-on-demand holder idiom for a lazy-loaded singleton. */
  private static final class LazyHolder {
    static final WebDriverFactory INSTANCE = new WebDriverFactory();
  }

  private WebDriverFactory() {}

  private WebDriver createWebDriver() {
    WebDriverManager.firefoxdriver().setup();
    FirefoxOptions options = new FirefoxOptions();
    if (isRunningInCI()) {
      options.addArguments("--headless");
    }
    return new FirefoxDriver(options);
  }

  private boolean isRunningInCI() {
    return System.getenv("GITLAB_CI") != null || System.getenv("GITHUB_ACTIONS") != null;
  }

  /**
   * Provides an instance of this factory to the end user.
   *
   * @return An instance of this factory
   */
  public static WebDriverFactory getInstance() {
    return LazyHolder.INSTANCE;
  }

  /**
   * Provides the Selenium {@link WebDriver} instance to the user.
   *
   * @return The Selenium {@link WebDriver} instance
   */
  public WebDriver getDriver() {
    WebDriver driver = createWebDriver();
    driver.manage().window().setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    return driver;
  }

  /**
   * Creates a Selenium {@link WebDriverWait} instance for the given {@link WebDriver} instance.
   *
   * @param driver The {@link WebDriver} instance
   * @return The Selenium {@link WebDriverWait} instance
   */
  public WebDriverWait getWait(WebDriver driver) {
    return new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
  }
}
