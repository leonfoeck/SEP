package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.postgres;

import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.AdvertisementDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.DAOFactory;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.RequestDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.SystemDataDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.UserDAO;
import java.sql.Connection;

/**
 * PostgreSQL's implementation of the {@link DAOFactory} interface.
 *
 * @author Jakob Edmaier
 */
public class PostgreSQLDAOFactory implements DAOFactory {

  private final Connection connection;

  /**
   * Constructs a new {@link PostgreSQLDAOFactory}. All DAOs created by that instance will use the
   * given database connection.
   *
   * @param connection The database connection.
   */
  public PostgreSQLDAOFactory(Connection connection) {
    if (connection == null) {
      throw new IllegalArgumentException("Connection must not be null");
    }
    this.connection = connection;
  }

  /** {@inheritDoc} */
  public UserDAO createUserDAO() {
    return new PostgreSQLUserDAO(connection);
  }

  /** {@inheritDoc} */
  public AdvertisementDAO createAdvertisementDAO() {
    return new PostgreSQLAdvertisementDAO(connection);
  }

  /** {@inheritDoc} */
  public SystemDataDAO createSystemDataDAO() {
    return new PostgreSQLSystemDataDAO(connection);
  }

  /** {@inheritDoc} */
  public RequestDAO createRequestDAO() {
    return new PostgreSQLRequestDAO(connection);
  }
}
