package ru.tensor.sbis.design.message_panel.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.message_panel.R
import ru.tensor.sbis.design.message_panel.view.MessagePanel.LayoutParams.Gravity.TOP
import ru.tensor.sbis.design.message_panel.view.controller.MessagePanelViewController
import ru.tensor.sbis.design.message_panel.view.layout.MessagePanelLayout
import ru.tensor.sbis.design.message_panel.vm.MessagePanelApi
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.message_panel.common.R as RMPCommon

/**
 * Панель ввода сообщений.
 * TODO WIP https://online.sbis.ru/opendoc.html?guid=6d77f60d-d7c3-455e-b235-5a51bbb843b5
 *
 * @author vv.chekurda
 */
class MessagePanel private constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = RMPCommon.attr.messagePanelTheme,
    @StyleRes defStyleRes: Int = R.style.MessagePanelDefaultTheme,
    private val controller: MessagePanelViewController
) : FrameLayout(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0,
        @StyleRes defStyleRes: Int = 0
    ) : this(context, attrs, defStyleAttr, defStyleRes, MessagePanelViewController())

    private val layout = MessagePanelLayout(this, isInEditMode)

    val api: MessagePanelApi
        get() = controller.viewModel

    /**
     * Признак блокировки ввода/установки текста в поле ввода.
     */
    var isInputLocked: Boolean
        get() = controller.isInputLocked
        set(value) {
            controller.isInputLocked = value
        }

    @get:Px
    var topOffset: Int
        get() = layout.topOffset
        set(value) {
            layout.topOffset = value
        }

    init {
        layout.init()
        controller.attachLayout(layout, attrs)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        controller.onAttachedToWindow()
        layout.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        controller.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layout.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
            measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
        )
    }

    override fun getSuggestedMinimumHeight(): Int =
        layout.getSuggestedMinimumHeight()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        layout.onLayout()
    }

    override fun setAlpha(alpha: Float) {
        super.setAlpha(alpha)
        layout.inputView.alpha = alpha
    }

    override fun setTranslationY(translationY: Float) {
        super.setTranslationY(translationY)
        controller.setTranslationY(translationY)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        val childIndex = layout.addView(child, index, params)
        super.addView(child, childIndex, params)
    }

    override fun generateLayoutParams(attrs: AttributeSet?) =
        LayoutParams(context, attrs)

    class LayoutParams : FrameLayout.LayoutParams {

        enum class Gravity {
            TOP,
            BOTTOM
        }

        var layoutGravity: Gravity = TOP

        constructor(width: Int, height: Int) : super(width, height)

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            context.withStyledAttributes(attrs, RMPCommon.styleable.MessagePanel_Layout) {
                val ordinal = getInteger(RMPCommon.styleable.MessagePanel_Layout_MessagePanel_gravity, TOP.ordinal)
                layoutGravity = Gravity.values()[ordinal]
            }
        }
    }
}