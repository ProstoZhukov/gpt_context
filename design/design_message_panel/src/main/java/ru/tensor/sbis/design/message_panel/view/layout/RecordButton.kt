package ru.tensor.sbis.design.message_panel.view.layout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.message_panel.common.R as RMPCommon

/**
 * Реализация TextView для обхода высокой версии API для признака наличия лонг-клик слушателей.
 *
 * @author vv.chekurda
 */
class RecordButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val iconLayout = makeIcon(context, SbisMobileIcon.Icon.smi_MicrophoneNull.character.toString())

    private val recordIcon: String
        get() = if (isAudioRecord) {
            SbisMobileIcon.Icon.smi_MicrophoneNull
        } else {
            SbisMobileIcon.Icon.smi_CameraVideoMessage
        }.character.toString()

    private var listener: RecordButtonActionListener? = null
        set(value) {
            field = value
            updateHasListenerState()
        }
    private val longClickAction = Runnable { listener?.onClick(isLongPressed = true) }
    private val _hasActionListener = BehaviorSubject.createDefault(false)
    private val _hasActionListenerState = MutableStateFlow(false)

    var isAudioRecord: Boolean = true
        set(value) {
            field = value
            val isChanged = iconLayout.configure { text = recordIcon }
            if (isChanged) invalidate()
        }

    val hasActionListener: Observable<Boolean> = _hasActionListener
    val hasActionListenerState: StateFlow<Boolean> = _hasActionListenerState

    init {
        isAudioRecord = true
        isHapticFeedbackEnabled = false
        setOnClickListener { listener?.onClick(isLongPressed = false) }
        isClickable = false
    }

    fun setActionListener(listener: RecordButtonActionListener?) {
        this.listener = listener
        isClickable = listener != null
    }

    private fun updateHasListenerState() {
        val hasListener = listener != null
        isClickable = hasListener
        isLongClickable = hasListener
        _hasActionListener.onNext(hasListener)
        _hasActionListenerState.value = hasListener
    }

    override fun getSuggestedMinimumWidth(): Int =
        paddingStart + paddingEnd + iconLayout.width

    override fun getSuggestedMinimumHeight(): Int =
        paddingTop + paddingBottom + iconLayout.height

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val horizontalPadding = paddingStart + paddingEnd
        val verticalPadding = paddingTop + paddingBottom
        iconLayout.layout(
            paddingStart + ((measuredWidth - horizontalPadding - iconLayout.width) / 2f).roundToInt(),
            paddingTop + ((measuredHeight - verticalPadding - iconLayout.height) / 2f).roundToInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        iconLayout.draw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        iconLayout.onTouch(this, event)
        checkLongPressed(event)
        return super.onTouchEvent(event)
    }

    private fun checkLongPressed(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> handler.postDelayed(longClickAction, LONG_CLICK_DELAY_MS)
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> handler.removeCallbacks(longClickAction)
        }
    }

    private fun makeIcon(context: Context, icon: String): TextLayout =
        TextLayout {
            paint.apply {
                typeface = TypefaceManager.getSbisMobileIconTypeface(context)
                textSize = resources.getDimensionPixelSize(RDesign.dimen.size_title1_scaleOff).toFloat()
            }
            text = icon
        }.apply {
            val colorStateList = ContextCompat.getColorStateList(
                context,
                RMPCommon.color.design_message_panel_control_selectable_color
            )!!
            textPaint.color = colorStateList.defaultColor
            this.colorStateList = colorStateList
            makeClickable(this@RecordButton)
        }
}

fun interface RecordButtonActionListener {
    fun onClick(isLongPressed: Boolean)
}

private const val LONG_CLICK_DELAY_MS = 150L