package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDTO;
import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.ExtendedRequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import de.uni_passau.fim.talent_tauscher.model.backing.util.EmailBuilder;
import de.uni_passau.fim.talent_tauscher.model.business.services.AdvertisementService;
import de.uni_passau.fim.talent_tauscher.model.business.services.RequestService;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import de.uni_passau.fim.talent_tauscher.model.business.util.EmailDispatcher;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/** Backing bean for the page for writing requests. */
@Named
@ViewScoped
public class WriteRequestBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private ExtendedRequestDTO request;

  private UserDetailsDTO adCreator;

  private AdvertisementDetailsDTO ad;

  private boolean emailDispatchedForMail;

  private String oldEmail = "";

  @Inject private RequestService requestService;

  @Inject private AdvertisementService advertisementService;

  @Inject private UserService userService;

  @Inject private EmailBuilder emailBuilder;

  @Inject private EmailDispatcher emailDispatcher;

  @Inject private UserSession session;

  /** Default constructor. */
  public WriteRequestBean() {
    // Default constructor
  }

  /** Initializes the bean by creating instances of all used DAOs. */
  @PostConstruct
  public void init() {
    request = new ExtendedRequestDTO();
    adCreator = new UserDetailsDTO();
    ad = new AdvertisementDetailsDTO();
    emailDispatchedForMail = false;
  }

  /**
   * Loads the advertisement that is requested as well as the user who created the advertisement.
   */
  public void loadData() {
    ad = advertisementService.getAdvertisementDetails(ad);
    adCreator.setId(ad.getUserId());
    adCreator = userService.getUserDetails(adCreator);
    UserDetailsDTO currentUser = new UserDetailsDTO();
    currentUser.setId(session.getCurrentUserId());
    currentUser = userService.getUserDetails(currentUser);
    request.setRequestTimestamp(LocalDateTime.now());
    request.setAdvertisementId(ad.getId());
    request.setSenderId(currentUser.getId());
    request.setRequestCreatorUsername(currentUser.getNickname());
    request.setRecipientUsername(adCreator.getNickname());
    request.setAdvertisementTitle(ad.getTitle());
  }

  /**
   * Returns the advertisement.
   *
   * @return The advertisement.
   */
  public AdvertisementDTO getAd() {
    return ad;
  }

  /**
   * Returns the creator of the advertisement.
   *
   * @return The creator of the advertisement.
   */
  public UserDetailsDTO getAdCreator() {
    return adCreator;
  }

  /**
   * Returns the request object.
   *
   * @return The request object.
   */
  public ExtendedRequestDTO getRequest() {
    return request;
  }

  /**
   * Sends the request and navigates the user to the outgoing-requests page.
   *
   * @return The link to the outgoing-requests page.
   */
  public String submit() {
    sendEmail();
    request.setId(requestService.createRequest(request));
    return "/view/registered/outgoingRequests?faces-redirect=true";
  }

  /**
   * Returns if the form has been successfully submitted.
   *
   * @return If the form has been successfully submitted.
   */
  public boolean isEmailDispatchedForMail() {
    return emailDispatchedForMail;
  }

  private void sendEmail() {
    emailDispatchedForMail = adCreator.getEmail().equals(oldEmail);
    if (!emailDispatchedForMail) {
      oldEmail = adCreator.getEmail();
      emailDispatchedForMail = true;
      emailDispatcher.send(emailBuilder.buildIncomingRequestEmail(request, adCreator));
    }
  }
}
