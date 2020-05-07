package org.kart.service;


import org.kart.model.vo.KartLogVO;
import org.kart.model.vo.RaceSummaryVO;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface KartLogService {

    List<KartLogVO> process(File file) throws Exception;

    RaceSummaryVO getKartLogDetails(UUID id);
}
