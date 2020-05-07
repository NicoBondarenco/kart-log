package org.kart.model.bo;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;

@Data
@Builder
public class PilotRaceSummaryBO implements Serializable {

    private final String code;
    private final String pilot;
    private final Integer laps;
    private final Duration duration;
    private final Integer bestLap;
    private final Duration bestDuration;
    private final BigDecimal speedSum;

}
