package nl.dimensiontech.domotics.chargerservice.service;

import com.google.common.io.Files;
import nl.dimensiontech.domotics.chargerservice.domain.Proof;
import nl.dimensiontech.domotics.chargerservice.event.EntityCreatedEvent;
import nl.dimensiontech.domotics.chargerservice.repository.ProofRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProofServiceTest {

    @Mock
    private ProofRepository proofRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ProofService proofService;

    @Captor
    private ArgumentCaptor<Proof> proofArgumentCaptor;

    @Test
    public void testSave() throws IOException {
        // given
        String fileName = "src/test/resources/images/startOfMonth.jpeg";
        File file = new File(fileName);
        byte[] bytes = Files.toByteArray(file);

        Proof proof = new Proof();
        proof.setFile(bytes);

        // when
        proofService.save(proof);

        // then
        verify(eventPublisher,times(1)).publishEvent(isA(EntityCreatedEvent.class));
        verify(proofRepository, times(1)).save(proofArgumentCaptor.capture());

        Proof capturedProof = proofArgumentCaptor.getValue();
        assertThat(capturedProof.getFile()).isNotEmpty();
        assertThat(capturedProof.getFile().length).isLessThan(bytes.length);
    }

    @Test
    public void testGetProofByDate() {
        // given
        LocalDate date = LocalDate.of(2021, 3, 18);
        Proof proof = new Proof();
        when(proofRepository.findByDate(date)).thenReturn(Optional.of(proof));

        // when
        Optional<Proof> optionalProof = proofService.getProofByDate(date);

        // then
        assertThat(optionalProof).isPresent();
        assertThat(optionalProof.get()).isEqualTo(proof);
    }

    @Test
    public void testGetProof() {
        // given
        Proof proof = new Proof();
        when(proofRepository.findById(1L)).thenReturn(Optional.of(proof));

        // when
        Optional<Proof> optionalProof = proofService.getProof(1L);

        // then
        assertThat(optionalProof).isPresent();
        assertThat(optionalProof.get()).isEqualTo(proof);
    }

    @Test
    public void testGetProofs() {
        // given
        Pageable pageable = Pageable.ofSize(1);
        PageImpl<Proof> proofPage = new PageImpl<>(Collections.singletonList(new Proof()));
        when(proofRepository.findAll(pageable)).thenReturn(proofPage);

        // when
        Page<Proof> proofPageResult = proofService.getProofs(pageable);

        // then
        assertThat(proofPageResult).isEqualTo(proofPage);
    }

}