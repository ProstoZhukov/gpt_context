package ru.tensor.sbis.communicator.core.data.events

import org.apache.commons.lang3.StringUtils

/** @SelfDocumented */
enum class MessagesEvent(val type: String, val value: String = StringUtils.EMPTY) {
    NETWORK_ERROR("error", "network"),
    OTHER_ERROR("error", "other"),
    UNATTACHED_PHONE_NUMBER_ERROR("error", "unattached_phone_number"),

    NETWORK_AVAILABLE("network", "available"),

    REGISTRY("registry", "changed"),
    NEWER("newer", "added"),
    OLDER("older", "added"),
    THEME_ID("theme_id", "theme_id"),
    REQUEST_ID("request_id"),
    QUOTED_MESSAGE_REMOVED("cursor_message_removed", "permanently"),
    FROM_UUID("fromUUID",""),

    //ThemeController события для обновления информации о переписке
    AFFECTED_THEMES_ANY("affected_themes", "any"),
    AFFECTED_THEMES_LIST("affected_themes"),
    THEME("theme"),
    MESSAGE_ID("message_id"),

    DIRECTION_TO_OLDER("direction", "to_older"),
    DIRECTION_TO_NEWER("direction", "to_newer"),
    DIRECTION_TO_BOTH("direction", "to_both"),

    HAS_NEWER("has_more_newer", "true"),
    HAS_OLDER("has_more_older", "true"),
    HAS_NOT_NEWER(HAS_NEWER.type, "false"),
    HAS_NOT_OLDER(HAS_OLDER.type, "false"),

    //ThemeController SabyChats
    @Suppress("unused")
    DOCUMENT_UUID("document_uuid"),
    @Suppress("unused")
    THEME_UUID_CHANGED("theme_uuid_changed"),

    THEME_REMOVED_PERMANENTLY("theme_removed", "permanently"),

    MESSAGE_MODEL("message_model"),

    MESSAGE_STATUS("message_status"),

    //ThemeController CRM
    OLD_THEME_UUID("old_theme_uuid", "old_theme_uuid");

    fun isExistsIn(params: HashMap<String, String>): Boolean {
        return params[type] == value
    }
}

