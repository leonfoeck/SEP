package de.uni_passau.fim.talent_tauscher.model.persistence.util;

import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.ConfigUnreadableException;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Utility class for accessing the resource files of the application. */
public final class ResourceReader {

  private static final int SUM_IMAGE_BYTES = 1024;

  private static final String BASE_PATH = "de/uni_passau/fim/talent_tauscher/model/persistence/";

  private static final String DEFAULT_AD_IMAGE_PATH = BASE_PATH + "defaultADImage.png";
  private static final String DEFAULT_AVATAR_PATH = BASE_PATH + "defaultAvatar.png";
  private static final String DEFAULT_LOGO_PATH = BASE_PATH + "defaultLogo.png";
  private static final String DB_SCHEMA_PATH = BASE_PATH + "dbSchema.sql";
  private static final String INITIAL_DATA_PATH = BASE_PATH + "startData.sql";
  private static final String TEST_DATA_PATH = BASE_PATH + "testData.sql";

  private final Logger logger = LoggerProducer.get(ResourceReader.class);
  private final Function<String, InputStream> contextStreamProvider;
  private final String basePath;

  /**
   * Creates a new instance of the {@link ResourceReader} class.
   *
   * @param contextStreamProvider The function to provide the input stream.
   * @param basePath The base path for the resources.
   */
  public ResourceReader(Function<String, InputStream> contextStreamProvider, String basePath) {
    this.contextStreamProvider = contextStreamProvider;
    this.basePath = basePath;
  }

  /**
   * Fetches the default advertisement image.
   *
   * @return The content of the image file as a byte array.
   * @author Jakob Edmaier
   */
  public byte[] fetchDefaultAdvertisementImage() {
    return fetchImage(getFullPath(DEFAULT_AD_IMAGE_PATH));
  }

  /**
   * Fetches the default Avatar image.
   *
   * @return The content of the image file as a byte array.
   * @author Sturm
   */
  public byte[] fetchDefaultAvatarImage() {
    return fetchImage(getFullPath(DEFAULT_AVATAR_PATH));
  }

  /**
   * Fetches the default Logo image.
   *
   * @return The content of the image file as a byte array.
   * @author Sturm
   */
  public byte[] fetchDefaultLogoImage() {
    return fetchImage(getFullPath(DEFAULT_LOGO_PATH));
  }

  /**
   * Fetches an image from the provided path.
   *
   * @param path The path to the image file.
   * @return The content of the image file as a byte array.
   * @author Leon Föckersperger
   */
  @SuppressWarnings("PMD.ExceptionAsFlowControl")
  private byte[] fetchImage(String path) {
    logger.fine("Fetching image from path: " + path);
    try (InputStream stream = contextStreamProvider.apply(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      if (stream == null) {
        throw new FileNotFoundException("Image file not found: " + path);
      }
      byte[] buffer = new byte[SUM_IMAGE_BYTES];
      int bytesRead;
      while ((bytesRead = stream.read(buffer)) != -1) {
        baos.write(buffer, 0, bytesRead);
      }
      return baos.toByteArray();
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to load image.", e);
      throw new RuntimeException("Failed to load image: " + path, e);
    }
  }

  /**
   * Fetches the SQL script from the provided path.
   *
   * @param path The path to the SQL script.
   * @return The content of the SQL script as a String.
   * @author Leon Föckersperger
   */
  @SuppressWarnings("PMD.ExceptionAsFlowControl")
  private String fetchSqlScript(String path) {
    try (InputStream input = contextStreamProvider.apply(path)) {
      if (input == null) {
        throw new FileNotFoundException("SQL script file not found: " + path);
      }
      return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to load DB script.", e);
      throw new ConfigUnreadableException("DB script could not be loaded: " + path, e);
    }
  }

  /**
   * Fetches the SQL schema definition.
   *
   * @return The SQL file as a string.
   * @author Jakob Edmaier
   */
  public String fetchSQLSchema() {
    return fetchSqlScript(getFullPath(DB_SCHEMA_PATH));
  }

  /**
   * Fetches the SQL script to insert the initial data into the database.
   *
   * @return The SQL file as a string.
   * @author Jakob Edmaier
   */
  public String fetchInitialDataDefinition() {
    return fetchSqlScript(getFullPath(INITIAL_DATA_PATH));
  }

  /**
   * Fetches the SQL script to insert the test data into the database.
   *
   * @return The SQL file as a string.
   * @author Jakob Edmaier
   */
  public String fetchTestDataDefinition() {
    return fetchSqlScript(getFullPath(TEST_DATA_PATH));
  }

  private String getFullPath(String fileName) {
    return basePath + fileName;
  }
}
