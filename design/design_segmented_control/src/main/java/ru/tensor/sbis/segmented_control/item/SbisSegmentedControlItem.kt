package ru.tensor.sbis.segmented_control.item

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.segmented_control.control.models.SbisSegmentedControlSize
import ru.tensor.sbis.segmented_control.item.api.SbisSegmentedControlItemApi
import ru.tensor.sbis.segmented_control.item.api.SbisSegmentedControlItemController
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlCounter
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlIcon
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlIconSize
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlTitle
import ru.tensor.sbis.segmented_control.utils.ColorsByState
import ru.tensor.sbis.segmented_control.utils.drawers.ControlComponentDrawer
import ru.tensor.sbis.segmented_control.utils.drawers.ControlTextComponentDrawer
import ru.tensor.sbis.segmented_control.utils.drawers.CounterDrawer
import ru.tensor.sbis.segmented_control.utils.drawers.TextDrawer
import ru.tensor.sbis.segmented_control.utils.drawers.TextIconDrawer
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Отдельный сегмент комопнента сегмент-контрол.
 *
 * @see [ru.tensor.sbis.segmented_control.SbisSegmentedControl]
 *
 * @author ps.smirnyh
 */
internal class SbisSegmentedControlItem internal constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: SbisSegmentedControlItemController
) : View(context, attrs, defStyleAttr, defStyleRes),
    SbisSegmentedControlItemApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0,
        @StyleRes defStyleRes: Int = 0
    ) : this(context, attrs, defStyleAttr, defStyleRes, SbisSegmentedControlItemController())

    init {
        controller.attach(this)
    }

    private var sidePadding = 0f

    private var innerSpacing = 0f

    private var contentWidth: Float = 0f

    // Отступ контента при компоновке EQUAL
    private var fullWidthSidePadding = 0f

    private var titleDrawer: ControlTextComponentDrawer? = null

    private var iconDrawer: ControlComponentDrawer? = null

    private var counterDrawer: ControlComponentDrawer? = null

    private var accessibilityText = ""

    /** @SelfDocumented */
    internal var size: SbisSegmentedControlSize = SbisSegmentedControlSize.S
        set(value) {
            field = value
            onSizeUpdated(field)
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val constrainedWidth = MeasureSpec.getSize(widthMeasureSpec)
        val constrainedHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (sidePadding == 0f || innerSpacing == 0f) {
            sidePadding = size.sideSpacing.getDimen(context)
            innerSpacing = size.innerSpacing.getDimen(context)
        }

        var width = sidePadding * 2f
        width += iconDrawer?.width ?: 0F

        if (iconDrawer != null && titleDrawer != null) {
            width += innerSpacing
        }

        counterDrawer?.let {
            width += if (iconDrawer == null && titleDrawer == null) {
                it.width
            } else {
                it.width + innerSpacing
            }
        }

        val freeSpace = (constrainedWidth - width).coerceAtLeast(0f)
        contentWidth = width
        width += titleDrawer?.let { title ->
            when (mode) {
                MeasureSpec.UNSPECIFIED -> {
                    title.width
                }

                MeasureSpec.AT_MOST -> {
                    title.maxWidth = freeSpace
                    fullWidthSidePadding = 0f
                    title.width
                }

                MeasureSpec.EXACTLY -> {
                    title.maxWidth = freeSpace
                    var finalContentWidth = contentWidth + title.width
                    if (finalContentWidth < constrainedWidth) {
                        contentWidth -= sidePadding * 2f
                        finalContentWidth -= sidePadding * 2f
                        sidePadding = 0f
                        fullWidthSidePadding = (constrainedWidth - finalContentWidth) / 2f
                    }
                    freeSpace
                }

                else -> 0f
            }.also {
                contentWidth += title.width
            }
        } ?: if (mode == MeasureSpec.EXACTLY) {
            fullWidthSidePadding = freeSpace / 2f
            freeSpace
        } else {
            fullWidthSidePadding = 0f
            0F
        }

        setMeasuredDimension(
            width.roundToInt().coerceAtLeast(suggestedMinimumWidth),
            constrainedHeight
        )

    }

    override fun onDraw(canvas: Canvas) = with(canvas) {
        super.onDraw(canvas)

        // отступ для выравнивания при компоновке EQUAL
        translate(fullWidthSidePadding, 0f)
        val title = titleDrawer
        val icon = iconDrawer
        when {
            icon != null && title != null ->
                drawIconWithTitle(icon, title)

            icon != null ->
                drawIcon(icon)

            title != null ->
                drawTitle(title)

            else ->
                // для полноты картины поддерживается отображение только счётчиков
                drawCounter()
        }
    }

    override fun getBaseline(): Int {
        val iconHeight = iconDrawer?.height ?: 0F
        val iconBaseline = (measuredHeight - iconHeight) / 2F + iconHeight
        val titleHeight = titleDrawer?.height ?: 0F
        val titleBaseline = (measuredHeight - titleHeight) / 2F + titleHeight
        // выберем самый "низкий" элемент из содержимого
        return max(iconBaseline, titleBaseline).roundToInt()
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.text = accessibilityText
    }

    /** Обновить иконку. */
    internal fun updateIcon(
        icon: SbisSegmentedControlIcon?,
        size: SbisSegmentedControlIconSize
    ): Boolean {
        if (icon == null) {
            val updated = iconDrawer != null
            iconDrawer = null
            return updated
        }

        val iconSize = size.globalVar.getDimenPx(context)

        val newIconDrawer = TextIconDrawer(icon.icon, iconSize, minimumWidth, context)

        return if (newIconDrawer != iconDrawer) {
            iconDrawer = newIconDrawer
            true
        } else {
            false
        }
    }

    /** Обновить заголовок. */
    internal fun updateTitle(
        title: SbisSegmentedControlTitle?,
        defaultSize: FontSize
    ): Boolean {
        if (title == null) {
            val updated = titleDrawer != null
            titleDrawer = null
            return updated
        }
        val titleSize = (title.size?.titleSize ?: defaultSize).getScaleOffDimenPx(context)

        val newTextDrawer = TextDrawer(title.text, titleSize)

        return if (newTextDrawer != titleDrawer) {
            titleDrawer = newTextDrawer
            true
        } else {
            false
        }
    }

    /** Callback при изменении выбранного состояния. */
    internal fun onSelectedChanged(
        isSelected: Boolean,
        textColors: ColorsByState,
        iconColors: ColorsByState
    ) {
        if (!isEnabled) return
        val needColorIcon = if (isSelected) textColors.inStateColor else textColors.defaultColor
        val needColorText = if (isSelected) iconColors.inStateColor else iconColors.defaultColor
        titleDrawer?.let {
            if (it.setTint(needColorText)) invalidate()
        }
        iconDrawer?.let {
            if (it.setTint(needColorIcon)) invalidate()
        }
    }

    /** Callback при изменении enable состояния. */
    internal fun onStateChanged(
        isEnable: Boolean,
        textColors: ColorsByState,
        iconColors: ColorsByState
    ) {
        isEnabled = isEnable
        val needColorText = if (isEnable) textColors.defaultColor else textColors.inStateColor
        val needColorIcon = if (isEnable) iconColors.defaultColor else iconColors.inStateColor
        titleDrawer?.let {
            if (it.setTint(needColorText)) invalidate()
        }
        iconDrawer?.let {
            if (it.setTint(needColorIcon)) invalidate()
        }
    }

    /** Обновить счетчик. */
    internal fun updateCounter(
        counter: SbisSegmentedControlCounter?,
        defaultSize: FontSize
    ): Boolean {
        if (counter == null) {
            val updated = counterDrawer != null
            counterDrawer = null
            return updated
        }
        val counterSize = (controller.title?.size?.titleSize ?: defaultSize).getScaleOffDimenPx(context)

        val newCounterDrawer = CounterDrawer(counter.counter.toString(), counterSize).apply {
            setTint(counter.style.styleColor.getIconColor(context))
        }

        return if (newCounterDrawer != counterDrawer) {
            counterDrawer = newCounterDrawer
            true
        } else {
            false
        }
    }

    /** Обновить текст accessibility. */
    internal fun updateAccessibilityText(
        title: SbisSegmentedControlTitle?,
        counter: SbisSegmentedControlCounter?
    ) {
        accessibilityText = when {
            title != null && counter != null -> "${title.text}|${counter.counter}"
            title != null -> title.text.toString()
            counter != null -> counter.counter.toString()
            else -> ""
        }
    }

    private fun onSizeUpdated(size: SbisSegmentedControlSize) {
        val updateIcon = updateIcon(model.icon, model.icon?.size ?: size.iconSize)
        val updateTitle = updateTitle(model.title, size.titleSize)
        sidePadding = size.sideSpacing.getDimen(context)
        innerSpacing = size.innerSpacing.getDimen(context)
        if (updateIcon || updateTitle) {
            if (isLaidOut) {
                requestLayout()
            }
        }
    }

    private fun Canvas.drawIcon(
        icon: ControlComponentDrawer,
        leftPadding: Float = sidePadding
    ) {
        withTranslation(leftPadding, (measuredHeight - icon.height) / 2F) {
            icon.draw(this)
        }
        val counterPadding = contentWidth - sidePadding - (counterDrawer?.width ?: 0f)
        drawCounter(counterPadding)
    }

    private fun Canvas.drawTitle(
        title: ControlComponentDrawer
    ) {
        withTranslation(sidePadding, (measuredHeight - title.height) / 2F) {
            title.draw(this)
        }
        val counterPadding = contentWidth - sidePadding - (counterDrawer?.width ?: 0f)
        drawCounter(counterPadding)
    }

    private fun Canvas.drawIconWithTitle(
        icon: ControlComponentDrawer,
        title: ControlComponentDrawer
    ) {
        withTranslation(sidePadding, (measuredHeight - title.height) / 2F) {
            title.draw(this)
        }
        withTranslation(
            sidePadding + title.width + innerSpacing,
            (measuredHeight - icon.height) / 2F
        ) {
            icon.draw(this)
        }
        val counterPadding = contentWidth - sidePadding - (counterDrawer?.width ?: 0f)
        drawCounter(counterPadding)
    }

    private fun Canvas.drawCounter(
        leftPadding: Float = sidePadding
    ) {
        counterDrawer?.let { counter ->
            withTranslation(leftPadding, (measuredHeight - counter.height) / 2F) {
                counter.draw(this)
            }
        }
    }

}