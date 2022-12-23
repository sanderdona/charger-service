package nl.dimensiontech.domotics.chargerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final ConfigProperties configProperties;
    private final JavaMailSender mailSender;

    @Async
    public void sendGeneratedDeclaration(File file) {

        ConfigProperties.EmailConfig config = configProperties.getEmailConfig();

        try {
            log.info("Sending e-mail with attachment {}", file.getName());

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom(config.getFromAddress());
            messageHelper.setTo(config.getToAddress());
            messageHelper.setSubject("Declaratie gegenereerd");
            messageHelper.setText("Declaratie " + file.getName() + " gegenereerd.");

            FileSystemResource resource = new FileSystemResource(file);
            messageHelper.addAttachment(file.getName(), resource);

            mailSender.send(mimeMessage);

            log.info("E-mail sent");

        } catch (MessagingException e) {
            log.error("Failed to send e-mail: {}", e.getMessage());
        }
    }

    @Async
    public void sendReminder() {

        ConfigProperties.EmailConfig config = configProperties.getEmailConfig();
        Month previousMonth = Month.from(LocalDate.now().minusMonths(1));
        String monthDisplayName = previousMonth.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("NL"));

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(config.getFromAddress());
        mailMessage.setTo(config.getToAddress());
        mailMessage.setSubject("Upload bewijs");
        mailMessage.setText("Upload een bewijs voor de maand " + monthDisplayName + ".");

        mailSender.send(mailMessage);
    }

}
