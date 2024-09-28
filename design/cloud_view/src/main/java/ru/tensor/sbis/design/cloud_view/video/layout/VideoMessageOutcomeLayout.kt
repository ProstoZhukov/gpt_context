package ru.tensor.sbis.design.cloud_view.video.layout

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import ru.tensor.sbis.design.cloud_view.layout.children.CloudDateTimeView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudStatusView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudTitleView
import ru.tensor.sbis.design.cloud_view.video.VideoMessageCloudView
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeAtMostSpec
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasure
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredHeight
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredWidth
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.cloud_view.R as RCloudView
import ru.tensor.sbis.design.R as RDesign

/**
 * Разметка исходящего видеосообщения [VideoMessageCloudView].
 * @see VideoMessageCloudViewLayout
 *
 * @author vv.chekurda
 */
internal class VideoMessageOutcomeLayout(
    parent: ViewGroup,
    @StyleRes styleRes: Int = RCloudView.style.OutcomeCloudViewCellStyle
) : VideoMessageCloudViewLayout(parent, styleRes) {

    /**
     * Минимальный отступ от левого края разметки до левого края облачка [backgroundView].
     */
    private val minimumCloudLeftSpacing =
        resources.getDimensionPixelSize(RCloudView.dimen.cloud_view_big_horizontal_margin)

    /**
     * Отступ от статуса [statusView].
     */
    private val statusSpacing = context.getDimenPx(RDesign.attr.offset_st)

    /**
     * Получить дополнительный отступ от правого края [titleView] до статуса [statusView].
     */
    private val statusAdditionalLeftSpacing: Int
        get() = if (statusView.data.sendingState == SendingState.NEEDS_MANUAL_SEND || !statusView.isVisible) {
            statusSpacing
        } else 0

    /**
     * Метка для отображения фона цветом ошибки.
     */
    private var isErrorBackground: Boolean = false

    override val titleView: CloudTitleView = CloudTitleView(context, RCloudView.style.OutcomeCloudViewTitleStyle)
    override val statusView: CloudStatusView = CloudStatusView(context, RCloudView.style.OutcomeCloudViewStatusStyle)
    override val timeView: CloudDateTimeView = CloudDateTimeView(context, RCloudView.style.OutcomeCloudViewTimeStyle)
    override val dateView: CloudDateTimeView = CloudDateTimeView(context, RCloudView.style.OutcomeCloudViewDateStyle)

    private val isCloudVisible: Boolean
        get() = messageLayout.isVisible

    init {
        titleView.id = RCloudView.id.cloud_view_title
        statusView.id = RCloudView.id.cloud_view_status
        timeView.id = RCloudView.id.cloud_view_time
        dateView.id = RCloudView.id.cloud_view_date
        videoMessageView.id = RCloudView.id.cloud_view_content
        backgroundView.id = RCloudView.id.cloud_view_background_income
        addViews(
            backgroundView,
            titleView,
            statusView,
            timeView,
            dateView,
            videoMessageView,
            quoteMarkerView
        )
        statusView.sendingErrorStateListener = ::showErrorBackground
    }

    private fun showErrorBackground(isError: Boolean) {
        if (isError != isErrorBackground) {
            backgroundView.background = AppCompatResources.getDrawable(
                context,
                if (isError) RCloudView.drawable.cloud_view_undelivered_bg
                else RCloudView.drawable.cloud_view_outcome_bg
            )
        }
        isErrorBackground = isError
    }

    override fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val wrappedSpec = MeasureSpecUtils.makeUnspecifiedSpec()

        val cloudHorizontalPadding = if (isCloudVisible) cloudHorizontalPadding else 0
        val cloudVerticalPadding = if (isCloudVisible) cloudVerticalPadding else 0

        val cloudMaxWidth = width - minimumCloudLeftSpacing
        val contentMaxWidth = cloudMaxWidth - cloudHorizontalPadding * 2
        val contentWidthSpec = makeAtMostSpec(contentMaxWidth)
        messageLayout.safeMeasure(contentWidthSpec, wrappedSpec)
        quoteMarkerView.safeMeasure(wrappedSpec, wrappedSpec)
        videoMessageView.measure(cloudMaxWidth, wrappedSpec)

        timeView.measure(wrappedSpec, wrappedSpec)
        dateView.safeMeasure(wrappedSpec, wrappedSpec)
        statusView.measure(wrappedSpec, wrappedSpec)

        val statusWidthWithSpacing = statusAdditionalLeftSpacing + statusView.measuredWidth
        val titleMaxWidth =
            width - minimumCloudLeftSpacing - cloudHorizontalPadding - statusWidthWithSpacing - timeView.measuredWidth
        val titleWidthSpec = makeAtMostSpec(titleMaxWidth)
        titleView.measure(titleWidthSpec, wrappedSpec)

        val titleHeight = if (titleView.isVisible) {
            titleView.baseline - timeView.baseline + timeView.measuredHeight + titleBottomPadding
        } else timeView.measuredHeight

        val heightList = listOf(
            dateView.safeMeasuredHeight,
            titleHeight,
            messageLayout.safeMeasuredHeight,
            quoteMarkerView.safeMeasuredHeight,
            videoMessageView.measuredHeight,
            when {
                isCloudVisible -> cloudVerticalPadding * 2
                titleView.isVisible -> titleBottomPadding
                else -> 0
            }
        )

        setMeasuredDimension(width, heightList.sum())
    }

    override fun layout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.layout(changed, left, top, right, bottom)

        val cloudHorizontalPadding = if (isCloudVisible) cloudHorizontalPadding else 0
        val cloudVerticalPadding = if (isCloudVisible) cloudVerticalPadding else 0

        val dateLeft = rightPos - dateView.measuredWidth
        val dateTop = topPos
        dateView.safeLayout(dateLeft, dateTop)

        val targetBaseline = if (titleView.isVisible) titleView.baseline else timeView.baseline
        val targetBaselinePos = dateView.bottom + targetBaseline + cloudVerticalPadding

        val timeLeft = rightPos - timeView.measuredWidth
        val timeTop = targetBaselinePos - timeView.baseline
        timeView.layout(timeLeft, timeTop)

        val statusLeft = timeView.left - statusView.measuredWidth
        val statusTop = targetBaselinePos - statusView.baseline
        statusView.layout(statusLeft, statusTop)

        val contentMaxRight = rightPos - cloudHorizontalPadding
        val expectedMessageLeft = contentMaxRight - messageLayout.safeMeasuredWidth
        val expectedTitleLeft = statusLeft - statusAdditionalLeftSpacing - titleView.measuredWidth
        val expectedVideoLeft = contentMaxRight - videoMessageView.collapsedSize
        val contentMaxLeft = minOf(expectedMessageLeft, expectedTitleLeft, expectedVideoLeft)

        val titleTop = targetBaselinePos - titleView.baseline
        titleView.layout(contentMaxLeft, titleTop)

        val messageTop = maxOf(titleView.bottom, timeView.bottom)
        messageLayout.safeLayout(contentMaxLeft, messageTop)
        quoteMarkerView.safeLayout(
            rightPos - quoteMarkerView.safeMeasuredWidth,
            messageLayout.bottom + cloudVerticalPadding
        )

        val videoMessageTop = when {
            isCloudVisible -> quoteMarkerView.bottom
            titleView.isVisible -> messageLayout.bottom + titleBottomPadding
            else -> messageLayout.bottom
        }
        videoMessageView.layout(rightPos - videoMessageView.measuredWidth, videoMessageTop)

        backgroundView.layout(
            contentMaxLeft - cloudHorizontalPadding,
            dateView.bottom,
            rightPos,
            messageLayout.bottom + cloudVerticalPadding
        )
    }
}