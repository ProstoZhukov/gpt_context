package ru.tensor.sbis.video_monitoring_decl.model

/**
 * Статус камеры
 *
 * @property OFFLINE оффлайн
 * @property ONLINE онлайн
 * @property NOT_RESPONDED не ответил за положенное время
 * @property NOT_AVAILABLE сервис видеонаблюдения недоступен
 * @property PARAMS_NOT_DEFINED не указаны настройки камеры
 * @property NO_STREAM камера добавлена, но поток не отдается, равносильно статусу [OFFLINE]
 * @property NOT_REQUESTED не опрашивалась
 * @property NOT_A_CAMERA не камера
 */
enum class CameraStatus {
    OFFLINE,
    ONLINE,
    NOT_RESPONDED,
    NOT_AVAILABLE,
    PARAMS_NOT_DEFINED,
    NO_STREAM,
    NOT_REQUESTED,
    NOT_A_CAMERA
}
