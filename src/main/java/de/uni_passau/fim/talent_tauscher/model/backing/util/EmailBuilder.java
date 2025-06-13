package de.uni_passau.fim.talent_tauscher.model.backing.util;

import de.uni_passau.fim.talent_tauscher.dto.EmailDTO;
import de.uni_passau.fim.talent_tauscher.dto.ExtendedRequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.dto.VerificationTokenDTO;
import de.uni_passau.fim.talent_tauscher.model.business.exceptions.EmailException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The {@code EmailBuilder} class is responsible for creating {@link EmailDTO} objects for various
 * email notifications within the application. It handles the localization and formatting of email
 * subjects and bodies based on provided templates and data.
 */
@ApplicationScoped
public class EmailBuilder implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private static final Pattern REQUEST_URL_PATTERN = Pattern.compile("^(.*?/)view/.*");

  @Inject private JFUtils jfUtils;

  @Inject private transient HttpServletRequest request;

  private static final Locale[] SUPPORTED_LOCALES = {Locale.GERMAN, Locale.ENGLISH};

  /** Default constructor. */
  public EmailBuilder() {
    // Default constructor
  }

  /**
   * Builds an email for password reset.
   *
   * @param user the user who requested the password reset
   * @param token the verification token for password reset
   * @return the constructed {@link EmailDTO} for password reset
   * @author Jakob Edmaier
   */
  public EmailDTO buildPasswordResetEmail(UserAuthenticationDTO user, VerificationTokenDTO token) {
    EmailDTO email =
        buildEmailTemplate(
            "ma_emailSubject_passwordResetEmail",
            "ma_emailBody_passwordResetEmail",
            user.getEmail());

    String targetURL =
        getBaseURL() + "view/anonymous/passwordResetInput.xhtml?secret=" + token.getToken();

    email.setBody(
        email.getBody().replace("<nickname>", user.getNickname()).replace("<target>", targetURL));
    return email;
  }

  /**
   * Builds an email for account verification.
   *
   * @param user the user to be verified
   * @param token the verification token for email verification
   * @return the constructed {@link EmailDTO} for email verification
   * @author Jakob Edmaier
   */
  public EmailDTO buildVerificationEmail(UserDTO user, VerificationTokenDTO token) {
    EmailDTO email =
        buildEmailTemplate(
            "ma_emailSubject_verificationEmail", "ma_emailBody_verificationEmail", user.getEmail());

    String targetURL =
        getBaseURL() + "view/anonymous/emailVerification.xhtml?secret=" + token.getToken();

    email.setBody(
        email.getBody().replace("<nickname>", user.getNickname()).replace("<target>", targetURL));
    return email;
  }

  /**
   * Builds an email notifying about an incoming request.
   *
   * @param request the incoming request details
   * @param adCreator the creator of the advertisement related to the request
   * @return the constructed {@link EmailDTO} for incoming request notification
   * @author Leon Föckersperger
   */
  public EmailDTO buildIncomingRequestEmail(ExtendedRequestDTO request, UserDTO adCreator) {
    EmailDTO email =
        buildEmailTemplate(
            "ma_emailSubject_incomingRequestEmail",
            "ma_emailBody_incomingRequestEmail",
            adCreator.getEmail());

    String targetURL = getBaseURL() + "view/registered/writeResponse.xhtml?id=" + request.getId();

    String body =
        email
            .getBody()
            .replace("<ad-creator>", adCreator.getNickname())
            .replace("<target>", targetURL)
            .replace("<advertisement>", request.getAdvertisementTitle())
            .replace("<nickname>", request.getRequestCreatorUsername())
            .replace("<requestText>", encodeUserInput(request.getRequest()));
    email.setBody(body);
    return email;
  }

  /**
   * Builds an email notifying about an incoming response to a request.
   *
   * @param request the incoming request details
   * @param requestCreator the creator of the request
   * @return the constructed {@link EmailDTO} for incoming response notification
   * @author Jakob Edmaier
   */
  public EmailDTO buildIncomingResponseEmail(ExtendedRequestDTO request, UserDTO requestCreator) {
    String emailBodyKey =
        request.getAccepted()
            ? "ma_emailBody_accepted_incomingResponseEmail"
            : "ma_emailBody_rejected_incomingResponseEmail";

    EmailDTO email =
        buildEmailTemplate(
            "ma_emailSubject_incomingResponseEmail", emailBodyKey, requestCreator.getEmail());

    String body =
        email
            .getBody()
            .replace("<nickname>", requestCreator.getNickname())
            .replace("<target>", getBaseURL())
            .replace("<advertisement>", request.getAdvertisementTitle())
            .replace("<ad-creator>", request.getRecipientUsername())
            .replace("<response>", encodeUserInput(request.getResponse()));
    email.setBody(body);
    return email;
  }

  /**
   * Builds an email notifying about an outgoing request.
   *
   * @author Jakob Edmaier
   * @param input the user input to encode
   * @return the encoded user input
   */
  private String encodeUserInput(String input) {
    return input.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
  }

  /**
   * Retrieves the base URL from the current HTTP request.
   *
   * @return the base URL
   * @throws EmailException if the base URL cannot be determined
   * @author Jakob Edmaier
   */
  private String getBaseURL() {
    String requestURL = request.getRequestURL().toString();
    Matcher matcher = REQUEST_URL_PATTERN.matcher(requestURL);
    if (!matcher.matches()) {
      throw new EmailException("Failed to build server URL.");
    }
    return matcher.group(1);
  }

  /**
   * Builds an email template from localized subject and body templates.
   *
   * @author Jakob Edmaier
   * @param subjectKey the key for the localized subject template
   * @param bodyKey the key for the localized body template
   * @param recipientAddress the recipient's email address
   * @return the constructed {@link EmailDTO} with the localized subject and body
   */
  private EmailDTO buildEmailTemplate(String subjectKey, String bodyKey, String recipientAddress) {
    EmailDTO email = new EmailDTO();
    email.setRecipientAddress(recipientAddress);

    String subject =
        Arrays.stream(SUPPORTED_LOCALES)
            .map(locale -> jfUtils.getMessageFromResourceBundle(subjectKey, locale))
            .collect(Collectors.joining(" / "));
    email.setSubject(subject);

    StringBuilder sb = new StringBuilder();
    for (Locale locale : SUPPORTED_LOCALES) {
      String template = jfUtils.getMessageFromResourceBundle(bodyKey, locale);
      sb.append(template).append("<br/>");
    }

    email.setBody(sb.toString());
    return email;
  }
}
