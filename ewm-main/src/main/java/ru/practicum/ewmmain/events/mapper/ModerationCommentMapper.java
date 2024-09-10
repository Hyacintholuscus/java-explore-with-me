package ru.practicum.ewmmain.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewmmain.events.dto.ModerationCommentDto;
import ru.practicum.ewmmain.events.model.ModerationComment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModerationCommentMapper {
    @Mapping(source = "comment.eventId", target = "event")
    ModerationCommentDto toDto(ModerationComment comment);
}
