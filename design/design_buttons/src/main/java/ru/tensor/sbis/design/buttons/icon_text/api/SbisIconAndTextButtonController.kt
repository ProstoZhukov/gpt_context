package ru.tensor.sbis.design.buttons.icon_text.api

import android.view.View
import ru.tensor.sbis.design.buttons.base.api.AbstractSbisButtonController
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitleSize
import ru.tensor.sbis.design.buttons.base.utils.drawers.ButtonTextComponentDrawer
import ru.tensor.sbis.design.buttons.base.utils.drawers.TextDrawer
import ru.tensor.sbis.design.buttons.base.utils.drawers.updateVisibilityByState
import ru.tensor.sbis.design.buttons.button.models.SbisButtonPlacement
import ru.tensor.sbis.design.buttons.icon_text.SbisIconAndTextButtonModel
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.theme.models.AbstractHeightModel

/**
 * Базовый контроллер для управления состоянием и внешним видом кнопки с текстом и иконкой.
 *
 * @author mb.kruglova
 */
abstract class SbisIconAndTextButtonController<SIZE : AbstractHeightModel> internal constructor(
    defaultSize: SIZE,
    styleHolder: SbisButtonCustomStyle
) : AbstractSbisButtonController<SIZE>(defaultSize, styleHolder),
    SbisButtonIconAndTextApi<SIZE> {

    internal var titleDrawer: ButtonTextComponentDrawer? = null
        private set

    internal var accessibilityText = ""

    internal var placement = SbisButtonPlacement.STANDALONE

    /**
     * Вычислены ли у кнопки размеры в случае смены текста или иконки.
     */
    internal var isMeasured: Boolean = true

    private val textLayout: TextLayout = TextLayout()

    internal fun updateComponentDrawer(model: SbisIconAndTextButtonModel): Boolean {
        val titleSizeChanged = updateTitleDrawer(button, model.title, getTitleSize(model, size))
        if (titleSizeChanged) updateTitle()
        val iconSizeChanged = updateIconDrawer(button, model.icon, getIconSize(model, size))
        if (iconSizeChanged) updateIcon()
        val sizeChanged = titleSizeChanged || iconSizeChanged
        if (sizeChanged) button.requestLayout()
        return sizeChanged
    }

    internal fun updateComponentStyle(model: SbisIconAndTextButtonModel): Boolean {
        val iconStyle = model.icon?.style
        val iconStyleChanged = when {
            iconStyle != null -> {
                iconStyle.loadStyle(button.context) { default, contrast, transparent ->
                    styleHolder.iconColors = default
                    styleHolder.iconContrastColors = contrast
                    styleHolder.iconTransparentColors = transparent
                }
                true
            }
            else -> false
        }
        if (iconStyleChanged) {
            updateIcon()
        }

        val titleStyle = model.title?.style
        val titleStyleChanged = when {
            titleStyle != null -> {
                titleStyle.loadStyle(button.context) { default, contrast, transparent ->
                    styleHolder.titleColors = default
                    styleHolder.titleContrastColors = contrast
                    styleHolder.titleTransparentColors = transparent
                }
                true
            }
            else -> false
        }

        if (titleStyleChanged) {
            updateTitle()
        }

        return iconStyleChanged || titleStyleChanged
    }

    abstract fun getIconSize(model: SbisIconAndTextButtonModel, size: SIZE): SbisButtonIconSize

    abstract fun getTitleSize(model: SbisIconAndTextButtonModel, size: SIZE): SbisButtonTitleSize

    fun updateTitleDrawer(
        view: View,
        title: SbisButtonTitle?,
        size: SbisButtonTitleSize
    ): Boolean {
        if (title == null) {
            val updated = titleDrawer != null
            titleDrawer = null
            return updated
        }
        val titleSize = globalStyleHolder.getTitleSize(view, size, title.scaleOn ?: scaleOn)

        val newTextDrawer = title.text?.let { TextDrawer(view.context, textLayout, it, titleSize) }

        return if (newTextDrawer != titleDrawer) {
            titleDrawer = newTextDrawer
            true
        } else {
            false
        }
    }

    internal fun updateTitle() {
        updateTitleStyle(button, styleHolder)
        titleDrawer?.updateVisibilityByState(state)
    }

    abstract fun updateTitleStyle(
        view: View,
        styleHolder: SbisButtonCustomStyle
    ): Boolean

    override fun onStateUpdated(state: SbisButtonState) {
        if (model.state != state) model = model.copy(state = state)
        iconDrawer?.updateVisibilityByState(state)
        titleDrawer?.updateVisibilityByState(state)
    }

    override fun onLayout() {
        textLayout.layout(0, 0)
    }
}