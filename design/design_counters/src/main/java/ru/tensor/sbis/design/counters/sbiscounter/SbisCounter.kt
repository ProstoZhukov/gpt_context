package ru.tensor.sbis.design.counters.sbiscounter

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import ru.tensor.sbis.design.counters.utils.Formatter
import ru.tensor.sbis.design.theme.res.SbisDimen

/**
 * Виджет счётчика со скруглёнными углами.
 * При построении "плоской" view нужно использовать [SbisCounterDrawable].
 *
 * [Ссылка на стандарт](http://axure.tensor.ru/MobileStandart8/#p=%D1%81%D1%87%D0%B5%D1%82%D1%87%D0%B8%D0%BA%D0%B8&g=1)
 *
 * @author ma.kolpakov
 */
class SbisCounter @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val sbisCounterDrawable = SbisCounterDrawable(context, attrs)

    /** Стилизация счетчика в зависимости от места его применения. */
    var useCase: SbisCounterUseCase
        get() = sbisCounterDrawable.useCase
        set(value) {
            sbisCounterDrawable.useCase = value
        }

    /**
     * Использовать основной или второстепенный цвет фона счетчика.
     */
    var style: SbisCounterStyle
        get() = sbisCounterDrawable.style
        set(value) {
            sbisCounterDrawable.style = value
        }

    override fun setEnabled(enabled: Boolean) {
        sbisCounterDrawable.isEnabled = enabled
        super.setEnabled(enabled)
    }

    override fun isEnabled(): Boolean {
        return sbisCounterDrawable.isEnabled && super.isEnabled()
    }

    /**
     * @see SbisCounterDrawable.formatter
     */
    @Deprecated(message = "Используйте counterFormatter")
    var formatter: SbisCounterFormatter?
        get() = sbisCounterDrawable.formatter
        set(value) {
            sbisCounterDrawable.formatter = value
        }

    /**
     * @see SbisCounterDrawable.counterFormatter
     */
    var counterFormatter: Formatter
        get() = sbisCounterDrawable.counterFormatter
        set(value) {
            sbisCounterDrawable.counterFormatter = value
        }

    /**
     * @see SbisCounterDrawable.customBorderRadius
     */
    var customBorderRadius: SbisDimen?
        get() = sbisCounterDrawable.customBorderRadius
        set(value) {
            sbisCounterDrawable.customBorderRadius = value
        }

    /**
     * @see SbisCounterDrawable.count
     */
    var counter: Int
        get() = sbisCounterDrawable.count
        set(value) {
            if (sbisCounterDrawable.setCount(value) && !isInLayout) {
                requestLayout()
            }
            isVisible = sbisCounterDrawable.countText.isNotEmpty()
        }

    init {
        background = sbisCounterDrawable

        if (isInEditMode) {
            counter = 123
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(paddingLeft + background.minimumWidth + paddingRight, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(background.minimumHeight, MeasureSpec.EXACTLY)
        )
    }
}