package dep.mgmt.service;

import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.Email;
import io.github.bibekaryal86.shdsvc.dtos.EmailRequest;
import io.github.bibekaryal86.shdsvc.dtos.EmailResponse;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailService {

  private static final Logger log = LoggerFactory.getLogger(EmailService.class);

  private final Email email;

  public EmailService() {
    this.email = new Email();
  }

  public void sendEmail(
      final String subject,
      final String html,
      final String attachmentFileName,
      final String attachment) {
    log.info("Sending email...");
    final EmailRequest emailRequest =
        buildEmailRequest(subject, html, attachmentFileName, attachment);
    final EmailResponse emailResponse = email.sendEmailMailgun(emailRequest);
    log.info("Email Response: [{}]", emailResponse);
  }

  private EmailRequest buildEmailRequest(
      final String subject,
      final String emailHtmlContent,
      final String attachmentFileName,
      final String attachment) {
    final String emailToFromAddress =
        CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_MAILJET_EMAIL_ADDRESS);
    final String emailToFromName = "Dependency Management";
    final EmailRequest.EmailContact emailToFromContact =
        new EmailRequest.EmailContact(emailToFromAddress, emailToFromName);
    final EmailRequest.EmailAttachment emailAttachment =
        new EmailRequest.EmailAttachment(attachment, attachmentFileName, null);
    return new EmailRequest(
        emailToFromContact,
        List.of(emailToFromContact),
        Collections.emptyList(),
        new EmailRequest.EmailContent(subject, null, emailHtmlContent),
        List.of(emailAttachment));
  }
}
