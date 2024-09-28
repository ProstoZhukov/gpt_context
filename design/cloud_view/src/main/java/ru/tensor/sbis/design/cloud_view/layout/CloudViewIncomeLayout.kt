package ru.tensor.sbis.design.cloud_view.layout

import android.view.View
import android.view.View.MeasureSpec.getSize
import android.view.ViewGroup
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.cloud_view.layout.children.CloudDateTimeView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudStatusView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudTitleView
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeAtMostSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasure
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredHeight
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.cloud_view.content.utils.MessagesViewPool
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.R as RDesign
import kotlin.math.max

/**
 * Разметка входящего сообщения ячейки-облака [CloudView].
 * @see CloudViewLayout
 *
 * @author vv.chekurda
 */
internal class CloudViewIncomeLayout(
    parent: ViewGroup,
    @StyleRes styleRes: Int = R.style.IncomeCloudViewCellStyle
) : CloudViewLayout(parent, styleRes) {

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
                    left = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_2xs),
                    right = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_xs)
                )
                setSize(PhotoSize.XS)
                this@CloudViewIncomeLayout.addView(this, 0)
                lazyPersonView = this
            }
        }
    private lateinit var lazyPersonView: PersonView
    private val hasPersonView: Boolean
        get() = ::lazyPersonView.isInitialized && lazyPersonView.isVisible

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
    }

    override fun setViewPool(viewPool: MessagesViewPool) {
        contentView.setViewPool(viewPool)
    }

    override fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getSize(widthMeasureSpec)
        val wrappedSpec = makeUnspecifiedSpec()

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
        val contentWidthSpec = makeAtMostSpec(contentMaxWidth)
        contentView.measure(contentWidthSpec, wrappedSpec)
        val titleMaxWidth = if (hasAttachments) contentView.measuredWidth else contentMaxWidth
        val titleWidthSpec = makeAtMostSpec(titleMaxWidth)
        titleView.safeMeasure(titleWidthSpec, wrappedSpec)

        var height = cloudVerticalPadding * 2 + dateView.safeMeasuredHeight
        val contentHeight = titleView.safeMeasuredHeight
            .plus(contentView.measuredHeight)
            .plus(measureAdditionalContent(contentWidthSpec, wrappedSpec))
            .plus(
                if (titleView.safeMeasuredHeight != 0 && hasFirstAttachment) firstAttachmentTopSpacing
                else 0
            )
        height += maxOf(photoHeight, contentHeight)

        setMeasuredDimension(width, height)
    }

    override fun layout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.layout(changed, left, top, right, bottom)

        val dateLeft = rightPos - dateView.measuredWidth
        val dateTop = topPos
        dateView.safeLayout(dateLeft, dateTop)

        val targetBaseline = when {
            titleView.isVisible -> titleView.baseline + cloudVerticalPadding
            contentView.baseline > 0 -> contentView.baseline + cloudVerticalPadding
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

        val contentTop = if (titleView.isVisible) {
            titleView.bottom + if (hasFirstAttachment) firstAttachmentTopSpacing else 0
        } else {
            cloudTop + cloudVerticalPadding
        }
        contentView.layout(cloudLeft + cloudHorizontalPadding, contentTop)

        var lastChild: View = contentView
        additionalContentList.forEach {
            if (!it.isVisible) return@forEach
            it.layout(titleLeft, lastChild.bottom)
            lastChild = it
        }

        val maxContentRight = maxOf(titleView.right, contentView.right, titleLeft + additionalContentMaxWidth)
        val cloudRight = maxContentRight + cloudHorizontalPadding
        backgroundView.layout(cloudLeft, cloudTop, cloudRight, bottomPos)
    }
}