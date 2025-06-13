package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.postgres;

import de.uni_passau.fim.talent_tauscher.dto.AuthTokenDTO;
import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.PasswordResetDTO;
import de.uni_passau.fim.talent_tauscher.dto.RequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.VerificationTokenDTO;
import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.UserDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.EntityNotFoundException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreAccessException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreOperation;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/** PostgreSQL's implementation of the {@link UserDAO} interface. */
@SuppressWarnings({"checkstyle:MagicNumber", "PMD.ExceptionAsFlowControl"})
public class PostgreSQLUserDAO extends PostgreSQLDAO implements UserDAO {

  private final Logger logger = LoggerProducer.get(PostgreSQLUserDAO.class);

  /**
   * Constructs a new {@link PostgreSQLUserDAO} using the given database connection.
   *
   * @param connection The database connection.
   */
  PostgreSQLUserDAO(Connection connection) {
    super(connection);
  }

  /** {@inheritDoc} */
  @Override
  public List<UserDTO> getUsers(PaginationDTO pagination) {
    String baseQuery =
        "SELECT id, avatar, nickname, talent_points, has_admin_verified, email_address, is_email_verified FROM \"user\"";

    try (PreparedStatement stmt = buildQuery(baseQuery, pagination)) {
      ResultSet rst = stmt.executeQuery();
      return readUsers(rst);
    } catch (SQLException e) {
      throw wrapSQLException(e, StoreOperation.READ);
    }
  }

  /** {@inheritDoc} */
  @Override
  public int getCount(PaginationDTO pagination) {
    String baseQuery = "SELECT COUNT(*) FROM \"user\"";

    try (PreparedStatement stmt = buildWhereClause(baseQuery, pagination)) {
      ResultSet rst = stmt.executeQuery();
      if (rst.next()) {
        return rst.getInt(1);
      }
    } catch (SQLException e) {
      throw wrapSQLException(e, StoreOperation.READ);
    }
    return 0; // Default to 0 if there are no rows or an exception occurred
  }

