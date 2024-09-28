package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.participant_view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.profile.person.ActivityStatusView
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.ImageSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.TextColor

/**
 * Вью единственного участника на экране информации диалога/канала.
 *
 * @author dv.baranov
 */
internal class ConversationInformationParticipantView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
    private val controller: ConversationInformationParticipantViewController
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    ConversationInformationParticipantViewAPI by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0,
        @StyleRes defStyleRes: Int = 0
    ) : this(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
        ConversationInformationParticipantViewController()
    )

    private val activityStatusViewRightOffset = dp(STATUS_VIEW_RIGHT_OFFSET)
    private val mainTextHorizontalOffset = dp(MAIN_TEXT_HORIZONTAL_PADDING)

    private val personView = PersonView(context).apply {
        id = R.id.themes_registry_participant_view_photo_id
        layoutParams = LayoutParams(ImageSize.XL.getDimenPx(context), ImageSize.XL.getDimenPx(context))
    }

    private val fullNameTextView = SbisTextView(context).apply {
        id = R.id.themes_registry_participant_view_full_name_id
        setTextColor(TextColor.DEFAULT.getValue(context))
        textSize = FontSize.X3L.getScaleOnDimenPx(context).toFloat()
        textAlignment = TEXT_ALIGNMENT_CENTER
        gravity = Gravity.CENTER
    }

    private val positionTextView = SbisTextView(context).apply {
        id = R.id.themes_registry_participant_view_position_id
        setTextColor(TextColor.DEFAULT.getValue(context))
        alpha = 0.6f
        textSize = FontSize.XS.getScaleOnDimenPx(context).toFloat()
        textAlignment = TEXT_ALIGNMENT_CENTER
        gravity = Gravity.CENTER
    }

    private val activityStatusTextView = SbisTextView(context).apply {
        id = R.id.themes_registry_participant_view_status_text_id
        setTextColor(TextColor.DEFAULT.getValue(context))
        alpha = 0.6f
        textSize = FontSize.X3S.getScaleOnDimenPx(context).toFloat()
        textAlignment = TEXT_ALIGNMENT_TEXT_END
        gravity = Gravity.END
        setPadding(0, 0, Offset.X3S.getDimenPx(context), 0)
    }

    private val activityStatusView = ActivityStatusView(context).apply {
        id = R.id.themes_registry_participant_view_status_icon_id
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    }

    init {
        controller.initController(this)
        addView(personView)
        addView(fullNameTextView)
        addView(positionTextView)
        addView(activityStatusTextView)
        addView(activityStatusView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        personView.measure(
            makeExactlySpec(ImageSize.XL.getDimenPx(context)),
            makeExactlySpec(ImageSize.XL.getDimenPx(context))
        )
        val availableMainTextWidth = width - mainTextHorizontalOffset * 2 - paddingStart - paddingEnd
        fullNameTextView.measure(makeExactlySpec(availableMainTextWidth), makeUnspecifiedSpec())
        val positionHeight = if (positionTextView.text.isNullOrEmpty()) makeExactlySpec(0) else makeUnspecifiedSpec()
        positionTextView.measure(makeExactlySpec(availableMainTextWidth), positionHeight)
        activityStatusView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        val activityStatusTextWidth = (width - paddingStart - paddingEnd - personView.measuredWidth) / 2 -
            activityStatusView.measuredWidth - activityStatusViewRightOffset
        activityStatusTextView.measure(makeExactlySpec(activityStatusTextWidth), makeUnspecifiedSpec())
        val height = personView.measuredHeight + fullNameTextView.measuredHeight + positionTextView.measuredHeight +
            paddingTop + paddingBottom
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        personView.layout(getStartPositionInCenter(personView), 0)
        fullNameTextView.layout(getStartPositionInCenter(fullNameTextView), personView.bottom)
        positionTextView.layout(getStartPositionInCenter(positionTextView), fullNameTextView.bottom)
        activityStatusTextView.layout(personView.right, personView.top)
        activityStatusView.layout(activityStatusTextView.right, personView.top + dp(2))
    }

    private fun getStartPositionInCenter(view: View) =
        (measuredWidth - view.measuredWidth - paddingStart - paddingEnd) / 2

    private fun View.layout(start: Int, top: Int) = layout(start, top, start + measuredWidth, top + measuredHeight)
}

private const val MAIN_TEXT_HORIZONTAL_PADDING = 32
private const val STATUS_VIEW_RIGHT_OFFSET = 18