package nl.dimensiontech.domotics.chargerservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class ChargeSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private Car car;

    private int odoMeter;

    @Column(nullable = false, columnDefinition = "varchar(25) default 'ANONYMOUS'")
    @Enumerated(value = EnumType.STRING)
    private ChargeSessionType chargeSessionType = ChargeSessionType.ANONYMOUS;

    @CreationTimestamp
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private float startkWh;

    private LocalDateTime endedAt;

    private float endkWh;

    private float totalkwH;
}
