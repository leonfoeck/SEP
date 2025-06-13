package de.uni_passau.fim.talent_tauscher.model.backing.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Utility class for accessing strings from a resource bundle.
 *
 * @author Jakob Edmaier
 */
@ApplicationScoped
public class JFUtils implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Inject private transient Logger logger;

  /** Default constructor. */
  public JFUtils() {
    // Default constructor
  }

  /**
   * Returns the string corresponding to the given key from the resource bundle, using the locale of
   * the current request. If no message is found, {@code null} is returned.
   *
   * @param messageKey The message key.
   * @return The string from the resource bundle, or {@code null} if no string is found.
   */
  public String getMessageFromResourceBundle(String messageKey) {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    return getMessageFromResourceBundle(messageKey, facesContext.getELContext().getLocale());
  }

  /**
   * Returns the string corresponding to the given key and locale from the resource bundle. If no
   * message is found, {@code null} is returned.
   *
   * @param messageKey The message key.
   * @param locale The locale to use.
   * @return The string from the resource bundle, or {@code null} if no string is found.
   */
  public String getMessageFromResourceBundle(String messageKey, Locale locale) {
    try {
      ResourceBundle messages =
          ResourceBundle.getBundle(
              "de.uni_passau.fim.talent_tauscher.model.backing.i18n.messages", locale);
      return messages.getString(messageKey);
    } catch (MissingResourceException e) {
      logger.warning("Cannot find message for key " + messageKey);
      return null;
    }
  }
}
