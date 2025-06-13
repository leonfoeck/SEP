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

/**
 * Validator ensuring that a string is in a valid nickname format. The only restriction is that a
 * nickname must not contain the symbol "@" so that one can distinguish between nicknames and email
 * addresses.
 *
 * @author Sturm
 */
@Named
@Dependent
@FacesValidator(value = "nicknameFormatValidator", managed = true)
public class NicknameFormatValidator implements Validator<String> {

  @Inject private JFUtils jfUtils;

  /** {@inheritDoc} */
  @Override
  public void validate(final FacesContext fctx, final UIComponent component, final String nickname)
      throws ValidatorException {
    if (nickname.contains("@")) {
      String message = jfUtils.getMessageFromResourceBundle("v_nick_wrong_format");
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    }
  }
}
