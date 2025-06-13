package de.uni_passau.fim.talent_tauscher.model.backing.validation;

import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Validator ensuring that an email address is unique among all users.
 *
 * @author Sturm
 */
@Named
@Dependent
@FacesValidator(value = "emailUniqueValidator", managed = true)
public class EmailUniqueValidator implements Validator<String> {

  @Inject private UserService userService;

  @Inject private JFUtils jfUtils;

  /** Default constructor. */
  public EmailUniqueValidator() {
    // Default constructor
  }

  /** {@inheritDoc} */
  @Override
  public void validate(final FacesContext fctx, final UIComponent component, final String email)
      throws ValidatorException {
    String oldValue = component.getValueExpression("originalMail").getValue(fctx.getELContext());
    if (!(oldValue != null && oldValue.equalsIgnoreCase(email))) {
      UserAuthenticationDTO user = new UserAuthenticationDTO();
      user.setEmail(email);
      if (!userService.isEmailUnique(user)) {
        String message = jfUtils.getMessageFromResourceBundle("v_email_taken");
        throw new ValidatorException(
            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
      }
    }
  }
}
