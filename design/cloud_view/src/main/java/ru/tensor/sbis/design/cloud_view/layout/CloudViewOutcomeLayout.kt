package ru.tensor.sbis.design.cloud_view.layout

import android.view.View
import android.view.View.MeasureSpec.getSize
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.cloud_view.layout.children.CloudDateTimeView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudStatusView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudTitleView
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeAtMostSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import androidx.core.view.isVisible
import ru.tensor.sbis.design.cloud_view.content.utils.MessagesViewPool
import ru.tensor.sbis.design.cloud_view.utils.thread.CloudThreadHelper
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredHeight
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredWidth
import ru.tensor.sbis.design.utils.getDimenPx
import java.util.UUID

/**
 * Разметка исходящего сообщения ячейки-облака [CloudView].
 * @see CloudViewLayout
 *
 * @author vv.chekurda
 */
internal class CloudViewOutcomeLayout(
    parent: ViewGroup,
    @StyleRes styleRes: Int = R.style.OutcomeCloudViewCellStyle
) : CloudViewLayout(parent, styleRes) {

    /**
     * Минимальный отступ от левого края разметки до левого края облачка [backgroundView].
     */
    private val minimumCloudLeftSpacing = resources.getDimensionPixelSize(R.dimen.cloud_view_big_horizontal_margin)

    /**
     * Отступ от статуса [statusView].
     */
    private val statusSpacing = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_st)

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

    private var viewPool: MessagesViewPool? = null
    private var messageUuid: UUID? = null
    private var childThreadWidth: Int? = null
    private val ignoreAttachmentsWidth: Boolean
        get() = childThreadWidth != null &&
            childThreadWidth!! > contentView.mMessageWidthWithAttachments + cloudHorizontalPadding * 2

    override val titleView: CloudTitleView = CloudTitleView(context, R.style.OutcomeCloudViewTitleStyle)
    override val statusView: CloudStatusView = CloudStatusView(context, R.style.OutcomeCloudViewStatusStyle)
    override val timeView: CloudDateTimeView = CloudDateTimeView(context, R.style.OutcomeCloudViewTimeStyle)
    override val dateView: CloudDateTimeView = CloudDateTimeView(context, R.style.OutcomeCloudViewDateStyle)

    init {
        titleView.id = R.id.cloud_view_title
        statusView.id = R.id.cloud_view_status
        timeView.id = R.id.cloud_view_time
        dateView.id = R.id.cloud_view_date
        contentView.id = R.id.cloud_view_content
        backgroundView.id = R.id.cloud_view_background_income
        addViews(
            backgroundView,
            titleView,
            statusView,
            timeView,
            dateView,
            contentView
        )
        statusView.sendingErrorStateListener = ::showErrorBackground
    }

    override fun setViewPool(viewPool: MessagesViewPool) {
        contentView.setViewPool(viewPool)
        this.viewPool = viewPool
    }

    override fun setMessageUuid(uuid: UUID?) {
        messageUuid = uuid
    }

    private fun showErrorBackground(isError: Boolean) {
        if (isError != isErrorBackground) {
            backgroundView.background = AppCompatResources.getDrawable(
                context,
                if (isError) R.drawable.cloud_view_undelivered_bg
                else R.drawable.cloud_view_outcome_bg
            )
        }
        isErrorBackground = isError
    }

    private fun updateChildThreadWidth(availableWidth: Int) {
        val viewPool = viewPool
        val messageUuid = messageUuid
        if (messageUuid == null || viewPool == null) {
            childThreadWidth = null
            return
        }
        val threadData = CloudThreadHelper.getChildThreadData(messageUuid)

        childThreadWidth = if (threadData != null) {
            val threadView = viewPool.getCloudThreadView()
            viewPool.addView(threadView)

            threadView.data = threadData
            threadView.measure(
                makeAtMostSpec(availableWidth + parent.paddingStart + parent.paddingEnd),
                makeUnspecifiedSpec()
            )
            threadView.getBackgroundWidth()
        } else {
            null
        }
    }

    override fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getSize(widthMeasureSpec)
        updateChildThreadWidth(availableWidth = width)
        val wrappedSpec = makeUnspecifiedSpec()
        val contentMaxWidth = width - minimumCloudLeftSpacing - cloudHorizontalPadding * 2
        val contentWidthSpec = makeAtMostSpec(contentMaxWidth)
        if (hasAttachments && ignoreAttachmentsWidth) {
            contentView.setMessage(contentView.cloudViewData, true, false)
        }
        contentView.measure(contentWidthSpec, wrappedSpec)

        timeView.measure(wrappedSpec, wrappedSpec)
        dateView.measure(wrappedSpec, wrappedSpec)
        statusView.measure(wrappedSpec, wrappedSpec)

        val statusWidthWithSpacing = statusAdditionalLeftSpacing + statusView.measuredWidth

        val titleMaxWidth = if (hasAttachments && !ignoreAttachmentsWidth) {
            contentView.measuredWidth - statusWidthWithSpacing - timeView.measuredWidth
        } else {
            width - minimumCloudLeftSpacing - cloudHorizontalPadding - statusWidthWithSpacing - timeView.measuredWidth
        }
        val titleWidthSpec = makeAtMostSpec(titleMaxWidth)
        titleView.measure(titleWidthSpec, wrappedSpec)

        val titleHeight = if (titleView.isVisible) {
            titleView.baseline - timeView.baseline + timeView.measuredHeight
        } else timeView.safeMeasuredHeight

        val heightList = listOf(
            if (titleView.isVisible || timeView.isGone) cloudVerticalPadding else 0,
            dateView.safeMeasuredHeight,
            titleHeight,
            contentView.measuredHeight,
            measureAdditionalContent(contentWidthSpec, wrappedSpec),
            cloudVerticalPadding,
            if (hasFirstAttachment) firstAttachmentTopSpacing
            else 0
        )

        setMeasuredDimension(width, heightList.sum())
    }

    override fun layout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.layout(changed, left, top, right, bottom)

        val dateLeft = rightPos - dateView.safeMeasuredWidth
        val dateTop = topPos
        dateView.safeLayout(dateLeft, dateTop)

        val targetBaseline = when {
            titleView.isVisible -> timeView.baseline
            timeView.isVisible -> timeView.baseline
            else -> 0
        }
        val targetBaselinePos = dateView.bottom + targetBaseline + if (titleView.isVisible) cloudVerticalPadding else 0

        val timeLeft = rightPos - timeView.measuredWidth
        val timeTop = targetBaselinePos - timeView.baseline
        timeView.safeLayout(timeLeft, timeTop)

        val statusLeft = timeView.left - statusView.measuredWidth
        val statusTop = targetBaselinePos - statusView.baseline
        statusView.layout(statusLeft, statusTop)

        val contentMaxRight = rightPos - cloudHorizontalPadding
        val expectedContentLeft = contentMaxRight - contentView.measuredWidth
        val expectedTitleLeft = statusLeft - statusAdditionalLeftSpacing - titleView.measuredWidth
        val externalContentMaxLeft = contentMaxRight - additionalContentMaxWidth
        val expectedThreadLeft = rightPos - (childThreadWidth ?: 0) + cloudHorizontalPadding

        val titleTop = targetBaselinePos - titleView.baseline
        val contentLeft = minOf(expectedContentLeft, expectedTitleLeft, externalContentMaxLeft, expectedThreadLeft)
        titleView.safeLayout(contentLeft, titleTop)
        val contentTop = maxOf(titleView.bottom, timeView.bottom)
            .plus(if (hasFirstAttachment) firstAttachmentTopSpacing else 0)
            .plus(if (titleView.isGone && timeView.isGone) cloudVerticalPadding else 0)
        contentView.layout(contentLeft, contentTop)

        var lastChild: View = contentView
        additionalContentList.forEach {
            if (!it.isVisible) return@forEach
            it.layout(contentLeft, lastChild.bottom)
            lastChild = it
        }

        backgroundView.layout(
            contentLeft - cloudHorizontalPadding,
            dateView.bottom,
            rightPos,
            bottomPos
        )
    }
}