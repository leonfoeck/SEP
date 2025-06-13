package de.uni_passau.fim.talent_tauscher.model.backing.util;

import de.uni_passau.fim.talent_tauscher.model.backing.session.UserSession;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;
import java.io.Serial;

/** Responsible for checking the permissions when a user navigates to another page. */
public class TrespassListener implements PhaseListener {
  @Serial private static final long serialVersionUID = 1L;

  public static final int MAX_URL_TOKENS = 3;

  /**
   * This method is called when a user navigates to a new page. When an authenticated user tries to
   * access a page without sufficient rights, he is redirected to a 404 page. If the user is not
   * authenticated, he is redirected to the login page.
   *
   * @param event The phase event.
   */
  @Override
  public void afterPhase(PhaseEvent event) {
    final FacesContext fctx = event.getFacesContext();
    final UIViewRoot viewRoot = fctx.getViewRoot();
    final String[] urlTokens = viewRoot.getViewId().substring(1).split("/");
    UserSession userSession = CDI.current().select(UserSession.class).get();

    if (urlTokens.length < MAX_URL_TOKENS || !urlTokens[0].equals("view")) {
      sendTo404(fctx);
    } else {
      switch (urlTokens[1]) {
        case "anonymous" -> {
          if ((urlTokens[2].equals("register.xhtml") || urlTokens[2].equals("login.xhtml"))
              && userSession.isUserLoggedIn()) {
            NavigationHandler navHandler = fctx.getApplication().getNavigationHandler();
            navHandler.handleNavigation(
                fctx, null, "/view/anonymous/allAdvertisements.xhtml?faces-redirect=true");
          }
        }
        case "registered" -> {
          if (!userSession.isUserLoggedIn()) {
            NavigationHandler navHandler = fctx.getApplication().getNavigationHandler();
            navHandler.handleNavigation(
                fctx, null, "/view/anonymous/login.xhtml?faces-redirect=true");
          }
        }
        case "admin" -> {
          if (!userSession.isUserLoggedIn() || !userSession.isUserAdmin()) {
            sendTo404(fctx);
          }
        }
        default -> sendTo404(fctx);
      }
    }
    PhaseListener.super.afterPhase(event);
  }

  /**
   * Not used.
   *
   * @param event Ignored.
   */
  @Override
  public void beforePhase(PhaseEvent event) {
    PhaseListener.super.beforePhase(event);
  }

  /**
   * Returns the phase id.
   *
   * @return {@code PhaseId.RESTORE_VIEW}
   */
  @Override
  public PhaseId getPhaseId() {
    return PhaseId.RESTORE_VIEW;
  }

  private void sendTo404(FacesContext fctx) {
    NavigationHandler navHandler = fctx.getApplication().getNavigationHandler();
    navHandler.handleNavigation(fctx, null, "/view/anonymous/error404.xhtml");
  }
}
