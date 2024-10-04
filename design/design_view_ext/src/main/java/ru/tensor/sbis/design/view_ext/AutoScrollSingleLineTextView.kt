package ru.tensor.sbis.design.view_ext

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * TextView с автоматической прокруткой при прикосновении, если длина текста больше длины TextView
 *
 * @author sa.nikitin
 */
class AutoScrollSingleLineTextView : AppCompatTextView {

    /** @SelfDocumented */
    var fullyTouchable: Boolean = false

    //region constructors
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.textViewStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }
    //endregion

    @Suppress("UsePropertyAccessSyntax")
    private fun init(attrs: AttributeSet?) {
        setSingleLine(true)
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        isHorizontalFadingEdgeEnabled = true
        isVerticalScrollBarEnabled = false

        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.AutoScrollSingleLineTextView, 0, 0)
        fullyTouchable = attrArray.getBoolean(R.styleable.AutoScrollSingleLineTextView_fullyTouchable, false)
        attrArray.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if ((fullyTouchable || touchEventInLeftHalf(event)) && canMarquee()) {
                    isSelected = true
                    true
                } else {
                    super.onTouchEvent(event)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isSelected = false
                true
            }
            else -> super.onTouchEvent(event)
        }

    /**
     * Метод определяет, было ли касание в левой половине вьюшки
     */
    private fun touchEventInLeftHalf(event: MotionEvent?): Boolean = event != null && event.x < width / 2

    private fun canMarquee(): Boolean {
        val width = right - left - compoundPaddingLeft - compoundPaddingRight
        return width > 0 && (layout.getLineWidth(0) > width)
    }
}