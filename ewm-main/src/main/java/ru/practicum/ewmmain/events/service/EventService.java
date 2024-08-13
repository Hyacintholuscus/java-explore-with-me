package ru.practicum.ewmmain.events.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmmain.events.dto.CreateEventDto;
import ru.practicum.ewmmain.events.dto.EventLongDto;
import ru.practicum.ewmmain.events.dto.EventShortDto;
import ru.practicum.ewmmain.events.dto.UpdateEventDto;
import ru.practicum.ewmmain.events.params.EventAdminSearchParam;
import ru.practicum.ewmmain.events.params.EventPublicSearchParam;

import java.util.List;

public interface EventService {
    EventLongDto create(Long userId, CreateEventDto createEventDto);

    EventLongDto updateByUser(Long userId, Long eventId, UpdateEventDto updateDto);

    EventLongDto updateByAdmin(Long eventId, UpdateEventDto updateDto);

    EventLongDto getByUserId(Long userId, Long eventId);

    List<EventShortDto> getAllByUser(Long userId, Pageable pageable);

    List<EventLongDto> getEventsForAdmin(EventAdminSearchParam param);

    List<EventShortDto> getPublicEvents(EventPublicSearchParam param);

    EventLongDto getPublicById(Long id);
}
