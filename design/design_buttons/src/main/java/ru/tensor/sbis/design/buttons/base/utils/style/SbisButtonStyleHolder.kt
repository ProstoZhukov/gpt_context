package ru.tensor.sbis.design.buttons.base.utils.style

import android.view.View
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitleSize
import ru.tensor.sbis.design.buttons.button.models.SbisButtonPlacement
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.theme.models.AbstractHeightModel

/**
 * Глобальные ресурсы для кнопок.
 *
 * @author mb.kruglova
 */
internal class SbisButtonStyleHolder {

    companion object {
        private const val PREVIEW_BUTTON_SIZE_PX = 120
        private const val PREVIEW_CONTENT_SIZE_PX = 40
        private const val PREVIEW_INNER_SPACING = 12F
        private const val PREVIEW_INNER_SPACING_DEFAULT = 0F
        private const val PREVIEW_SIDE_PADDING = 40F
        private const val PREVIEW_CORNER_RADIUS = 60F
    }

    /** @SelfDocumented */
    internal fun getTitleSize(button: View, size: SbisButtonTitleSize, scaleOn: Boolean = false) = when {
        button.isInEditMode -> PREVIEW_CONTENT_SIZE_PX
        scaleOn -> size.globalVar.getScaleOnDimenPx(button.context)
        else -> size.globalVar.getScaleOffDimenPx(button.context)
    }

    /** @SelfDocumented */
    internal fun getIconSize(button: View, size: SbisButtonIconSize, scaleOn: Boolean = false) = when {
        button.isInEditMode -> PREVIEW_CONTENT_SIZE_PX
        scaleOn -> size.globalVar.getScaleOnDimenPx(button.context)
        else -> size.globalVar.getDimenPx(button.context)
    }

    /** @SelfDocumented */
    internal fun getInnerSpacing(button: View, size: AbstractHeightModel): Float {
        return when {
            button.isInEditMode -> PREVIEW_INNER_SPACING
            size is SbisButtonSize -> size.getInnerSpacingDimen(button.context)
            else -> PREVIEW_INNER_SPACING_DEFAULT
        }
    }

    /** @SelfDocumented */
    internal fun getSize(button: View, size: AbstractHeightModel) =
        if (button.isInEditMode) PREVIEW_BUTTON_SIZE_PX else size.globalVar.getDimenPx(button.context)

    /** @SelfDocumented */
    internal fun getSidePadding(button: View, placement: SbisButtonPlacement) =
        if (button.isInEditMode) PREVIEW_SIDE_PADDING else placement.getDimen(button.context)

    /** @SelfDocumented */
    internal fun getCornerRadius(button: View, size: SbisButtonSize, cornerRadius: Float): Float {
        return when {
            button.isInEditMode -> PREVIEW_CORNER_RADIUS
            cornerRadius.isNaN() -> size.getCornerRadiusDimen(button.context)
            else -> cornerRadius
        }
    }
}