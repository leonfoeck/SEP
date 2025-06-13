package de.uni_passau.fim.talent_tauscher.it.model.backing.beans;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.EmailDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.beans.WriteRequestBean;
import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import de.uni_passau.fim.talent_tauscher.model.backing.util.EmailBuilder;
import de.uni_passau.fim.talent_tauscher.model.business.services.AdvertisementService;
import de.uni_passau.fim.talent_tauscher.model.business.services.RequestService;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import de.uni_passau.fim.talent_tauscher.model.business.util.EmailDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("checkstyle:MagicNumber")
@ExtendWith(MockitoExtension.class)
class WriteRequestBeanIT {

  @Mock private RequestService requestService;
  @Mock private AdvertisementService advertisementService;
  @Mock private UserService userService;
  @Mock private EmailBuilder emailBuilder;
  @Mock private EmailDispatcher emailDispatcher;
  @Mock private UserSession session;

  @InjectMocks private WriteRequestBean writeRequestBean;

  @BeforeEach
  public void setUp() {
    writeRequestBean.init();
  }

  @Test
  void testInit() {
    assertNotNull(writeRequestBean.getRequest());
    assertNotNull(writeRequestBean.getAdCreator());
    assertNotNull(writeRequestBean.getAd());
  }

  @Test
  void testLoadData() {
    // Arrange
    AdvertisementDetailsDTO adDetails = new AdvertisementDetailsDTO();
    adDetails.setId(1L);
    adDetails.setUserId(2L);
    adDetails.setTitle("Test Ad");

    UserDetailsDTO adCreator = new UserDetailsDTO();
    adCreator.setId(2L);
    adCreator.setNickname("adCreator");
    adCreator.setEmail("mustermann@example.com");

    UserDetailsDTO currentUser = new UserDetailsDTO();
    currentUser.setId(3L);
    currentUser.setNickname("currentUser");

    when(advertisementService.getAdvertisementDetails(any())).thenReturn(adDetails);
    when(userService.getUserDetails(any(UserDetailsDTO.class)))
        .thenReturn(adCreator)
        .thenReturn(currentUser);
    when(session.getCurrentUserId()).thenReturn(3L);

    // Act
    writeRequestBean.loadData();

    // Assert
    assertEquals("Test Ad", writeRequestBean.getRequest().getAdvertisementTitle());
    assertEquals("currentUser", writeRequestBean.getRequest().getRequestCreatorUsername());
    assertEquals("adCreator", writeRequestBean.getRequest().getRecipientUsername());
  }

  @Test
  void testSubmit() {
    // Arrange
    when(requestService.createRequest(any())).thenReturn(1L);

    // Act
    String result = writeRequestBean.submit();

    // Assert
    assertEquals("/view/registered/outgoingRequests?faces-redirect=true", result);
    verify(emailDispatcher, times(0)).send(any());
  }

  @Test
  void testSendEmail() {
    // Arrange
    AdvertisementDetailsDTO adDetails = new AdvertisementDetailsDTO();
    adDetails.setId(1L);
    adDetails.setUserId(2L);
    adDetails.setTitle("Test Ad");

    UserDetailsDTO adCreator = new UserDetailsDTO();
    adCreator.setId(2L);
    adCreator.setNickname("adCreator");
    adCreator.setEmail("recipient@example.com");

    UserDetailsDTO currentUser = new UserDetailsDTO();
    currentUser.setId(3L);
    currentUser.setNickname("currentUser");

    when(advertisementService.getAdvertisementDetails(any())).thenReturn(adDetails);
    when(userService.getUserDetails(any(UserDetailsDTO.class)))
        .thenReturn(adCreator)
        .thenReturn(currentUser);
    when(session.getCurrentUserId()).thenReturn(3L);
    when(emailBuilder.buildIncomingRequestEmail(any(), any())).thenReturn(new EmailDTO());

    // Act
    writeRequestBean.loadData();
    writeRequestBean.submit();

    // Assert
    verify(emailDispatcher, times(1)).send(any());
  }
}
