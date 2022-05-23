package nl.dimensiontech.domotics.chargerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.domain.Proof;
import nl.dimensiontech.domotics.chargerservice.event.EntityCreatedEvent;
import nl.dimensiontech.domotics.chargerservice.repository.ProofRepository;
import nl.dimensiontech.domotics.chargerservice.util.ImageUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProofService {

    private final ProofRepository proofRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Proof save(Proof proof) {
        resizeImage(proof);
        proofRepository.save(proof);
        publishCreatedEvent(proof);
        return proof;
    }

    private void publishCreatedEvent(Proof proof) {
        eventPublisher.publishEvent(new EntityCreatedEvent<>(this, proof));
    }

    @Transactional
    public Optional<Proof> getProofByDate(LocalDate date) {
        return proofRepository.findByDate(date);
    }

    public Optional<Proof> getProof(Long id) {
        return proofRepository.findById(id);
    }

    public Page<Proof> getProofs(Pageable pageable) {
        return proofRepository.findAll(pageable);
    }

    private void resizeImage(Proof proof) {
        try {

            BufferedImage image = ImageUtil.toBufferedImage(proof.getFile());
            BufferedImage scaledImage = ImageUtil.resize(image, 240, 180);
            byte[] file = ImageUtil.toByteArray(scaledImage, "jpeg");
            proof.setFile(file);

        } catch (IOException e) {
            log.error("Image resizing went wrong!");
            throw new IllegalStateException("Image resizing went wrong");
        }
    }

}
