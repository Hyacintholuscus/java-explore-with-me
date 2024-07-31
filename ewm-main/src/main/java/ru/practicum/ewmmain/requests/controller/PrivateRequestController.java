package ru.practicum.ewmmain.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.requests.dto.RequestDto;
import ru.practicum.ewmmain.requests.dto.UpdateStRequestDto;
import ru.practicum.ewmmain.requests.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}")
public class PrivateRequestController {
    private final RequestService requestService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/requests")
    public RequestDto createRequestDto(@PathVariable
                                           @Positive(message = "User's id should be positive")
                                           Long userId,
                                       @RequestParam
                                       @Positive(message = "Event's id should be positive")
                                       Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable
                                        @Positive(message = "User's id should be positive")
                                        Long userId,
                                    @PathVariable
                                    @Positive(message = "Request's id should be positive")
                                    Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public Map<String, List<RequestDto>> changeStateRequest(@PathVariable
                                                                @Positive(message = "User's id should be positive")
                                                                Long userId,
                                                            @PathVariable
                                                            @Positive(message = "Event's id should be positive")
                                                            Long eventId,
                                                            @Valid @RequestBody UpdateStRequestDto updateDto) {
        if (updateDto.getRequestIds().isEmpty()) {
            return new HashMap<>();
        } else return requestService.changeStatusRequests(userId, eventId, updateDto);
    }

    @GetMapping("/requests")
    public List<RequestDto> getUserRequests(@PathVariable
                                            @Positive(message = "User's id should be positive")
                                            Long userId) {
        return requestService.getByUserRequests(userId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<RequestDto> getUserEventRequests(@PathVariable
                                                     @Positive(message = "User's id should be positive")
                                                     Long userId,
                                                 @PathVariable
                                                 @Positive(message = "Event's id should be positive")
                                                 Long eventId) {
        return requestService.getByUserEventRequests(userId, eventId);
    }
}
