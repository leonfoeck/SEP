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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Validator ensuring that an avatar image has the required format and size. The maximum allowed
 * file size is set in the system data. Valid image formats are PNG and JPEG.
 *
 * @author Sturm
 */
@Named
@Dependent
@FacesValidator(value = "avatarValidator", managed = true)
public class AvatarValidator implements Validator<Part> {

  @Inject private SystemDataService systemDataService;

  @Inject private JFUtils jfUtils;

  private static final double ALLOWED_ASPECT_RATIO_DIFFERENCE = 0.05;

  /** Default constructor. */
  public AvatarValidator() {
    // Default constructor
  }

  /** {@inheritDoc} */
  @Override
  public void validate(final FacesContext fctx, final UIComponent component, final Part avatar)
      throws ValidatorException {
    long maxImageSize = systemDataService.get().getMaxImageSize();

    List<FacesMessage> errors = new ArrayList<>();
    if (avatar.getSize() > maxImageSize * AdvertisementImageValidator.MAX_FILE_MULTIPLIER) {
      String message = jfUtils.getMessageFromResourceBundle("v_file_size");
      errors.add((new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message)));
    }
    if (!(avatar.getContentType().equals("image/jpeg")
        || avatar.getContentType().equals("image/png"))) {
      String message = jfUtils.getMessageFromResourceBundle("v_file_type");
      errors.add((new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message)));
    }
    try {
      BufferedImage img = ImageIO.read(avatar.getInputStream());

      if (img == null) {
        String message = jfUtils.getMessageFromResourceBundle("v_file_conversion");
        errors.add((new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message)));
      } else {
        if ((Math.abs(img.getHeight() - img.getWidth())
            > Math.max(img.getHeight(), img.getWidth()) * ALLOWED_ASPECT_RATIO_DIFFERENCE)) {
          String message = jfUtils.getMessageFromResourceBundle("v_file_format");
          errors.add((new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message)));
        }
      }
    } catch (IOException e) {
      String message = jfUtils.getMessageFromResourceBundle("v_file_conversion");
      errors.add((new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message)));
    }

    if (!errors.isEmpty()) {
      throw new ValidatorException(errors);
    }
  }
}
