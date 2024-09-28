package ru.tensor.sbis.design.cloud_view.video.layout

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.cloud_view.layout.children.CloudDateTimeView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudStatusView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudTitleView
import ru.tensor.sbis.design.cloud_view.video.VideoMessageCloudView
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasure
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredHeight
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredWidth
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.utils.getDimenPx
import kotlin.math.max

/**
 * Разметка входящего видеосообщения [VideoMessageCloudView].
 * @see VideoMessageCloudViewLayout
 *
 * @author vv.chekurda
 */
internal class VideoMessageIncomeLayout(
    parent: ViewGroup,
    @StyleRes styleRes: Int = R.style.IncomeCloudViewCellStyle
) : VideoMessageCloudViewLayout(parent, styleRes) {

    /**
     * Минимальный отступ от левого края разметки до левого края облачка [backgroundView].
     */
    private val minimumCloudLeftSpacing = context.getDimenPx(RDesign.attr.offset_m)

    /**
     * Минимальный отступ от правого края разметки до правого края облачка [backgroundView].
     */
    private val minimumCloudRightSpacing = resources.getDimensionPixelSize(R.dimen.cloud_view_income_end_spacing_width)

    /**
     * Отступ левого края статуса [statusView] до [titleView]
     */
    private val statusLeftSpacing = context.getDimenPx(RDesign.attr.offset_xs)

    override val titleView: CloudTitleView = CloudTitleView(context, R.style.IncomeCloudViewTitleStyle)
    override val statusView: CloudStatusView = CloudStatusView(context, R.style.IncomeCloudViewStatusStyle)
    override val timeView: CloudDateTimeView = CloudDateTimeView(context, R.style.IncomeCloudViewTimeStyle)
    override val dateView: CloudDateTimeView = CloudDateTimeView(context, R.style.IncomeCloudViewDateStyle)
    override val personView: PersonView
        get() = if (::lazyPersonView.isInitialized) lazyPersonView else {
            PersonView(context).apply {
                id = R.id.cloud_view_person_photo
                updatePadding(
                    left = context.getDimenPx(RDesign.attr.offset_2xs),
                    right = context.getDimenPx(RDesign.attr.offset_xs)
                )
                setSize(PhotoSize.XS)
                this@VideoMessageIncomeLayout.addView(this, 0)
                lazyPersonView = this
            }
        }
    private lateinit var lazyPersonView: PersonView
    private val hasPersonView: Boolean
        get() = ::lazyPersonView.isInitialized && lazyPersonView.isVisible

    private val isCloudVisible: Boolean
        get() = messageLayout.isVisible

    init {
        titleView.id = R.id.cloud_view_title
        statusView.id = R.id.cloud_view_status
        timeView.id = R.id.cloud_view_time
        dateView.id = R.id.cloud_view_date
        videoMessageView.id = R.id.cloud_view_content
        backgroundView.id = R.id.cloud_view_background_income
        addViews(
            backgroundView,
            titleView,
            statusView,
            timeView,
            dateView,
            videoMessageView,
            quoteMarkerView
        )
    }

    override fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val wrappedSpec = MeasureSpecUtils.makeUnspecifiedSpec()

        val cloudHorizontalPadding = if (isCloudVisible) cloudHorizontalPadding else 0
        val cloudVerticalPadding = if (isCloudVisible) cloudVerticalPadding else 0

        timeView.measure(wrappedSpec, wrappedSpec)
        dateView.safeMeasure(wrappedSpec, wrappedSpec)
        statusView.measure(wrappedSpec, wrappedSpec)

        val (photoWidth, photoHeight) = if (hasPersonView) {
            personView.run {
                measure(wrappedSpec, wrappedSpec)
                measuredWidth to measuredHeight
            }
        } else 0 to 0

        val cloudLeftPos = max(minimumCloudLeftSpacing, photoWidth)
        val statusLeftPos = width - max(timeView.measuredWidth + statusView.measuredWidth, minimumCloudRightSpacing)
        val cloudMaxRightPos = statusLeftPos - statusLeftSpacing
        val cloudMaxWidth = cloudMaxRightPos - cloudLeftPos
        val contentMaxWidth = cloudMaxWidth - cloudHorizontalPadding * 2
        val contentWidthSpec = MeasureSpecUtils.makeAtMostSpec(contentMaxWidth)
        titleView.safeMeasure(contentWidthSpec, wrappedSpec)
        messageLayout.safeMeasure(contentWidthSpec, wrappedSpec)
        quoteMarkerView.safeMeasure(wrappedSpec, wrappedSpec)
        val videoMessageWidthSpec = MeasureSpecUtils.makeAtMostSpec(cloudMaxWidth)
        videoMessageView.measure(videoMessageWidthSpec, wrappedSpec)

        var height = dateView.safeMeasuredHeight
        val contentHeight = titleView.safeMeasuredHeight
            .plus(messageLayout.safeMeasuredHeight)
            .plus(quoteMarkerView.safeMeasuredHeight)
            .plus(videoMessageView.measuredHeight)
            .plus(
                when {
                    isCloudVisible -> cloudVerticalPadding * 2
                    titleView.isVisible -> titleBottomPadding
                    else -> 0
                }
            )
        height += maxOf(photoHeight, contentHeight)

        setMeasuredDimension(width, height)
    }

    override fun layout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.layout(changed, left, top, right, bottom)

        val cloudHorizontalPadding = if (isCloudVisible) cloudHorizontalPadding else 0
        val cloudVerticalPadding = if (isCloudVisible) cloudVerticalPadding else 0

        val dateLeft = rightPos - dateView.measuredWidth
        val dateTop = topPos
        dateView.safeLayout(dateLeft, dateTop)

        val targetBaseline = when {
            titleView.isVisible -> titleView.baseline + cloudVerticalPadding
            videoMessageView.baseline > 0 -> videoMessageView.baseline + cloudVerticalPadding
            else -> timeView.baseline
        }
        val targetBaselinePos = dateView.bottom + targetBaseline
        val cloudTop = dateView.bottom

        var personRight = 0
        if (hasPersonView) {
            personView.layout(leftPos, cloudTop)
            personRight = personView.right
        }

        val timeLeft = rightPos - timeView.measuredWidth
        val timeTop = targetBaselinePos - timeView.baseline
        timeView.layout(timeLeft, timeTop)

        val statusLeft = timeView.left - statusView.measuredWidth
        val statusTop = targetBaselinePos - statusView.baseline
        statusView.layout(statusLeft, statusTop)

        val cloudLeft = max(leftPos + minimumCloudLeftSpacing, personRight)

        val titleTop = targetBaselinePos - titleView.baseline
        val titleLeft = cloudLeft + cloudHorizontalPadding
        titleView.safeLayout(titleLeft, titleTop)

        val messageTop = if (titleView.isVisible) titleView.bottom else dateView.bottom + cloudVerticalPadding
        messageLayout.safeLayout(titleLeft, messageTop)
        quoteMarkerView.safeLayout(
            cloudLeft + videoMessageView.videoPlayerMeasuredWidth - quoteMarkerView.safeMeasuredWidth,
            messageLayout.bottom + cloudVerticalPadding
        )

        val videoMessageTop = when {
            isCloudVisible -> quoteMarkerView.bottom
            titleView.isVisible -> messageLayout.bottom + titleBottomPadding
            else -> messageLayout.bottom
        }
        videoMessageView.layout(cloudLeft, videoMessageTop)

        val maxContentRight = maxOf(titleView.right, messageLayout.right)
        val cloudRight = maxContentRight + cloudHorizontalPadding
        backgroundView.layout(
            cloudLeft,
            cloudTop,
            cloudRight,
            messageLayout.bottom + cloudVerticalPadding
        )
    }
}