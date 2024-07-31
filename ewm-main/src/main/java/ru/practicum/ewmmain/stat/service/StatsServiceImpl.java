package ru.practicum.ewmmain.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.stat.client.StatsClient;
import ru.practicum.ewmstatsutil.Formatter;
import ru.practicum.ewmstatsutil.dto.CreateStatsDto;
import ru.practicum.ewmstatsutil.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private static final String APP_NAME = "ewm-service";
    private final DateTimeFormatter formatter = Formatter.getFormatter();
    private final StatsClient statsClient;

    @Override
    public void createStats(String uri, String ip) {
        log.info("Отправка информации в сервис статистики для uri {}", uri);

        CreateStatsDto stats = CreateStatsDto.builder()
                .app(APP_NAME)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        CreateStatsDto receivedDto = statsClient.createStats(stats);
        log.info("Информация сохранена {}", receivedDto);
    }

    @Override
    public List<StatsDto> getStats(List<Long> eventsId, boolean unique) {
        log.info("Получение статистики с сервиса статистики для events {}", eventsId);

        String start = LocalDateTime.now().minusYears(20).format(formatter);
        String end = LocalDateTime.now().plusYears(20).format(formatter);

        String[] uris = eventsId.stream()
                .map(id -> String.format("/events/%d", id))
                .toArray(String[]::new);

        return statsClient.getStats(start, end, uris, unique);
    }

    @Override
    public Map<Long, Long> getView(List<Long> eventsId, boolean unique) {
        log.info("Получение просмотров с сервиса статистики для events {}", eventsId);

        List<StatsDto> stats = getStats(eventsId, unique);
        Map<Long, Long> views = new HashMap<>();
        for (StatsDto stat : stats) {
            Long id = Long.valueOf(stat.getUri().replace("/events/", ""));
            Long view = stat.getHits();
            views.put(id, view);
        }
        return views;
    }
}
