package ru.practicum.ewmmain.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.categories.model.Category;
import ru.practicum.ewmmain.categories.storage.CategoryStorage;
import ru.practicum.ewmmain.events.dto.*;
import ru.practicum.ewmmain.events.enums.EventSort;
import ru.practicum.ewmmain.events.enums.StateAction;
import ru.practicum.ewmmain.events.mapper.ModerationCommentMapper;
import ru.practicum.ewmmain.events.enums.CommentStatus;
import ru.practicum.ewmmain.events.enums.EventState;
import ru.practicum.ewmmain.events.model.ModerationComment;
import ru.practicum.ewmmain.events.params.EventAdminSearchParam;
import ru.practicum.ewmmain.events.params.EventPublicSearchParam;
import ru.practicum.ewmmain.events.storage.ModerationCommentStorage;
import ru.practicum.ewmmain.stat.service.StatsService;
import ru.practicum.ewmmain.events.mapper.EventMapper;
import ru.practicum.ewmmain.events.model.Event;
import ru.practicum.ewmmain.events.storage.EventStorage;
import ru.practicum.ewmmain.exception.ConflictException;
import ru.practicum.ewmmain.exception.ForbiddenException;
import ru.practicum.ewmmain.exception.NotFoundException;
import ru.practicum.ewmmain.exception.NotValidException;
import ru.practicum.ewmmain.user.model.User;
import ru.practicum.ewmmain.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventStorage eventStorage;
    private final EventMapper eventMapper;
    private final UserStorage userStorage;
    private final CategoryStorage categoryStorage;
    private final StatsService statsService;
    private final ModerationCommentStorage modCommentStorage;
    private final ModerationCommentMapper modCommentMapper;

    private Category getCategory(Long categoryId) {
        return categoryStorage.findById(categoryId).orElseThrow(() -> {
            log.error("NotFound. Категории с id {} не существует.", categoryId);
            return new NotFoundException("Event's category doesn't exists.");
        });
    }

    private void throwNotValidTime(Long eventId) {
        log.error("NotValid. Дата и время события с id {} от пользователя " +
                "менее 2 часов от текущего момента.", eventId);
        throw new NotValidException("The date and time of the event cannot be earlier " +
                "than two hours from the current moment");
    }

    @Override
    public EventLongDto create(Long userId, CreateEventDto createEventDto) {
        log.info("Запрос на создание события от пользователя с id {}", userId);

        User initiator = userStorage.findById(userId).orElseThrow(() -> {
            log.error("Forbidden. Несуществующий пользователь с id {} пытается взаимодействовать с событием.",
                    userId);
            return new ForbiddenException("You haven't access. Please, log in.");
        });
        Category category = getCategory(createEventDto.getCategory());

        if (createEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throwNotValidTime(userId);
        }
        Event event = eventStorage.save(eventMapper.toEvent(createEventDto, initiator, category, EventState.PENDING));
        return eventMapper.toLongDto(event);
    }

    private Event updateFields(Event event, UpdateEventDto updateDto, String initiator) {
        int i = initiator.equals("admin") ? 1 : 2;

        if (updateDto.getTitle() != null) event.setTitle(updateDto.getTitle());
        if (updateDto.getAnnotation() != null) event.setAnnotation(updateDto.getAnnotation());
        if (updateDto.getDescription() != null) event.setDescription(updateDto.getDescription());
        if (updateDto.getCategory() != null) event.setCategory(getCategory(updateDto.getCategory()));
        if (updateDto.getParticipantLimit() != null) event.setParticipantLimit(updateDto.getParticipantLimit());
        if (updateDto.getPaid() != null) event.setPaid(updateDto.getPaid());
        if (updateDto.getLocation() != null) {
            if (updateDto.getLocation().getLat() != null) {
                event.setLat(updateDto.getLocation().getLat());
            } else if (updateDto.getLocation().getLon() != null) {
                event.setLon(updateDto.getLocation().getLon());
            }
        }
        if (updateDto.getEventDate() != null) {
            if (updateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(i))) {
                throwNotValidTime(event.getId());
            } else event.setEventDate(updateDto.getEventDate());
        }
        if (updateDto.getRequestModeration() != null) event.setRequestModeration(updateDto.getRequestModeration());
        return event;
    }

    @Override
    public EventLongDto updateByUser(Long userId, Long eventId, UpdateEventDto updateDto) {
        log.info("Запрос на обновление события с id {} от пользователя с id {}", eventId, userId);

        Event toUpdateEvent = eventStorage.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            log.error("NotFound. Обновление пользователем. Событие с id {} пользователя с id {} не найдено.",
                    eventId, userId);
            return new NotFoundException(String.format("Event with id = %d was not found", eventId));
        });

        if (toUpdateEvent.getState().equals(EventState.PUBLISHED)) {
            log.error("Conflict. Попытка изменить опубликованное событие с id {} от пользователя с id {}",
                    eventId, userId);
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        Event event = updateFields(toUpdateEvent, updateDto, "user");
        if (updateDto.getStateAction() != null
                && updateDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(EventState.PENDING);
        } else if (updateDto.getStateAction() != null
                && updateDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            event.setState(EventState.CANCELED);
        }

        Event updatedEvent = eventStorage.save(event);
        return eventMapper.toLongDto(updatedEvent);
    }

    private Event getById(Long eventId) {
        return eventStorage.findById(eventId).orElseThrow(() -> {
            log.error("NotFound. Запрос от дминистратора. Событие с id {} не найдено.", eventId);
            return new NotFoundException(String.format("Event with id = %d was not found", eventId));
        });
    }

    @Override
    public EventLongDto updateByAdmin(Long eventId, UpdateEventDto updateDto) {
        log.info("Запрос на обновление события с id {} от администратора", eventId);

        Event toUpdateEvent = getById(eventId);

        if (updateDto.getStateAction() != null && updateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)
                && toUpdateEvent.getState().equals(EventState.PENDING)) {
            toUpdateEvent.setState(EventState.PUBLISHED);
            toUpdateEvent.setPublishedOn(LocalDateTime.now());
        } else if (updateDto.getStateAction() != null && updateDto.getStateAction().equals(StateAction.REJECT_EVENT)
                && !toUpdateEvent.getState().equals(EventState.PUBLISHED)) {
            toUpdateEvent.setState(EventState.CANCELED);
        } else if (updateDto.getStateAction() != null) {
            log.error("Conflict. Статус события ({}) не подходит для публикации / отклонения.",
                    toUpdateEvent.getState().toString());
            throw new ConflictException("Cannot publish or reject the event because it's not in the right state");
        }

        Event event = updateFields(toUpdateEvent, updateDto, "admin");
        Event updatedEvent = eventStorage.save(event);
        return eventMapper.toLongDto(updatedEvent);
    }

    @Transactional(readOnly = true)
    @Override
    public EventLongDto getByUserId(Long userId, Long eventId) {
        log.info("Запрос на получение события с id {}", eventId);

        Event event = eventStorage.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            log.error("NotFound. Событие с id {} пользователя с id {} не найдено.", eventId, userId);
            return new NotFoundException(String.format("Event with id = %d was not found", eventId));
        });
        Map<Long, Long> view = statsService.getView(new ArrayList<>(List.of(event.getId())), false);
        return eventMapper.toLongDto(event, view.getOrDefault(event.getId(), 0L));
    }

    private Map<Long, Long> getView(List<Event> events, boolean unique) {
        if (!events.isEmpty()) {
            List<Long> eventsId = events.stream()
                    .map(Event::getId)
                    .collect(Collectors.toList());
            return statsService.getView(eventsId, unique);
        } else return new HashMap<>();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAllByUser(Long userId, EventState state, Pageable pageable) {
        log.info("Запрос на получение событий от пользователя с id {}", userId);

        List<Event> events = (state != null)
                ? eventStorage.findByInitiatorIdAndState(userId, state, pageable)
                : eventStorage.findByInitiatorId(userId, pageable);
        Map<Long, Long> view = getView(events, false);
        return events.stream()
                .map(e -> eventMapper.toShortDto(e, view.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventLongDto> getEventsForAdmin(EventAdminSearchParam param) {
        log.info("Запрос от администратора на получение событий");

        List<Event> events = eventStorage.searchEventsForAdmin(param);
        Map<Long, Long> view = getView(events, false);
        return events.stream()
                .map(e -> eventMapper.toLongDto(e, view.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PublicEventShortDto> getPublicEvents(EventPublicSearchParam param) {
        log.info("Запрос получить опубликованные события");

        if (param.getStart().isAfter(param.getEnd())) {
            log.error("NotValid. При поиске опубликованных событий rangeStart после rangeEnd.");
            throw new NotValidException("The start of the range must be before the end of the range.");
        }

        List<Event> events = eventStorage.searchPublicEvents(param);

        Comparator<PublicEventShortDto> comparator = Comparator.comparing(PublicEventShortDto::getId);

        if ((param.getSort() != null) && (param.getSort().equals(EventSort.EVENT_DATE))) {
            comparator = Comparator.comparing(PublicEventShortDto::getEventDate);
        } else if ((param.getSort() != null) && (param.getSort().equals(EventSort.VIEWS))) {
            comparator = Comparator.comparing(PublicEventShortDto::getViews, Comparator.reverseOrder());
        }

        Map<Long, Long> view = getView(events, false);
        return events.stream()
                .map(e -> eventMapper.toPublicShortDto(e, view.getOrDefault(e.getId(), 0L)))
                .sorted(comparator)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    @Override
    public PublicEventLongDto getPublicById(Long id) {
        log.info("Запрос получить опубликованное событие с id {}", id);

        Event event = eventStorage.findPublicEventById(id).orElseThrow(() -> {
            log.error("NotFound. Событие с id {} не найдено.", id);
            return new NotFoundException(String.format("Event with id = %d was not found", id));
        });
        Map<Long, Long> view = statsService.getView(new ArrayList<>(List.of(event.getId())), true);
        return eventMapper.toPublicLongDto(event, view.getOrDefault(event.getId(), 0L));
    }

    @Override
    public ModerationCommentDto createModerationComment(Long eventId, CreateModerationCommentDto commentDto) {
        log.info("Запрос создать комментарий к событию с id {}, отправленному на доработку", eventId);

        Event event = getById(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            log.error("Conflict. Статус события ({}) не подходит для создания комментария.", event.getState());
            throw new ConflictException("Cannot create comment for this event because it is published");
        }

        ModerationComment comment = modCommentStorage.save(ModerationComment.builder()
                .text(commentDto.getText())
                .eventId(eventId)
                .status(CommentStatus.CREATED)
                .created(LocalDateTime.now())
                .build());
        return modCommentMapper.toDto(comment);
    }

    @Override
    public ModerationCommentDto updateModerationComment(Long comId, CreateModerationCommentDto commentDto) {
        log.info("Запрос обновить комментарий с id {}", comId);

        ModerationComment comment = modCommentStorage.findById(comId).orElseThrow(() -> {
            log.error("NotFound. Комментарий с id {} не найден.", comId);
            return new NotFoundException(String.format("Comment with id = %d was not found", comId));
        });
        if (!commentDto.getText().equals(comment.getText())) {
            comment.setText(commentDto.getText());
            comment.setStatus(CommentStatus.EDITED);
            return modCommentMapper.toDto(modCommentStorage.save(comment));
        } else return modCommentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventLongDto> getPendingEvents(Pageable pageable) {
        log.info("Запрос от администратора получить список событий, ожидающих модерацию");

        return eventStorage.findPendingEvents(pageable).stream()
                .map(e -> eventMapper.toLongDto(e, 0L))
                .collect(Collectors.toList());
    }
}
