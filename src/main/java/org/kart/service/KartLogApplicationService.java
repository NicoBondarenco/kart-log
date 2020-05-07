package org.kart.service;

import org.kart.exception.KartLogException;
import org.kart.model.bo.PilotRaceSummaryBO;
import org.kart.model.entity.KartLog;
import org.kart.model.entity.KartLogItem;
import org.kart.model.vo.KartLogVO;
import org.kart.model.vo.PilotRaceSummaryVO;
import org.kart.model.vo.RaceSummaryVO;
import org.kart.repository.KartLogRepository;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;

public class KartLogApplicationService implements KartLogService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final KartLogRepository repository;

    public KartLogApplicationService(KartLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<KartLogVO> process(File file) throws Exception {
        List<KartLogItem> items = Files.lines(file.toPath())
                .filter(line -> line != null && !line.trim().isEmpty() && !line.toLowerCase().contains("hora"))
                .map(this::fromLine)
                .collect(Collectors.toList());

        KartLog kartLog = KartLog.builder()
                .id(UUID.randomUUID())
                .name(file.getName())
                .ocurred(OffsetDateTime.now(ZoneOffset.UTC))
                .items(items)
                .build();

        items.forEach(item -> item.setKartLogId(kartLog.getId()));

        findLastLap(items);

        repository.insert(kartLog);

        return repository.findAll().stream().map(this::fromKartLog).collect(Collectors.toList());
    }

    @Override
    public RaceSummaryVO getKartLogDetails(UUID id) {
        KartLog kartLog = repository.findById(id).orElseThrow(() -> new KartLogException("Log da corrida de kart não encontrado"));

        List<KartLogItem> items = ofNullable(kartLog.getItems()).orElse(new LinkedList<>());
        List<KartLogItem> validItems = validLaps(items);

        Map<String, List<KartLogItem>> grouped = validItems.stream()
                .collect(groupingBy(KartLogItem::getPilot));

        List<PilotRaceSummaryVO> results = fromBO(grouped);
        PilotRaceSummaryVO bestLap = results.stream().min(comparing(PilotRaceSummaryVO::getBestDuration)).get();

        return RaceSummaryVO.builder()
                .id(kartLog.getId())
                .pilot(bestLap.getCode() + " - " + bestLap.getPilot())
                .bestLap(bestLap.getBestLap())
                .bestDuration(bestLap.getBestDuration())
                .items(results)
                .build();
    }

    private List<PilotRaceSummaryVO> fromBO(Map<String, List<KartLogItem>> grouped) {

        List<PilotRaceSummaryVO> result = new LinkedList<>();

        List<PilotRaceSummaryBO> collect = grouped.values().stream()
                .map(this::fromItems)
                .sorted(comparing(PilotRaceSummaryBO::getLaps)
                        .reversed()
                        .thenComparing(PilotRaceSummaryBO::getDuration))
                .collect(Collectors.toCollection(LinkedList::new));

        for (int i = 0; i < collect.size(); i++) {
            PilotRaceSummaryBO summary = collect.get(i);
            result.add(PilotRaceSummaryVO.builder()
                    .position(i + 1)
                    .code(summary.getCode())
                    .pilot(summary.getPilot())
                    .laps(summary.getLaps())
                    .duration(summary.getDuration())
                    .bestLap(summary.getBestLap())
                    .bestDuration(summary.getBestDuration())
                    .avarageSpeed(summary.getSpeedSum().divide(BigDecimal.valueOf(summary.getLaps()), 3, RoundingMode.HALF_UP))
                    .afterFirst(collect.get(0).getDuration().minus(summary.getDuration()))
                    .build()
            );
        }

        return result;
    }

    private PilotRaceSummaryBO fromItems(List<KartLogItem> items) {
        KartLogItem bestLap = items.stream().min(comparing(KartLogItem::getLapTime)).get();

        if (items.size() == 1) {
            PilotRaceSummaryBO singleLap = ofNullable(items.get(0))
                    .map(this::fromItem)
                    .get();
            return reduceSummaries(
                    singleLap,
                    PilotRaceSummaryBO.builder()
                            .duration(Duration.ZERO)
                            .speedSum(BigDecimal.ZERO)
                            .build(),
                    bestLap,
                    items.size()
            );
        }

        return items.stream()
                .map(this::fromItem)
                .reduce((item1, item2) -> reduceSummaries(item1, item2, bestLap, items.size()))
                .get();
    }

    private PilotRaceSummaryBO reduceSummaries(PilotRaceSummaryBO item1, PilotRaceSummaryBO item2, KartLogItem bestLap, Integer totalLaps) {
        return PilotRaceSummaryBO.builder()
                .code(item1.getCode())
                .pilot(item1.getPilot())
                .laps(totalLaps)
                .duration(item1.getDuration().plus(item2.getDuration()))
                .bestLap(bestLap.getLap())
                .bestDuration(bestLap.getLapTime())
                .speedSum(item1.getSpeedSum().add(item2.getSpeedSum()))
                .build();
    }

    private PilotRaceSummaryBO fromItem(KartLogItem item) {
        String[] split = item.getPilot().split(" ");
        return PilotRaceSummaryBO.builder()
                .code(split[0])
                .pilot(split[2])
                .laps(0)
                .duration(item.getLapTime())
                .bestLap(0)
                .bestDuration(item.getLapTime())
                .speedSum(item.getAvarageSpeed())
                .build();
    }

    private List<KartLogItem> validLaps(List<KartLogItem> items) {
        KartLogItem lastLap = findLastLap(items);
        List<KartLogItem> validItems = items.stream()
                .filter(item -> item.getTime().isBefore(lastLap.getTime()))
                .collect(Collectors.toList());
        validItems.add(lastLap);
        return validItems;
    }

    private KartLogItem findLastLap(List<KartLogItem> items) {
        return items.stream()
                .filter(item -> item.getLap().equals(4))
                .min(comparing(KartLogItem::getTime))
                .orElseThrow(() -> new KartLogException("A corrida n\u00E3o foi finalizada"));
    }

    private KartLogVO fromKartLog(KartLog kartLog) {
        return KartLogVO.builder()
                .id(kartLog.getId())
                .name(kartLog.getName())
                .ocurred(kartLog.getOcurred())
                .build();
    }

    private KartLogItem fromLine(String line) throws KartLogException {
        try {
            String[] split = line.split(" ");
            String[] lapTimeValues = split[5].replaceAll("[^\\d]", "-").split("-");
            return KartLogItem.builder()
                    .id(UUID.randomUUID())
                    .time(LocalTime.parse(split[0], TIME_FORMATTER))
                    .pilot(String.join(" ", split[1], split[2], split[3]))
                    .lap(Integer.valueOf(split[4]))
                    .lapTime(Duration
                            .ofMinutes(Long.parseLong(lapTimeValues[0]))
                            .plusSeconds(Long.parseLong(lapTimeValues[1]))
                            .plusMillis(Long.parseLong(lapTimeValues[2]))
                    )
                    .avarageSpeed(new BigDecimal(split[6].replace(",", ".")))
                    .build();
        } catch (Exception e) {
            throw new KartLogException("Error reading line " + line, e);
        }
    }

}
