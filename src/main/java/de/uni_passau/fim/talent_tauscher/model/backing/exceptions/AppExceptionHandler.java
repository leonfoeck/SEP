package de.uni_passau.fim.talent_tauscher.model.backing.exceptions;

import de.uni_passau.fim.talent_tauscher.dto.ErrorDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.exceptions.EmailException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.EntityNotFoundException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreDeleteException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreReadException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreUpdateException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreValidationException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.TransactionException;
import jakarta.el.ELException;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.FacesException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Responsible for handling fatal exceptions thrown in lower levels of the application and
 * redirecting the user to a suitable error page.
 *
 * @author Sturm
 */
public class AppExceptionHandler extends ExceptionHandlerWrapper {

  private FacesContext fctx;

  /**
   * Constructor for the AppExceptionHandler.
   *
   * @param wrapped the wrapped ExceptionHandler
   */
  public AppExceptionHandler(ExceptionHandler wrapped) {
    super(wrapped);
  }

  /**
   * Handles fatal exceptions by redirecting the user to a suitable error page and setting a
   * localized error message, depending on the type of the exception caught.
   */
  @Override
  public void handle() throws FacesException {
    fctx = FacesContext.getCurrentInstance();

    Iterator<ExceptionQueuedEvent> events = getUnhandledExceptionQueuedEvents().iterator();

    if (events.hasNext()) {
      ExceptionQueuedEvent event = events.next();
      events.remove();

      clearList(events);

      ExceptionQueuedEventContext context = event.getContext();
      Throwable exception = context.getException();

      exception = unpackException(exception);

      if (exception instanceof StoreDeleteException
          || exception instanceof StoreUpdateException
          || exception instanceof StoreValidationException
          || exception instanceof TransactionException
          || exception instanceof EmailException) {
        sendFacesMessage(exception);
      } else if (exception instanceof EntityNotFoundException
          || exception instanceof StoreReadException
          || exception instanceof UnauthorizedAccessException) {
        sendTo404();
      } else {
        sendToErrorPage(exception);
      }
    }
  }

  private Throwable unpackException(Throwable exception) {
    while ((exception instanceof FacesException || exception instanceof ELException)
        && exception.getCause() != null) {
      exception = exception.getCause();
    }
    return exception;
  }

  private void sendToErrorPage(Throwable exception) {
    JFUtils jfUtils = CDI.current().select(JFUtils.class).get();

    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setException(exception);
    errorDTO.setMessage(jfUtils.getMessageFromResourceBundle("v_critical_error"));
    errorDTO.setStacktrace(Arrays.toString(exception.getStackTrace()));

    ExternalContext extctx = fctx.getExternalContext();
    extctx.getFlash().put("errorDTO", errorDTO);

    NavigationHandler navHandler = fctx.getApplication().getNavigationHandler();
    navHandler.handleNavigation(fctx, null, "/view/anonymous/error.xhtml?faces-redirect=true");
  }

  private void sendTo404() {
    NavigationHandler navHandler = fctx.getApplication().getNavigationHandler();
    navHandler.handleNavigation(fctx, null, "/view/anonymous/error404.xhtml?faces-redirect=true");
  }

  private void sendFacesMessage(Throwable exception) {
    JFUtils jfUtils = CDI.current().select(JFUtils.class).get();
    String message;
    if (exception instanceof StoreDeleteException) {
      message = jfUtils.getMessageFromResourceBundle("v_delete_fail");
    } else if (exception instanceof StoreUpdateException) {
      message = jfUtils.getMessageFromResourceBundle("v_write_fail");
    } else if (exception instanceof StoreValidationException) {
      message = jfUtils.getMessageFromResourceBundle("v_validation_fail");
    } else if (exception instanceof EmailException) {
      message = jfUtils.getMessageFromResourceBundle("v_email_fail");
    } else {
      message = jfUtils.getMessageFromResourceBundle("v_other_fail");
    }
    fctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    fctx.getExternalContext().getFlash().setKeepMessages(true);
    fctx.getPartialViewContext().getRenderIds().add("messages");
  }

  private void clearList(Iterator<ExceptionQueuedEvent> events) {

    while (events.hasNext()) {
      events.next();
      events.remove();
    }
  }
}
