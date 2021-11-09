package nl.dimensiontech.domotics.chargerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfiguration {

    private final ConfigProperties configProperties;

    @Bean
    public JavaMailSender javaMailSender() {

        ConfigProperties.EmailConfig config = configProperties.getEmailConfig();

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPort());

        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(config.getPassword());

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", config.getProtocol());
        properties.put("mail.smtp.auth", config.isAuthEnabled());
        properties.put("mail.smtp.starttls.enable", config.isTlsEnabled());
        properties.put("mail.debug", config.isDebugEnabled());

        return mailSender;
    }

}
