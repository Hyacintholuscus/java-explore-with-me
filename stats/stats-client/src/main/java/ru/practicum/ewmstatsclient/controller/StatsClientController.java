package ru.practicum.ewmstatsclient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmstatsclient.client.StatsClient;
import ru.practicum.ewmstatsutil.dto.CreateStatsDto;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class StatsClientController {
    private final StatsClient statsClient;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/hit")
    public ResponseEntity<Object> hit(@Valid @RequestBody CreateStatsDto creationDto) {
        InetAddressValidator validator = InetAddressValidator.getInstance();
        if (!validator.isValid(creationDto.getIp())) {
            log.error("ValidationException. Запрос добавить статистику с некорректным ip = {}", creationDto.getIp());
            throw new ValidationException("Ip doesn't match the correct format.");
        }
        return statsClient.hit("/hit", creationDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam(required = false) String[] uris,
                                           @RequestParam(defaultValue = "false") boolean unique) {
        if (!isValidDateTimeFormat(start) || !isValidDateTimeFormat(end)
                || (uris != null && uris.length != 0 && !isValidUris(uris))) {
            log.error("ValidationException. Запрос получить статистику с некорректными параметрами: " +
                    "дата = {} / {} / список uri = {}", start, end, uris);
            throw new ValidationException("The request was formulated incorrectly.");
        } else if (uris == null) {
            Map<String, Object> parameters = Map.of(
                    "start", encodeDateTime(start),
                    "end", encodeDateTime(end),
                    "unique", unique
            );
            return statsClient.getStats("/stats?start={start}&end={end}&unique={unique}", parameters);
        } else {
            Map<String, Object> parameters = Map.of(
                    "start", encodeDateTime(start),
                    "end", encodeDateTime(end),
                    "uris", uris,
                    "unique", unique
            );
            return statsClient.getStats("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        }
    }

    private boolean isValidDateTimeFormat(String value) {
        LocalDateTime ldt;
        String format = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        try {
            ldt = LocalDateTime.parse(value, formatter);
            String result = ldt.format(formatter);
            return result.equals(value);
        } catch (DateTimeParseException e) {
            log.error("Дата = {} не соответствует формату.", value);
            return false;
        }
    }

    private String encodeDateTime(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private boolean isValidUris(String[] uris) {
        Pattern pattern = Pattern.compile("^/events(/(?<!-)[1-9][0-9]{0,18})?$");
        List<String> urisList = List.of(uris);
        List<String> validUris = urisList.stream()
                .filter(u -> pattern.matcher(u).matches())
                .collect(Collectors.toList());
        return validUris.size() == urisList.size();
    }
}
