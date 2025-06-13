package de.uni_passau.fim.talent_tauscher.model.persistence.lifecycle;

import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.ConnectionPool;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreUpdateException;
import de.uni_passau.fim.talent_tauscher.model.persistence.util.ResourceReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PostgreSQL's implementation of the {@link StoreSetup} interface.
 *
 * @author Leon Föckersperger
 */
@SuppressWarnings("checkstyle:MagicNumber")
class PostgreSQLSetup implements StoreSetup {

  private static final Set<String> VALID_TABLES =
      Set.of("\"user\"", "system_settings", "ad", "request_response");

  private static final int SUM_TEST_DATA = 15;

  private static final Set<String> VALID_COLUMNS =
      Set.of(
          "id",
          "first_name",
          "last_name",
          "nickname",
          "user_role",
          "avatar",
          "talent_points",
          "email_address",
          "password_hash",
          "password_salt",
          "hash_method",
          "secret_for_email_verification",
          "secret_for_password_reset",
          "has_admin_verified",
          "is_email_verified",
          "phone_number",
          "created_at",
          "updated_at",
          "country",
          "city",
          "postal_code",
          "street",
          "address_addition",
          "house_number",
          "css_name",
          "logo",
          "data_protection",
          "max_pic_size",
          "contact_info",
          "is_admin_confirmation_needed_registration",
          "imprint",
          "sum_paginated_items",
          "creator_id",
          "title",
          "image",
          "free_text",
          "cost_in_talent_points",
          "date_of_publication",
          "date_of_completion",
          "is_active",
          "street_shown",
          "name_shown",
          "phone_number_shown",
          "request_creator_id",
          "timestamp_request",
          "timestamp_response",
          "free_text_request",
          "free_text_response",
          "result");

  private static final Logger LOGGER = LoggerProducer.get(PostgreSQLSetup.class);

  private final ResourceReader resourceReader;

  /**
   * Constructs a new {@code PostgreSQLSetup} instance. And sets all the pathes necessary for the
   * setup.
   *
   * @param resourceReader The reader for the needed resource files.
   */
  PostgreSQLSetup(ResourceReader resourceReader) {
    this.resourceReader = resourceReader;
  }

  /**
   * Checks if the schema already exists in the database.
   *
   * @return {@code true} if the schema exists, otherwise {@code false}.
   */
  @Override
  public boolean schemaExists() {
    LOGGER.fine("Checking if schema exists.");
    Connection connection = ConnectionPool.getInstance().getConnection();
    try (PreparedStatement statement =
        connection.prepareStatement("SELECT EXISTS (SELECT 1 FROM \"user\");")) {
      try (ResultSet set = statement.executeQuery()) {
        return set.next() && set.getBoolean(1);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "DB operation checking the schema failed", e);
      return false;
    } finally {
      ConnectionPool.getInstance().releaseConnection(connection);
    }
  }

  /** Creates the schema for the datastore using the provided SQL script. */
  @Override
  public void createSchema() {
    executeSqlScript(resourceReader.fetchSQLSchema(), "Schema was created.");
  }

  /** Initializes the data store with the initial data required for the application. */
  @Override
  public void initializeData() {
    executeSqlScript(resourceReader.fetchInitialDataDefinition(), "Data was initialized.");
    updateImage(resourceReader.fetchDefaultLogoImage(), "system_settings", "logo", 1);
    executeSqlScript(resourceReader.fetchTestDataDefinition(), "Test data was initialized.");
    byte[] defaultADImage = resourceReader.fetchDefaultAdvertisementImage();
    for (int i = 1; i <= SUM_TEST_DATA; i++) {
      LOGGER.fine("Updating image for ad with id: " + i);
      updateImage(defaultADImage, "ad", "image", i);
    }

    byte[] defaultUserImage = resourceReader.fetchDefaultAvatarImage();
    for (int i = 1; i <= 3; i++) {
      LOGGER.fine("Updating image for user with id: " + i);
      updateImage(defaultUserImage, "\"user\"", "avatar", i);
    }
  }

  /**
   * Executes the provided SQL script.
   *
   * @param sqlScript The SQL script to execute.
   * @param successMessage The success message to log upon successful execution.
   */
  private void executeSqlScript(String sqlScript, String successMessage) {
    LOGGER.fine("Executing SQL script.");
    Connection connection = ConnectionPool.getInstance().getConnection();
    try (PreparedStatement statement = connection.prepareStatement(sqlScript)) {
      statement.executeUpdate();
      LOGGER.info(successMessage);
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed DB operation.", e);
      throw new StoreUpdateException("Failed DB operation: ", e);
    } finally {
      ConnectionPool.getInstance().releaseConnection(connection);
    }
  }

  /**
   * Updates the image in the specified table and column for the given ID.
   *
   * @param imageBytes The byte array of the image.
   * @param table The name of the table.
   * @param column The name of the column.
   * @param id The ID of the row to update.
   */
  public void updateImage(byte[] imageBytes, String table, String column, int id) {
    if (!isValidIdentifier(table, true) || !isValidIdentifier(column, false)) {
      throw new IllegalArgumentException("Invalid table or column name.");
    }

    String sql = String.format("UPDATE %s SET %s = ? WHERE id = ?", table, column);
    LOGGER.fine("Updating image in the database.");
    Connection connection = ConnectionPool.getInstance().getConnection();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setBytes(1, imageBytes);
      statement.setInt(2, id);
      statement.executeUpdate();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "DB operation updating the image failed.", e);
      throw new StoreUpdateException("DB operation updating the image failed.", e);
    } finally {
      ConnectionPool.getInstance().releaseConnection(connection);
    }
  }

  /**
   * Validates if the provided identifier is valid.
   *
   * @param identifier The identifier to validate.
   * @param isTable A flag indicating if the identifier is a table name.
   * @return {@code true} if the identifier is valid, otherwise {@code false}.
   */
  private boolean isValidIdentifier(String identifier, boolean isTable) {
    return isTable ? VALID_TABLES.contains(identifier) : VALID_COLUMNS.contains(identifier);
  }
}
