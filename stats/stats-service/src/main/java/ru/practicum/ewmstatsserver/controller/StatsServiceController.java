package ru.practicum.ewmstatsserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmstatsserver.service.StatsService;
import ru.practicum.ewmstatsutil.dto.CreateStatsDto;
import ru.practicum.ewmstatsutil.dto.StatsDto;

import javax.validation.ValidationException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsServiceController {
    private final StatsService statsService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/hit")
    public CreateStatsDto hits(@RequestBody CreateStatsDto creationDto) {
        return statsService.save(creationDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam String start,
                                             @RequestParam String end,
                                             @RequestParam(required = false) List<String> uris,
                                             @RequestParam(defaultValue = "false") boolean unique) {
        uris = (uris == null) ? new ArrayList<>() : uris;
        return statsService.getStats(decodeDateTime(start), decodeDateTime(end), uris, unique);
    }

    private LocalDateTime decodeDateTime(String value) {
        LocalDateTime ldt;
        String format = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        String dateTime = URLDecoder.decode(value, StandardCharsets.UTF_8);
        try {
            ldt = LocalDateTime.parse(dateTime, formatter);
            return ldt;
        } catch (DateTimeParseException e) {
            log.error("Дата = {} не соответствует формату.", dateTime);
            throw new ValidationException("Date doesn't match the correct format.");
        }
    }
}
