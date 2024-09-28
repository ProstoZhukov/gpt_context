package ru.tensor.sbis.design.confirmation_dialog

import androidx.annotation.StringRes
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SecondaryButtonStyle
import java.io.Serializable

/**
 * Модель кнопки диалога подтверждения
 *
 * Текст задаётся строкой labelString или ресурсом labelRes. Приоритет у строки.
 *
 * @author ma.kolpakov
 */
data class ButtonModel<out ID : Any>(
    val id: ID,
    @StringRes
    val labelRes: Int? = null,
    val style: SbisButtonStyle = SecondaryButtonStyle,
    val isPrimary: Boolean = false,
    val viewId: Int? = null,
    val labelString: String? = null
) : Serializable