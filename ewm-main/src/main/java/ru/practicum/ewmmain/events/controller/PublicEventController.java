package ru.practicum.ewmmain.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.events.params.EventPublicSearchParam;
import ru.practicum.ewmmain.stat.service.StatsService;
import ru.practicum.ewmmain.events.service.EventSort;
import ru.practicum.ewmmain.events.dto.EventLongDto;
import ru.practicum.ewmmain.events.dto.EventShortDto;
import ru.practicum.ewmmain.events.service.EventService;
import ru.practicum.ewmstatsutil.Formatter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventController {
    private final EventService eventService;
    private final StatsService statsService;

    @GetMapping({"", "/"})
    public List<EventShortDto> getAllPublicEvents(@RequestParam(defaultValue = "") String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) String rangeStart,
                                                  @RequestParam(required = false) String rangeEnd,
                                                  @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                  @RequestParam(required = false) EventSort sort,
                                                  @RequestParam(defaultValue = "0")
                                                      @PositiveOrZero(message = "Parameter 'from' shouldn't be negative")
                                                      int from,
                                                  @RequestParam(defaultValue = "10")
                                                      @Positive(message = "Parameter 'size' should be positive")
                                                      int size, HttpServletRequest request) {
        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, Formatter.getFormatter())
                : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, Formatter.getFormatter())
                : LocalDateTime.now().plusYears(20);

        List<EventShortDto> events = eventService.getPublicEvents(EventPublicSearchParam.builder()
                .text(text)
                .categoriesId(categories)
                .paid(paid)
                .start(start)
                .end(end)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build());
        statsService.createStats(request.getRequestURI(), request.getRemoteAddr());
        return events;
    }

    @GetMapping({"/{id}", "/{id}/"})
    public EventLongDto getPublicEventById(@PathVariable
                                               @Positive(message = "Event's id should be positive")
                                               Long id, HttpServletRequest request) {
        EventLongDto event = eventService.getPublicById(id);
        statsService.createStats(request.getRequestURI(), request.getRemoteAddr());
        return event;
    }
}
