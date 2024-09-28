@file:Suppress("KDocUnresolvedReference")

package ru.tensor.sbis.design.stubview.layout_strategies

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.DimenRes
import androidx.annotation.Px
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.stubview.R
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies.IconMeasuringStrategy
import ru.tensor.sbis.design.utils.getDimenPx

/**
 * Базовый класс стратегии позиционирования элементов заглушки.
 * Инициализирует общие параметры и измеряет иконку в [measure], а также содержит обшие методы.
 *
 * @param icon иконка заглушки
 * @param message [SbisTextView] заголовка
 * @param details [TextView] описания
 * @param context контекст для доступа к ресурсам
 *
 * @author ma.kolpakov
 */
internal abstract class BaseStubViewComposer(
    protected val icon: View?,
    protected val message: SbisTextView,
    protected val details: TextView,
    private val iconMeasuringStrategy: IconMeasuringStrategy,
    context: Context,
) : StubViewComposer {

    protected companion object {
        /** @SelfDocumented */
        const val PERCENT_10 = 0.1
    }

    /** @SelfDocumented */
    protected var hasMessage: Boolean = false
        private set

    /** @SelfDocumented */
    protected var hasDetails: Boolean = false
        private set

    /** @SelfDocumented */
    protected var hasAnyText: Boolean = false
        private set

    /** @SelfDocumented */
    protected val hasIcon: Boolean = icon != null

    /** Отступ для краёв заглуки */
    @Px
    protected open val stubViewPaddingVertical: Int = context.getDimenPx(RDesign.attr.offset_m)

    /** Отступ для краёв заглуки */
    @Px
    protected open val stubViewPaddingHorizontal: Int = context.getDimenPx(RDesign.attr.offset_m)

    /** @SelfDocumented */
    @Px
    protected val iconMinSize = context.getPx(R.dimen.stub_view_icon_size_min)

    /** @SelfDocumented */
    @Px
    protected val iconMaxSize = context.getPx(R.dimen.stub_view_icon_size_max)

    /** @SelfDocumented */
    @Px
    protected val iconBottomPadding = context.getDimenPx(RDesign.attr.offset_l)

    /** @SelfDocumented */
    @Px
    protected val iconRightPadding = context.getDimenPx(RDesign.attr.offset_m)

    /** @SelfDocumented */
    @Px
    protected val messageBottomPadding = context.getDimenPx(RDesign.attr.offset_s)

    /** Ширина контейнера (вся доступная ширина parent view) */
    @Px
    protected var containerWidth: Int = 0
        private set

    /** Ширина контейнера с учётом отступов [stubViewPadding] слева и справа  */
    @Px
    protected var containerWidthWithPadding: Int = 0
        private set

    /** Высота контейнера (вся доступная высота parent view) */
    @Px
    protected var containerHeight: Int = 0
        private set

    /** Верхний отступ заглушки. 10% от высоты контейнера, но не меньше, чем [stubViewPadding] */
    @get:Px
    open val stubViewTopPadding: Int
        get() {
            val tenPercentsOfContainerHeight = (containerHeight * PERCENT_10).toInt()
            return tenPercentsOfContainerHeight.coerceAtLeast(stubViewPaddingVertical)
        }

    @CallSuper
    override fun measure(@Px containerWidth: Int, @Px containerHeight: Int) {
        this.containerWidth = containerWidth
        this.containerHeight = containerHeight

        updateValues()

        icon?.let {
            iconMeasuringStrategy.measure(it, containerWidth, iconMinSize, iconMaxSize)
        }
    }

    private fun updateValues() {
        containerWidthWithPadding = containerWidth - stubViewPaddingHorizontal * 2
        hasMessage = message.text?.isNotBlank() == true
        hasDetails = details.text?.isNotBlank() == true
        hasAnyText = hasMessage || hasDetails
    }

    /**
     * Позиционирование View по левой верхней точке.
     *
     * @param top верх view (`y` координата)
     * @param left левый край view (`x` координата)
     */
    protected fun View.layoutByTopAndLeft(@Px top: Int, @Px left: Int) {
        val right = left + this.measuredWidth
        val bottom = top + this.measuredHeight
        this.layout(left, top, right, bottom)
    }

    private fun Context.getPx(@DimenRes dimenId: Int): Int = resources.getDimensionPixelSize(dimenId)
}
