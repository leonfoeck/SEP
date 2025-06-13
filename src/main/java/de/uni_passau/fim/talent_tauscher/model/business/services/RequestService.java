package de.uni_passau.fim.talent_tauscher.model.business.services;

import de.uni_passau.fim.talent_tauscher.dto.ExtendedRequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.RequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.TransactionFactory;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.DAOFactory;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.RequestDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.Transaction;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.UserDAO;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/** Manages interactions with {@link RequestDAO}. */
@ApplicationScoped
public class RequestService implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Retrieves the request with the specified id.
   *
   * @param request The request to fetch. The id must be set.
   * @return The request.
   * @author Jakob Edmaier
   */
  public ExtendedRequestDTO getRequest(ExtendedRequestDTO request) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      RequestDAO requestDAO = transaction.getDAOFactory().createRequestDAO();
      ExtendedRequestDTO result = requestDAO.getRequest(request);
      transaction.commit();
      return result;
    }
  }

  /**
   * Creates a new request.
   *
   * @param request The request.
   * @return The id of the newly created request.
   */
  public long createRequest(RequestDTO request) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      RequestDAO requestDAO = transaction.getDAOFactory().createRequestDAO();
      long id = requestDAO.createRequest(request);
      transaction.commit();
      return id;
    }
  }

  /**
   * Updates the given request.
   *
   * @param request The request to update.
   */
  public void rejectRequest(RequestDTO request) {
    request.setAccepted(false);
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      RequestDAO requestDAO = transaction.getDAOFactory().createRequestDAO();
      requestDAO.updateRequest(request);
      transaction.commit();
    }
  }

  /**
   * Accepts the given request and transfers the talent points from the requesting user to the ad
   * provider.
   *
   * @param request The request to update.
   */
  public void acceptRequest(RequestDTO request) {
    request.setAccepted(true);
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      DAOFactory daoFactory = transaction.getDAOFactory();
      RequestDAO requestDAO = daoFactory.createRequestDAO();
      UserDAO userDAO = daoFactory.createUserDAO();
      requestDAO.updateRequest(request);
      userDAO.transferTalentPoints(request);
      transaction.commit();
    }
  }

  /**
   * Retrieves a list of incoming requests of a user. The results are filtered, sorted and paginated
   * according to the given pagination information.
   *
   * @param user The user. The id must be set.
   * @param pagination The pagination information.
   * @return The list of requests.
   */
  public List<ExtendedRequestDTO> getIncomingRequestsForUser(
      UserDTO user, PaginationDTO pagination) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      RequestDAO requestDAO = transaction.getDAOFactory().createRequestDAO();
      List<ExtendedRequestDTO> result = requestDAO.getIncomingRequests(user, pagination);
      transaction.commit();
      return result;
    }
  }

  /**
   * Retrieves a list of outgoing requests of a user. The results are filtered, sorted and paginated
   * according to the given pagination information.
   *
   * @param user The user. The id must be set.
   * @param pagination The pagination information
   * @return The list of requests.
   */
  public List<ExtendedRequestDTO> getOutgoingRequestsForUser(
      UserDTO user, PaginationDTO pagination) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      RequestDAO requestDAO = transaction.getDAOFactory().createRequestDAO();
      List<ExtendedRequestDTO> result = requestDAO.getOutgoingRequests(user, pagination);
      transaction.commit();
      return result;
    }
  }

  /**
   * Retrieves the total number of outgoing requests of a user that match the filter constraints in
   * {@code pagination}.
   *
   * @param user The user. The id must be set.
   * @param pagination The pagination information.
   * @return The number of matching requests.
   */
  public int getOutgoingRequestsCount(UserDTO user, PaginationDTO pagination) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      RequestDAO requestDAO = transaction.getDAOFactory().createRequestDAO();
      int count = requestDAO.getOutgoingRequestsCount(user, pagination);
      transaction.commit();
      return count;
    }
  }

  /**
   * Retrieves the total number of incoming requests of a user that match the filter constraints in
   * {@code pagination}.
   *
   * @param user The user. The id must be set.
   * @param pagination The pagination information.
   * @return The number of matching requests.
   */
  public int getIncomingRequestsCount(UserDTO user, PaginationDTO pagination) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      RequestDAO requestDAO = transaction.getDAOFactory().createRequestDAO();
      int count = requestDAO.getIncomingRequestsCount(user, pagination);
      transaction.commit();
      return count;
    }
  }
}
