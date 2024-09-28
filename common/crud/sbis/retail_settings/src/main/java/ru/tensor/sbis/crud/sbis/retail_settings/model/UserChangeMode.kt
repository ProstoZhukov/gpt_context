package ru.tensor.sbis.crud.sbis.retail_settings.model

import ru.tensor.sbis.retail_settings.generated.UserChangeMode as ControllerUserChangeMode

/**
 * Перечисление типов вариантов входа пользователя: PINCODE, LOGIN
 */
enum class UserChangeMode {

    PINCODE,
    LOGIN
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerUserChangeMode.map(): UserChangeMode =
        when (this) {
            ControllerUserChangeMode.PINCODE -> UserChangeMode.PINCODE
            ControllerUserChangeMode.LOGIN -> UserChangeMode.LOGIN
        }

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun UserChangeMode.map(): ControllerUserChangeMode =
        when (this) {
            UserChangeMode.PINCODE -> ControllerUserChangeMode.PINCODE
            UserChangeMode.LOGIN -> ControllerUserChangeMode.LOGIN
        }