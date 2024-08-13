package ru.practicum.ewmmain.events.storage;

import ru.practicum.ewmmain.events.model.Event;
import ru.practicum.ewmmain.events.params.EventAdminSearchParam;
import ru.practicum.ewmmain.events.params.EventPublicSearchParam;

import java.util.List;

public interface CustomizedEventStorage {
    List<Event> searchEventsForAdmin(EventAdminSearchParam param);

    List<Event> searchPublicEvents(EventPublicSearchParam param);
}
