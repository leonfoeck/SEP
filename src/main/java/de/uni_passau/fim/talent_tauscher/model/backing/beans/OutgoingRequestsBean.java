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

/** Backing bean for the list of outgoing requests. */
@Named
@ViewScoped
public class OutgoingRequestsBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Inject private UserSession session;

  @Inject private RequestService requestService;

  @Inject private UserService userService;

  @Inject private SystemDataService systemDataService;

  private UserDTO user;
  private PaginatedDataModel<ExtendedRequestDTO> dataModel;

  /** Default constructor. */
  public OutgoingRequestsBean() {
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
            return requestService.getOutgoingRequestsForUser(user, this.getPagination());
          }

          @Override
          public int fetchCount() {
            return requestService.getOutgoingRequestsCount(user, this.getPagination());
          }
        };
  }

  /**
   * Gets the data model used for the dynamic data table.
   *
   * @return The data model.
   */
  public PaginatedDataModel<ExtendedRequestDTO> getDataModel() {
    return dataModel;
  }

  /**
   * Gets the user for whom the outgoing requests are displayed.
   *
   * @return The user.
   */
  public UserDTO getUser() {
    return user;
  }
}
