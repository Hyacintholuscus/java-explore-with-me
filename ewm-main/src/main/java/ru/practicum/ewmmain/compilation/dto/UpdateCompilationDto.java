package ru.practicum.ewmmain.compilation.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Size;
import java.util.Set;

@Value
@Builder
public class UpdateCompilationDto {
    @Size(max = 50, message = "Title of compilation shouldn't be more than 50 characters")
    String title;
    Boolean pinned;
    Set<Long> events;
}
