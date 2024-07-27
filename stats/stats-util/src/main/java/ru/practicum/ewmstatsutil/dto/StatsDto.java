package ru.practicum.ewmstatsutil.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StatsDto {
    String app;
    String uri;
    Long hits;
}
