package ru.practicum.ewmmain.compilation.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewmmain.events.dto.PublicEventShortDto;

import java.util.Set;

@Value
@Builder
public class CompilationDto {
    Long id;
    String title;
    Boolean pinned;
    Set<PublicEventShortDto> events;
}
