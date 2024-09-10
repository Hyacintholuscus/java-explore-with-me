package ru.practicum.ewmmain.events.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.events.model.ModerationComment;

import java.util.List;
import java.util.Set;

public interface ModerationCommentStorage extends JpaRepository<ModerationComment, Long> {
    Set<ModerationComment> findByEventIdInOrderByEventId(List<Long> eventsId);
}
