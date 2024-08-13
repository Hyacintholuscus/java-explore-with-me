package ru.practicum.ewmmain.user.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InitiatorDto {
    Long id;
    String name;
}
