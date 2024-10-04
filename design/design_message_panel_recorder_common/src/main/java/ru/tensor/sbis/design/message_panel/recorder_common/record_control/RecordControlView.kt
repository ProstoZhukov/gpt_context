package ru.tensor.sbis.design.message_panel.recorder_common.record_control

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract.RecordControlViewApi
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.controller.RecordControlViewController
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.layout.RecordControlLayout

/**
 * Панель управления записью аудио/видео сообщений.
 * @see RecordControlViewApi
 *
 * @author vv.chekurda
 */
class RecordControlView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: RecordControlViewController
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    RecordControlViewApi by controller {

    @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttrs: Int = 0,
        @StyleRes defStyleRes: Int = 0
    ) : this(context, attrs, defStyleAttrs, defStyleRes, RecordControlViewController())

    private val layout = RecordControlLayout(this)

    /**
     * Высота панели сообщений.
     */
    val panelHeight: Int
        get() = layout.panelHeight

    init {
        setWillNotDraw(false)
        layout.init()
        controller.attachLayout(layout)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layout.onMeasure(widthMeasureSpec)
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

    override fun onDraw(canvas: Canvas) {
        layout.onDraw(canvas)
    }

    override fun verifyDrawable(who: Drawable): Boolean =
        layout.verifyDrawable(who) || super.verifyDrawable(who)

    override fun onSaveInstanceState(): Parcelable =
        controller.onSaveInstanceState(super.onSaveInstanceState())

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(controller.onRestoreInstanceState(state))
    }
}