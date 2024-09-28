package ru.tensor.sbis.mediaplayer.zoom

/**
 * Перечисление состояний зумирования плеера
 *
 * @author as.chadov
 */
enum class ZoomState {
    /** Не используется */
    NONE,

    /** Перемещение */
    DRAG,

    /** Зумирование (сведение/разведение) */
    ZOOM
}