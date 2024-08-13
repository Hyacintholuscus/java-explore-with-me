package ru.practicum.ewmmain.requests.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewmmain.requests.model.RequestStatus;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Value
@Builder
public class UpdateStRequestDto {
    @NotNull(message = "List of request's id shouldn't be null")
    List<Long> requestIds;
    @NotNull(message = "Status shouldn't be null")
    RequestStatus status;
}
