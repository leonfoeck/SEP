package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.exceptions.UnauthorizedAccessException;
import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.AdvertisementService;
import de.uni_passau.fim.talent_tauscher.model.business.services.ResourceService;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.logging.Logger;
import org.omnifaces.util.Utils;

/**
 * Backing bean for the advertisement creation/editing screen.
 *
 * @author Jakob Edmaier
 */
@Named
@ViewScoped
public class EditAdvertisementBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private AdvertisementDetailsDTO advertisement;

  private transient Part imageInputPart;

  @Inject private AdvertisementService advertisementService;

  @Inject private UserService userService;

  @Inject private UserSession session;

  @Inject private transient Logger logger;

  @Inject private transient ExternalContext context;

  @Inject private JFUtils jfUtils;

  @Inject private ResourceService resourceService;

  /** Default constructor. */
  public EditAdvertisementBean() {
    // Default constructor
  }

  /** Initializes the bean by creating an instance of {@link AdvertisementDetailsDTO}. */
  @PostConstruct
  public void init() {
    advertisement = new AdvertisementDetailsDTO();
    advertisement.setImage(new byte[] {});
  }

  /** Loads the edited advertisement and fills the form. */
  public void loadData() {
    if (advertisement.getId() != 0) {
      // View param is set, so an existing advertisement is edited
      advertisement = advertisementService.getAdvertisementDetails(advertisement);
      if (advertisement.getUserId() != session.getCurrentUserId() && !session.isUserAdmin()) {
        throw new UnauthorizedAccessException("Unauthorized to edit this advertisement.");
      }
    } else {
      // A new advertisement is created
      advertisement.setUserId(session.getCurrentUserId());
      advertisement.setStartDate(LocalDate.now());

      // By default, the contact data is taken from the user profile.
      takeUserContactData();

      advertisement.setNameShown(true);
      advertisement.setStreetShown(true);
      advertisement.setPhoneNumberShown(true);
      setDefaultImage();
    }
  }

  /**
   * Gets the advertisement.
   *
   * @return The advertisement.
   */
  public AdvertisementDetailsDTO getAd() {
    return advertisement;
  }

  /**
   * Persists the changes to the data store. If that is successful, the user is redirected to the
   * list of created advertisements.
   *
   * @return The link to the list of created advertisements.
   */
  public String submit() {
    if (advertisement.getId() == 0) {
      long id = advertisementService.create(advertisement);
      logger.fine("created advertisement with id " + id);
    } else {
      advertisementService.update(advertisement);
      logger.fine("updated advertisement");
    }
    addMessage("m_updateSuccessMessage_editAdvertisement");
    return "/view/registered/createdAdvertisements?faces-redirect=true";
  }

  /** Deletes the advertisement image. */
  public void deleteImage() {
    setDefaultImage();
    imageInputPart = null;
  }

  private void setDefaultImage() {
    byte[] defaultImage =
        resourceService.getDefaultAdvertisementImage(context::getResourceAsStream);
    advertisement.setImage(defaultImage);
  }

  /**
   * Deletes the advertisement and redirects the user to the created-advertisements page.
   *
   * @return A redirect string to the createdAdvertisements page.
   */
  public String deleteAdvertisement() {
    advertisementService.delete(advertisement);
    addMessage("m_deleteSuccessMessage_editAdvertisement");
    return "/view/registered/createdAdvertisements?faces-redirect=true";
  }

  /**
   * Takes the profile data of the advertisement creator and sets the contact data of the
   * advertisement accordingly.
   */
  public void takeUserContactData() {
    UserDetailsDTO user = new UserDetailsDTO();
    user.setId(advertisement.getUserId());
    user = userService.getUserDetails(user);
    advertisement.setEmail(user.getEmail());
    advertisement.setPhoneNumber(user.getPhone());
    advertisement.setCountry(user.getCountry());
    advertisement.setCity(user.getCity());
    advertisement.setPostalCode(user.getPostalCode());
    advertisement.setStreet(user.getStreet());
  }

  /**
   * Sets the advertisement image to the image given in the {@link Part} object.
   *
   * @param inputFile The {@code Part} object containing the actual image.
   */
  public void setImageInputPart(Part inputFile) {
    if (inputFile != null && inputFile.getSubmittedFileName() != null) {
      try {
        imageInputPart = inputFile;
        advertisement.setImage(Utils.toByteArray(inputFile.getInputStream()));
      } catch (IOException e) {
        throw new RuntimeException("Failed to convert Part to byte array", e);
      }
    }
  }

  /**
   * Gets a {@link Part} proxy of the advertisement image.
   *
   * @return The proxy of the advertisement image.
   */
  public Part getImageInputPart() {
    return imageInputPart;
  }

  private void addMessage(String messageKey) {
    FacesContext fctx = FacesContext.getCurrentInstance();
    String message = jfUtils.getMessageFromResourceBundle(messageKey);
    fctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
    fctx.getExternalContext().getFlash().setKeepMessages(true);
  }
}
