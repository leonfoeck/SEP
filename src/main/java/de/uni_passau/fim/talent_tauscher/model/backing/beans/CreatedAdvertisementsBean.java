package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import de.uni_passau.fim.talent_tauscher.model.backing.util.PaginatedDataModel;
import de.uni_passau.fim.talent_tauscher.model.business.services.AdvertisementService;
import de.uni_passau.fim.talent_tauscher.model.business.services.SystemDataService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

/**
 * Backing bean for the list of advertisements created by a user.
 *
 * @author Leon Föckersperger
 */
@Named
@ViewScoped
public class CreatedAdvertisementsBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Inject private UserSession session;

  @Inject private AdvertisementService advertisementService;

  @Inject private SystemDataService systemDataService;

  private PaginatedDataModel<AdvertisementDTO> dataModel;

  /** Default constructor. */
  public CreatedAdvertisementsBean() {
    // Default constructor
  }

  /** Initializes the data model for the advertisements. */
  @PostConstruct
  public void init() {
    dataModel =
        new PaginatedDataModel<>(
            systemDataService.get().getSumPaginatedItems(), "date_of_publication") {
          @Override
          public Collection<AdvertisementDTO> fetchData() {
            UserDTO user = new UserDTO();
            user.setId(session.getCurrentUserId());
            return advertisementService.getCreatedAdvertisementsForUser(user, this.getPagination());
          }

          @Override
          public int fetchCount() {
            UserDTO user = new UserDTO();
            user.setId(session.getCurrentUserId());
            return advertisementService.getCreatedAdvertisementsCount(user, this.getPagination());
          }
        };
  }

  /**
   * Gets the data model used for the dynamic data table.
   *
   * @return The data model.
   */
  public PaginatedDataModel<AdvertisementDTO> getDataModel() {
    return dataModel;
  }
}
