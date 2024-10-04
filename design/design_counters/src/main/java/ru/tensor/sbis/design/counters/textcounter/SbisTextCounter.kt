package ru.tensor.sbis.design.counters.textcounter

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.counters.R
import ru.tensor.sbis.design.counters.textcounter.utils.SbisTextCounterFormatter
import ru.tensor.sbis.design.counters.utils.Formatter

/**
 * Обертка на текстовым счетчиком для отображения во view.
 *
 * [Ссылка на стандарт](http://axure.tensor.ru/MobileStandart8/#p=%D1%81%D1%87%D0%B5%D1%82%D1%87%D0%B8%D0%BA%D0%B8&g=1)
 *
 * @author da.zolotarev
 */
class SbisTextCounter @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.SbisTextCounterStyle,
    @StyleRes defStyleRes: Int = R.style.SbisTextCounterStyle
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val drawable = SbisTextCounterDrawable(context, attrs, defStyleAttr, defStyleRes)

    /**
     * @see [SbisTextCounterDrawable.accentedCounter]
     */
    var accentedCounter: Int
        get() = drawable.accentedCounter
        set(value) {
            drawable.accentedCounter = value
            requestLayout()
        }

    /**
     * @see [SbisTextCounterDrawable.unaccentedCounter]
     */
    var unaccentedCounter: Int
        get() = drawable.unaccentedCounter
        set(value) {
            drawable.unaccentedCounter = value
            requestLayout()
        }

    /** @see [SbisTextCounterDrawable.formatter] */
    @Deprecated(message = "Используйте counterFormatter")
    var formatter: SbisTextCounterFormatter?
        get() = drawable.formatter
        set(value) {
            drawable.formatter = value
            requestLayout()
        }

    var counterFormatter: Formatter
        get() = drawable.counterFormatter
        set(value) {
            drawable.counterFormatter = value
            requestLayout()
        }

    init {
        background = drawable
        if (isInEditMode) {
            accentedCounter = 10
            unaccentedCounter = 10
        }
    }

    override fun getBaseline() = drawable.getBaseline()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            background.minimumWidth,
            background.minimumHeight
        )
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.text = "$accentedCounter|$unaccentedCounter"
    }

}