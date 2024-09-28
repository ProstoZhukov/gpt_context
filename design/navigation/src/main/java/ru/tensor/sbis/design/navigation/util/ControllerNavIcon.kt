package ru.tensor.sbis.design.navigation.util

import androidx.annotation.StringRes

/**
 * Модель иконок приходящих с контроллера.
 *
 * Поля сделаны nullable, чтобы иметь возможность обработки в Rx "пустого" значения.
 *
 * @author da.zolotarev
 */
data class ControllerNavIcon(
    @StringRes val default: Int? = null,
    @StringRes val selected: Int? = null
)
