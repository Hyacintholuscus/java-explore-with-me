package ru.practicum.ewmmain.events.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewmmain.categories.dto.CategoryDto;
import ru.practicum.ewmmain.events.model.EventState;
import ru.practicum.ewmmain.events.model.Location;
import ru.practicum.ewmmain.user.dto.InitiatorDto;

import java.time.LocalDateTime;

@Value
@Builder
public class EventLongDto {
    Long id;
    InitiatorDto initiator;
    String title;
    String annotation;
    String description;
    CategoryDto category;
    Integer participantLimit;
    Integer confirmedRequests;
    Boolean paid;
    Location location;
    LocalDateTime eventDate;
    LocalDateTime createdOn;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    Long views;
}
