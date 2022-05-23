package nl.dimensiontech.domotics.chargerservice.controller;

import nl.dimensiontech.domotics.chargerservice.domain.Proof;
import nl.dimensiontech.domotics.chargerservice.service.ProofService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProofResourceTest {

    @Mock
    private ProofService proofService;

    @InjectMocks
    private ProofResource proofResource;

    @Captor
    private ArgumentCaptor<Proof> proofArgumentCaptor;

    @Test
    public void testGetProofById() {
        // given
        Proof proof = new Proof();
        proof.setId(1L);
        proof.setFile("dummy".getBytes());

        when(proofService.getProof(1L)).thenReturn(Optional.of(proof));

        // when
        ResponseEntity<Resource> responseEntity = proofResource.downloadProof("1");

        // then
        assertThat(responseEntity.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION)).containsOnly("attachment; filename=\"proof_1\"");
        assertThat(responseEntity.getHeaders().get(HttpHeaders.CONTENT_TYPE)).containsOnly("image/jpeg");
        assertThat(responseEntity.getBody()).isInstanceOf(ByteArrayResource.class);

        ByteArrayResource resource = (ByteArrayResource) responseEntity.getBody();

        assertThat(resource).isNotNull();
        assertThat(resource.getDescription()).isEqualTo("Byte array resource [proof_1.jpeg]");
        assertThat(resource.getByteArray()).isEqualTo("dummy".getBytes());
    }

    @Test
    public void testGetProofNotFound() {
        // given
        when(proofService.getProof(1L)).thenReturn(Optional.empty());

        // when
        ResponseEntity<Resource> responseEntity = proofResource.downloadProof("1");

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetProofsPage() {
        // given
        Pageable pageable = Pageable.ofSize(1);
        Proof proof = new Proof();
        when(proofService.getProofs(pageable)).thenReturn(new PageImpl<>(List.of(proof)));

        // when
        Page<Proof> proofsPage = proofResource.getAllProofs(pageable);

        // then
        assertThat(proofsPage.getTotalPages()).isEqualTo(1);
        assertThat(proofsPage.getContent().get(0)).isEqualTo(proof);
    }

    @Test
    public void testHandleNewProofUpload() {
        // given
        MockMultipartFile file = new MockMultipartFile("dummy", "dummy".getBytes());

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // when
        ResponseEntity<Void> responseEntity = proofResource.handleProofUpload("19/11/2021", file);

        // then
        verify(proofService, times(1)).save(proofArgumentCaptor.capture());

        assertThat(responseEntity.getHeaders().getLocation()).isEqualTo(URI.create("http://localhost/null"));

        Proof capturedProof = proofArgumentCaptor.getValue();
        assertThat(capturedProof.getDate()).isEqualTo(LocalDate.of(2021, 11, 19));
        assertThat(capturedProof.getFile()).isEqualTo("dummy".getBytes());
    }

    @Test
    public void testHandleProofUploadWithDateNoMatingPattern() {
        // given
        MockMultipartFile file = new MockMultipartFile("dummy", "dummy".getBytes());

        // when
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> proofResource.handleProofUpload("1/11/2021", file));

        // then
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).isEqualTo("1/11/2021 is not a valid date");
    }

    @Test
    public void testHandleReplaceExistingProofUpload() {
        // given
        MockMultipartFile file = new MockMultipartFile("new-dummy", "new-dummy".getBytes());

        Proof proof = new Proof();
        proof.setId(1L);
        proof.setDate(LocalDate.of(2021, 11, 19));
        proof.setFile("dummy".getBytes());
        when(proofService.getProofByDate(LocalDate.of(2021, 11, 19))).thenReturn(Optional.of(proof));

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // when
        ResponseEntity<Void> responseEntity = proofResource.handleProofUpload("19/11/2021", file);

        // then
        verify(proofService, times(1)).save(proofArgumentCaptor.capture());

        assertThat(responseEntity.getHeaders().getLocation()).isEqualTo(URI.create("http://localhost/1"));

        Proof capturedProof = proofArgumentCaptor.getValue();
        assertThat(capturedProof.getDate()).isEqualTo(LocalDate.of(2021, 11, 19));
        assertThat(capturedProof.getFile()).isEqualTo("new-dummy".getBytes());
    }

}