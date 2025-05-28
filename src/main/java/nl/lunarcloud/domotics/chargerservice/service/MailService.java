package nl.lunarcloud.domotics.chargerservice.service;

import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.lunarcloud.domotics.chargerservice.common.config.ConfigProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Clock;
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
    private final Clock clock;

    @Async
    public void sendGeneratedDeclaration(File file) {

        ConfigProperties.EmailConfig config = configProperties.getEmailConfig();
        String fromAddress = config.getFromAddress();
        String toAddress = config.getToAddress();
        String previousMonth = getPreviousMonth();

        try {
            log.info("Sending e-mail with attachment {}", file.getName());

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            mimeMessage.setFrom(fromAddress);
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
            mimeMessage.setSubject("Declaratie gegenereerd");

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Declaratie voor de maand " + previousMonth + " gegenereerd.");

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(file);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            mimeMessage.setContent(multipart);

            mailSender.send(mimeMessage);

            log.info("E-mail sent");
        } catch (Exception e) {
            log.error("Failed to send e-mail: {}", e.getMessage());
        }
    }

    @Async
    public void reportNotGenerated() {

        ConfigProperties.EmailConfig config = configProperties.getEmailConfig();
        String previousMonth = getPreviousMonth();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(config.getFromAddress());
        mailMessage.setTo(config.getToAddress());
        mailMessage.setSubject("Declaratie genereren niet mogelijk");
        mailMessage.setText("Genereren van een declaratie voor de maand " + previousMonth + " is niet gelukt.");

        mailSender.send(mailMessage);
    }

    private String getPreviousMonth() {
        Month previousMonth = Month.from(LocalDate.now(clock).minusMonths(1));
        return previousMonth.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("NL"));
    }
}
