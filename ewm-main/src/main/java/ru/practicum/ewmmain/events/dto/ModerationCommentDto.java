package ru.practicum.ewmmain.events.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewmmain.events.enums.CommentStatus;

import java.time.LocalDateTime;

@Value
@Builder
public class ModerationCommentDto {
    Long id;
    String text;
    Long event;
    CommentStatus status;
    LocalDateTime created;
}
