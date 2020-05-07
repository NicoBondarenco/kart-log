package org.kart.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

import static javax.persistence.ConstraintMode.CONSTRAINT;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "kart_log_item")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class KartLogItem implements Serializable {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private UUID id;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "pilot", nullable = false, length = 250)
    private String pilot;

    @Column(name = "lap", nullable = false)
    private Integer lap;

    @Column(name = "lap_time", nullable = false)
    private Duration lapTime;

    @Column(name = "avarage_speed", nullable = false)
    private BigDecimal avarageSpeed;

    @Column(name = "kart_log_id", nullable = false, length = 36)
    private UUID kartLogId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "kart_log_id",
            referencedColumnName = "id",
            nullable = false, insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_kart_log_item", value = CONSTRAINT))
    private KartLog kartLog;

}
