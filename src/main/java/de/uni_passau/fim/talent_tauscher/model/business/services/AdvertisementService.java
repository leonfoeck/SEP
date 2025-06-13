package de.uni_passau.fim.talent_tauscher.model.business.services;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDTO;
import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.TransactionFactory;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.AdvertisementDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/** Manages interactions with {@link AdvertisementDAO}. */
@ApplicationScoped
public class AdvertisementService implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** Default constructor. */
  public AdvertisementService() {
    // Default constructor
  }

  /**
   * Retrieves a list of advertisements. The results are filtered, sorted and paginated according to
   * the given pagination information.
   *
   * @param pagination The pagination information.
   * @return The list of advertisements.
   * @author Leon Föckersperger
   */
  public List<AdvertisementDTO> getAdvertisements(PaginationDTO pagination) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO adDAO = transaction.getDAOFactory().createAdvertisementDAO();
      List<AdvertisementDTO> advertisements = adDAO.getAdvertisements(pagination);
      transaction.commit();
      return advertisements;
    }
  }

  /**
   * Retrieves the number of advertisements that match the filter criteria of the given {@link
   * PaginationDTO}. The concrete entries are not loaded.
   *
   * @param pagination The pagination information.
   * @return The number of matching advertisements.
   * @author Leon Föckersperger
   */
  public int getAdvertisementCount(PaginationDTO pagination) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO adDAO = transaction.getDAOFactory().createAdvertisementDAO();
      int advertisementsCount = adDAO.getCount(pagination);
      transaction.commit();
      return advertisementsCount;
    }
  }

  /**
   * Retrieves detailed information about the advertisement with a given id.
   *
   * @param advertisement The advertisement. The id must be set.
   * @return The advertisement.
   * @author Jakob Edmaier
   */
  public AdvertisementDetailsDTO getAdvertisementDetails(AdvertisementDetailsDTO advertisement) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO adDAO = transaction.getDAOFactory().createAdvertisementDAO();
      advertisement = adDAO.getAdvertisementDetails(advertisement);
      transaction.commit();
      return advertisement;
    }
  }

  /**
   * Retrieves a list of advertisements claimed by the given user. The results are filtered, sorted
   * and paginated according to the given pagination information.
   *
   * @param user The user. The id must be set.
   * @param pagination The pagination information.
   * @return The list of advertisements.
   * @author Jakob Edmaier
   */
  public List<AdvertisementDTO> getClaimedAdvertisementsForUser(
      UserDTO user, PaginationDTO pagination) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO adDAO = transaction.getDAOFactory().createAdvertisementDAO();
      List<AdvertisementDTO> advertisements =
          adDAO.getClaimedAdvertisementsOfUser(user, pagination);
      transaction.commit();
      return advertisements;
    }
  }

  /**
   * Retrieves the total number of advertisements that have been claimed by the given user and match
   * the filter constraints in {@code pagination}.
   *
   * @param user The user. The id must be set.
   * @param pagination The pagination information.
   * @return The number of matching advertisements.
   * @author Jakob Edmaier
   */
  public int getClaimedAdvertisementsCount(UserDTO user, PaginationDTO pagination) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO adDAO = transaction.getDAOFactory().createAdvertisementDAO();
      int advertisementsCount = adDAO.getClaimedAdvertisementsCount(user, pagination);
      transaction.commit();
      return advertisementsCount;
    }
  }

  /**
   * Retrieves a list of advertisements created by the given user. The results are filtered, sorted
   * and paginated according to the given pagination information.
   *
   * @param user The user. The id must be set.
   * @param pagination The pagination information.
   * @return The list of advertisements.
   * @author Leon Föckersperger
   */
  public List<AdvertisementDTO> getCreatedAdvertisementsForUser(
      UserDTO user, PaginationDTO pagination) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO adDAO = transaction.getDAOFactory().createAdvertisementDAO();
      List<AdvertisementDTO> advertisements =
          adDAO.getCreatedAdvertisementsOfUser(user, pagination);
      transaction.commit();
      return advertisements;
    }
  }

  /**
   * Retrieves the total number of advertisements created by the given user that match the filter
   * constraints in {@code pagination}.
   *
   * @param user The user. The id must be set.
   * @param pagination The pagination information.
   * @return The number of matching advertisements.
   * @author Leon Föckersperger
   */
  public int getCreatedAdvertisementsCount(UserDTO user, PaginationDTO pagination) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO adDAO = transaction.getDAOFactory().createAdvertisementDAO();
      int advertisementsCount = adDAO.getCreatedAdvertisementsCount(user, pagination);
      transaction.commit();
      return advertisementsCount;
    }
  }

  /**
   * Creates a new advertisement.
   *
   * @param advertisement The advertisement to create.
   * @return The id of the newly created advertisement.
   * @author Jakob Edmaier
   */
  public long create(AdvertisementDetailsDTO advertisement) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO adDAO = transaction.getDAOFactory().createAdvertisementDAO();
      long id = adDAO.createAdvertisement(advertisement);
      transaction.commit();
      return id;
    }
  }

  /**
   * Updates the given advertisement.
   *
   * @param advertisement The advertisement to update.
   * @author Jakob Edmaier
   */
  public void update(AdvertisementDetailsDTO advertisement) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO adDAO = transaction.getDAOFactory().createAdvertisementDAO();
      adDAO.updateAdvertisement(advertisement);
      transaction.commit();
    }
  }

  /**
   * Deletes the given advertisement.
   *
   * @param advertisement The advertisement to delete.
   * @author Jakob Edmaier
   */
  public void delete(AdvertisementDTO advertisement) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO adDAO = transaction.getDAOFactory().createAdvertisementDAO();
      adDAO.deleteAdvertisement(advertisement);
      transaction.commit();
    }
  }
}
