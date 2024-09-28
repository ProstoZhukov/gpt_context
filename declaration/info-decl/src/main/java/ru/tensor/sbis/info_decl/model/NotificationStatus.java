package ru.tensor.sbis.info_decl.model;

/**
 * Created by am.boldinov on 30.05.16.
 */
public enum NotificationStatus {

    DEFAULT(0),
    DELETING(1);

    private final int value;

    NotificationStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static NotificationStatus fromValue(final int value) {
        for (NotificationStatus s : NotificationStatus.values()) {
            if (s.value == value) {
                return s;
            }
        }
        return null;
    }

}
