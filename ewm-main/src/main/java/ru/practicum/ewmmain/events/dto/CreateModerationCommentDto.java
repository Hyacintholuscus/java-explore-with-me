package ru.practicum.ewmmain.events.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreateModerationCommentDto {
    @NotBlank(message = "Text of comment shouldn't be blank")
    @Size(min = 3, max = 5000, message = "Text of event shouldn't be less than 3 and more than 5000 characters")
    String text;
}
