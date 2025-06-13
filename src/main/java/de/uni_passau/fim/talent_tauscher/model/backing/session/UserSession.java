package de.uni_passau.fim.talent_tauscher.model.backing.session;

import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serial;
import java.io.Serializable;

/**
 * Manages the current user session and stores the id of the logged-in user.
 *
 * @author Jakob Edmaier
 */
@Named
@SessionScoped
public class UserSession implements Serializable {
  @Serial private static final long serialVersionUID = 1;

  @Inject private transient UserService userService;

  @Inject private transient HttpServletRequest request;

  private long userId;
  private boolean userAdmin;
  private boolean userLoggedIn;

  /**
   * Checks if a user is currently logged in.
   *
   * @return {@code true} if the user is logged in, {@code false} otherwise.
   */
  public boolean isUserLoggedIn() {
    return userLoggedIn;
  }

  /**
   * Fetches the current user from the data store.
   *
   * @return The current user.
   * @throws IllegalStateException If no user is logged in.
   */
  public UserAuthenticationDTO getCurrentUser() {
    if (!userLoggedIn) {
      throw new IllegalStateException("No user logged in.");
    }
    UserAuthenticationDTO user = new UserAuthenticationDTO();
    user.setId(userId);
    return userService.getAuthDataOfUser(user);
  }

  /**
   * Returns the id of the currently logged-in user.
   *
   * @return The id of the user.
   * @throws IllegalStateException If no user is logged in.
   */
  public long getCurrentUserId() {
    if (!userLoggedIn) {
      throw new IllegalStateException("No user logged in.");
    }
    return userId;
  }

  /**
   * Sets the specified user as the logged-in user.
   *
   * @param user The user.
   */
  public void login(UserAuthenticationDTO user) {
    userLoggedIn = true;
    userId = user.getId();
    userAdmin = user.isAdmin();
    request.changeSessionId();
  }

  /** Logs out the current user and invalidates the session cookie. */
  public void logout() {
    userId = 0;
    userLoggedIn = false;
    userAdmin = false;
    FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
  }

  /**
   * Checks if the current user has administrator rights.
   *
   * @return {@code true} if the user has administrator rights, {@code false} otherwise
   */
  public boolean isUserAdmin() {
    return userLoggedIn && userAdmin;
  }
}
