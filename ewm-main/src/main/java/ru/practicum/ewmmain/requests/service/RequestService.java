package ru.practicum.ewmmain.requests.service;

import ru.practicum.ewmmain.requests.dto.RequestDto;
import ru.practicum.ewmmain.requests.dto.UpdateStRequestDto;

import java.util.List;
import java.util.Map;

public interface RequestService {
    RequestDto createRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    Map<String, List<RequestDto>> changeStatusRequests(Long userId, Long eventId, UpdateStRequestDto updateDto);

    List<RequestDto> getByUserRequests(Long userId);

    List<RequestDto> getByUserEventRequests(Long userId, Long eventId);
}
