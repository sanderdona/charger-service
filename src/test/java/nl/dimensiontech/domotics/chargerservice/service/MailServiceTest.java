package nl.dimensiontech.domotics.chargerservice.service;

import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private ConfigProperties configProperties;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;

    @Test
    public void testSendGeneratedDeclaration() throws IOException {
        // given
        File file = File.createTempFile("test_", ".pdf");

        ConfigProperties.EmailConfig emailConfig = new ConfigProperties.EmailConfig();
        emailConfig.setFromAddress("foo@bar.nl");
        emailConfig.setToAddress("bar@foo.nl");
        when(configProperties.getEmailConfig()).thenReturn(emailConfig);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // when
        mailService.sendGeneratedDeclaration(file);

        // then
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    public void testSendReminder() {
        // given
        ConfigProperties.EmailConfig emailConfig = new ConfigProperties.EmailConfig();
        emailConfig.setFromAddress("foo@bar.nl");
        emailConfig.setToAddress("bar@foo.nl");
        when(configProperties.getEmailConfig()).thenReturn(emailConfig);

        // when
        mailService.sendReminder();

        // then
        ArgumentCaptor<SimpleMailMessage> argumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(argumentCaptor.capture());

        SimpleMailMessage mailMessage = argumentCaptor.getValue();
        assertThat(mailMessage.getFrom()).isEqualTo("foo@bar.nl");
        assertThat(mailMessage.getTo()).isNotNull();
        assertThat(mailMessage.getTo()[0]).isEqualTo("bar@foo.nl");
        assertThat(mailMessage.getSubject()).isEqualTo("Upload bewijs");
        assertThat(mailMessage.getText()).contains("Upload een bewijs voor de maand ");
    }

}