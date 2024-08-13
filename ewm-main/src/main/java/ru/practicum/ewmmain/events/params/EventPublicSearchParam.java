package ru.practicum.ewmmain.events.params;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewmmain.events.enums.EventSort;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventPublicSearchParam {
    private String text;
    private List<Long> categoriesId;
    private Boolean paid;
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean onlyAvailable;
    private EventSort sort;
    private int from;
    private int size;
}
