package ru.tensor.sbis.crud4.hierarchy_storage

/**
 * События отображающие работу пользователя с компонентом swipeBack
 *
 * @author ma.kolpakov
 */
internal enum class SwipeBackEvent {
    IDLE,
    START,
    END,
    END_BACK
}