package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.model.business.services.AdvertisementService;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Backing bean for the advertisement details screen.
 *
 * @author Jakob Edmaier
 */
@Named
@ViewScoped
public class AdvertisementDetailsBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private UserDetailsDTO adCreator;

  private AdvertisementDetailsDTO advertisement;

  @Inject private AdvertisementService advertisementService;

  @Inject private UserService userService;

  /** Default constructor. */
  public AdvertisementDetailsBean() {
    // Default constructor
  }

  /** Initializes the bean by creating a new instance of {@link AdvertisementDetailsDTO}. */
  @PostConstruct
  public void init() {
    advertisement = new AdvertisementDetailsDTO();
    adCreator = new UserDetailsDTO();
  }

  /** Initializes the bean by loading the advertisement to be displayed. */
  public void loadData() {
    advertisement = advertisementService.getAdvertisementDetails(advertisement);
    adCreator.setId(advertisement.getUserId());
    adCreator = userService.getUserDetails(adCreator);
  }

  /**
   * Gets the displayed advertisement.
   *
   * @return The displayed advertisement.
   */
  public AdvertisementDetailsDTO getAdvertisement() {
    return advertisement;
  }

  /**
   * Gets the user who created the advertisement.
   *
   * @return The user who created the advertisement.
   */
  public UserDetailsDTO getAdCreator() {
    return adCreator;
  }

  /**
   * Checks whether the advertisement is currently available, i.e. if the advertisement is not
   * hidden and the current date lies within the availability period of the advertisement.
   *
   * @return Whether the advertisement is currently available.
   */
  public boolean isAdActive() {
    LocalDate today = LocalDate.now();
    return !advertisement.isHidden()
        && !advertisement.getStartDate().isAfter(today)
        && (advertisement.getEndDate() == null || !advertisement.getEndDate().isBefore(today));
  }
}
