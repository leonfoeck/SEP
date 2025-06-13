package de.uni_passau.fim.talent_tauscher.it.model.persistence.dataaccess.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDTO;
import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.ConnectionPool;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.TransactionFactory;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.AdvertisementDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.Transaction;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.EntityNotFoundException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.TransactionException;
import de.uni_passau.fim.talent_tauscher.it.util.TestUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Unit tests for PostgreSQLAdvertisement class.
 *
 * <p>This class tests the functionality of the PostgreSQL implementation of the AdvertisementDAO
 * interface.
 */
@SuppressWarnings("checkstyle:MagicNumber")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostgreSQLAdvertisementDAOIT {
  private static final AdvertisementDetailsDTO ADVERTISEMENT = new AdvertisementDetailsDTO();
  private static final UserDTO USER = new UserDTO();

  /** Initializes DTOs for testing before all tests. */
  @BeforeAll
  static void initDTOs() {
    TestUtils.load();
    ADVERTISEMENT.setTitle("Test Advertisement");
    ADVERTISEMENT.setDescription("Test Description");
    ADVERTISEMENT.setCost(10);
    ADVERTISEMENT.setStartDate(LocalDate.now());
    ADVERTISEMENT.setEndDate(LocalDate.now().plusDays(30));
    ADVERTISEMENT.setHidden(false);
    ADVERTISEMENT.setCity("Test City");
    ADVERTISEMENT.setCountry("Test Country");
    ADVERTISEMENT.setPostalCode("12345");
    ADVERTISEMENT.setStreet("Test Street");
    ADVERTISEMENT.setEmail("test@example.com");
    ADVERTISEMENT.setPhoneNumber("1234567890");
    ADVERTISEMENT.setStreetShown(true);
    ADVERTISEMENT.setNameShown(true);
    ADVERTISEMENT.setPhoneNumberShown(true);
    ADVERTISEMENT.setImage(new byte[] {1, 2, 3, 4});

    USER.setId(1L); // Assume this user already exists in the DB for simplicity
    ADVERTISEMENT.setUserId(USER.getId());
  }

  /** Cleans up the test data after all tests. */
  @AfterAll
  static void cleanUp() throws SQLException {
    try (Connection connection = ConnectionPool.getInstance().getConnection()) {
      try {
        PreparedStatement statement =
            connection.prepareStatement(
                "DELETE FROM ad WHERE title = 'Test Advertisement'");
        statement.executeUpdate();
        statement.close();
      } finally {
        ConnectionPool.getInstance().releaseConnection(connection);
      }
    }
    TestUtils.destroy();
  }

  /** Tests inserting an advertisement into the database. */
  @Test
  @Order(1)
  void testCreateAdvertisement() {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO advertisementDAO = transaction.getDAOFactory().createAdvertisementDAO();
      long advertisementId = advertisementDAO.createAdvertisement(ADVERTISEMENT);
      assertTrue(
          advertisementId > 0, "Advertisement should have been inserted and returned a valid ID.");
      ADVERTISEMENT.setId(advertisementId);
      transaction.commit();
    } catch (TransactionException e) {
      fail("Transaction failed: " + e.getMessage());
    }
  }

  /** Tests fetching an advertisement from the database. */
  @Test
  @Order(2)
  void testFetchAdvertisementDetails() {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO advertisementDAO = transaction.getDAOFactory().createAdvertisementDAO();
      AdvertisementDetailsDTO fetchedAdvertisement =
          advertisementDAO.getAdvertisementDetails(ADVERTISEMENT);
      assertNotNull(fetchedAdvertisement, "Fetched advertisement should not be null.");
      assertEquals(
          ADVERTISEMENT.getTitle(),
          fetchedAdvertisement.getTitle(),
          "Advertisement title should match.");
      transaction.commit();
    } catch (TransactionException e) {
      fail("Transaction failed: " + e.getMessage());
    }
  }

  /** Tests fetching advertisements with pagination. */
  @Test
  @Order(3)
  void testGetAdvertisements() {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO advertisementDAO = transaction.getDAOFactory().createAdvertisementDAO();
      PaginationDTO pagination = new PaginationDTO();
      pagination.setPage(1);
      pagination.setItemsPerPage(10);

      List<AdvertisementDTO> advertisements = advertisementDAO.getAdvertisements(pagination);
      assertNotNull(advertisements, "Advertisements should not be null.");
      assertFalse(advertisements.isEmpty(), "There should be at least one advertisement.");
      transaction.commit();
    } catch (TransactionException e) {
      fail("Transaction failed: " + e.getMessage());
    }
  }

  /** Tests fetching created advertisements for a user from the database. */
  @Test
  @Order(4)
  void testGetCreatedAdvertisementsOfUser() {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO advertisementDAO = transaction.getDAOFactory().createAdvertisementDAO();
      PaginationDTO pagination = new PaginationDTO();
      pagination.setPage(1);
      pagination.setItemsPerPage(10);

      List<AdvertisementDTO> createdAdvertisements =
          advertisementDAO.getCreatedAdvertisementsOfUser(USER, pagination);
      assertNotNull(createdAdvertisements, "Created advertisements should not be null.");
      assertFalse(
          createdAdvertisements.isEmpty(), "There should be at least one created advertisement.");
      transaction.commit();
    } catch (TransactionException e) {
      fail("Transaction failed: " + e.getMessage());
    }
  }

  /** Tests updating an advertisement in the database. */
  @Test
  @Order(5)
  void testUpdateAdvertisement() {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO advertisementDAO = transaction.getDAOFactory().createAdvertisementDAO();
      ADVERTISEMENT.setTitle("Updated Advertisement Title");
      advertisementDAO.updateAdvertisement(ADVERTISEMENT);
      AdvertisementDetailsDTO updatedAdvertisement =
          advertisementDAO.getAdvertisementDetails(ADVERTISEMENT);
      assertEquals(
          "Updated Advertisement Title",
          updatedAdvertisement.getTitle(),
          "Advertisement title should be updated.");
      transaction.commit();
    } catch (TransactionException e) {
      fail("Transaction failed: " + e.getMessage());
    }
  }

  /** Tests deleting an advertisement from the database. */
  @Test
  @Order(6)
  void testDeleteAdvertisement() {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      AdvertisementDAO advertisementDAO = transaction.getDAOFactory().createAdvertisementDAO();
      advertisementDAO.deleteAdvertisement(ADVERTISEMENT);
      AdvertisementDetailsDTO deletedAdvertisement = null;
      try {
        deletedAdvertisement = advertisementDAO.getAdvertisementDetails(ADVERTISEMENT);
      } catch (EntityNotFoundException e) {
        // Expected exception since the advertisement should be deleted
      }
      assertNull(deletedAdvertisement, "Advertisement should be deleted.");
      transaction.commit();
    } catch (TransactionException e) {
      fail("Transaction failed: " + e.getMessage());
    }
  }
}
