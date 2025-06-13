package de.uni_passau.fim.talent_tauscher.model.business.util;

import de.uni_passau.fim.talent_tauscher.dto.EmailDTO;
import de.uni_passau.fim.talent_tauscher.model.business.exceptions.EmailException;
import de.uni_passau.fim.talent_tauscher.model.persistence.config.AppConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.Serial;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Utility class for sending emails. Emails are sent asynchronously on a background thread. */
@ApplicationScoped
public class EmailDispatcher implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Inject private transient Logger logger;

  /** Default constructor. */
  public EmailDispatcher() {
    // Default constructor
  }

  /**
   * Sends an email object.
   *
   * @param email The email to send.
   * @throws EmailException If the email cannot be sent.
   */
  public void send(EmailDTO email) {
    AppConfig config = AppConfig.getInstance();
    Session mailSession = createSession(config);
    String senderAddress = config.getEmailFrom();

    try {
      Message message = new MimeMessage(mailSession);
      message.setFrom(new InternetAddress(senderAddress));
      message.setRecipients(
          Message.RecipientType.TO, InternetAddress.parse(email.getRecipientAddress()));
      message.setSubject(email.getSubject());
      message.setContent(email.getBody(), "text/html; charset=utf-8");
      Transport.send(message);
    } catch (MessagingException e) {
      logger.log(Level.SEVERE, e.getMessage());
      throw new EmailException(e.getMessage());
    }
  }

  private Session createSession(AppConfig config) {
    final String username = config.getEmailUsername();
    final String password = config.getEmailPassword();

    Properties props = new Properties();
    props.put("mail.smtp.auth", config.isEmailAuth());
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", config.getEmailHost());
    props.put("mail.smtp.port", config.getEmailPort());

    Authenticator authenticator =
        new Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        };

    return Session.getInstance(props, authenticator);
  }
}
