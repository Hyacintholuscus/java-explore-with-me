package ru.practicum.ewmmain.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.events.dto.CreateEventDto;
import ru.practicum.ewmmain.events.dto.EventLongDto;
import ru.practicum.ewmmain.events.dto.EventShortDto;
import ru.practicum.ewmmain.events.dto.UpdateEventDto;
import ru.practicum.ewmmain.events.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public EventLongDto createEvent(@PathVariable
                                          @Positive(message = "User's id should be positive")
                                          Long userId,
                                       @Valid @RequestBody CreateEventDto createEventDto) {
        return eventService.create(userId, createEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventLongDto updateEventByUser(@PathVariable
                                              @Positive(message = "User's id should be positive")
                                              Long userId,
                                          @PathVariable
                                          @Positive(message = "Event's id should be positive")
                                          Long eventId,
                                          @Valid @RequestBody UpdateEventDto updateDto) {
        return eventService.updateByUser(userId, eventId, updateDto);
    }

    @GetMapping
    public List<EventShortDto> getAllByUser(@PathVariable
                                            @Positive(message = "User's id should be positive")
                                            Long userId,
                                            @RequestParam(defaultValue = "0")
                                            @PositiveOrZero(message = "Parameter 'from' shouldn't be negative")
                                            int from,
                                            @RequestParam(defaultValue = "10")
                                            @Positive(message = "Parameter 'size' should be positive")
                                            int size) {
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return eventService.getAllByUser(userId, PageRequest.of(page, size, sort));
    }

    @GetMapping("/{eventId}")
    public EventLongDto getEventByUser(@PathVariable
                                           @Positive(message = "User's id should be positive")
                                           Long userId,
                                       @PathVariable
                                       @Positive(message = "Event's id should be positive")
                                       Long eventId) {
        return eventService.getByUserId(userId, eventId);
    }
}
