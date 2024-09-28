package ru.tensor.sbis.design.view.input.base

import android.content.res.ColorStateList
import androidx.annotation.ColorInt

/**
 * Информация при отрисовке для состояния валидации.
 * @property color цвет состояния валидации.
 * @property selector селектор состояния валидации.
 *
 * @author ps.smirnyh
 */
internal class ValidationStatusSelector(
    @ColorInt val color: Int,
    val selector: ColorStateList
)