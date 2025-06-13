package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.postgres;

import de.uni_passau.fim.talent_tauscher.dto.ExtendedRequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.RequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.RequestDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.EntityNotFoundException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreOperation;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreReadException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** PostgreSQL's implementation of the {@link RequestDAO} interface. */
@SuppressWarnings({"checkstyle:MagicNumber", "PMD.ExceptionAsFlowControl"})
public class PostgreSQLRequestDAO extends PostgreSQLDAO implements RequestDAO {

  private final Logger logger = LoggerProducer.get(PostgreSQLRequestDAO.class);

  // SQL Queries
  private static final String CREATE_REQUEST_QUERY =
      "INSERT INTO request_response "
          + "(ad_id, request_creator_id, timestamp_request, free_text_request) "
          + "VALUES (?, ?, ?, ?) RETURNING id;";

  private static final String UPDATE_REQUEST_QUERY =
      "UPDATE request_response "
          + "SET timestamp_response = now(), free_text_response = ?, result = ? "
          + "WHERE id = ?;";

  private static final String BASE_GET_REQUEST_QUERY =
      "SELECT req.id, req.ad_id, req.request_creator_id, req.timestamp_request, "
          + "req.timestamp_response, req.free_text_request, req.free_text_response, req.result, "
          + "ad.title AS ad_title, u.nickname AS ad_creator_nickname, v.nickname AS request_creator_nickname "
          + "FROM request_response req "
          + "JOIN ad ad ON req.ad_id = ad.id "
          + "JOIN \"user\" u ON u.id = ad.creator_id "
          + "JOIN \"user\" v ON v.id = req.request_creator_id ";

  private static final String GET_REQUEST_QUERY = BASE_GET_REQUEST_QUERY + "WHERE req.id = ?;";

  private static final String COUNT_INCOMING_REQUESTS_QUERY =
      "SELECT COUNT(*) "
          + "FROM request_response req "
          + "JOIN ad ad ON req.ad_id = ad.id "
          + "JOIN \"user\" u ON u.id = ad.creator_id ";

  private static final String COUNT_OUTGOING_REQUESTS_QUERY =
      "SELECT COUNT(*) "
          + "FROM request_response req "
          + "JOIN ad ad ON req.ad_id = ad.id "
          + "JOIN \"user\" u ON u.id = ad.creator_id "
          + "JOIN \"user\" v ON v.id = req.request_creator_id ";

  /**
   * Constructs a new {@link PostgreSQLRequestDAO} using the given database connection.
   *
   * @param connection The connection.
   */
  PostgreSQLRequestDAO(Connection connection) {
    super(connection);
  }

