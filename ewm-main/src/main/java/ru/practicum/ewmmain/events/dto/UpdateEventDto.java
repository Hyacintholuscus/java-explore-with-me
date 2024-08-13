package ru.practicum.ewmmain.events.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewmmain.events.enums.StateAction;
import ru.practicum.ewmmain.events.model.Location;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Value
@Builder
public class UpdateEventDto {
    @Size(min = 3, max = 120, message = "Annotation of event shouldn't be less than 3 and more than 120 characters")
    String title;
    @Size(min = 20, max = 2000, message = "Annotation of event shouldn't be less than 20 and more than 2000 characters")
    String annotation;
    @Size(min = 20, max = 7000, message = "Description of event shouldn't be less than 20 and more than 7000 characters")
    String description;
    @PositiveOrZero(message = "Category of event shouldn't be negative")
    Long category;
    @PositiveOrZero(message = "Participant limit of event shouldn't be negative")
    Integer participantLimit;
    Boolean paid;
    Location location;
    @Future(message = "Event date should be in future")
    LocalDateTime eventDate;
    Boolean requestModeration;
    StateAction stateAction;
}
