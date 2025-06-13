package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.ExtendedRequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import de.uni_passau.fim.talent_tauscher.model.backing.util.PaginatedDataModel;
import de.uni_passau.fim.talent_tauscher.model.business.services.RequestService;
import de.uni_passau.fim.talent_tauscher.model.business.services.SystemDataService;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

/** Backing bean for the list of incoming requests. */
@Named
@ViewScoped
public class IncomingRequestsBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Inject private UserSession session;

  @Inject private UserService userService;

  @Inject private RequestService requestService;

  @Inject private SystemDataService systemDataService;

  private PaginatedDataModel<ExtendedRequestDTO> dataModel;

  private UserDTO user;

  /** Default constructor. */
  public IncomingRequestsBean() {
    // Default constructor
  }

  /** Initializes the data model for the request list. */
  @PostConstruct
  public void init() {
    user = new UserDTO();
    user.setId(session.getCurrentUserId());
    user = userService.getUser(user);
    dataModel =
        new PaginatedDataModel<>(
            systemDataService.get().getSumPaginatedItems(), "timestamp_request") {
          @Override
          public Collection<ExtendedRequestDTO> fetchData() {
            return requestService.getIncomingRequestsForUser(user, this.getPagination());
          }

          @Override
          public int fetchCount() {
            return requestService.getIncomingRequestsCount(user, this.getPagination());
          }
        };

    // Make sorting order descending (not sure why two method calls are necessary)
    dataModel.setSorting("timestamp_request");
    dataModel.setSorting("timestamp_request");
  }

  /**
   * Returns the data model for the request list.
   *
   * @return The data model for the request list.
   */
  public PaginatedDataModel<ExtendedRequestDTO> getDataModel() {
    return dataModel;
  }

  /**
   * Gets the user for whom the incoming requests are displayed.
   *
   * @return The user.
   */
  public UserDTO getUser() {
    return user;
  }
}
