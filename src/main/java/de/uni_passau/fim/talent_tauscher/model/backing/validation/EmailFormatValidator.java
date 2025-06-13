package de.uni_passau.fim.talent_tauscher.model.backing.validation;

import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.regex.Pattern;

/**
 * Validator ensuring that a string is in a valid email address format. This regex covers most valid
 * email formats as per RFC 5322.
 *
 * @author Sturm
 */
@Named
@Dependent
@FacesValidator(value = "emailFormatValidator", managed = true)
public class EmailFormatValidator implements Validator<String> {

  @Inject private JFUtils jfUtils;

  private static final String EMAIL_REGEX =
      "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\."
          + "[a-zA-Z0-9-]+)*$";

  /** Default constructor. */
  public EmailFormatValidator() {
    // Default constructor
  }

  /**
   * Validates that the given email string matches the required email format.
   *
   * @param fctx the FacesContext
   * @param component the UIComponent
   * @param email the email string to validate
   * @throws ValidatorException if the email format is invalid
   */
  @Override
  public void validate(final FacesContext fctx, final UIComponent component, final String email)
      throws ValidatorException {
    if (email == null || email.isEmpty()) {
      String message = jfUtils.getMessageFromResourceBundle("v_email_empty");
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    }

    if (!Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
      String message = jfUtils.getMessageFromResourceBundle("v_email_wrong_format");
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    }
  }
}
