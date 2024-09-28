package ru.tensor.sbis.common.navigation

import ru.tensor.sbis.verification_decl.permission.PermissionScope
import ru.tensor.sbis.verification_decl.permission.PermissionScope.Companion.UNKNOWN_SCOPE_ID

/**
 * Перечень областей [PermissionScope] для компонентов навигации. В комментариях к областям указана
 * соответствующая настройка в онлайне
 *
 * @author ma.kolpakov
 * Создан 12/17/2018
 */
enum class NavPermissionScope(
        override val id: String
) : PermissionScope {

    /**
     * Настройка: не определена
     */
    NOTIFICATIONS(UNKNOWN_SCOPE_ID),

    /**
     * Настройка: не определена
     */
    MESSAGES(UNKNOWN_SCOPE_ID),

    /**
     * Настройка: не определена
     */
    NEWS(UNKNOWN_SCOPE_ID),

    /**
     * Настройка: Контакты
     */
    CONTACTS("Контакты"),

    /**
     * Настройка: Задачи
     */
    TASKS("Дела"),

    /**
     * Настройка: не определена
     */
    DISK(UNKNOWN_SCOPE_ID),

    /**
     * Настройка: диск компании
     */
    DISK_SHARED("Общие"),

    /**
     * Настройка: Календарь
     */
    CALENDAR("Календарь"),

    /**
     * Настройка: ВХОДЯЩИЕ документы
     */
    IN_DOCUMENT("Входящие"),

    /**
     * Настройка: ИСХОДЯЩИЕ документы
     */
    OUT_DOCUMENT("Исходящие")
}
