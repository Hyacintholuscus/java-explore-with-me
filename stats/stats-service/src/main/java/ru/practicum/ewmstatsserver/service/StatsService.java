package ru.practicum.ewmstatsserver.service;

import ru.practicum.ewmstatsutil.dto.CreateStatsDto;
import ru.practicum.ewmstatsutil.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    CreateStatsDto save(CreateStatsDto creationDto);

    List<StatsDto> getStats(LocalDateTime start,
                            LocalDateTime end,
                            List<String> uris,
                            boolean unique);
}
