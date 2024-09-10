package ru.practicum.ewmmain.events.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum AdminEventSort {
    EVENT_DATE_ASC(Sort.by(Sort.Direction.ASC, "eventDate")),
    EVENT_DATE_DESC(Sort.by(Sort.Direction.DESC, "eventDate")),
    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),
    ID_DESC(Sort.by(Sort.Direction.DESC, "id"));

    private final Sort sortValue;
}
