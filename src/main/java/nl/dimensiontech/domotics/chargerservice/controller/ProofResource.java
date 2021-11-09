package nl.dimensiontech.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.domain.Proof;
import nl.dimensiontech.domotics.chargerservice.service.ProofService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "proofs")
@RequiredArgsConstructor
public class ProofResource {

    private final ProofService proofService;

    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    public ResponseEntity<Resource> downloadProof(@PathVariable String id) {
        Optional<Proof> optionalProof = proofService.getProof(Long.valueOf(id));

        if (optionalProof.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Proof proof = optionalProof.get();
        Resource resource = new ByteArrayResource(proof.getFile(), "proof_" + proof.getId() + ".jpeg");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + proof.getId() + "\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> handleProofUpload(@RequestPart String date, @RequestPart MultipartFile file) {
        LocalDate localDate = getLocalDate(date);

        Optional<Proof> optionalProof = proofService.getProofByDate(localDate);
        Proof proof;

        if (optionalProof.isEmpty()) {
            proof = createProof(localDate, file);
        } else {
            proof = optionalProof.get();
            proof.setFile(getBytes(file));
        }

        URI uri = getUri(proof);
        proofService.save(proof);

        return ResponseEntity.created(uri).build();
    }

    private LocalDate getLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private URI getUri(Proof proof) {
        return ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/" + proof.getId())
                .build()
                .toUri();
    }

    private Proof createProof(LocalDate date, MultipartFile file) {
        Proof proof = new Proof();
        proof.setDate(date);
        proof.setFile(getBytes(file));
        return proof;
    }

    private byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException exception) {
            log.error("Something went wrong while getting the bytes");
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
