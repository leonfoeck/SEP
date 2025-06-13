package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.EmailDTO;
import de.uni_passau.fim.talent_tauscher.dto.ExtendedRequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.exceptions.UnauthorizedAccessException;
import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import de.uni_passau.fim.talent_tauscher.model.backing.util.EmailBuilder;
import de.uni_passau.fim.talent_tauscher.model.business.exceptions.EmailException;
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

/**
 * Backing bean for the page for writing responses.
 *
 * @author Jakob Edmaier
 */
@Named
@ViewScoped
public class WriteResponseBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private ExtendedRequestDTO request;
  private AdvertisementDetailsDTO advertisement;
  private UserDetailsDTO requestCreator;

  @Inject private UserSession userSession;

  private boolean emailDispatchedForMail;

  private String oldEmail;

  @Inject private RequestService requestService;

  @Inject private AdvertisementService advertisementService;

  @Inject private UserService userService;

  @Inject private EmailBuilder emailBuilder;

  @Inject private EmailDispatcher emailDispatcher;

  /** Default constructor. */
  public WriteResponseBean() {
    // Default constructor
  }

  /** Initializes the bean by creating instances of all used DTOs. */
  @PostConstruct
  public void init() {
    request = new ExtendedRequestDTO();
    advertisement = new AdvertisementDetailsDTO();
    requestCreator = new UserDetailsDTO();
    emailDispatchedForMail = false;
  }

  /** Retrieves the request identified by the view parameter. */
  public void loadData() {
    request = requestService.getRequest(request);
    advertisement.setId(request.getAdvertisementId());
    advertisement = advertisementService.getAdvertisementDetails(advertisement);

    if (advertisement.getUserId() != userSession.getCurrentUserId() && !userSession.isUserAdmin()) {
      throw new UnauthorizedAccessException("Cannot respond to requests of other users.");
    }

    requestCreator.setId(request.getSenderId());
    requestCreator = userService.getUserDetails(requestCreator);
  }

  /**
   * Gets the creator of the request.
   *
   * @return The creator of the request.
   */
  public UserDetailsDTO getRequestCreator() {
    return requestCreator;
  }

  /**
   * Checks if an email has been dispatched for the mail.
   *
   * @return True if an email has been dispatched for the mail, false otherwise.
   */
  public boolean isEmailDispatchedForMail() {
    return emailDispatchedForMail;
  }

  /**
   * Gets the request.
   *
   * @return The request.
   */
  public ExtendedRequestDTO getRequest() {
    return request;
  }

  /**
   * Gets the advertisement.
   *
   * @return The advertisement.
   */
  public AdvertisementDetailsDTO getAdvertisement() {
    return advertisement;
  }

  /**
   * Accepts the request and sends the response text. Redirects the user to the incoming-requests
   * page.
   *
   * @return The link to the incoming-requests page.
   */
  public String accept() {
    assert request.getAccepted() == null;
    request.setAccepted(true);
    sendEmail();
    requestService.acceptRequest(request);
    return "/view/registered/incomingRequests?faces-redirect=true";
  }

  /**
   * Rejects the request and sends the response text. Redirects the user to the incoming-requests
   * page.
   *
   * @return The link to the incoming-requests page.
   */
  public String reject() {
    assert request.getAccepted() == null;
    request.setAccepted(false);
    sendEmail();
    requestService.rejectRequest(request);
    return "/view/registered/incomingRequests?faces-redirect=true";
  }

  private void sendEmail() {
    emailDispatchedForMail = requestCreator.getEmail().equals(oldEmail);
    if (!emailDispatchedForMail) {
      oldEmail = requestCreator.getEmail();
      emailDispatchedForMail = true;
      EmailDTO email = emailBuilder.buildIncomingResponseEmail(request, requestCreator);
      try {
        emailDispatcher.send(email);
      } catch (EmailException e) {
        request.setAccepted(null);
        throw e;
      }
    }
  }
}
