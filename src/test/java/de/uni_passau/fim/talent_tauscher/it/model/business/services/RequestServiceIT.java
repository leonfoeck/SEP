package de.uni_passau.fim.talent_tauscher.it.model.business.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDTO;
import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.ExtendedRequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.PasswordInputDTO;
import de.uni_passau.fim.talent_tauscher.dto.RequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.business.services.AdvertisementService;
import de.uni_passau.fim.talent_tauscher.model.business.services.RequestService;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import de.uni_passau.fim.talent_tauscher.model.business.util.PasswordAuthentication;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreAccessException;
import de.uni_passau.fim.talent_tauscher.it.util.TestUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link RequestService} class. */
@SuppressWarnings({"checkstyle:VisibilityModifier", "checkstyle:MagicNumber"})
@EnableWeld
class RequestServiceIT {

  /** Weld initiator, which initializes the Weld container and injects all necessary beans. */
  @WeldSetup
  WeldInitiator weld =
      WeldInitiator.from(
              RequestService.class,
              UserService.class,
              AdvertisementService.class,
              PasswordAuthentication.class,
              Logger.class,
              LoggerProducer.class)
          .activate(RequestScoped.class, ApplicationScoped.class)
          .build();

  @Inject private RequestService requestService;

  @Inject private UserService userService;

  @Inject private AdvertisementService advertisementService;

  @BeforeAll
  static void init() {
    TestUtils.load();
  }

  /**
   * A request cannot be accepted if the request creator no longer exists. This method test that in
   * this case, no talent points are transferred to the advertisement creator.
   */
  @Test
  void testAcceptRequestFailure() {
    // Init entities
    long requestCreatorId = initRequestCreator();
    long adCreatorId = initAdCreator();
    long adId = initAdvertisement(adCreatorId);
    long requestId = initRequest(requestCreatorId, adId);

    ExtendedRequestDTO request = new ExtendedRequestDTO();
    request.setId(requestId);
    ExtendedRequestDTO result = requestService.getRequest(request);
    result.setAccepted(true);
    result.setResponse("I accept.");
    userService.delete(userWithId(requestCreatorId));
    UserDTO advertisementCreatorBefore = userService.getUser(userWithId(adCreatorId));
    assertThrows(
        StoreAccessException.class,
        () -> {
          requestService.acceptRequest(result);
        });
    UserDTO advertisementCreatorAfter = userService.getUser(userWithId(adCreatorId));
    assertEquals(
        advertisementCreatorAfter.getTalentPoints(), advertisementCreatorBefore.getTalentPoints());

    // Delete entities from database
    userService.delete(userWithId(adCreatorId));
    userService.delete(userWithId(requestCreatorId));
    AdvertisementDTO advertisement = new AdvertisementDTO();
    advertisement.setId(adId);
    advertisementService.delete(advertisement);
  }

  private long initRequestCreator() {
    UserDetailsDTO user = new UserDetailsDTO();
    user.setNickname("maxmuster1");
    user.setEmail("max.mustermann1@example.com");
    user.setCity("Musterstadt");
    user.setStreet("Musterstraße");
    user.setPostalCode("12345");
    user.setHouseNumber("1");
    user.setCountry("Musterland");
    user.setFirstname("Max");
    user.setLastname("Mustermann");
    user.setAdminVerified(true);

    PasswordInputDTO passwordInput = new PasswordInputDTO();
    passwordInput.setPassword("12345");
    passwordInput.setRepetition("12345");
    return userService.create(user, passwordInput);
  }

  private long initAdCreator() {
    UserDetailsDTO user = new UserDetailsDTO();
    user.setNickname("erikamuster1");
    user.setEmail("erika.musterfrau1@example.com");
    user.setCity("Beispielstadt");
    user.setPostalCode("12345");
    user.setStreet("Beispielstraße");
    user.setHouseNumber("1");
    user.setCountry("Beispielland");
    user.setFirstname("Beispiel");
    user.setLastname("User");
    user.setAdminVerified(true);

    PasswordInputDTO passwordInput = new PasswordInputDTO();
    passwordInput.setPassword("12345");
    passwordInput.setRepetition("12345");
    return userService.create(user, passwordInput);
  }

  private long initAdvertisement(long adCreatorId) {
    AdvertisementDetailsDTO advertisement = new AdvertisementDetailsDTO();
    advertisement.setUserId(adCreatorId);
    advertisement.setTitle("Anzeige");
    advertisement.setCost(100);
    advertisement.setStartDate(LocalDate.now());
    advertisement.setCity("Beispielstadt");
    advertisement.setPostalCode("12345");
    advertisement.setCountry("Beispielland");
    advertisement.setEmail("beispiel@example.com");
    return advertisementService.create(advertisement);
  }

  private long initRequest(long requestCreatorId, long advertisementId) {
    RequestDTO request = new RequestDTO();
    request.setAdvertisementId(advertisementId);
    request.setSenderId(requestCreatorId);
    request.setRequest("I'd like to.");
    request.setRequestTimestamp(LocalDateTime.now());
    return requestService.createRequest(request);
  }

  private UserDTO userWithId(long id) {
    UserDTO user = new UserDTO();
    user.setId(id);
    return user;
  }

  @AfterAll
  static void destroy() {
    TestUtils.destroy();
  }
}
