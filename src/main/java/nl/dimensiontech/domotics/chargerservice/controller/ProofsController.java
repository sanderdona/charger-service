package nl.dimensiontech.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.api.ProofsApi;
import nl.dimensiontech.domotics.chargerservice.api.model.PageProofDto;
import nl.dimensiontech.domotics.chargerservice.api.model.PageableDto;
import nl.dimensiontech.domotics.chargerservice.domain.Proof;
import nl.dimensiontech.domotics.chargerservice.mapper.PageMapper;
import nl.dimensiontech.domotics.chargerservice.service.ProofService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProofsController implements ProofsApi {

    private final ProofService proofService;
    private final PageMapper pageMapper;

    @Override
    public ResponseEntity<Resource> getProof(String proofId) {
        Proof proof = proofService.getProof(Long.valueOf(proofId));
        Resource resource = new ByteArrayResource(proof.getFile(), "proof_" + proof.getId() + ".jpeg");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"proof_" + proof.getId() + "\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @Override
    public ResponseEntity<PageProofDto> getProofs(PageableDto pageable) {
        Page<Proof> proofPage = proofService.getProofs(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        return ResponseEntity.ok(pageMapper.toProofDto(proofPage));
    }

    @Override
    public ResponseEntity<Void> createProof(LocalDate date, MultipartFile file) {
        Optional<Proof> optionalProof = proofService.getProofByDate(date);
        Proof proof;

        if (optionalProof.isEmpty()) {
            proof = new Proof();
            proof.setDate(date);
            proof.setFile(getBytes(file));
        } else {
            proof = optionalProof.get();
            proof.setFile(getBytes(file));
        }

        proofService.save(proof);
        URI uri = getUri(proof);

        return ResponseEntity.created(uri).build();
    }

    private URI getUri(Proof proof) {
        return ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/" + proof.getId())
                .build()
                .toUri();
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
