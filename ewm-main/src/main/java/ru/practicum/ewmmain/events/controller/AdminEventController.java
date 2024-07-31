package ru.practicum.ewmmain.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.events.model.EventState;
import ru.practicum.ewmmain.events.dto.EventLongDto;
import ru.practicum.ewmmain.events.dto.UpdateEventDto;
import ru.practicum.ewmmain.events.params.EventAdminSearchParam;
import ru.practicum.ewmmain.events.service.EventService;
import ru.practicum.ewmstatsutil.Formatter;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventLongDto updateEventByAdmin(@PathVariable
                                          @Positive(message = "Event's id should be positive")
                                          Long eventId,
                                          @Valid @RequestBody UpdateEventDto updateDto) {
        return eventService.updateByAdmin(eventId, updateDto);
    }

    @GetMapping
    public List<EventLongDto> getAllByAdmin(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<EventState> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) String rangeStart,
                                             @RequestParam(required = false) String rangeEnd,
                                             @RequestParam(defaultValue = "0")
                                            @PositiveOrZero(message = "Parameter 'from' shouldn't be negative")
                                            int from,
                                             @RequestParam(defaultValue = "10")
                                            @Positive(message = "Parameter 'size' should be positive")
                                            int size) {
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, Formatter.getFormatter())
                : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, Formatter.getFormatter())
                : LocalDateTime.now().plusYears(20);
        return eventService.getEventsForAdmin(EventAdminSearchParam.builder()
                .usersId(users)
                .states(states)
                .categoriesId(categories)
                .start(start)
                .end(end)
                .pageable(PageRequest.of(page, size, sort))
                .build());
    }
}
