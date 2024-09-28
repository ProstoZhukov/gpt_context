package ru.tensor.sbis.design.buttons.button.models

import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.counter.SbisButtonCounter
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.icon_text.SbisIconAndTextButtonModel

/**
 * Модель внешнего вида кнопки.
 *
 * @param icon Иконка на кнопке.
 * @param title Текст на кнопке.
 * @param counter Значение счетчика.
 * @param state Состояния кнопки.
 * @param style Стиль кнопки. Если не задан, применяется стиль кнопки.
 * @param backgroundType Тип фона и обводки кнопки. Применяться только для самостоятельных кнопок. Кнопки в группах
 * игнорируют этот параметр.
 * @param clickListener Обработчик нажатий.
 *
 * @author ma.kolpakov
 */
data class SbisButtonModel(
    override val icon: SbisButtonIcon? = null,
    override val title: SbisButtonTitle? = null,
    val counter: SbisButtonCounter? = null,
    override val state: SbisButtonState = SbisButtonState.ENABLED,
    override val style: SbisButtonStyle? = null,
    val backgroundType: SbisButtonBackground? = null,
    val clickListener: ((button: SbisButton) -> Unit)? = UNDEFINED_LISTENER
) : SbisIconAndTextButtonModel {

    internal fun compareIconTitleStyleTo(other: SbisButtonModel) = icon?.style != other.icon?.style ||
        title?.style != other.title?.style ||
        title?.position != other.title?.position

    internal fun compareCounterTo(other: SbisButtonModel) = counter?.counter != other.counter?.counter ||
        counter?.style != other.counter?.style

    internal fun compareStateTo(other: SbisButtonModel) = state != other.state

    internal fun compareStyleTo(other: SbisButtonModel) = style != other.style

    internal fun compareBackgroundTypeTo(other: SbisButtonModel) = backgroundType != other.backgroundType
}

/**
 * Значение по умолчанию для clickListener в [SbisButtonModel].
 * Позволяет использовать null для затирания события.
 */
internal val UNDEFINED_LISTENER = { _: SbisButton -> }