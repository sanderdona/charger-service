package nl.dimensiontech.domotics.chargerservice.domain;

import lombok.Data;
import nl.dimensiontech.domotics.chargerservice.listener.ChargeSessionEntityListener;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import javax.persistence.OneToOne;
import javax.persistence.FetchType;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import java.time.LocalDateTime;

@Entity
@Data
@EntityListeners(ChargeSessionEntityListener.class)
public class ChargeSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
