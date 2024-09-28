package ru.tensor.sbis.design.cloud_view.thread

import android.content.Context
import android.graphics.Outline
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.cloud_view.R
import ru.tensor.sbis.design.cloud_view.thread.data.ThreadData
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounter
import ru.tensor.sbis.design.counters.utils.Formatter
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeAtMostSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasure
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasuredWidth
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.list_header.DateViewMode
import ru.tensor.sbis.design.list_header.ItemDateView
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.list_header.format.isToday
import ru.tensor.sbis.design.profile.personcollagelist.PersonCollageLineView
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.util.dpToPx
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.persons.util.formatName
import ru.tensor.sbis.design.R as RDesign

/**
 * View ячейки облачка треда.
 * https://www.figma.com/file/ezcGDOSBsOHIrO4YCw84p9/%D0%A2%D1%80%D0%B5%D0%B4%D1%8B-%D0%9C%D0%9F?type=design&node-id=1751-17608&mode=design
 *
 * @author vv.chekurda
 */
class CloudThreadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val titleView = SbisTextView(getContext(), R.style.ThreadTitleStyle)
    private val messageText = SbisTextView(getContext(), R.style.ThreadMessageStyle)
    private val unreadCount = SbisCounter(getContext()).apply {
        counterFormatter = Formatter.HundredFormatter
    }
    private val messageCount = SbisTextView(getContext(), R.style.ThreadMessageCountStyle)
    private val collageLineView = PersonCollageLineView(getContext()).apply {
        setMaxVisibleCount(3)
    }
    private val dateTimeView = ItemDateView(getContext()).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getDimenPx(RDesign.attr.fontSize_3xs_scaleOff).toFloat())
    }
    private val documentIcon = SbisTextView(getContext(), R.style.ThreadDocumentIconStyle) {
        text = SbisMobileIcon.Icon.smi_Sabydoc.character.toString()
        isVisible = false
    }
    private val threadIcon = SbisTextView(getContext(), R.style.ThreadIconStyle) {
        text = SbisMobileIcon.Icon.smi_newDialog.character.toString()
    }
    private val messagesIcon = SbisTextView(getContext(), R.style.ThreadMessagesIconStyle) {
        text = SbisMobileIcon.Icon.smi_menuMessages.character.toString()
    }
    private val backgroundView = View(getContext()).apply {
        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(
                    0,
                    0,
                    view.measuredWidth,
                    view.measuredHeight,
                    getContext().getDimenPx(RDesign.attr.borderRadius_2xs).toFloat()
                )
            }
        }
        setBackgroundColor(getContext().getThemeColorInt(RDesign.attr.unaccentedBackgroundColor))
    }

    private val dimens = ThreadViewDimens(getContext())
    private val simpleTextColor = getContext().getThemeColorInt(RDesign.attr.textColor)
    private val serviceTextColor = getContext().getThemeColorInt(RDesign.attr.labelContrastTextColor)

    private val leftMinSpace: Int
        get() {
            val data = data
            return when {
                data == null || data.isOutgoing -> dimens.leftMinSpaceOutgoing
                data.isGroupConversation -> dimens.leftMinSpaceIncomingGrouped
                else -> dimens.leftMinSpaceIncoming
            }
        }
    private val dateTimeFormatter = ListDateFormatter.DateTimeWithTodayShort(getContext())

    private var requireResetRecipientsTitle = false
    private var lastRecipientsTitleAvailableWidth = 0

    var data: ThreadData? = null
        set(value) {
            val isChanged = field != value
            val oldValue = field
            field = value
            if (isChanged) {
                onDataChanged(oldValue, value)
                safeRequestLayout()
            }
        }

    init {
        listOf(
            threadIcon,
            backgroundView,
            titleView,
            documentIcon,
            dateTimeView,
            messagesIcon,
            collageLineView,
            messageCount,
            unreadCount,
            messageText
        ).forEach(::addView)

        updatePadding(right = paddingRight.takeIf { it != 0 } ?: context.getDimenPx(RDesign.attr.offset_3xs))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val isWrappedWidth = MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = width - paddingStart - paddingEnd

        threadIcon.safeMeasure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        documentIcon.safeMeasure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        messagesIcon.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        unreadCount.safeMeasure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        messageCount.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())

        dateTimeView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        collageLineView.measure(makeUnspecifiedSpec(), makeExactlySpec(dimens.collageLineViewHeight))

        val backgroundMaxWidth = availableWidth - leftMinSpace

        val messageTextAvailableWidth = backgroundMaxWidth - dimens.titleLeftSpace - collageLineView.measuredWidth -
            dimens.messageLeftSpace - dimens.messageRightSpace -
            messagesIcon.measuredWidth - dimens.messageIconRightSpace -
            messageCount.measuredWidth - dimens.messageCountRightSpace -
            unreadCount.safeMeasuredWidth - dimens.unreadCountRightSpace
        val messageTextWidthSpec =
            if (isWrappedWidth) makeAtMostSpec(messageTextAvailableWidth)
            else makeExactlySpec(messageTextAvailableWidth)
        messageText.measure(messageTextWidthSpec, makeUnspecifiedSpec())

        val titleAvailableWidth = backgroundMaxWidth - dimens.titleLeftSpace -
            documentIcon.safeMeasuredWidth - (dimens.documentIconRightSpace.takeIf { documentIcon.isVisible } ?: 0) -
            dimens.timeLeftSpace - dimens.timeRightSpace - dateTimeView.measuredWidth

        checkRecipientsTitle(titleAvailableWidth)
        titleView.measure(makeAtMostSpec(titleAvailableWidth), makeUnspecifiedSpec())

        // Используем наименьшую разницу, чтобы врапить облачко по самому длинному тексту
        val backgroundWidthDiff = (messageTextAvailableWidth - messageText.measuredWidth)
            .coerceAtMost(titleAvailableWidth - titleView.measuredWidth)
        // Чтобы заново не суммировать все значения считаем от обратного:
        // максимальная ширина облака минус разница доступной
        // ширины текста/заголовка от фактической ширины этого текста
        val backgroundWidth = backgroundMaxWidth - backgroundWidthDiff
        val backgroundHeight = dimens.titleTopSpace + titleView.measuredHeight +
            dimens.messageTopSpace + dimens.messageBottomSpace +
            messageText.measuredHeight.coerceAtLeast(collageLineView.measuredHeight)
        backgroundView.measure(makeExactlySpec(backgroundWidth), makeExactlySpec(backgroundHeight))

        val height = dimens.backgroundTopSpace + backgroundView.measuredHeight + paddingTop + paddingBottom
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val backgroundStart = if (data?.isOutgoing == true) {
            measuredWidth - backgroundView.measuredWidth - paddingEnd
        } else {
            paddingStart + leftMinSpace
        }
        backgroundView.layout(
            backgroundStart,
            dimens.backgroundTopSpace + paddingTop
        )
        threadIcon.layout(
            backgroundView.left - dimens.threadIconRightSpace - threadIcon.measuredWidth,
            paddingTop
        )

        val titleBaseline = backgroundView.top + dimens.titleTopSpace + titleView.baseline
        documentIcon.safeLayout(
            backgroundView.left + dimens.titleLeftSpace,
            titleBaseline - documentIcon.baseline
        )
        titleView.safeLayout(
            documentIcon.right + (dimens.documentIconRightSpace.takeIf { documentIcon.isVisible } ?: 0),
            titleBaseline - titleView.baseline
        )
        dateTimeView.layout(
            backgroundView.right - dimens.timeRightSpace - dateTimeView.measuredWidth,
            titleBaseline - dateTimeView.baseline
        )

        collageLineView.layout(
            backgroundView.left + dimens.titleLeftSpace,
            titleView.bottom + dimens.collageTopSpace
        )
        messageText.layout(
            collageLineView.right + dimens.messageLeftSpace,
            collageLineView.top +
                if (messageText.lineCount == 1) {
                    (collageLineView.measuredHeight - messageText.measuredHeight) / 2
                } else {
                    0
                }
        )

        val messageCountTop = messageText.bottom - messageCount.measuredHeight - context.dpToPx(1) / 2
        val messagesIconTop = messageCountTop + context.dpToPx(1) +
            (messageCount.measuredHeight - messagesIcon.measuredHeight) / 2
        val unreadCountTop = messageCountTop - (unreadCount.measuredHeight - messageCount.measuredHeight) / 2

        messageCount.layout(
            backgroundView.right - dimens.unreadCountRightSpace -
                unreadCount.safeMeasuredWidth - (dimens.messageCountRightSpace.takeIf { unreadCount.isVisible } ?: 0) -
                messageCount.measuredWidth,
            messageCountTop
        )

        unreadCount.layout(messageCount.right + dimens.messageCountRightSpace, unreadCountTop)
        messagesIcon.layout(
            messageCount.left - dimens.messageIconRightSpace - messagesIcon.measuredWidth,
            messagesIconTop
        )
    }

    private fun onDataChanged(oldData: ThreadData?, newData: ThreadData?) {
        val data = newData ?: return
        val formattedDateTime = dateTimeFormatter.format(data.date)
        val collageData = data.recipients.map {
            PersonData(
                it.uuid,
                it.photoUrl,
                it.initialsStubData as? InitialsStubData
            )
        }.take(3)

        documentIcon.isVisible = data.showDocumentIcon
        if (data.title != null) {
            titleView.text = data.title
            requireResetRecipientsTitle = false
        } else {
            val recipientsChanged = newData.recipients.take(RECIPIENTS_TITLE_LIMIT) != oldData?.recipients?.take(
                RECIPIENTS_TITLE_LIMIT
            )
            if (recipientsChanged) requireResetRecipientsTitle = true
        }
        dateTimeView.dateViewMode = if (data.date.isToday()) DateViewMode.TIME_ONLY else DateViewMode.DATE_ONLY
        dateTimeView.setFormattedDateTime(formattedDateTime)
        collageLineView.setDataList(collageData)
        collageLineView.setTotalCount(data.recipientCount)
        messageText.text = data.relevantMessageText
        messageText.setTextColor(if (data.isServiceText) serviceTextColor else simpleTextColor)
        messageCount.text = data.messageCount.toString()
        unreadCount.isVisible = data.unreadCount > 0
        unreadCount.counter = data.unreadCount
    }

    private fun checkRecipientsTitle(titleAvailableWidth: Int) {
        if (titleView.text.isNullOrBlank() ||
            (data?.title == null && titleAvailableWidth != lastRecipientsTitleAvailableWidth)
        ) {
            requireResetRecipientsTitle = true
            lastRecipientsTitleAvailableWidth = titleAvailableWidth
        }
        if (requireResetRecipientsTitle) {
            titleView.text = getParticipantsTitle(titleAvailableWidth)
            requireResetRecipientsTitle = false
        }
    }

    private fun getParticipantsTitle(availableWidth: Int): String {
        val data = data ?: return EMPTY
        val nameList = data.recipients.map { it.name.formatName(PersonNameTemplate.SURNAME_N) }
            .take(RECIPIENTS_TITLE_LIMIT)
        val names = nameList.joinToString()
        val namesWidth = titleView.paint.measureText(names)
        return if (namesWidth <= availableWidth) {
            names
        } else {
            val ellipsizedNames = TextUtils.ellipsize(
                names,
                titleView.paint,
                availableWidth.toFloat(),
                TextUtils.TruncateAt.END
            ).toString()
            getFormattedEllipsizedTitle(ellipsizedNames)
        }
    }

    private fun getFormattedEllipsizedTitle(names: String): String {
        val isEllipsized = names.lastOrNull().toString() == ELLIPSIZE_CHAR
        return if (!isEllipsized) {
            names
        } else {
            val lastTextIndex = names.lastIndex - 1
            var newLastIndex = lastTextIndex
            var isCorrectChar = false
            do {
                if (newLastIndex > 0) {
                    val char = names.getOrNull(newLastIndex)
                    if (char.toString() == StringUtils.SPACE || char == COMMA || char == DOT) {
                        newLastIndex--
                    } else {
                        isCorrectChar = true
                    }
                } else break
            } while (!isCorrectChar)

            if (lastTextIndex > 0) {
                StringBuilder()
                    .append(names.subSequence(0, newLastIndex + 1))
                    .append(ELLIPSIZE_CHAR)
                    .toString()
            } else {
                names
            }
        }
    }

    fun getBackgroundWidth(): Int =
        backgroundView.measuredWidth
}

