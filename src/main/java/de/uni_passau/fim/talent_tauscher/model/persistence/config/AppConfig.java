package de.uni_passau.fim.talent_tauscher.model.persistence.config;

import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.ConfigUnreadableException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Function;

/**
 * Loads the application properties file and provides the configuration parameters through its
 * getters. Implements the singleton design pattern.
 */
public final class AppConfig {
  private static AppConfig instance;
  private Properties props;

  private AppConfig() {}

  /**
   * Provides the singleton instance of this class.
   *
   * @return The singleton instance.
   */
  public static synchronized AppConfig getInstance() {
    if (instance == null) {
      instance = new AppConfig();
    }
    return instance;
  }

  /**
   * Reads the configuration files and saves the properties for later retrieval.
   *
   * @param contextStreamProvider The context stream provider of the servlet event.
   */
  public void init(Function<String, InputStream> contextStreamProvider) {
    try (InputStream configProperties =
        contextStreamProvider.apply("WEB-INF/config/application.properties")) {
      props = new Properties();
      props.load(configProperties);
    } catch (IOException e) {
      throw new ConfigUnreadableException();
    }
  }

  /**
   * Returns host address of the database server.
   *
   * @return The database server host.
   */
  public String getDBHost() {
    return props.getProperty("DB_HOST");
  }

  /**
   * Returns the port of the database server.
   *
   * @return The database server port.
   */
  public String getDBPort() {
    return props.getProperty("DB_PORT");
  }

  /**
   * Returns the database driver.
   *
   * @return The database driver.
   */
  public String getDBDriver() {
    return props.getProperty("DB_DRIVER");
  }

  /**
   * Returns the prefix for the database connection.
   *
   * @return The prefix for the database connection.
   */
  public String getDBConnectionPrefix() {
    return props.getProperty("DB_CONNECTION_PREFIX");
  }

  /**
   * Returns the name of the database.
   *
   * @return The name of the database.
   */
  public String getDBName() {
    return props.getProperty("DB_NAME");
  }

  /**
   * Returns the username for authentication at the database server.
   *
   * @return The username for authentication at the database server.
   */
  public String getDBUsername() {
    return props.getProperty("DB_USER");
  }

  /**
   * Returns the password for authentication at the database server.
   *
   * @return The password for authentication at the database server.
   */
  public String getDBPassword() {
    return props.getProperty("DB_PASSWORD");
  }

  /**
   * Checks if SSL should be used for database connections.
   *
   * @return {@code true} if SSL should be used, {@code false} otherwise.
   */
  public boolean isDBUseSSL() {
    return Boolean.parseBoolean(props.getProperty("DB_SSL"));
  }

  /**
   * Returns the SSL factory for database connections.
   *
   * @return The SSL factory for database connections.
   */
  public String getDBSSLFactory() {
    return props.getProperty("DB_SSL_FACTORY");
  }

  /**
   * Returns the maximum number of database connections that should be pooled.
   *
   * @return The maximum number of database connections that should be pooled.
   */
  public int getDBConnectionCount() {
    return Integer.parseInt(props.getProperty("DB_CONNECTION_COUNT"));
  }

  /**
   * Returns the number of seconds to wait for a database connection before throwing an exception.
   *
   * @return The number of seconds to wait for a database connection before throwing an exception.
   */
  public int getDBConnectionTimeout() {
    return Integer.parseInt(props.getProperty("DB_TIMEOUT"));
  }

  /**
   * Returns the email address from which emails are sent.
   *
   * @return The email address from which emails are sent.
   */
  public String getEmailFrom() {
    return props.getProperty("MAIL_ADDRESS_FROM");
  }

  /**
   * Returns the host address of the SMTP server.
   *
   * @return The host address of the SMTP server.
   */
  public String getEmailHost() {
    return props.getProperty("SMTP_HOST");
  }

  /**
   * Returns the host of the SMTP server.
   *
   * @return The host of the SMTP server.
   */
  public String getEmailPort() {
    return props.getProperty("SMTP_PORT");
  }

  /**
   * Returns the username for authentication at the SMTP server.
   *
   * @return The username for authentication at the SMTP server.
   */
  public String getEmailUsername() {
    return props.getProperty("SMTP_USERNAME");
  }

  /**
   * Returns the password for authentication at the SMTP server.
   *
   * @return The password for authentication at the SMTP server.
   */
  public String getEmailPassword() {
    return props.getProperty("SMTP_PASSWORD");
  }

  /**
   * Checks whether authentication is required for sending emails.
   *
   * @return {@code true} if authentication is required, {@code false} otherwise.
   */
  public boolean isEmailAuth() {
    return Boolean.parseBoolean(props.getProperty("SMTP_AUTH"));
  }
}
