package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.HelpDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import jakarta.annotation.PostConstruct;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Backing bean for the help panel.
 *
 * <p>This bean manages the state and behavior of the help panel within the application, including
 * toggling the visibility of help text.
 */
@Named
@ViewScoped
public class HelpBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = Logger.getLogger(HelpBean.class.getName());

  @Inject private UserSession userSession;

  @Inject private JFUtils jfUtils;

  private HelpDTO help;

  /** Default constructor. */
  public HelpBean() {
    // Default constructor
  }

  /**
   * Initializes the bean by creating a new {@link HelpDTO} instance.
   *
   * <p>This method is called after the bean's construction and dependency injection to set up the
   * initial state.
   */
  @PostConstruct
  public void init() {
    help = new HelpDTO();
    help.setText("");
    help.setVisible(false);
  }

  /**
   * Gets the {@link HelpDTO} instance.
   *
   * @return The {@link HelpDTO} instance containing help text and visibility state.
   */
  public HelpDTO getHelp() {
    return help;
  }

  /**
   * Toggles the visibility of the help panel.
   *
   * <p>This method changes the visibility state of the help text, allowing it to be shown or hidden
   * based on the current state.
   */
  public void toggleHelpText() {
    if (!help.isVisible()) {
      String currentUserRole = determineUserRole();
      String viewId = getCurrentViewId().orElse("failedHelp");
      String messageKey = generateMessageKey(viewId, currentUserRole);
      String helpText = jfUtils.getMessageFromResourceBundle(messageKey);
      while (helpText == null && !currentUserRole.equals("anonymous")) {
        currentUserRole = nextLowerUserRole(currentUserRole);
        messageKey = generateMessageKey(viewId, currentUserRole);
        helpText = jfUtils.getMessageFromResourceBundle(messageKey);
      }
      help.setText(
          Objects.requireNonNullElse(
              helpText, jfUtils.getMessageFromResourceBundle("h_failedHelp")));
    }
    help.setVisible(!help.isVisible());
  }

  /**
   * Determines the role of the current user.
   *
   * @return a string representing the user role.
   */
  private String determineUserRole() {
    if (userSession.isUserAdmin()) {
      return "admin";
    } else if (userSession.isUserLoggedIn()) {
      return "registered";
    } else {
      return "anonymous";
    }
  }

  /**
   * Retrieves the current view ID from the FacesContext.
   *
   * @return an Optional containing the view ID if present, otherwise an empty Optional.
   */
  private Optional<String> getCurrentViewId() {
    FacesContext context = FacesContext.getCurrentInstance();
    if (context == null) {
      LOGGER.warning("FacesContext is null, unable to retrieve view ID.");
      return Optional.empty();
    }
    UIViewRoot viewRoot = context.getViewRoot();
    return Optional.ofNullable(viewRoot)
        .map(UIViewRoot::getViewId)
        .map(id -> id.replace(".xhtml", ""));
  }

  /**
   * Generates a message key based on the view ID and user role.
   *
   * @param viewId the ID of the current view.
   * @param userRole the role of the current user.
   * @return a string representing the message key.
   */
  private String generateMessageKey(String viewId, String userRole) {
    if ("failedHelp".equals(viewId)) {
      return "h_failedHelp";
    }
    return "h_" + viewId + "_" + userRole;
  }

  /**
   * Gets the next lower user role for the given user role. If given the user role is anonymous, it
   * is returned as it is.
   *
   * @param userRole the user role.
   * @return the next lower user role.
   */
  private String nextLowerUserRole(String userRole) {
    return userRole.equals("admin") ? "registered" : "anonymous";
  }
}