private class ThreadViewDimens(context: Context) {

    private val twoDp = context.dpToPx(2)
    private val fourDp = context.dpToPx(4)
    private val sixDp = context.dpToPx(6)
    private val tenDp = context.dpToPx(10)

    val collageLineViewHeight = context.dpToPx(24)
    val leftMinSpaceOutgoing = context.dpToPx(54)
    val leftMinSpaceIncoming = context.dpToPx(30)
    val leftMinSpaceIncomingGrouped = context.dpToPx(40)

    val backgroundTopSpace = fourDp

    val threadIconRightSpace = twoDp

    val titleLeftSpace = tenDp
    val titleTopSpace = sixDp

    val documentIconRightSpace = twoDp

    val timeLeftSpace = fourDp
    val timeRightSpace = tenDp

    val collageTopSpace = twoDp

    val messageLeftSpace = sixDp
    val messageRightSpace = fourDp
    val messageTopSpace = collageTopSpace
    val messageBottomSpace = sixDp

    val messageIconRightSpace = fourDp

    val messageCountRightSpace = fourDp

    val unreadCountRightSpace = tenDp
}

private const val COMMA = ','
private const val DOT = '.'
private const val ELLIPSIZE_CHAR = "\u2026"
private const val RECIPIENTS_TITLE_LIMIT = 5