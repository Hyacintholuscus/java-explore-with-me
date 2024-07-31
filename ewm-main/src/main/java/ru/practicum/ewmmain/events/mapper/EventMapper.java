package ru.practicum.ewmmain.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewmmain.categories.mapper.CategoryMapper;
import ru.practicum.ewmmain.categories.model.Category;
import ru.practicum.ewmmain.events.model.EventState;
import ru.practicum.ewmmain.events.dto.CreateEventDto;
import ru.practicum.ewmmain.events.dto.EventLongDto;
import ru.practicum.ewmmain.events.dto.EventShortDto;
import ru.practicum.ewmmain.events.model.Event;
import ru.practicum.ewmmain.user.mapper.UserMapper;
import ru.practicum.ewmmain.user.model.User;

import java.time.LocalDateTime;

@Mapper(uses = {UserMapper.class, CategoryMapper.class}, componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {LocalDateTime.class})
public interface EventMapper {
    @Mapping(source = "views", target = "views")
    EventShortDto toShortDto(Event event, Long views);

    @Mapping(source = "event.lat", target = "location.lat")
    @Mapping(source = "event.lon", target = "location.lon")
    @Mapping(source = "views", target = "views")
    EventLongDto toLongDto(Event event, Long views);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(source = "createEventDto.location.lat", target = "lat")
    @Mapping(source = "createEventDto.location.lon", target = "lon")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "category", target = "category")
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "createdOn", expression = "java(LocalDateTime.now())")
    @Mapping(target = "confirmedRequests", constant = "0")
    Event toEvent(CreateEventDto createEventDto, User initiator, Category category, EventState state);
}
