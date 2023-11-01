package nl.dimensiontech.domotics.chargerservice.cucumber.steps;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.mail.Session;
import nl.dimensiontech.domotics.chargerservice.fake.FakeMimeMessage;
import nl.dimensiontech.domotics.chargerservice.scheduling.SchedulingService;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class ReportStepDefs {

    @Autowired
    private SchedulingService schedulingService;

    @Autowired
    private JavaMailSender mailSender;

    private FakeMimeMessage mimeMessage;

    private String mailMessageContent;

    @Before
    public void before() {
        Properties properties = new Properties();
        mimeMessage = new FakeMimeMessage(Session.getDefaultInstance(properties));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @When("the monthly cronjob is triggered")
    public void theMonthlyCronjobIsTriggered() {
        schedulingService.generateMonthlyReport();
    }

    @Then("an e-mail is sent to {string} with subject {string}")
    public void anEMailIsSentToWithSubject(String recipient, String subject) throws Exception {
        ArgumentCaptor<SimpleMailMessage> mailMessageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            try {
                // wait for the message to arrive.
                verify(mailSender).send(mailMessageCaptor.capture());
            } catch (Exception e) {
                return false;
            }

            return true;
        });

        SimpleMailMessage mailMessage = mailMessageCaptor.getValue();
        mailMessageContent = mailMessage.getText();

        assertNotNull(mailMessage.getTo());
        assertEquals(1, mailMessage.getTo().length);
        assertEquals(recipient, mailMessage.getTo()[0]);
        assertEquals(subject, mailMessage.getSubject());
    }

    @Then("an e-mail with attachment is sent to {string} with subject {string}")
    public void anEMailWithAttachmentIsSentToWithSubject(String recipient, String subject) throws Exception {
        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            try {
                // wait for the message to arrive.
                verify(mailSender).send(mimeMessage);
            } catch (Exception e) {
                return false;
            }

            return true;
        });

        assertEquals(1, mimeMessage.getAllRecipients().length);
        assertEquals(recipient, mimeMessage.getAllRecipients()[0].toString());
        assertEquals(subject, mimeMessage.getSubject());

        mailMessageContent = mimeMessage.getBodyParts().get(0).getContent().toString();
    }

    @And("it contains the message:")
    public void itContainsTheMessage(String content) {
        assertEquals(content, mailMessageContent);
    }

    @And("a file with the name {string}")
    public void aFileWithTheTitle(String fileName) throws Exception {
        assertEquals(fileName, mimeMessage.getBodyParts().get(1).getFileName());
    }

    @And("the file contains the amount of {string} for the month {string} on license plate {string}")
    public void theFileContainsTheAmountOfForTheMonthOnLicensePlate(String amount, String month, String licensePlate) throws Exception {
        File targetFile = new File("received-file.pdf");
        if (mimeMessage.getBodyParts().get(1).getContent() instanceof FileInputStream fileInputStream) {
            Files.copy(fileInputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            PdfReader pdfReader = new PdfReader(targetFile.getName());
            PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfReader);
            String text = pdfTextExtractor.getTextFromPage(1);

            assertThat(text, containsString("Totaal " + amount));
            assertThat(text, containsString("Periode " + month));
            assertThat(text, containsString("Kenteken " + licensePlate));
        } else {
            throw new AssertionError("No attachment found");
        }
    }
}
