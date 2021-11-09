package nl.dimensiontech.domotics.chargerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final ConfigProperties configProperties;
    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(File file) {

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

}
