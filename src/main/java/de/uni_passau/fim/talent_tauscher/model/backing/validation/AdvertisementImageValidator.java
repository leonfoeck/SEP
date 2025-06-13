package de.uni_passau.fim.talent_tauscher.model.backing.validation;

import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.business.services.SystemDataService;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import java.util.ArrayList;
import java.util.List;

/**
 * Validator ensuring that an advertisement image has the required format and size. The maximum
 * allowed file size is set in the system data. Valid image formats are PNG and JPEG.
 *
 * @author Jakob Edmaier
 */
@Named
@Dependent
@FacesValidator(value = "advertisementImageValidator", managed = true)
public class AdvertisementImageValidator implements Validator<Part> {

  private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png");

  @Inject private SystemDataService systemDataService;

  @Inject private JFUtils jfUtils;

  /** The maximum multiplier for the file size. */
  public static final int MAX_FILE_MULTIPLIER = 1000;

  /** Default constructor. */
  public AdvertisementImageValidator() {
    // Default constructor
  }

  /** {@inheritDoc} */
  @Override
  public void validate(final FacesContext fctx, final UIComponent component, final Part image)
      throws ValidatorException {

    List<FacesMessage> errors = new ArrayList<>();
    long maxFileSizeKB = systemDataService.get().getMaxImageSize();

    if (image.getSize() > maxFileSizeKB * MAX_FILE_MULTIPLIER) {
      String message = jfUtils.getMessageFromResourceBundle("v_maximumFileSizeExceeded");
      errors.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    }

    if (!ALLOWED_IMAGE_TYPES.contains(image.getContentType())) {
      String message = jfUtils.getMessageFromResourceBundle("v_invalidImageFormat");
      errors.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    }

    if (!errors.isEmpty()) {
      throw new ValidatorException(errors);
    }
  }
}
