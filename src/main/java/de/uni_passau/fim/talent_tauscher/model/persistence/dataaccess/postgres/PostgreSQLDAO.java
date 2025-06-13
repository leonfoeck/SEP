package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.postgres;

import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreAccessException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreConnectionException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreDeleteException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreOperation;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreReadException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreUpdateException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreValidationException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Superclass for all PostgreSQL DAO implementations. Implements common behavior such as
 * constructing prepared statements and wrapping {@code SQLExceptions} into suitable persistence
 * layer exceptions.
 *
 * @author Jakob Edmaier
 */
class PostgreSQLDAO {

  private final Connection connection;

  /**
   * Constructs a new {@link PostgreSQLDAO}.
   *
   * @param connection The database connection.
   */
  protected PostgreSQLDAO(Connection connection) {
    this.connection = connection;
  }

  /**
   * Takes an {@link SQLException} and wraps it into a suitable persistence layer exception,
   * depending on the SQL state code retrieved by the {@link SQLException#getSQLState()} method and
   * the type of the store operation (e.g., create, read, ...).
   *
   * @param e The SQL exception.
   * @param operation The type of the store operation.
   * @return The persistence layer exception.
   */
  protected StoreAccessException wrapSQLException(SQLException e, StoreOperation operation) {
    String sqlState = e.getSQLState();
    if (sqlState.startsWith("23")) {
      return new StoreValidationException(null, e);
    } else if (sqlState.startsWith("08")) {
      return new StoreConnectionException(null, e);
    } else {
      return switch (operation) {
        case CREATE, UPDATE -> new StoreUpdateException(null, e);
        case READ -> new StoreReadException(null, e);
        case DELETE -> new StoreDeleteException(null, e);
      };
    }
  }

  /**
   * Constructs a prepared statement by combining a base query with a WHERE-clause built from the
   * filters in the given pagination object. The prepared statement is ready-to-use, i.e., the
   * filter values are already bound.
   *
   * @param baseQuery The base query string (e.g. {@code "SELECT * FROM table"}).
   * @param pagination The pagination object.
   * @return The prepared statement.
   */
  protected PreparedStatement buildWhereClause(String baseQuery, PaginationDTO pagination)
      throws SQLException {
    return new QueryBuilder(baseQuery, pagination).addWhereClause().build(connection);
  }

  protected PreparedStatement buildWhereClauseWithFixedConditions(
      String baseQuery, PaginationDTO pagination, Map<String, Object> conditions)
      throws SQLException {
    QueryBuilder queryBuilder = new QueryBuilder(baseQuery, pagination);
    conditions.forEach(
        (condition, value) -> queryBuilder.addFixedCondition(condition + " = ?", value));
    return queryBuilder.addWhereClause().build(connection);
  }

  /**
   * Constructs a prepared statement by combining a base query with WHERE-, ORDER BY, and
   * LIMIT-OFFSET-clauses built from the given {@link PaginationDTO} object. The prepared statement
   * is ready-to-use, i.e., the filter values are already bound.
   *
   * @param baseQuery The base query string (e.g. {@code "SELECT * FROM table"}).
   * @param pagination The pagination object.
   * @return The prepared statement.
   */
  protected PreparedStatement buildQuery(String baseQuery, PaginationDTO pagination)
      throws SQLException {
    return new QueryBuilder(baseQuery, pagination)
        .addWhereClause()
        .addOrderBy()
        .addLimitOffset()
        .build(connection);
  }

  /**
   * Helper method to construct a prepared statement with fixed conditions.
   *
   * @param baseQuery The base query string.
   * @param pagination The pagination object.
   * @param conditions A map of fixed conditions (column name to value).
   * @return The prepared statement.
   */
  protected PreparedStatement buildQueryWithFixedConditions(
      String baseQuery, PaginationDTO pagination, Map<String, Object> conditions)
      throws SQLException {
    QueryBuilder queryBuilder = new QueryBuilder(baseQuery, pagination);
    conditions.forEach(
        (condition, value) -> queryBuilder.addFixedCondition(condition + " = ?", value));
    return queryBuilder.addWhereClause().addOrderBy().addLimitOffset().build(connection);
  }

  /**
   * Gets the database connection of this DAO.
   *
   * @return The database connection of this DAO.
   */
  protected Connection getConnection() {
    return connection;
  }

  /** Builder class for constructing prepared statements from pagination objects. */
  private static class QueryBuilder {
    private final StringBuilder sb;
    private final PaginationDTO pagination;
    private final List<Object> parameters;
    private boolean whereClauseAdded;

    QueryBuilder(String baseQuery, PaginationDTO pagination) {
      this.sb = new StringBuilder(baseQuery);
      this.pagination = pagination;
      this.parameters = new ArrayList<>();
      this.whereClauseAdded = baseQuery.contains("WHERE");
    }

    QueryBuilder addWhereClause() {
      if (!pagination.getFilters().isEmpty()) {
        Map<String, String> filters =
            pagination.getFilters().entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().trim().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!filters.isEmpty()) {
          appendWhereOrAnd();
          sb.append(
              filters.keySet().stream()
                  .map(s -> s + "::text ILIKE ?")
                  .collect(Collectors.joining(" AND ")));
          parameters.addAll(filters.values());
        }
      }
      return this;
    }

    void addFixedCondition(String condition, Object value) {
      appendWhereOrAnd();
      sb.append(condition);
      parameters.add(value);
    }

    QueryBuilder addOrderBy() {
      if (pagination.getSortBy() != null && !pagination.getSortBy().isEmpty()) {
        sb.append(" ORDER BY ")
            .append(pagination.getSortBy())
            .append(pagination.isAscending() ? " ASC" : " DESC");
      }
      return this;
    }

    QueryBuilder addLimitOffset() {
      if (pagination.getItemsPerPage() < Integer.MAX_VALUE) {
        int offset = (pagination.getPage() - 1) * pagination.getItemsPerPage();
        sb.append(" LIMIT ? OFFSET ?");
        parameters.add(pagination.getItemsPerPage());
        parameters.add(Math.max(0, offset));
      }
      return this;
    }

    private void appendWhereOrAnd() {
      if (whereClauseAdded) {
        sb.append(" AND ");
      } else {
        sb.append(" WHERE ");
        whereClauseAdded = true;
      }
    }

    PreparedStatement build(Connection connection) throws SQLException {
      sb.append(";");
      PreparedStatement stmt = connection.prepareStatement(sb.toString());
      for (int i = 0; i < parameters.size(); i++) {
        stmt.setObject(i + 1, parameters.get(i));
      }
      return stmt;
    }
  }
}
