package de.uni_passau.fim.talent_tauscher.model.backing.validation;

import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Validator ensuring that a nickname is unique among all users.
 *
 * @author Sturm
 */
@Named
@Dependent
@FacesValidator(value = "nicknameUniqueValidator", managed = true)
public class NicknameUniqueValidator implements Validator<String> {

  @Inject private UserService userService;

  @Inject private JFUtils jfUtils;

  /** {@inheritDoc} */
  @Override
  public void validate(final FacesContext fctx, final UIComponent component, final String nickname)
      throws ValidatorException {
    String oldValue = (String) ((UIInput) component).getValue();
    if (!(oldValue != null && oldValue.equals(nickname))) {
      UserAuthenticationDTO user = new UserAuthenticationDTO();
      user.setNickname(nickname);
      if (!userService.isNicknameUnique(user)) {
        String message = jfUtils.getMessageFromResourceBundle("v_nick_taken");
        throw new ValidatorException(
            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
      }
    }
  }
}
