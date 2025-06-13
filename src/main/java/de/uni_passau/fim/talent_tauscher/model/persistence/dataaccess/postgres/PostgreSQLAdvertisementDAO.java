package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.postgres;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDTO;
import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.AdvertisementDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.EntityNotFoundException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreOperation;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreReadException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreUpdateException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** PostgreSQL's implementation of the {@link AdvertisementDAO} interface. */
public class PostgreSQLAdvertisementDAO extends PostgreSQLDAO implements AdvertisementDAO {

  private final Logger logger = LoggerProducer.get(PostgreSQLAdvertisementDAO.class);

  /**
   * Constructs a new {@link PostgreSQLAdvertisementDAO} using the given database connection.
   *
   * @param connection The connection.
   */
  PostgreSQLAdvertisementDAO(Connection connection) {
    super(connection);
  }

  /**
   * {@inheritDoc}
   *
   * @author Leon Föckersperger
   */
  @SuppressWarnings("checkstyle:MagicNumber")
  @Override
  public int getCount(PaginationDTO pagination) {
    // We only need the WHERE clause for counting rows
    String baseQuery =
        """
      SELECT COUNT(*)
      FROM ad
      WHERE date_of_publication <= NOW()::DATE AND (date_of_completion IS NULL OR date_of_completion >= NOW()::DATE)
      """;

    Map<String, Object> conditions = new HashMap<>();
    conditions.put("is_active", true);

    try (PreparedStatement preparedStatement =
        buildWhereClauseWithFixedConditions(baseQuery, pagination, conditions)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt(1); // The count is in the first column
      }
    } catch (SQLException e) {
      throw new StoreReadException("Error executing getCount", e);
    }
    return 0; // Default to 0 if there are no rows or an exception occurred
  }

  /**
   * {@inheritDoc}
   *
   * @author Leon Föckersperger
   */
  @Override
  public List<AdvertisementDTO> getAdvertisements(PaginationDTO pagination) {
    List<AdvertisementDTO> advertisements = new ArrayList<>();
    String query =
        """
      SELECT id, title, cost_in_talent_points, city, image, creator_id, postal_code
      FROM ad
      WHERE date_of_publication <= NOW()::DATE AND (date_of_completion IS NULL OR date_of_completion >= NOW()::DATE)
      """;

    Map<String, Object> conditions = new HashMap<>();
    conditions.put("is_active", true);

    try (PreparedStatement preparedStatement =
        buildQueryWithFixedConditions(query, pagination, conditions)) {
      readAdvertisements(preparedStatement, advertisements);
    } catch (SQLException e) {
      throw new StoreReadException("Error executing getAdvertisements", e);
    }
    return advertisements;
  }

  /**
   * {@inheritDoc}
   *
   * @author Jakob Edmaier
   */
  @Override
  public List<AdvertisementDTO> getClaimedAdvertisementsOfUser(
      UserDTO user, PaginationDTO pagination) {
    List<AdvertisementDTO> advertisements = new ArrayList<>();
    String query =
        """
        SELECT ad.id, ad.title, ad.cost_in_talent_points, ad.city, ad.image, ad.creator_id, ad.postal_code
        FROM ad ad
        JOIN request_response req ON ad.id = req.ad_id
        """;

    Map<String, Object> conditions = new HashMap<>();
    conditions.put("req.request_creator_id", user.getId());
    conditions.put("req.result", true);

    try (PreparedStatement preparedStatement =
        buildQueryWithFixedConditions(query, pagination, conditions)) {
      readAdvertisements(preparedStatement, advertisements);
    } catch (SQLException e) {
      logger.severe(
          "Failed to load claimed advertisements: error while reading from the database.");
      throw wrapSQLException(e, StoreOperation.READ);
    }
    return advertisements;
  }

  /**
   * {@inheritDoc}
   *
   * @author Leon Föckersperger
   */
  public List<AdvertisementDTO> getCreatedAdvertisementsOfUser(
      UserDTO user, PaginationDTO pagination) {
    List<AdvertisementDTO> advertisements = new ArrayList<>();
    String query =
        "SELECT id, title, cost_in_talent_points, city, image, creator_id, postal_code "
            + "FROM ad";
    Map<String, Object> conditions = new HashMap<>();
    conditions.put("creator_id", user.getId());
    try (PreparedStatement preparedStatement =
        buildQueryWithFixedConditions(query, pagination, conditions)) {
      readAdvertisements(preparedStatement, advertisements);
    } catch (SQLException e) {
      throw new StoreReadException("Error executing getCreatedAdvertisementsOfUser", e);
    }
    return advertisements;
  }

  /**
   * {@inheritDoc}
   *
   * @author Jakob Edmaier
   */
  @Override
  public int getClaimedAdvertisementsCount(UserDTO user, PaginationDTO pagination) {
    String query =
        """
        SELECT COUNT(*)
        FROM ad ad
        JOIN request_response req ON ad.id = req.ad_id
        """;

    Map<String, Object> conditions = new HashMap<>();
    conditions.put("req.request_creator_id", user.getId());
    conditions.put("req.result", true);

    try (PreparedStatement preparedStatement =
        buildWhereClauseWithFixedConditions(query, pagination, conditions)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt(1); // The count is in the first column
      }
    } catch (SQLException e) {
      logger.severe(
          "Failed to load number of claimed advertisements: error while reading from the database.");
      throw wrapSQLException(e, StoreOperation.READ);
    }
    return 0;
  }

  /**
   * {@inheritDoc}
   *
   * @author Leon Föckersperger
   */
  @SuppressWarnings("checkstyle:MagicNumber")
  @Override
  public int getCreatedAdvertisementsCount(UserDTO user, PaginationDTO pagination) {
    String baseQuery = "SELECT COUNT(*) FROM ad";
    Map<String, Object> conditions = new HashMap<>();
    conditions.put("creator_id", user.getId());
    try (PreparedStatement preparedStatement =
        buildWhereClauseWithFixedConditions(baseQuery, pagination, conditions)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt(1); // The count is in the first column
      }
    } catch (SQLException e) {
      throw new StoreReadException("Error executing getCreatedAdvertisementsCount", e);
    }
    return 0; // Default to 0 if there are no rows or an exception occurred
  }

  /**
   * {@inheritDoc}
   *
   * @author Jakob Edmaier
   */
  @SuppressWarnings({"checkstyle:MagicNumber", "PMD.ExceptionAsFlowControl"})
  @Override
  public long createAdvertisement(AdvertisementDetailsDTO advertisement) {
    String query =
        """
            INSERT INTO ad
                (creator_id, title, free_text, cost_in_talent_points,
                date_of_publication, date_of_completion, is_active, city,
                country, postal_code, street, email_address,
                phone_number, street_shown, name_shown, phone_number_shown,
                image)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.setLong(1, advertisement.getUserId());
      stmt.setString(2, advertisement.getTitle());
      stmt.setString(3, advertisement.getDescription());
      stmt.setInt(4, advertisement.getCost());
      stmt.setDate(5, Date.valueOf(advertisement.getStartDate()));
      stmt.setDate(6, convertToNullableSQLDate(advertisement.getEndDate()));
      stmt.setBoolean(7, !advertisement.isHidden());
      stmt.setString(8, advertisement.getCity());
      stmt.setString(9, advertisement.getCountry());
      stmt.setString(10, advertisement.getPostalCode());
      stmt.setString(11, advertisement.getStreet());
      stmt.setString(12, advertisement.getEmail());
      stmt.setString(13, advertisement.getPhoneNumber());
      stmt.setBoolean(14, advertisement.isStreetShown());
      stmt.setBoolean(15, advertisement.isNameShown());
      stmt.setBoolean(16, advertisement.isPhoneNumberShown());
      stmt.setBytes(17, advertisement.getImage());
      ResultSet resultSet = stmt.executeQuery();

      if (resultSet.next()) {
        return resultSet.getLong("id");
      } else {
        logger.severe("No result set obtained.");
        throw new StoreUpdateException();
      }
    } catch (SQLException e) {
      logger.severe("Failed to create advertisement: error while writing to the database.");
      throw wrapSQLException(e, StoreOperation.CREATE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @author Jakob Edmaier
   */
  @SuppressWarnings({"checkstyle:MagicNumber", "PMD.ExceptionAsFlowControl"})
  @Override
  public AdvertisementDetailsDTO getAdvertisementDetails(AdvertisementDetailsDTO advertisement) {
    String query = "SELECT * FROM ad WHERE id = ?";

    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.setLong(1, advertisement.getId());
      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next()) {
        return fillAdvertisementDetails(advertisement, resultSet);
      } else {
        throw new EntityNotFoundException();
      }
    } catch (SQLException e) {
      logger.severe("Failed to load advertisement details");
      throw wrapSQLException(e, StoreOperation.READ);
    }
  }

  /**
   * Fills the advertisement details with the data from the result set.
   *
   * @param advertisement The advertisement details.
   * @param resultSet The result set.
   * @return The filled advertisement details.
   * @author Jakob Edmaier
   */
  private AdvertisementDetailsDTO fillAdvertisementDetails(
      AdvertisementDetailsDTO advertisement, ResultSet resultSet) throws SQLException {
    advertisement.setId(resultSet.getLong("id"));
    advertisement.setUserId(resultSet.getLong("creator_id"));
    advertisement.setTitle(resultSet.getString("title"));
    advertisement.setImage(resultSet.getBytes("image"));
    advertisement.setDescription(resultSet.getString("free_text"));
    advertisement.setCost(resultSet.getInt("cost_in_talent_points"));
    advertisement.setStartDate(
        convertToNullableLocalDate(resultSet.getDate("date_of_publication")));
    advertisement.setEndDate(convertToNullableLocalDate(resultSet.getDate("date_of_completion")));
    advertisement.setHidden(!resultSet.getBoolean("is_active"));
    advertisement.setCountry(resultSet.getString("country"));
    advertisement.setCity(resultSet.getString("city"));
    advertisement.setPostalCode(resultSet.getString("postal_code"));
    advertisement.setStreet(resultSet.getString("street"));
    advertisement.setEmail(resultSet.getString("email_address"));
    advertisement.setPhoneNumber(resultSet.getString("phone_number"));
    advertisement.setStreetShown(resultSet.getBoolean("street_shown"));
    advertisement.setNameShown(resultSet.getBoolean("name_shown"));
    advertisement.setPhoneNumberShown(resultSet.getBoolean("phone_number_shown"));
    return advertisement;
  }

  private LocalDate convertToNullableLocalDate(Date date) {
    if (date == null) {
      return null;
    } else {
      return date.toLocalDate();
    }
  }

  /** {@inheritDoc} */
  @Override
  public AdvertisementDTO getAdvertisement(AdvertisementDTO advertisement) {
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @author Jakob Edmaier
   */
  @SuppressWarnings("checkstyle:MagicNumber")
  @Override
  public void updateAdvertisement(AdvertisementDetailsDTO advertisement) {
    String query =
        """
            UPDATE ad
            SET title = ?, free_text = ?, cost_in_talent_points = ?,
                date_of_publication = ?, date_of_completion = ?, is_active = ?, city = ?,
                country = ?, postal_code = ?, street = ?, email_address = ?,
                phone_number = ?, street_shown = ?, name_shown = ?, phone_number_shown = ?,
                image = ?
            WHERE id = ?;
            """;

    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.setString(1, advertisement.getTitle());
      stmt.setString(2, advertisement.getDescription());
      stmt.setInt(3, advertisement.getCost());
      stmt.setDate(4, Date.valueOf(advertisement.getStartDate()));
      stmt.setDate(5, convertToNullableSQLDate(advertisement.getEndDate()));
      stmt.setBoolean(6, !advertisement.isHidden());
      stmt.setString(7, advertisement.getCity());
      stmt.setString(8, advertisement.getCountry());
      stmt.setString(9, advertisement.getPostalCode());
      stmt.setString(10, advertisement.getStreet());
      stmt.setString(11, advertisement.getEmail());
      stmt.setString(12, advertisement.getPhoneNumber());
      stmt.setBoolean(13, advertisement.isStreetShown());
      stmt.setBoolean(14, advertisement.isNameShown());
      stmt.setBoolean(15, advertisement.isPhoneNumberShown());
      stmt.setBytes(16, advertisement.getImage());
      stmt.setLong(17, advertisement.getId());
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.severe("Failed to update advertisement: error while writing to the database.");
      throw wrapSQLException(e, StoreOperation.UPDATE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @author Jakob Edmaier
   */
  @SuppressWarnings("checkstyle:MagicNumber")
  @Override
  public void deleteAdvertisement(AdvertisementDTO advertisement) {
    String query = "DELETE FROM ad WHERE id = ?";

    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.setLong(1, advertisement.getId());
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.severe("Failed to delete advertisement");
      throw wrapSQLException(e, StoreOperation.DELETE);
    }
  }

  /**
   * Converts a LocalDate to a nullable SQL date.
   *
   * @param date The date.
   * @return The SQL date or null if the date is null.
   * @author Jakob Edmaier
   */
  private Date convertToNullableSQLDate(LocalDate date) {
    return date != null ? Date.valueOf(date) : null;
  }

  /**
   * Reads the advertisements from the result set and adds them to the list.
   *
   * @param preparedStatement The prepared statement.
   * @param advertisements The list of advertisements.
   * @throws SQLException If an error occurs while reading the result set.
   * @author Leon Föckersperger
   */
  private void readAdvertisements(
      PreparedStatement preparedStatement, List<AdvertisementDTO> advertisements)
      throws SQLException {
    try (ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        advertisements.add(readAdvertisement(resultSet));
      }
    }
  }

  /**
   * Reads an advertisement from the result set.
   *
   * @param resultSet The result set.
   * @return The advertisementDTO.
   * @throws SQLException If an error occurs while reading the result set.
   * @author Leon Föckersperger
   */
  private AdvertisementDTO readAdvertisement(ResultSet resultSet) throws SQLException {
    AdvertisementDTO advertisementDTO = new AdvertisementDTO();
    advertisementDTO.setId(resultSet.getLong("id"));
    advertisementDTO.setTitle(resultSet.getString("title"));
    advertisementDTO.setCost(resultSet.getInt("cost_in_talent_points"));
    advertisementDTO.setCity(resultSet.getString("city"));
    advertisementDTO.setImage(resultSet.getBytes("image"));
    advertisementDTO.setUserId(resultSet.getLong("creator_id"));
    advertisementDTO.setPostalCode(resultSet.getString("postal_code"));
    return advertisementDTO;
  }
}
