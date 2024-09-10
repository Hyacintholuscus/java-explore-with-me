package ru.practicum.ewmmain.events.params;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewmmain.events.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventAdminSearchParam {
    private List<Long> usersId;
    private List<EventState> states;
    private List<Long> categoriesId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Pageable pageable;
}
