package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.PasswordInputDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.VerificationTokenDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import de.uni_passau.fim.talent_tauscher.model.backing.util.EmailBuilder;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.AdvertisementService;
import de.uni_passau.fim.talent_tauscher.model.business.services.RequestService;
import de.uni_passau.fim.talent_tauscher.model.business.services.ResourceService;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import de.uni_passau.fim.talent_tauscher.model.business.util.EmailDispatcher;
import de.uni_passau.fim.talent_tauscher.model.business.util.VerificationTokenGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import org.omnifaces.util.Utils;

/**
 * Backing bean for the user profile page.
 *
 * @author Sturm
 */
@Named
@ViewScoped
public class ProfileBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private UserDetailsDTO user;

  private PasswordInputDTO passwordInput;

  private transient Part avatarInputPart;

  private String oldEmail;

  private boolean emailDispatchedForMail;

  private String originalMail;

  @Inject private UserSession userSession;

  @Inject private UserService userService;

  @Inject private VerificationTokenGenerator tokenGenerator;

  @Inject private JFUtils jfUtils;

  @Inject private ResourceService resourceService;

  @Inject private AdvertisementService advertisementService;

  @Inject private EmailBuilder emailBuilder;

  @Inject private EmailDispatcher emailDispatcher;

  @Inject private RequestService requestService;

  /** Default constructor. */
  public ProfileBean() {
    // Default constructor
  }

  /**
   * Initializes the bean by retrieving the user data and filling the form and system settings data
   * for admin.
   */
  @PostConstruct
  public void init() {
    UserDetailsDTO user = new UserDetailsDTO();
    user.setId(userSession.getCurrentUserId());
    this.user = userService.getUserDetails(user);
    this.passwordInput = new PasswordInputDTO();
    this.oldEmail = user.getEmail();
    this.originalMail = user.getEmail();
    this.emailDispatchedForMail = false;
  }

  /**
   * Gets the user.
   *
   * @return The user.
   */
  public UserDetailsDTO getUser() {
    return user;
  }

  /**
   * Returns the password input.
   *
   * @return The password input.
   */
  public PasswordInputDTO getPasswordInput() {
    return passwordInput;
  }

  /** Deletes the avatar image. */
  public void deleteAvatar() {
    user.setAvatar(
        resourceService.getDefaultAvatarImage(
            FacesContext.getCurrentInstance().getExternalContext()::getResourceAsStream));
    avatarInputPart = null;
  }

  /**
   * Submits the changes and redirects the user to the created-advertisements page.
   *
   * @return The link to the created-advertisements page.
   */
  public String submit() {
    if (!passwordInput
        .getPassword()
        .equals(passwordInput.getRepetition())) { // passwords don't match
      String message = jfUtils.getMessageFromResourceBundle("v_passwords_dont_match");
      FacesContext.getCurrentInstance()
          .addMessage(
              "userInput:password",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
      return null;
    }

    VerificationTokenDTO token =
        new VerificationTokenDTO(user.getEmail(), tokenGenerator.generateToken());
    emailDispatchedForMail = user.getEmail().equals(oldEmail);

    if (!emailDispatchedForMail && !user.getEmail().equals(originalMail)) {
      oldEmail = user.getEmail();
      emailDispatchedForMail = true;
      emailDispatcher.send(emailBuilder.buildVerificationEmail(user, token));
    }

    if (userService.update(user, passwordInput)) {
      userService.putEmailVerificationToken(token);
      String message = jfUtils.getMessageFromResourceBundle("f_changes_saved");
      FacesContext.getCurrentInstance()
          .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
      return "/view/registered/createdAdvertisements.xhtml?faces-redirect=true";
    } else {
      String message = jfUtils.getMessageFromResourceBundle("v_other_fail");
      FacesContext.getCurrentInstance()
          .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
      return null;
    }
  }

  /**
   * Takes the Part object from the file input, converts it to a byte array and sets it in the
   * UserDetailsDTO.
   *
   * @param inputFile The uploaded avatar image.
   */
  public void setAvatarInputPart(Part inputFile) {
    if (inputFile != null && inputFile.getSubmittedFileName() != null) {
      try {
        user.setAvatar(Utils.toByteArray(inputFile.getInputStream()));
      } catch (IOException e) {
        String message = jfUtils.getMessageFromResourceBundle("v_other_fail");
        FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
      }
    }
  }

  /**
   * Returns a Part Object for the avatar file input.
   *
   * @return A Part Object for the avatar file input.
   */
  public Part getAvatarInputPart() {
    return avatarInputPart;
  }

  /**
   * Logs out the user, deletes the user account and redirects the (now anonymous) user to the
   * all-advertisements page.
   *
   * @return The link to the all-advertisements page.
   */
  public String deleteUser() {
    userService.delete(user);
    userSession.logout();
    String message = jfUtils.getMessageFromResourceBundle("f_confirmDeleteMessage_profile");
    FacesContext.getCurrentInstance()
        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
    return "/view/anonymous/allAdvertisements?faces-redirect=true";
  }

  /**
   * Returns a warning String containing how many Ads will be deleted.
   *
   * @return A warning String containing how many Ads will be deleted.
   */
  public String getDeleteWarning() {
    int createdAds = advertisementService.getCreatedAdvertisementsCount(user, new PaginationDTO());
    int sentRequests = requestService.getOutgoingRequestsCount(user, new PaginationDTO());
    String warning = jfUtils.getMessageFromResourceBundle("f_confirmDeleteMessage_profile");
    return String.format(warning, createdAds, sentRequests);
  }

  /**
   * Get whether it was tried to dispatch the verification mail.
   *
   * @return If it was tried to dispatch the verification mail.
   */
  public boolean isEmailDispatched() {
    return emailDispatchedForMail;
  }

  /** Notify the backing Bean that the email input has been updated. */
  public void emailInputUpdated() {
    emailDispatchedForMail =
        oldEmail.equals(user.getEmail()) && originalMail.equals(user.getEmail());
  }

  /**
   * Return the Email-Address of the user when the page was loaded.
   *
   * @return The Email-Address of the user when the page was loaded.
   */
  public String getOriginalMail() {
    return originalMail;
  }
}
