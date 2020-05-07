package org.kart.model.vo;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;

@Data
@Builder
public class PilotRaceSummaryVO implements Serializable {

    private final Integer position;
    private final String code;
    private final String pilot;
    private final Integer laps;
    private final Duration duration;
    private final Integer bestLap;
    private final Duration bestDuration;
    private final BigDecimal avarageSpeed;
    private final Duration afterFirst;

}
