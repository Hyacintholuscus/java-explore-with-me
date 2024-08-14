package ru.practicum.ewmmain.events.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmmain.events.dto.*;
import ru.practicum.ewmmain.events.enums.EventState;
import ru.practicum.ewmmain.events.params.EventAdminSearchParam;
import ru.practicum.ewmmain.events.params.EventPublicSearchParam;

import java.util.List;

public interface EventService {
    EventLongDto create(Long userId, CreateEventDto createEventDto);

    EventLongDto updateByUser(Long userId, Long eventId, UpdateEventDto updateDto);

    EventLongDto updateByAdmin(Long eventId, UpdateEventDto updateDto);

    EventLongDto getByUserId(Long userId, Long eventId);

    List<EventShortDto> getAllByUser(Long userId, EventState state, Pageable pageable);

    List<EventLongDto> getEventsForAdmin(EventAdminSearchParam param);

    List<EventShortDto> getPublicEvents(EventPublicSearchParam param);

    PublicEventLongDto getPublicById(Long id);

    ModerationCommentDto createModerationComment(Long eventId, CreateModerationCommentDto commentDto);

    ModerationCommentDto updateModerationComment(Long comId, CreateModerationCommentDto commentDto);

    List<EventLongDto> getPendingEvents(Pageable pageable);
}
