package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.AuthTokenDTO;
import de.uni_passau.fim.talent_tauscher.dto.LoginDTO;
import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.PasswordInputDTO;
import de.uni_passau.fim.talent_tauscher.dto.SystemDataDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.VerificationTokenDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import de.uni_passau.fim.talent_tauscher.model.backing.util.EmailBuilder;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.AdvertisementService;
import de.uni_passau.fim.talent_tauscher.model.business.services.RequestService;
import de.uni_passau.fim.talent_tauscher.model.business.services.ResourceService;
import de.uni_passau.fim.talent_tauscher.model.business.services.SystemDataService;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import de.uni_passau.fim.talent_tauscher.model.business.util.EmailDispatcher;
import de.uni_passau.fim.talent_tauscher.model.business.util.PasswordAuthentication;
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

/** Backing bean for the user creation/editing screen. */
@Named
@ViewScoped
public class EditUserBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private static final int DEFAULT_TALENT_POINTS = 50;

  private UserDetailsDTO user;

  private PasswordInputDTO passwordInput;

  private boolean wasAdmin;

  private transient Part avatarInputPart;

  private LoginDTO adminPassword;

  private SystemDataDTO systemData;

  private String oldEmail;

  private String originalMail;

  private boolean resentToOriginalEmail;

  private boolean emailDispatchedForMail;

  @Inject private UserService userService;

  @Inject private VerificationTokenGenerator tokenGenerator;

  @Inject private JFUtils jfUtils;

  @Inject private ResourceService resourceService;

  @Inject private AdvertisementService advertisementService;

  @Inject private EmailBuilder emailBuilder;

  @Inject private EmailDispatcher emailDispatcher;

  @Inject private PasswordAuthentication passwordAuthentication;

  @Inject private UserSession userSession;

  @Inject private SystemDataService systemDataService;

  @Inject private RequestService requestService;

  /** Default constructor. */
  public EditUserBean() {
    // Default constructor
  }

  /** Initializes the bean by creating an instance of {@link UserDetailsDTO}. */
  @PostConstruct
  public void init() {
    user = new UserDetailsDTO();
    passwordInput = new PasswordInputDTO();
    adminPassword = new LoginDTO();
    systemData = systemDataService.get();
  }

  /** Loads the user data and fills the form. */
  public void loadData() {
    if (user.getId() != 0) {
      user = userService.getUserDetails(user);
      wasAdmin = user.isAdmin();
      originalMail = user.getEmail();
    } else {
      user.setAvatar(
          resourceService.getDefaultAvatarImage(
              FacesContext.getCurrentInstance().getExternalContext()::getResourceAsStream));
      wasAdmin = false;
      user.setAdminVerified(true);
      user.setTalentPoints(DEFAULT_TALENT_POINTS);
      originalMail = "";
    }
    oldEmail = originalMail;
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
   * Gets the password input.
   *
   * @return The password input.
   */
  public PasswordInputDTO getPasswordInput() {
    return passwordInput;
  }

  /** Deletes the avatar image of the user. */
  public void deleteAvatar() {
    user.setAvatar(
        resourceService.getDefaultAvatarImage(
            FacesContext.getCurrentInstance().getExternalContext()::getResourceAsStream));
    avatarInputPart = null;
  }

  /**
   * Persists the changes and redirects the admin to the user administration page.
   *
   * @return The link to the user administration page.
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

    if (!wasAdmin
        && user.isAdmin()
        && !checkAdminPassword()) { // check admin password when making user admin
      return null;
    }

    VerificationTokenDTO token =
        new VerificationTokenDTO(user.getEmail(), tokenGenerator.generateToken());
    emailDispatchedForMail = user.getEmail().equals(oldEmail);

    if (shouldSendMail()) {
      oldEmail = user.getEmail();
      emailDispatchedForMail = true;
      emailDispatcher.send(emailBuilder.buildVerificationEmail(user, token));
    }

    if (user.getId() != 0) { // update existing user
      if (userService.update(user, passwordInput)) { // update successful
        userService.putEmailVerificationToken(token);
        String message = jfUtils.getMessageFromResourceBundle("f_changes_saved");
        FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
      } else { // update failed
        String message = jfUtils.getMessageFromResourceBundle("v_other_fail");
        FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        return null;
      }
    } else { // create new user
      if (userService.create(user, passwordInput) != -1L) { // creation successful
        userService.putEmailVerificationToken(token);
        String message = jfUtils.getMessageFromResourceBundle("m_user_created");
        FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
      } else { // creation failed
        String message = jfUtils.getMessageFromResourceBundle("v_validation_fail");
        FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        return null;
      }
    }
    return "/view/admin/userAdministration.xhtml?faces-redirect=true";
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
    String message = jfUtils.getMessageFromResourceBundle("f_confirmDeleteMessage_profile");
    FacesContext.getCurrentInstance()
        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
    return "/view/admin/userAdministration.xhtml?faces-redirect=true";
  }

  /**
   * Returns a warning String containing how many Ads will be deleted.
   *
   * @return A warning String containing how many Ads will be deleted.
   */
  public String getDeleteWarning() {
    int createdAds = advertisementService.getCreatedAdvertisementsCount(user, new PaginationDTO());
    int sentRequests = requestService.getOutgoingRequestsCount(user, new PaginationDTO());
    String warning = jfUtils.getMessageFromResourceBundle("f_confirmDeleteMessage_editUser");
    return String.format(warning, createdAds, sentRequests);
  }

  /**
   * Gets a LoginDTO to check the admin passwort.
   *
   * @return The LoginDTO to check the admin passwort.
   */
  public LoginDTO getAdminPassword() {
    return adminPassword;
  }

  private boolean checkAdminPassword() {
    UserAuthenticationDTO adminUser = new UserAuthenticationDTO();
    adminUser.setId(userSession.getCurrentUserId());
    AuthTokenDTO adminAuthToken = userService.getAuthDataOfUser(adminUser).getAuthToken();
    if (!passwordAuthentication.authenticate(adminPassword.getPassword(), adminAuthToken)) {
      String message = jfUtils.getMessageFromResourceBundle("f_admin_passwort_invalid");
      FacesContext.getCurrentInstance()
          .addMessage(
              "userInput:confirmationPassword",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
      return false;
    } else {
      return true;
    }
  }

  /**
   * Gets the system configuration data.
   *
   * @return The system configuration data.
   */
  public SystemDataDTO getSystemData() {
    return systemData;
  }

  /**
   * Returns whether the admin password field has to be shown and needs to be filled.
   *
   * @return If the admin password field has to be shown and needs to be filled.
   */
  public boolean isAdminPasswordRequired() {
    return !wasAdmin && user.isAdmin();
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

  private boolean shouldSendMail() {
    if (!user.isEmailVerified()) {
      if (user.getEmail().equals(originalMail)) {
        if (!resentToOriginalEmail) {
          resentToOriginalEmail = true;
          return true;
        } else {
          return false;
        }
      } else {
        return !emailDispatchedForMail;
      }
    } else {
      return false;
    }
  }
}
