package ru.tensor.sbis.design.cloud_view.utils.swipe

import android.content.Context
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.layout.CloudViewLayout

/**
 * Реализация поведения компонента ячейка-облако [CloudView] для цитирования по свайпу.
 * @see DefaultSwipeToQuoteBehavior
 *
 * @author vv.chekurda
 */
internal class CloudSwipeToQuoteBehaviorImpl(context: Context) : DefaultSwipeToQuoteBehavior(context) {

    /**
     * Разметка ячейки-облака.
     */
    private lateinit var layout: CloudViewLayout

    /**
     * Присоединить view ячейки.
     *
     * @param layout разметка ячейки-облака.
     */
    fun attachView(layout: CloudViewLayout) {
        this.layout = layout
        translatableViews = layout.run {
            listOfNotNull(
                backgroundView,
                titleView,
                contentView,
                statusView.takeIf { isOutcome },
                timeView.takeIf { isOutcome }
            )
        }
    }

    override fun translateViews(dx: Float) {
        translatableViews.forEach { it.translationX = dx }
        // View аватарки инициализируется лениво, поэтому в момент составления списка может быть null.
        layout.personView?.translationX = dx
    }

    override fun alphaViews(alpha: Float) {
        layout.timeView.alpha = alpha
        layout.statusView.alpha = alpha
    }

    /**
     * Получить позицию стрелки с координатами на осях X и Y.
     */
    override fun getArrowPosition(): Pair<Int, Int> {
        // По x стрелку рисуем не от центра,
        // а от правой границы, чтобы drawable полностью находилась за чертой позиции появления.
        val displacedCenterX = -arrowHalfWidth
        val timeViewBaseline = getViewTopByRecycler() + layout.timeView.top + layout.timeView.baseline
        val x = layout.measuredWidth + arrowStartPosition + getArrowSwipeDx() + displacedCenterX
        val y = timeViewBaseline - arrowHalfHeight + arrowDrawableBottomSpacing
        return x to y
    }
}