package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDTO;
import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import java.util.List;

/** Interface for data store accesses concerning advertisements. */
public interface AdvertisementDAO {

  /**
   * Fetches the total number of advertisements that match the filter constraints in {@code
   * pagination}.
   *
   * @param pagination The pagination information.
   * @return The number of advertisements that match the filter constraints.
   */
  int getCount(PaginationDTO pagination);

  /**
   * Fetches a list of advertisements. The results are filtered, sorted and paginated according to
   * the given pagination information.
   *
   * @param pagination The pagination information.
   * @return The list of advertisements.
   */
  List<AdvertisementDTO> getAdvertisements(PaginationDTO pagination);

  /**
   * Fetches a list of advertisements claimed by a user. The results are filtered, sorted and
   * paginated according to the given pagination information.
   *
   * @param user The user whose claimed advertisements are to be fetched. The user id must be set.
   * @param pagination The pagination information.
   * @return The list of advertisements.
   */
  List<AdvertisementDTO> getClaimedAdvertisementsOfUser(UserDTO user, PaginationDTO pagination);

  /**
   * Fetches a list of advertisements created by a user.
   *
   * @param user The user whose created advertisements are to be fetched.
   * @param pagination The pagination information.
   * @return The list of advertisements.
   */
  List<AdvertisementDTO> getCreatedAdvertisementsOfUser(UserDTO user, PaginationDTO pagination);

  /**
   * Fetches the total number of advertisements that have been claimed by the given user and match
   * the filter constraints in {@code pagination}.
   *
   * @param user The user. The user id must be set.
   * @param pagination The pagination information.
   * @return The number of matching advertisements.
   */
  int getClaimedAdvertisementsCount(UserDTO user, PaginationDTO pagination);

  /**
   * Fetches the total number of advertisements that have been created by the given user.
   *
   * @param user The user. The user id must be set.
   * @param pagination The pagination information.
   * @return The number of matching advertisements.
   */
  int getCreatedAdvertisementsCount(UserDTO user, PaginationDTO pagination);

  /**
   * Stores a new advertisement to the data store.
   *
   * @param advertisement The advertisement to store.
   * @return The id of the newly created advertisement.
   */
  long createAdvertisement(AdvertisementDetailsDTO advertisement);

  /**
   * Fetches detailed information about the advertisement with the given id.
   *
   * @param advertisement The advertisement to fetch. The id must be set.
   * @return The advertisement.
   */
  AdvertisementDetailsDTO getAdvertisementDetails(AdvertisementDetailsDTO advertisement);

  /**
   * Fetches the advertisement with the given id.
   *
   * @param advertisement The advertisement to fetch. The id must be set.
   * @return The advertisement.
   */
  AdvertisementDTO getAdvertisement(AdvertisementDTO advertisement);

  /**
   * Updates the given advertisement in the data store.
   *
   * @param advertisement The advertisement to update.
   */
  void updateAdvertisement(AdvertisementDetailsDTO advertisement);

  /**
   * Deletes the given advertisement from the data store.
   *
   * @param advertisement The advertisement to delete.
   */
  void deleteAdvertisement(AdvertisementDTO advertisement);
}