  /** {@inheritDoc} */
  @Override
  public long createRequest(RequestDTO request) {
    try (PreparedStatement stmt =
        getConnection().prepareStatement(CREATE_REQUEST_QUERY, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setLong(1, request.getAdvertisementId());
      stmt.setLong(2, request.getSenderId());
      stmt.setTimestamp(3, Timestamp.valueOf(request.getRequestTimestamp()));
      stmt.setString(4, request.getRequest());

      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new StoreUpdateException("Creating request failed, no rows affected.");
      }

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getLong(1);
        } else {
          throw new StoreUpdateException("Creating request failed, no ID obtained.");
        }
      }
    } catch (SQLException e) {
      logger.severe("Failed to create request: " + e.getMessage());
      throw wrapSQLException(e, StoreOperation.CREATE);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void updateRequest(RequestDTO request) {
    try (PreparedStatement stmt = getConnection().prepareStatement(UPDATE_REQUEST_QUERY)) {
      stmt.setString(1, request.getResponse());
      stmt.setObject(2, request.getAccepted());
      stmt.setLong(3, request.getId());
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.severe("Failed to update request: " + e.getMessage());
      throw wrapSQLException(e, StoreOperation.UPDATE);
    }
  }

  /** {@inheritDoc} */
  @Override
  public ExtendedRequestDTO getRequest(ExtendedRequestDTO request) {
    try (PreparedStatement stmt = getConnection().prepareStatement(GET_REQUEST_QUERY)) {
      stmt.setLong(1, request.getId());
      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next()) {
        return fillExtendedRequest(resultSet, request);
      } else {
        logger.info("Could not find request with id " + request.getId());
        throw new EntityNotFoundException();
      }
    } catch (SQLException e) {
      logger.severe("Failed to load request: " + e.getMessage());
      throw wrapSQLException(e, StoreOperation.READ);
    }
  }

  /** {@inheritDoc} */
  @Override
  public List<ExtendedRequestDTO> getIncomingRequests(UserDTO user, PaginationDTO pagination) {
    return getRequests(BASE_GET_REQUEST_QUERY, pagination, Map.of("u.id", user.getId()));
  }

  /** {@inheritDoc} */
  @Override
  public int getIncomingRequestsCount(UserDTO user, PaginationDTO pagination) {
    return getRequestCount(COUNT_INCOMING_REQUESTS_QUERY, pagination, Map.of("u.id", user.getId()));
  }

  /** {@inheritDoc} */
  @Override
  public List<ExtendedRequestDTO> getOutgoingRequests(UserDTO user, PaginationDTO pagination) {
    return getRequests(BASE_GET_REQUEST_QUERY, pagination, Map.of("v.id", user.getId()));
  }

  /** {@inheritDoc} */
  @Override
  public int getOutgoingRequestsCount(UserDTO user, PaginationDTO pagination) {
    return getRequestCount(COUNT_OUTGOING_REQUESTS_QUERY, pagination, Map.of("v.id", user.getId()));
  }

  private List<ExtendedRequestDTO> getRequests(
      String query, PaginationDTO pagination, Map<String, Object> fixedConditions) {
    try (PreparedStatement stmt =
        buildQueryWithFixedConditions(query, pagination, fixedConditions)) {
      ResultSet resultSet = stmt.executeQuery();
      List<ExtendedRequestDTO> requests = new ArrayList<>();
      while (resultSet.next()) {
        ExtendedRequestDTO request = new ExtendedRequestDTO();
        fillExtendedRequest(resultSet, request);
        requests.add(request);
      }
      return requests;
    } catch (SQLException e) {
      logger.severe("Failed to load requests: " + e.getMessage());
      throw wrapSQLException(e, StoreOperation.READ);
    }
  }

  private int getRequestCount(
      String query, PaginationDTO pagination, Map<String, Object> fixedConditions) {
    try (PreparedStatement stmt =
        buildWhereClauseWithFixedConditions(query, pagination, fixedConditions)) {
      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt(1);
      } else {
        throw new StoreReadException("Failed to count requests.");
      }
    } catch (SQLException e) {
      logger.severe("Failed to count requests: " + e.getMessage());
      throw wrapSQLException(e, StoreOperation.READ);
    }
  }

  private ExtendedRequestDTO fillExtendedRequest(ResultSet resultSet, ExtendedRequestDTO request)
      throws SQLException {
    request.setId(resultSet.getLong("id"));
    request.setAdvertisementId(resultSet.getLong("ad_id"));
    request.setSenderId(resultSet.getLong("request_creator_id"));
    request.setRequestTimestamp(
        convertToNullableLocalDateTime(resultSet.getTimestamp("timestamp_request")));
    request.setResponseTimeStamp(
        convertToNullableLocalDateTime(resultSet.getTimestamp("timestamp_response")));
    request.setRequest(resultSet.getString("free_text_request"));
    request.setResponse(resultSet.getString("free_text_response"));
    request.setAccepted(resultSet.getObject("result", Boolean.class));
    request.setRequestCreatorUsername(resultSet.getString("request_creator_nickname"));
    request.setRecipientUsername(resultSet.getString("ad_creator_nickname"));
    request.setAdvertisementTitle(resultSet.getString("ad_title"));
    return request;
  }

  private LocalDateTime convertToNullableLocalDateTime(Timestamp timestamp) {
    return timestamp == null ? null : timestamp.toLocalDateTime();
  }
}
