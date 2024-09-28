package ru.tensor.sbis.design.checkbox

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.annotation.StyleRes
import androidx.core.graphics.withTranslation
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import ru.tensor.sbis.design.checkbox.drawers.CheckboxContentDrawer
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxContent
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValue
import ru.tensor.sbis.design.checkbox.style.SbisCheckboxStyleHolder
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight
import ru.tensor.sbis.design.theme.HorizontalPosition.LEFT
import ru.tensor.sbis.design.theme.HorizontalPosition.RIGHT
import java.lang.Integer.min
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Механика и инфраструктура чекбокса
 *
 * @author mb.kruglova
 */
class SbisCheckboxView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int = R.attr.sbisCheckboxDefaultsTheme,
    @StyleRes defStyleRes: Int = R.style.SbisCheckboxDefaultsTheme,
    private val controller: SbisCheckboxController,
    private val styleHolder: SbisCheckboxStyleHolder = SbisCheckboxStyleHolder.create(context)
) : View(context, attrs, defStyleAttr, defStyleRes),
    SbisCheckboxAPI by controller {

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(
            android.R.attr.state_checked
        )
    }

    private val innerSpacing by lazy { styleHolder.innerSpacing }
    private val verticalOffset by lazy { styleHolder.verticalOffset }
    private var currentVerticalOffset = 0

    @Dimension
    private var checkboxWidth = 0F

    private var checkboxDrawable: Drawable? = null
    private var presetCheckboxDrawable: Drawable? = null

    /**@SelfDocumented*/
    var isCheckBoxChecked: Boolean = false
        set(value) {
            field = value
            val sbisCheckboxValue = value.map()
            if (controller.value != sbisCheckboxValue) {
                controller.value = sbisCheckboxValue
                return
            }
            refreshPresetDrawable()
            refreshDrawableState()
        }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.sbisCheckboxDefaultsTheme,
        @StyleRes defStyleRes: Int = R.style.SbisCheckboxDefaultsTheme,
    ) : this(context, attrs, defStyleAttr, defStyleRes, SbisCheckboxController())

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes, styleHolder)
        setButtonDrawable(controller.checkboxImage)
    }

    /**
     * Создание checked состояния для чекбокса
     */
    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isCheckBoxChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    /**
     * Вызывается при смене состояния чекбокса
     */
    override fun drawableStateChanged() {
        super.drawableStateChanged()
        val checkboxDrawable: Drawable? = checkboxDrawable
        if (checkboxDrawable != null && checkboxDrawable.isStateful && checkboxDrawable.setState(drawableState)) {
            invalidate()
        }
    }

    /**
     * Вызывается по клику на чекбокс
     */
    override fun performClick(): Boolean {
        isCheckBoxChecked = if (value == SbisCheckboxValue.UNDEFINED) false else isCheckBoxChecked.not()
        return super.performClick()
    }

    /**
     * Установление значения чекбокса (отмечен галочкой или нет)
     */
    @Deprecated("Обращаться напрямую к isChecked")
    fun setChecked(checked: Boolean) {
        isCheckBoxChecked = checked
    }

    /**
     * Установление доступности чекбокса для взаимодействия
     */
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        refreshPresetDrawable()
        controller.setContentColor()
        controller.setCheckboxImage()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = with(controller) {
        val textHeight = textLayout.textPaint.fontMetrics.let { it.descent - it.ascent }
        var componentHeight = maxOf(size.getCheckboxSizeDimen(context), textHeight.toInt())
        checkboxWidth = size.getCheckboxSizeDimen(context).toFloat()
        var desiredWidth = checkboxWidth
        val desireTextWidth = textLayout.getDesiredWidth(textLayout.text)

        currentVerticalOffset = if (useVerticalOffset) verticalOffset * 2 else 0

        componentHeight += currentVerticalOffset

        if (controller.content !is SbisCheckboxContent.NoContent) {
            val contentWidth = if (controller.content is SbisCheckboxContent.TextContent) desireTextWidth.toFloat()
            else (iconDrawer?.width ?: 0F)
            desiredWidth += innerSpacing + contentWidth
        }
        if (!validationState.text.isNullOrEmpty()) {
            desiredWidth =
                max(desiredWidth, textLayoutValidation.width.toFloat())
        }

        val paddings = checkbox.paddingStart + checkbox.paddingEnd + checkbox.marginStart + checkbox.marginEnd
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec) - paddings
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val suitableWidth = when {
            availableWidth > 0 && (mode == MeasureSpec.EXACTLY || mode == MeasureSpec.AT_MOST) ->
                min(desiredWidth.toInt(), availableWidth)

            else -> desiredWidth.toInt()
        }
        val resolvedWidth = MeasureSpec.makeMeasureSpec(suitableWidth, mode)

        val content = controller.content
        if (content is SbisCheckboxContent.TextContent) {
            if (availableWidth in 1 until desiredWidth.toInt()) {
                textLayout.configure {
                    layoutWidth = availableWidth - checkboxWidth.toInt() - innerSpacing
                }
            } else {
                textLayout.configure {
                    layoutWidth = desireTextWidth
                }
            }
            // Высота равна отступам, высоте textLayout и смещению текста относительно чекбокса
            if (textLayout.maxLines > 1 && textLayout.height > componentHeight)
                componentHeight = currentVerticalOffset + textLayout.height +
                    ((size.getCheckboxSizeDimen(context) - textLayout.textPaint.textHeight.toFloat()) / 2F).toInt()
        }
        if (!validationState.text.isNullOrEmpty()) {
            textLayoutValidation.configure {
                maxWidth = availableWidth
            }
            componentHeight += textLayoutValidation.height + styleHolder.textValidationTopMargin
        }

        setMeasuredDimension(resolvedWidth, componentHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (controller.content is SbisCheckboxContent.TextContent) layoutTextContent()
        if (!validationState.text.isNullOrEmpty()) layoutTextValidation()
        super.onLayout(changed, left, top, right, bottom)
    }

    private fun layoutTextContent(): Unit = with(controller) {
        val fontMetrics = textLayout.textPaint.fontMetrics
        textLayout.layout(
            if (position == RIGHT) checkboxWidth.toInt() + innerSpacing else 0,
            // Offset сверху + смещение относительно чекбокса
            if (textLayout.maxLines > 1)
                currentVerticalOffset / 2 + (size.getCheckboxSizeDimen(context) - textLayout.textPaint.textHeight) / 2
            else getCheckboxY(fontMetrics.descent.roundToInt() - fontMetrics.ascent.roundToInt()).toInt()
        )
    }

    private fun layoutTextValidation(): Unit = with(controller) {
        textLayoutValidation.layout(
            0,
            getCheckboxY().toInt() + size.getCheckboxSizeDimen(context) + styleHolder.textValidationTopMargin
        )
    }

    override fun onDraw(canvas: Canvas): Unit = with(controller) {
        super.onDraw(canvas)

        val buttonDrawable: Drawable? = presetCheckboxDrawable ?: checkboxDrawable
        val height = size.getCheckboxSizeDimen(context)
        buttonDrawable?.setBounds(0, 0, height, height)

        textLayoutValidation.draw(canvas)

        when (controller.content) {
            SbisCheckboxContent.NoContent -> {
                canvas.withTranslation(0F, getCheckboxY()) {
                    buttonDrawable?.draw(canvas)
                }
            }

            is SbisCheckboxContent.TextContent ->
                drawTextContent(canvas, buttonDrawable)

            is SbisCheckboxContent.IconContent ->
                iconDrawer?.let { drawIconContent(canvas, buttonDrawable, it) }
        }
    }

    private fun drawTextContent(
        canvas: Canvas,
        drawable: Drawable?
    ) {
        val checkboxX = if (controller.position == LEFT && controller.textLayout.width > 0F)
            controller.textLayout.width.toFloat() + innerSpacing else 0F
        val checkboxY = if (controller.textLayout.maxLines > 1) currentVerticalOffset / 2F
        else getCheckboxY()

        controller.textLayout.draw(canvas)

        canvas.withTranslation(checkboxX, checkboxY) {
            drawable?.draw(canvas)
        }
    }

    private fun drawIconContent(
        canvas: Canvas,
        drawable: Drawable?,
        iconContent: CheckboxContentDrawer,
    ) {
        val checkboxX = if (controller.position == LEFT && iconContent.width > 0F)
            iconContent.width + innerSpacing else 0F
        val checkboxY = getCheckboxY()

        val contentX = if (controller.position == RIGHT) checkboxWidth + innerSpacing else 0F
        val contentY = getCheckboxY(iconContent.height.toInt())

        canvas.withTranslation(contentX, contentY) {
            iconContent.draw(canvas)
        }
        canvas.withTranslation(checkboxX, checkboxY) {
            drawable?.draw(canvas)
        }
    }

    private fun getCheckboxY(height: Int = size.getCheckboxSizeDimen(context)): Float {
        return if (validationState.text.isNullOrEmpty())
            (measuredHeight - height) / 2F
        else
            (measuredHeight - controller.textLayoutValidation.height - styleHolder.textValidationTopMargin - height) /
                2F
    }

    internal fun setButtonDrawable(drawable: Drawable?) {
        if (checkboxDrawable?.constantState == drawable?.constantState) return
        checkboxDrawable = drawable
        if (drawable != null) {
            if (drawable.isStateful) {
                drawable.state = drawableState
            }
        }
    }

    internal fun refreshPresetDrawable() {
        presetCheckboxDrawable = if (isEnabled) controller.getPresetCheckboxImage() else null
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.text = controller.accessibilityText
    }

    /**
     * Получение значения, является ли чекбокс недоступным для взаимодействия и не выбранным
     */
    internal fun disabledAndUnchecked(): Boolean = !this.isEnabled && value == SbisCheckboxValue.UNCHECKED

    private fun Boolean.map() = if (this) SbisCheckboxValue.CHECKED else SbisCheckboxValue.UNCHECKED
}