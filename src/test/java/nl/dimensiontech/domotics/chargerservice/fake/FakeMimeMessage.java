package nl.dimensiontech.domotics.chargerservice.fake;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.List;

public class FakeMimeMessage extends MimeMessage {

    public List<BodyPart> bodyParts = new ArrayList<>();

    public FakeMimeMessage(Session session) {
        super(session);
    }

    @Override
    public void setContent(Multipart multipart) throws MessagingException {

        for (int parts = 0; parts < multipart.getCount(); parts++) {
            bodyParts.add(multipart.getBodyPart(parts));
        }

        super.setContent(multipart);
    }

    public List<BodyPart> getBodyParts() {
        return bodyParts;
    }
}