  /**
   * {@inheritDoc}
   *
   * @author Sturm
   */
  @SuppressWarnings("checkstyle:MagicNumber")
  @Override
  public Optional<UserAuthenticationDTO> findUserByNickname(UserAuthenticationDTO user) {
    String query =
        """
        SELECT id, nickname, email_address, password_hash, password_salt, hash_method, has_admin_verified, is_email_verified, user_role
        FROM "user"
        WHERE nickname ILIKE ?;
        """;
    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {

      stmt.setString(1, user.getNickname());

      ResultSet result = stmt.executeQuery();

      if (result.next()) {
        return Optional.of(fillUserAuthenticationDTO(user, result));
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw wrapSQLException(e, StoreOperation.CREATE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @author Sturm
   */
  @SuppressWarnings({"checkstyle:MagicNumber", "PMD.ExceptionAsFlowControl"})
  @Override
  public Optional<UserAuthenticationDTO> findUserByEmail(UserAuthenticationDTO user) {
    String query =
        """
        SELECT id, nickname, email_address, password_hash, password_salt, hash_method, has_admin_verified, is_email_verified, user_role
        FROM "user"
        WHERE email_address ILIKE ?;
        """;
    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {

      stmt.setString(1, user.getEmail());

      ResultSet result = stmt.executeQuery();

      if (result.next()) {
        return Optional.of(fillUserAuthenticationDTO(user, result));
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw wrapSQLException(e, StoreOperation.CREATE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @author Jakob Edmaier
   */
  @SuppressWarnings("checkstyle:MagicNumber")
  @Override
  public UserAuthenticationDTO getAuthDataOfUser(UserAuthenticationDTO user) {
    String query =
        """
        SELECT id, nickname, user_role, email_address, password_hash, password_salt, hash_method, has_admin_verified, is_email_verified
        FROM "user"
        WHERE id = ?
        """;

    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.setLong(1, user.getId());
      ResultSet resultSet = stmt.executeQuery();

      if (resultSet.next()) {
        return fillUserAuthenticationDTO(user, resultSet);
      } else {
        throw new EntityNotFoundException();
      }
    } catch (SQLException e) {
      logger.info(
          "Failed to load user with id "
              + user.getId()
              + ": error while reading from the database.");
      throw wrapSQLException(e, StoreOperation.READ);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @author Jakob Edmaier
   */
  @Override
  @SuppressWarnings("PMD.ExceptionAsFlowControl")
  public UserDetailsDTO getUserDetails(UserDetailsDTO user) {
    String query = "SELECT * FROM \"user\" WHERE id = ?";

    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.setLong(1, user.getId());
      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next()) {
        return fillUserDetails(user, resultSet);
      } else {
        throw new EntityNotFoundException();
      }
    } catch (SQLException e) {
      logger.severe(
          "Failed to load user with id "
              + user.getId()
              + ": error while reading from the database.");
      throw wrapSQLException(e, StoreOperation.READ);
    }
  }

  /** {@inheritDoc} */
  @Override
  public UserDTO getUser(UserDTO user) {
    String query =
        """
        SELECT id, email_address, nickname, avatar, talent_points, has_admin_verified
        FROM "user" WHERE id = ?
        """;

    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.setLong(1, user.getId());
      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next()) {
        return fillUser(user, resultSet);
      } else {
        throw new EntityNotFoundException();
      }
    } catch (SQLException e) {
      logger.severe(
          "Failed to load user with id "
              + user.getId()
              + ": error while reading from the database.");
      throw wrapSQLException(e, StoreOperation.READ);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @author Sturm
   */
  @SuppressWarnings("checkstyle:MagicNumber")
  @Override
  public long createUser(UserDetailsDTO user) {
    String query =
        """
        INSERT INTO "user" (nickname, email_address, first_name, last_name, user_role, password_hash, password_salt, hash_method,
                                  phone_number, updated_at, city, postal_code, street, address_addition, house_number, country, avatar,
                                  has_admin_verified, is_email_verified, talent_points)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id;
        """;
    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {

      setUserDetails(user, stmt);

      ResultSet result = stmt.executeQuery();
      if (result.next()) {
        return result.getLong("id");
      } else {
        throw new StoreAccessException("Error on retrieving id");
      }
    } catch (SQLException e) {
      throw wrapSQLException(e, StoreOperation.CREATE);
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("checkstyle:MagicNumber")
  @Override
  public void updateUser(UserDetailsDTO user) {
    String query =
        """
        UPDATE "user"
        SET nickname=?, email_address=?, first_name=?, last_name=?, user_role=?, password_hash=?, password_salt=?, hash_method=?, phone_number=?,
            updated_at=?, city=?, postal_code=?, street=?, address_addition=?, house_number=?, country=?, avatar=?, has_admin_verified=?,
            is_email_verified=?, talent_points=?
        WHERE id=?
        """;
    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {

      setUserDetails(user, stmt);
      stmt.setLong(21, user.getId());

      stmt.executeUpdate();
    } catch (SQLException e) {
      throw wrapSQLException(e, StoreOperation.UPDATE);
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("checkstyle:MagicNumber")
  @Override
  public void deleteUser(UserDTO user) {
    String query =
        """
        DELETE FROM "user"
        WHERE id=?
        """;
    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.setLong(1, user.getId());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw wrapSQLException(e, StoreOperation.DELETE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @author Sturm
   */
  @SuppressWarnings("checkstyle:MagicNumber")
  @Override
  public void putEmailVerificationToken(VerificationTokenDTO token) {
    String query =
        """
        UPDATE "user"
        SET secret_for_email_verification=?
        WHERE email_address=?
        """;
    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.setString(1, token.getToken());
      stmt.setString(2, token.getEmailAddress());

      stmt.executeUpdate();
    } catch (SQLException e) {
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
  public void putPasswordResetToken(VerificationTokenDTO token) {
    String query =
        """
        UPDATE "user"
        SET secret_for_password_reset=?
        WHERE email_address=?
        """;
    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.setString(1, token.getToken());
      stmt.setString(2, token.getEmailAddress());
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.severe("Failed to set password reset token: error while writing to the database.");
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
  public boolean verifyEmail(VerificationTokenDTO token) {
    String query =
        """
        UPDATE "user"
        SET is_email_verified = TRUE, secret_for_email_verification = NULL
        WHERE secret_for_email_verification = ?
        """;

    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.setString(1, token.getToken());
      int rowsAffected = stmt.executeUpdate();

      if (rowsAffected == 1) {
        return true;
      } else {
        throw new EntityNotFoundException();
      }
    } catch (SQLException e) {
      logger.info("Failed to verify email: error while writing to the database.");
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
  public boolean resetPassword(PasswordResetDTO passwordReset) {
    String query =
        """
        UPDATE "user"
        SET password_hash = ?, password_salt = ?, hash_method = ?, secret_for_password_reset = NULL
        WHERE secret_for_password_reset = ?
        """;

    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      AuthTokenDTO authToken = passwordReset.getAuthToken();
      stmt.setString(1, authToken.getPasswordHash());
      stmt.setString(2, authToken.getSalt());
      stmt.setString(3, authToken.getAlgorithm());
      stmt.setString(4, passwordReset.getVerificationToken());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected == 1) {
        return true;
      } else {
        throw new EntityNotFoundException();
      }
    } catch (SQLException e) {
      logger.info("Failed to reset password: error while writing to the database.");
      throw wrapSQLException(e, StoreOperation.UPDATE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @author Jakob Edmaier
   */
  @Override
  public void transferTalentPoints(RequestDTO request) {
    if (!request.getAccepted()) {
      throw new IllegalArgumentException("Request must be accepted.");
    }

    String updateRequester =
        """
        UPDATE "user"
        SET talent_points = talent_points - res.cost_in_talent_points
        FROM (SELECT req.request_creator_id as request_creator_id, ad.cost_in_talent_points as cost_in_talent_points
                     FROM request_response req
                     JOIN ad ad ON req.ad_id = ad.id
                     WHERE req.id = ?) res
        WHERE id = res.request_creator_id;
        """;

    String updateProvider =
        """
        UPDATE "user"
        SET talent_points = talent_points + res.cost_in_talent_points
        FROM (SELECT ad.creator_id as creator_id, ad.cost_in_talent_points as cost_in_talent_points
                     FROM request_response req
                     RIGHT JOIN ad ad ON req.ad_id = ad.id
                     WHERE req.id = ?) res
        WHERE id = res.creator_id;
        """;

    try (PreparedStatement stmt1 = getConnection().prepareStatement(updateRequester);
        PreparedStatement stmt2 = getConnection().prepareStatement(updateProvider)) {
      stmt1.setLong(1, request.getId());
      stmt2.setLong(1, request.getId());
      int rowsAffected1 = stmt1.executeUpdate();
      int rowsAffected2 = stmt2.executeUpdate();
      if (rowsAffected1 != 1 || rowsAffected2 != 1) {
        throw new StoreUpdateException();
      }
    } catch (SQLException e) {
      logger.info("Failed to transfer talent points: error while writing to the database.");
      throw wrapSQLException(e, StoreOperation.UPDATE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @author Sturm
   */
  @Override
  public void verifyAllUsers() {
    String query =
        """
        UPDATE "user"
        SET has_admin_verified='true'
        """;

    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.info("Failed to verify all users");
      throw wrapSQLException(e, StoreOperation.UPDATE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @author Jakob Edmaier
   */
  @Override
  public UserAuthenticationDTO getUserByPasswordResetSecret(
      UserAuthenticationDTO user, PasswordResetDTO passwordReset) {
    String query =
        """
        SELECT id, nickname, user_role, email_address, password_hash, password_salt, hash_method, has_admin_verified, is_email_verified
        FROM "user"
        WHERE secret_for_password_reset = ?
        """;

    try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
      stmt.setString(1, passwordReset.getVerificationToken());
      ResultSet resultSet = stmt.executeQuery();

      if (resultSet.next()) {
        return fillUserAuthenticationDTO(user, resultSet);
      } else {
        logger.info(
            "Failed to load user by secret for password reset: " + "verification token not found.");
        throw new EntityNotFoundException();
      }
    } catch (SQLException e) {
      logger.severe(
          "Failed to load user by secret for password reset: "
              + "error while reading from the database.");
      throw wrapSQLException(e, StoreOperation.READ);
    }
  }

  /**
   * Fills a {@link UserDTO} with the data from the given {@link ResultSet}.
   *
   * @param user The user to fill.
   * @param resultSet The result set.
   * @return The filled user.
   * @author Jakob Edmaoier
   */
  private UserDTO fillUser(UserDTO user, ResultSet resultSet) throws SQLException {
    user.setId(resultSet.getLong("id"));
    user.setEmail(resultSet.getString("email_address"));
    user.setNickname(resultSet.getString("nickname"));
    user.setAvatar(resultSet.getBytes("avatar"));
    user.setTalentPoints(resultSet.getInt("talent_points"));
    user.setAdminVerified(resultSet.getBoolean("has_admin_verified"));
    return user;
  }

  /**
   * Fills a {@link UserDetailsDTO} with the data from the given {@link ResultSet}.
   *
   * @param user The user to fill.
   * @param resultSet The result set.
   * @return The filled user.
   * @author Jakob Edmaoier
   */
  private UserDetailsDTO fillUserDetails(UserDetailsDTO user, ResultSet resultSet)
      throws SQLException {
    boolean isAdmin = "ADMIN".equals(resultSet.getString("user_role"));
    user.setId(resultSet.getLong("id"));
    user.setEmail(resultSet.getString("email_address"));
    user.setFirstname(resultSet.getString("first_name"));
    user.setLastname(resultSet.getString("last_name"));
    user.setNickname(resultSet.getString("nickname"));
    user.setAdmin(isAdmin);
    user.setAvatar(resultSet.getBytes("avatar"));
    user.setTalentPoints(resultSet.getInt("talent_points"));
    user.setAuthToken(getAuthToken(resultSet));
    user.setAdminVerified(resultSet.getBoolean("has_admin_verified"));
    user.setEmailVerified(resultSet.getBoolean("is_email_verified"));
    user.setPhone(resultSet.getString("phone_number"));
    user.setCity(resultSet.getString("city"));
    user.setPostalCode(resultSet.getString("postal_code"));
    user.setStreet(resultSet.getString("street"));
    user.setHouseNumber(resultSet.getString("house_number"));
    user.setAddressAddition(resultSet.getString("address_addition"));
    user.setCountry(resultSet.getString("country"));
    return user;
  }

  /**
   * Fills a {@link UserAuthenticationDTO} with the data from the given {@link ResultSet}.
   *
   * @param user The user to fill.
   * @param resultSet The result set.
   * @return The filled user.
   * @author Jakob Edmaier
   */
  private UserAuthenticationDTO fillUserAuthenticationDTO(
      UserAuthenticationDTO user, ResultSet resultSet) throws SQLException {
    boolean isAdmin = "ADMIN".equals(resultSet.getString("user_role"));
    AuthTokenDTO authToken = new AuthTokenDTO();
    authToken.setAlgorithm(resultSet.getString("hash_method"));
    authToken.setSalt(resultSet.getString("password_salt"));
    authToken.setPasswordHash(resultSet.getString("password_hash"));

    user.setId(resultSet.getLong("id"));
    user.setNickname(resultSet.getString("nickname"));
    user.setEmail(resultSet.getString("email_address"));
    user.setAdmin(isAdmin);
    user.setEmailVerified(resultSet.getBoolean("is_email_verified"));
    user.setAdminVerified(resultSet.getBoolean("has_admin_verified"));
    user.setAuthToken(authToken);
    return user;
  }

  /**
   * Fills a {@link AuthTokenDTO} with the data from the given {@link ResultSet}.
   *
   * @param resultSet The result set.
   * @return The filled auth token.
   * @author Jakob Edmaier
   */
  private AuthTokenDTO getAuthToken(ResultSet resultSet) throws SQLException {
    AuthTokenDTO authToken = new AuthTokenDTO();
    authToken.setPasswordHash(resultSet.getString("password_hash"));
    authToken.setSalt(resultSet.getString("password_salt"));
    authToken.setAlgorithm(resultSet.getString("hash_method"));
    return authToken;
  }

  private void setUserDetails(UserDetailsDTO user, PreparedStatement stmt) throws SQLException {
    stmt.setString(1, user.getNickname());
    stmt.setString(2, user.getEmail());
    stmt.setString(3, user.getFirstname());
    stmt.setString(4, user.getLastname());
    stmt.setString(6, user.getAuthToken().getPasswordHash());
    stmt.setString(7, user.getAuthToken().getSalt());
    stmt.setString(8, user.getAuthToken().getAlgorithm());
    stmt.setString(9, user.getPhone());
    stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
    stmt.setString(11, user.getCity());
    stmt.setString(12, user.getPostalCode());
    stmt.setString(13, user.getStreet());
    stmt.setString(14, user.getAddressAddition());
    stmt.setString(15, user.getHouseNumber());
    stmt.setString(16, user.getCountry());
    stmt.setBytes(17, user.getAvatar());
    stmt.setBoolean(18, user.isAdminVerified());
    stmt.setBoolean(19, user.isEmailVerified());
    stmt.setInt(20, user.getTalentPoints());

    if (user.isAdmin()) {
      stmt.setObject(5, "ADMIN", Types.OTHER);
    } else {
      stmt.setObject(5, "USER", Types.OTHER);
    }
  }

  private List<UserDTO> readUsers(ResultSet resultSet) throws SQLException {
    List<UserDTO> users = new ArrayList<>();
    while (resultSet.next()) {
      UserDTO user = new UserDTO();
      user.setId(resultSet.getLong("id"));
      user.setAvatar(resultSet.getBytes("avatar"));
      user.setNickname(resultSet.getString("nickname"));
      user.setTalentPoints(resultSet.getInt("talent_points"));
      user.setAdminVerified(resultSet.getBoolean("has_admin_verified"));
      user.setEmail(resultSet.getString("email_address"));
      user.setEmailVerified(resultSet.getBoolean("is_email_verified"));

      users.add(user);
    }
    return users;
  }
}
