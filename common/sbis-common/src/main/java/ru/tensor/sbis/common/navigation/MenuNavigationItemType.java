package ru.tensor.sbis.common.navigation;

/**
 * Created by aa.mironychev on 30.03.17.
 */

import androidx.annotation.NonNull;

/**
 * Declare navigation drawer menu contract
 */
public enum MenuNavigationItemType {

    //region Saby Clients
    RETAIL_ORDERS_SALON,
    QUEUES_SCHEDULE,
    //endregion

    MESSAGES,
    DIALOGS,
    CHATS,
    EMPLOYEES_SECTION,
    EMPLOYEES,
    CONTACTS,
    NOTIFICATIONS,
    NEWS,
    KNOWLEDGE,
    INSTRUCTIONS,
    DISK,
    CALENDAR,
    MEETINGS,
    CALL_HISTORY,
    I_AM_HERE,
    MAIN_PAGE,
    MY_TASKS,
    TASKS_FROM_ME,
    CONTRACTORS,
    MY_MOTIVATION,
    MY_DOCS,
    FEATURE_MANAGE,
    //
    SETTINGS;
    //

    /**
     * Пункт меню по умолчанию
     */
    @SuppressWarnings("unused")
    public static final MenuNavigationItemType DEFAULT_ITEM = NOTIFICATIONS;

    public int toInt() {
        return ordinal();
    }

    @NonNull
    public static MenuNavigationItemType fromInt(int value, MenuNavigationItemType defaultValue) {
        MenuNavigationItemType[] items = values();
        if (value > -1 && value < items.length) {
            return items[value];
        }
        return defaultValue;
    }

}
