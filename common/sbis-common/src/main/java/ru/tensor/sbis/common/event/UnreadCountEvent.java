package ru.tensor.sbis.common.event;

/**
 * @author am.boldinov
 */
public class UnreadCountEvent {

    public enum EventType {
        TASKS_TOTAL,
        TASKS_UNREAD,
        TASKS_FROM_ME,
        TASKS_FROM_ME_OVERDUE
    }

    public final EventType eventCode;

    public UnreadCountEvent(EventType eventCode) {
        this.eventCode = eventCode;
    }
}
