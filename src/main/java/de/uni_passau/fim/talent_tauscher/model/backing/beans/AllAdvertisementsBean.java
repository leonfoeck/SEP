package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDTO;
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
 * Backing bean for the advertisement overview screen.
 *
 * <p>This bean manages the state and behavior of the advertisement overview within the application,
 * including pagination and fetching advertisements from the corresponding service.
 *
 * @author Leon Föckersperger
 */
@Named
@ViewScoped
public class AllAdvertisementsBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Inject private AdvertisementService advertisementService;

  @Inject private SystemDataService systemDataService;

  private PaginatedDataModel<AdvertisementDTO> dataModel;

  /** Default constructor. */
  public AllAdvertisementsBean() {
    // Default constructor
  }

  /**
   * Initializes the data model for the data table.
   *
   * <p>This method is called after the bean's construction and dependency injection to set up the
   * initial state.
   */
  @PostConstruct
  public void init() {
    dataModel =
        new PaginatedDataModel<>(
            systemDataService.get().getSumPaginatedItems(), "date_of_publication") {
          @Override
          public Collection<AdvertisementDTO> fetchData() {
            return advertisementService.getAdvertisements(this.getPagination());
          }

          @Override
          public int fetchCount() {
            return advertisementService.getAdvertisementCount(this.getPagination());
          }
        };
  }

  /**
   * Gets the data model used for the dynamic data table.
   *
   * @return The data model containing the advertisements.
   */
  public PaginatedDataModel<AdvertisementDTO> getDataModel() {
    return dataModel;
  }
}
