package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces;

import de.uni_passau.fim.talent_tauscher.dto.ExtendedRequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.RequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import java.util.List;

/** Interface for data store accesses concerning requests. */
public interface RequestDAO {

  /**
   * Stores a new request to the data store.
   *
   * @param request The request to store.
   * @return The id of the newly created request.
   */
  long createRequest(RequestDTO request);

  /**
   * Updates the given request in the data store.
   *
   * @param request The request to update.
   */
  void updateRequest(RequestDTO request);

  /**
   * Fetches the request with a given id from the data store.
   *
   * @param request The request to fetch. The id must be set.
   * @return The request.
   */
  ExtendedRequestDTO getRequest(ExtendedRequestDTO request);

  /**
   * Fetches a list of incoming requests of the given user from the data store. The results are
   * filtered, sorted and paginated according to the given pagination information.
   *
   * @param user The user to fetch the requests for.
   * @param pagination The pagination information.
   * @return The list of requests.
   */
  List<ExtendedRequestDTO> getIncomingRequests(UserDTO user, PaginationDTO pagination);

  /**
   * Fetches the total number of incoming requests of the given user that match the filter
   * constraints in {@code pagination}.
   *
   * @param user The user to fetch the requests for.
   * @param pagination The pagination information.
   * @return The number of matching requests.
   */
  int getIncomingRequestsCount(UserDTO user, PaginationDTO pagination);

  /**
   * Fetches a list of outgoing requests of the given user from the data store. The results are
   * filtered, sorted and paginated according to the given pagination information.
   *
   * @param user The user to fetch the requests for.
   * @param pagination The pagination information.
   * @return The list of requests.
   */
  List<ExtendedRequestDTO> getOutgoingRequests(UserDTO user, PaginationDTO pagination);

  /**
   * Fetches the total number of outgoing requests of the given user that match the filter
   * constraints in {@code pagination}.
   *
   * @param user The user to fetch the requests for.
   * @param pagination The pagination information.
   * @return The number of matching requests.
   */
  int getOutgoingRequestsCount(UserDTO user, PaginationDTO pagination);
}
