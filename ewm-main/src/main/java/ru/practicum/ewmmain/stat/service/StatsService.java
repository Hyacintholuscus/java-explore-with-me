package ru.practicum.ewmmain.stat.service;

import ru.practicum.ewmstatsutil.dto.StatsDto;

import java.util.List;
import java.util.Map;

public interface StatsService {
    void createStats(String uri, String ip);

    List<StatsDto> getStats(List<Long> eventsId, boolean unique);

    Map<Long, Long> getView(List<Long> eventsId, boolean unique);
}
