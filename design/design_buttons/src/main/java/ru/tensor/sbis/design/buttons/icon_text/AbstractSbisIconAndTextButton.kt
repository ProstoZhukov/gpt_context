package ru.tensor.sbis.design.buttons.icon_text

import android.content.Context
import android.util.AttributeSet
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.buttons.base.AbstractSbisButton
import ru.tensor.sbis.design.buttons.base.api.ButtonIconApiAdapter
import ru.tensor.sbis.design.buttons.icon_text.api.SbisButtonIconAndTextApi
import ru.tensor.sbis.design.buttons.base.api.SbisButtonIconApi
import ru.tensor.sbis.design.buttons.base.api.SbisButtonTitleApi
import ru.tensor.sbis.design.buttons.base.api.SbisButtonTitleApiAdapter
import ru.tensor.sbis.design.buttons.icon_text.api.SbisIconAndTextButtonController
import ru.tensor.sbis.design.theme.models.AbstractHeightModel
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Общая база для кнопки с текстом и иконкой.
 *
 * @author mb.kruglova
 */
abstract class AbstractSbisIconAndTextButton<
    SIZE : AbstractHeightModel,
    out VIEW_CONTROLLER : SbisIconAndTextButtonController<SIZE>>
internal constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    controller: VIEW_CONTROLLER
) :
    AbstractSbisButton<SIZE, VIEW_CONTROLLER>(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
        controller
    ),
    SbisButtonIconAndTextApi<SIZE> by controller,
    SbisButtonIconApi by ButtonIconApiAdapter(
        { controller.model.icon },
        { controller.model = controller.model.copy(icon = it) }
    ),
    SbisButtonTitleApi by SbisButtonTitleApiAdapter(
        { controller.model.title },
        { controller.model = controller.model.copy(title = it) },
        context
    ) {

    @Dimension
    var contentWidth = 0F

    @Dimension
    var sidePadding = 0F

    @Dimension
    var innerSpacing = 0F

    override fun getBaseline(): Int = with(controller) {
        val iconHeight = iconDrawer?.height ?: 0F
        val iconBaseline = (measuredHeight - iconHeight) / 2F + iconHeight
        val titleHeight = titleDrawer?.height ?: 0F
        val titleBaseline = (measuredHeight - titleHeight) / 2F + titleHeight
        max(iconBaseline, titleBaseline).roundToInt()
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.text = controller.accessibilityText
    }
}