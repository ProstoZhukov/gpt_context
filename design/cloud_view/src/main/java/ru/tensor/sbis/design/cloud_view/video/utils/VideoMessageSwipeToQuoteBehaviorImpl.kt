package ru.tensor.sbis.design.cloud_view.video.utils

import android.content.Context
import ru.tensor.sbis.design.cloud_view.utils.swipe.DefaultSwipeToQuoteBehavior
import ru.tensor.sbis.design.cloud_view.video.VideoMessageCloudView
import ru.tensor.sbis.design.cloud_view.video.layout.VideoMessageCloudViewLayout

/**
 * Реализация поведения кячейки видеосообщения [VideoMessageCloudView] для цитирования по свайпу.
 * @see DefaultSwipeToQuoteBehavior
 *
 * @author da.zhukov
 */
class VideoMessageSwipeToQuoteBehaviorImpl(context: Context) : DefaultSwipeToQuoteBehavior(context) {

    /**
     * Разметка ячейки видеосообщения.
     */
    private lateinit var layout: VideoMessageCloudViewLayout

    /**
     * Присоединить view ячейки.
     *
     * @param layout разметка ячейки-облака.
     */
    fun attachView(layout: VideoMessageCloudViewLayout) {
        this.layout = layout
        translatableViews = layout.run {
            listOfNotNull(
                backgroundView,
                titleView,
                videoMessageView,
                statusView.takeIf { isOutcome },
                timeView.takeIf { isOutcome },
                quoteMarkerView
            )
        }
    }

    override fun translateViews(dx: Float) {
        translatableViews.forEach { it.translationX = dx }
        // View аватарки инициализируется лениво, поэтому в момент составления списка может быть null.
        layout.personView?.translationX = dx
        layout.messageLayout.translationX = dx
    }

    override fun alphaViews(alpha: Float) {
        layout.timeView.alpha = alpha
        layout.statusView.alpha = alpha
    }

    override fun getArrowPosition(): Pair<Int, Int> {
        // По x стрелку рисуем не от центра,
        // а от правой границы, чтобы drawable полностью находилась за чертой позиции появления.
        val displacedCenterX = -arrowHalfWidth
        val timeViewBaseline = view.top + layout.timeView.top + layout.timeView.baseline
        val x = layout.measuredWidth + arrowStartPosition + getArrowSwipeDx() + displacedCenterX
        val y = timeViewBaseline - arrowHalfHeight + arrowDrawableBottomSpacing
        return x to y
    }
}