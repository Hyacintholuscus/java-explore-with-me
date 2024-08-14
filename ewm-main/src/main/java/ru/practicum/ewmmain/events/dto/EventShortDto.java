package ru.practicum.ewmmain.events.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewmmain.categories.dto.CategoryDto;
import ru.practicum.ewmmain.user.dto.InitiatorDto;

import java.time.LocalDateTime;

@Value
@Builder
public class EventShortDto {
    Long id;
    InitiatorDto initiator;
    String title;
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    Boolean paid;
    LocalDateTime eventDate;
    Integer views;
}
