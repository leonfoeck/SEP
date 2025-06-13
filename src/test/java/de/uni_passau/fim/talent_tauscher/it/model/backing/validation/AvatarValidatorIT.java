package de.uni_passau.fim.talent_tauscher.it.model.backing.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import de.uni_passau.fim.talent_tauscher.dto.SystemDataDTO;
import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.backing.validation.AvatarValidator;
import de.uni_passau.fim.talent_tauscher.model.business.services.SystemDataService;
import de.uni_passau.fim.talent_tauscher.it.util.TestUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.logging.Logger;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Unit tests for the {@link AvatarValidator} class. */
@ExtendWith(WeldJunit5Extension.class)
@SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:VisibilityModifier"})
public class AvatarValidatorIT {

  /** Weld setup for dependency injection. */
  @WeldSetup
  WeldInitiator weld =
      WeldInitiator.from(SystemDataService.class, JFUtils.class, Logger.class, LoggerProducer.class)
          .activate(RequestScoped.class, ApplicationScoped.class)
          .build();

  @Mock private Part part;

  @Inject private SystemDataService systemDataService;

  @Mock private JFUtils jfUtils;

  /** Sets up the ConnectionPool before all tests. */
  @BeforeAll
  public static void load() {
    TestUtils.load();
  }

  /** Destroys the ConnectionPool after all tests. */
  @AfterAll
  public static void destroy() {
    TestUtils.destroy();
  }

  /** Tests the image validation. */
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  @Test
  void avatarValidationTest() {
    AutoCloseable openMocks = MockitoAnnotations.openMocks(this);
    SystemDataDTO systemData = systemDataService.get();
    long mockedSize = (systemData.getMaxImageSize() * 1000) + 1; // size in KB

    try {
      when(part.getContentType()).thenReturn("image/gif");
      when(part.getSize()).thenReturn(mockedSize);
      when(part.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

      when(jfUtils.getMessageFromResourceBundle(anyString())).thenReturn("gif");

      AvatarValidator avatarValidator = new AvatarValidator();

      Field systemDataServiceField = AvatarValidator.class.getDeclaredField("systemDataService");
      systemDataServiceField.setAccessible(true);
      systemDataServiceField.set(avatarValidator, systemDataService);
      Field jfUtilsField = AvatarValidator.class.getDeclaredField("jfUtils");
      jfUtilsField.setAccessible(true);
      jfUtilsField.set(avatarValidator, jfUtils);

      FacesContext fctx = null;
      UIComponent component = null;

      ValidatorException e =
          assertThrows(
              ValidatorException.class, () -> avatarValidator.validate(fctx, component, part));
      assertEquals(3, e.getFacesMessages().size());

      openMocks.close();
    } catch (Exception e) { // Unreachable
      System.err.println(e.getCause().getMessage());
    }
  }
}
