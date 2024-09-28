package ru.tensor.sbis.design.buttons.base.api

import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonDrawableIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon

/**
 * Реализация вспомогательного API для модификации иконок в кнопках.
 *
 * @author ma.kolpakov
 */
internal class ButtonIconApiAdapter(
    private val getIconModel: () -> SbisButtonIcon?,
    private val setIconModel: (SbisButtonIcon) -> Unit
) : SbisButtonIconApi {

    override fun setIcon(icon: SbisMobileIcon.Icon?) =
        setIconChar(icon?.character)

    override fun setIconChar(icon: Char?) =
        setIconSpannable(icon?.toString())

    override fun setIconDrawable(iconRes: Int) {
        val newModel = when (val iconModel = getIconModel()) {
            null -> SbisButtonDrawableIcon(iconRes)
            is SbisButtonDrawableIcon -> iconModel.copy(iconRes = iconRes)
            is SbisButtonTextIcon -> SbisButtonDrawableIcon(iconRes, iconModel.size, iconModel.style)
        }
        setIconModel(newModel)
    }

    override fun setIconSpannable(icon: CharSequence?) {
        val newModel = when (val iconModel = getIconModel()) {
            null -> SbisButtonTextIcon(icon)
            is SbisButtonDrawableIcon -> SbisButtonTextIcon(icon, iconModel.size, iconModel.style)
            is SbisButtonTextIcon -> iconModel.copy(icon = icon)
        }
        setIconModel(newModel)
    }

    override fun setIconScaleOn(scaleOn: Boolean?) {
        val newModel = when (val iconModel = getIconModel()) {
            null -> SbisButtonTextIcon(icon = "", scaleOn = scaleOn)
            is SbisButtonDrawableIcon -> iconModel.copy(scaleOn = scaleOn)
            is SbisButtonTextIcon -> iconModel.copy(scaleOn = scaleOn)
        }
        setIconModel(newModel)
    }
}