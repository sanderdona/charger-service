package nl.lunarcloud.domotics.chargerservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class ChargeSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID uuid;

    @OneToOne(fetch = FetchType.EAGER)
    private Car car;

    private Integer odoMeter;

    @Column(nullable = false, columnDefinition = "varchar(25) default 'ANONYMOUS'")
    @Enumerated(value = EnumType.STRING)
    private ChargeSessionType chargeSessionType = ChargeSessionType.ANONYMOUS;

    @CreationTimestamp
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @Column(nullable = false)
    private Double startKwh;

    private Double endKwh;

    private Double totalKwh;
}