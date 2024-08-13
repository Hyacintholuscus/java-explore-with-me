package ru.practicum.ewmstatsutil.dto;

import lombok.Builder;
import lombok.Value;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Value
@Builder
public class CreateStatsDto {
    Long id;
    @NotNull(message = "Parameter 'app' shouldn't be null.")
    @Pattern(regexp = "^ewm-main-service$", message = "Data must come from the main service.")
    String app;
    @NotNull(message = "URI shouldn't be null.")
    @Pattern(regexp = "^/events(/(?<!-)[1-9][0-9]{0,18})?$",
            message = "URI must match the correct format.")
    String uri;
    @NotNull(message = "IP shouldn't be null.")
    String ip;
    @NotNull(message = "Parameter 'timestamp' shouldn't be null.")
    @PastOrPresent(message = "Parameter 'timestamp' shouldn't be in future.")
    LocalDateTime timestamp;
}
