package ru.practicum.ewmmain.events.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewmmain.events.model.Event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventStorage extends JpaRepository<Event, Long>, CustomizedEventStorage {
    List<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    @Query("select e from Event e " +
            "where e.id = :id " +
            "and e.state = 'PUBLISHED'")
    Optional<Event> findPublicEventById(@Param("id")Long id);

    Set<Event> findByIdIn(Collection<Long> eventsId);
}
