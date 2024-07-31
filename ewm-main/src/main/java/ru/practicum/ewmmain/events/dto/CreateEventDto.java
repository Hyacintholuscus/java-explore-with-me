package ru.practicum.ewmmain.events.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Builder;
import lombok.Value;
import ru.practicum.ewmmain.events.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Value
@Builder
public class CreateEventDto {
    @NotBlank(message = "Title of event shouldn't be blank")
    @Size(min = 3, max = 120, message = "Title of event shouldn't be less than 3 and more than 120 characters")
    String title;
    @NotBlank(message = "Annotation of event shouldn't be blank")
    @Size(min = 20, max = 2000, message = "Annotation of event shouldn't be less than 20 and more than 2000 characters")
    String annotation;
    @NotBlank(message = "Description of event shouldn't be blank")
    @Size(min = 20, max = 7000, message = "Description of event shouldn't be less than 20 and more than 7000 characters")
    String description;
    @NotNull(message = "Category of event shouldn't be null")
    @PositiveOrZero(message = "Category of event shouldn't be negative")
    Long category;
    @PositiveOrZero(message = "Participant limit of event shouldn't be negative")
    @JsonSetter(nulls = Nulls.SKIP)
    Integer participantLimit = 0;
    @JsonSetter(nulls = Nulls.SKIP)
    Boolean paid = false;
    Location location;
    @Future(message = "Event date should be in future")
    LocalDateTime eventDate;
    @JsonSetter(nulls = Nulls.SKIP)
    Boolean requestModeration = true;
    StateAction stateAction;
}
