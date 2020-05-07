package org.kart.model.vo;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RaceSummaryVO implements Serializable {

    private final UUID id;
    private final String pilot;
    private final Integer bestLap;
    private final Duration bestDuration;
    private final List<PilotRaceSummaryVO> items;

}
