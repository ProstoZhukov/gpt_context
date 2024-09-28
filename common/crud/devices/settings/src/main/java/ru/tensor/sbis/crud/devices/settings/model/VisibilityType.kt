package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.devices.settings.generated.VisibilityType as ControllerVisibilityType

/**
 * Перечисление типов видимости: SHOW_ALL, VISIBLE_ONLY, INVISIBLE_ONLY
 */
enum class VisibilityType {
    SHOW_ALL, VISIBLE_ONLY, INVISIBLE_ONLY;
}

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun VisibilityType.map(): ControllerVisibilityType =
        when (this) {
            VisibilityType.VISIBLE_ONLY -> ControllerVisibilityType.VISIBLE_ONLY
            VisibilityType.INVISIBLE_ONLY -> ControllerVisibilityType.INVISIBLE_ONLY
            VisibilityType.SHOW_ALL -> ControllerVisibilityType.SHOW_ALL
        }

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerVisibilityType.map(): VisibilityType =
        when (this) {
            ControllerVisibilityType.VISIBLE_ONLY -> VisibilityType.VISIBLE_ONLY
            ControllerVisibilityType.INVISIBLE_ONLY -> VisibilityType.INVISIBLE_ONLY
            ControllerVisibilityType.SHOW_ALL -> VisibilityType.SHOW_ALL
        }
