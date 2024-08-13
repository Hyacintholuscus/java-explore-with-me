package ru.practicum.ewmmain.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.events.enums.EventState;
import ru.practicum.ewmmain.events.model.Event;
import ru.practicum.ewmmain.events.storage.EventStorage;
import ru.practicum.ewmmain.exception.ConflictException;
import ru.practicum.ewmmain.exception.ForbiddenException;
import ru.practicum.ewmmain.exception.NotFoundException;
import ru.practicum.ewmmain.requests.model.RequestStatus;
import ru.practicum.ewmmain.requests.dto.RequestDto;
import ru.practicum.ewmmain.requests.dto.UpdateStRequestDto;
import ru.practicum.ewmmain.requests.mapper.RequestMapper;
import ru.practicum.ewmmain.requests.model.Request;
import ru.practicum.ewmmain.requests.storage.RequestStorage;
import ru.practicum.ewmmain.user.model.User;
import ru.practicum.ewmmain.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestStorage requestStorage;
    private final RequestMapper requestMapper;
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    private void throwConflictLimit(Long eventId) {
        log.error("Conflict. У события с id {} достигнут лимитом участников.", eventId);
        throw new ConflictException("The participant limit has been reached");
    }

    private void throwNotFoundEvent(Long eventId, Long userId) {
        log.error("NotFound. Событие с id {} у пользователя с id {} не существует.", eventId, userId);
        throw new NotFoundException(String.format("Event with id = %d was not found", eventId));
    }

    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        log.info("Запрос создать запрос на участие в событии с id {} от пользователя с id {}.", eventId, userId);

        // Проверка на повторный запрос
        Request request = requestStorage.findByRequesterIdAndEventId(userId, eventId);
        if (request != null) {
            log.error("Conflict. Повторный запрос на участие от пользователя с id {} в событии с id {}.",
                    userId, eventId);
            throw new ConflictException("You can't add a repeat request");
        }

        // Проверка, существует ли событие
        Event event = eventStorage.findById(eventId).orElseThrow(() -> {
            log.error("NotFound. Взаимодействие с запросом по не найденному событию с id {}.", eventId);
            return new NotFoundException(String.format("Event with id = %d was not found", eventId));
        });

        // Проверка статуса и лимита события
        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.error("Conflict. Пользователь с id {} пытается запросить участие " +
                            "в неопубликованном событии с id {}.", userId, eventId);
            throw new ConflictException("You can't participate in an unpublished event");
        } else if (!event.getParticipantLimit().equals(0)
                && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throwConflictLimit(eventId);
        }

        // Проверка, существует ли пользователь, и является ли он инициатором события
        User requester = userStorage.findById(userId).orElseThrow(() -> {
            log.error("Forbidden. Несуществующий пользователь с id {} пытается взаимодействовать с запросом.",
                    userId);
            return new ForbiddenException("You haven't access. Please, log in");
        });
        if (event.getInitiator().getId().equals(requester.getId())) {
            log.error("Conflict. Пользователь с id {} пытается запросить участие в своём событии с id {}.", userId, eventId);
            throw new ConflictException("The event initiator cannot add a request to participate in his event");
        }

        RequestStatus status;
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            status = RequestStatus.CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventStorage.save(event);
        } else status = RequestStatus.PENDING;
        Request newRequest = requestStorage.save(new Request(null,
                requester,
                event,
                status,
                LocalDateTime.now()));
        return requestMapper.toDto(newRequest);
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Запрос отменить запрос с id {} от пользователя с id {}.", requestId, userId);

        Request request = requestStorage.findById(requestId).orElseThrow(() -> {
            log.error("NotFound. Запроса с id {} не существует.", requestId);
            return new NotFoundException(String.format("Request with id = %d was not found", requestId));
        });
        if (request.getRequester().getId().equals(userId)) {
            request.setStatus(RequestStatus.CANCELED);
            Request closedRequest = requestStorage.save(request);
            return requestMapper.toDto(closedRequest);
        } else {
            log.error("NotFound. Запроса с id {} у пользователя с id {} не существует.", requestId, userId);
            throw new NotFoundException(String.format("Request with id = %d was not found", requestId));
        }
    }

    @Override
    public Map<String, List<RequestDto>> changeStatusRequests(Long userId, Long eventId,
                                                              UpdateStRequestDto updateDto) {
        log.info("Запрос изменить статус запросов на участие в событии с id {} от пользователя с id {}.",
                eventId, userId);

        List<Request> requests = requestStorage.findByIdInAndEventId(updateDto.getRequestIds(), eventId);
        if ((requests.size() != updateDto.getRequestIds().size())) {
            log.error("NotFound. Запроса/ов или события с id {} не существует.", eventId);
            throw new NotFoundException(String.format("Request/s or event with id = %d was not found", eventId));
        } else if (!requests.stream().allMatch(r -> r.getStatus().equals(RequestStatus.PENDING))) {
            log.error("Conflict. Изменение статуса заявок, не находящихся в состоянии PENDING.");
            throw new ConflictException("The status can only be changed" +
                    " for applications that are in a pending status");
        }

        Event event = requests.get(0).getEvent();
        if (!event.getInitiator().getId().equals(userId)) throwNotFoundEvent(eventId, userId);

        switch (updateDto.getStatus()) {
            case CONFIRMED:
                return confirmRequests(requests, event);
            case REJECTED:
                return rejectRequests(requests);
            default:
                return new HashMap<>();
        }
    }

    private Map<String, List<RequestDto>> confirmRequests(List<Request> requests, Event event) {
        if (!event.getParticipantLimit().equals(0) && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throwConflictLimit(event.getId());
        }

        Map<String, List<RequestDto>> result = new HashMap<>();
        List<RequestDto> confirmedRequests;

        // Проверка, будет ли достигнут лимит во время принятия заявок
        int difference = event.getParticipantLimit() - event.getConfirmedRequests();
        int countRequests = requests.size();
        if (difference < countRequests) {
            List<Request> confirmed = requests.subList(0, difference);
            confirmed.forEach(r -> r.setStatus(RequestStatus.CONFIRMED));
            confirmedRequests = confirmed.stream()
                    .map(requestMapper::toDto)
                    .collect(Collectors.toList());
            List<Request> rejected = new ArrayList<>(requests.subList(difference, countRequests));
            rejected.forEach(r -> r.setStatus(RequestStatus.REJECTED));
            List<RequestDto> rejectedRequests = rejected.stream()
                    .map(requestMapper::toDto)
                    .collect(Collectors.toList());
            result.put("rejectedRequests", rejectedRequests);
            requestStorage.saveAll(requests);
            // Увеличение количества принятых заявок в модели event
            event.setConfirmedRequests(event.getConfirmedRequests() + difference);
            eventStorage.save(event);
        } else {
            requests.forEach(r -> r.setStatus(RequestStatus.CONFIRMED));
            confirmedRequests = requestStorage.saveAll(requests).stream()
                    .map(requestMapper::toDto)
                    .collect(Collectors.toList());

            // Увеличение количества принятых заявок в модели event
            event.setConfirmedRequests(event.getConfirmedRequests() + countRequests);
            eventStorage.save(event);
        }
        result.put("confirmedRequests", confirmedRequests);
        return result;
    }

    private Map<String, List<RequestDto>> rejectRequests(List<Request> requests) {
        requests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
        List<RequestDto> rejectedRequests = requestStorage.saveAll(requests).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
        Map<String, List<RequestDto>> rejected = new HashMap<>();
        rejected.put("rejectedRequests", rejectedRequests);
        return rejected;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getByUserRequests(Long userId) {
        log.info("Запрос получить запросы на участие в событиях пользователя с id {}.", userId);

        return requestStorage.findByRequesterId(userId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getByUserEventRequests(Long userId, Long eventId) {
        log.info("Запрос получить запросы на участие в событии с id {} пользователя с id {}.", eventId, userId);

        List<Request> requests = requestStorage.findByEventId(eventId);
        if (requests.isEmpty()) return new ArrayList<>();
        if (requests.get(0).getRequester().getId().equals(userId)) throwNotFoundEvent(eventId, userId);

        return requests.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }
}
