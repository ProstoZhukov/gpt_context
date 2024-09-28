package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.devices.settings.generated.InterfaceTheme as ControllerInterfaceTheme

/**
 * Перечисление цветов темы
 */
enum class InterfaceTheme {
    DARK,
    LIGHT,
}

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun InterfaceTheme.map(): ControllerInterfaceTheme =
        when (this) {
            InterfaceTheme.DARK -> ControllerInterfaceTheme.DARK
            InterfaceTheme.LIGHT -> ControllerInterfaceTheme.LIGHT
        }

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerInterfaceTheme.map(): InterfaceTheme =
        when (this) {
            ControllerInterfaceTheme.DARK -> InterfaceTheme.DARK
            ControllerInterfaceTheme.LIGHT -> InterfaceTheme.LIGHT
        }