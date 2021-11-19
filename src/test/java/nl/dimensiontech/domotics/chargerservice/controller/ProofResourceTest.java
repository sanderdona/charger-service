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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(responseEntity.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION)).containsOnly("attachment; filename=\"1\"");
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