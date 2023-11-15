package nl.dimensiontech.domotics.chargerservice.domain;

import lombok.Data;
import nl.dimensiontech.domotics.chargerservice.repository.listener.ChargeSessionEntityListener;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@EntityListeners(ChargeSessionEntityListener.class)
public class ChargeSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    private Car car;

    private Integer odoMeter;

    @Column(nullable = false, columnDefinition = "varchar(25) default 'ANONYMOUS'")
    @Enumerated(value = EnumType.STRING)
    private ChargeSessionType chargeSessionType = ChargeSessionType.ANONYMOUS;

    @CreationTimestamp
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private Double startKwh;

    private LocalDateTime endedAt;

    private Double endKwh;

    private Double totalKwh;
}
