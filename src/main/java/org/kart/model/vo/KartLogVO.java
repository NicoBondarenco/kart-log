package org.kart.model.vo;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
public class KartLogVO implements Serializable {

    private final UUID id;
    private final String name;
    private final OffsetDateTime ocurred;

    @Override
    public String toString() {
        return name + " - " + DateTimeFormatter.ISO_DATE_TIME.format(ocurred);
    }
}
