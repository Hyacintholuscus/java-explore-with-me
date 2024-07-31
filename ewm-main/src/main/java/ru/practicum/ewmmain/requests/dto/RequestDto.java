package ru.practicum.ewmmain.requests.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewmmain.requests.model.RequestStatus;

import java.time.LocalDateTime;

@Value
@Builder
public class RequestDto {
    Long id;
    Long requester;
    Long event;
    RequestStatus status;
    LocalDateTime created;
}
