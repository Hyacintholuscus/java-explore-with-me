package ru.practicum.ewmmain.requests.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.requests.model.Request;

import java.util.List;

public interface RequestStorage extends JpaRepository<Request, Long> {
    Request findByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findByIdInAndEventId(List<Long> requestIds, Long eventId);

    List<Request> findByRequesterId(Long requesterId);

    List<Request> findByEventId(Long eventId);
}
