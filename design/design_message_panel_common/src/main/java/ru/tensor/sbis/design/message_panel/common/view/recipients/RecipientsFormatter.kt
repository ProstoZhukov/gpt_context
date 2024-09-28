package ru.tensor.sbis.design.message_panel.common.view.recipients

import android.content.res.Resources
import android.text.TextUtils
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView.RecipientsViewData
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientDepartmentItem
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientPersonItem
import ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate
import ru.tensor.sbis.persons.util.formatName

/**
 * Форматтер текста имен получателей и счетчиков для компонента [MessagePanelRecipientsView].
 *
 * @author vv.chekurda
 */
internal class RecipientsFormatter(resources: Resources) {

    private val maxShowingRecipientsCount =
        if (resources.getBoolean(R.bool.is_tablet)) MAX_SHOWING_RECIPIENTS_COUNT_TABLET
        else MAX_SHOWING_RECIPIENTS_COUNT_PHONE

    /**
     * Получить отформатированный текст счетчика для значения [value].
     */
    fun getFormattedCount(value: Int): CharSequence =
        if (value > 0) COUNTER_FORMAT.format(value) else StringUtils.EMPTY

    /**
     * Получить отформатированный текст имен получателей по модели [data].
     */
    fun getFormattedRecipientsNames(data: RecipientsViewData): CharSequence =
        data.recipients.take(maxShowingRecipientsCount)
            .joinToString { item ->
                when (item) {
                    is RecipientPersonItem -> item.name.formatName(PersonNameTemplate.SURNAME_N)
                        .takeIf { formattedName -> formattedName.isNotBlank() }
                        // Для подразделений в качестве Face.
                        ?: item.data1
                    is RecipientDepartmentItem -> item.name
                }
            }

    /**
     * Получить отформатированный текст имен получателей и счетчик получателей,
     * которые скрыты, тк не влезли в указанный [availableWidth].
     *
     * @param data данные получателей.
     * @param availableWidth доступная ширина для разметки [namesLayout] и [counterLayout].
     * @param namesLayout текстовая разметка перечни имен получателей.
     * @param counterLayout текстовая разметка для счетчика получателей, которые не влезли в [availableWidth].
     * @return отформатированный текст имен получателей и счетчик скрытых получателей.
     */
    fun getFormattedNamesAndCount(
        data: RecipientsViewData,
        availableWidth: Int,
        namesLayout: TextLayout,
        counterLayout: TextLayout
    ): Pair<CharSequence, Int> {
        if (!data.hasRecipients || availableWidth < 0) return StringUtils.EMPTY to 0

        val recipientsString = getFormattedRecipientsNames(data)
        val recipientsWidth = namesLayout.getDesiredWidth(recipientsString)

        fun ellipsizeRecipients(recipientsString: CharSequence, count: Int = 0): Pair<CharSequence, Int> {
            val moreCountWidth = getFormattedCount(count).let { countString ->
                if (countString.isNotEmpty()) counterLayout.getDesiredWidth(countString)
                else 0
            }
            val recipientsHorizontalPadding = namesLayout.run { paddingStart + paddingEnd }
            val availableTextWidth = availableWidth - recipientsHorizontalPadding - moreCountWidth
            val ellipsizedRecipients = TextUtils.ellipsize(
                recipientsString,
                namesLayout.textPaint,
                availableTextWidth.toFloat(),
                TextUtils.TruncateAt.END
            )

            val recipientsSplit = ellipsizedRecipients.split(", ")
            val additionalCount = recipientsSplit.lastOrNull()?.let { lastRecipient ->
                val isEllipsized = lastRecipient.lastOrNull()?.toString() == ELLIPSIZE_CHAR
                val isSoSmall = isEllipsized && lastRecipient.length < MIN_RECIPIENT_SYMBOLS_COUNT_WITH_ELLIPSIZE
                if (isSoSmall) 1 else 0
            } ?: 0

            val newMoreCount = data.recipients.size - recipientsSplit.size + additionalCount
            return if (newMoreCount != count) {
                ellipsizeRecipients(recipientsString, newMoreCount)
            } else {
                ellipsizedRecipients to newMoreCount
            }
        }

        return if (recipientsWidth <= availableWidth) {
            recipientsString to 0
        } else {
            ellipsizeRecipients(recipientsString)
        }
    }
}

/** Формат отображения счетчика скрытых получателей, которые не поместились в панель. */
private const val COUNTER_FORMAT = "+%s"
/** Эмпирическая величина количетсва имен получателей, которые могут поместиться на длинном телефоне, с учетом коротких фамилий. */
private const val MAX_SHOWING_RECIPIENTS_COUNT_PHONE = 12
/** Эмпирическая величина количетсва имен получателей, которые могут поместиться на длинном планшете, с учетом коротких фамилий. */
private const val MAX_SHOWING_RECIPIENTS_COUNT_TABLET = MAX_SHOWING_RECIPIENTS_COUNT_PHONE * 2
/** Минимальное количество символов в фамилии последнего отображаемого получателя, чтобы счесть его видимым. */
private const val MIN_RECIPIENT_SYMBOLS_COUNT = 4
/** То же самое, что и [MIN_RECIPIENT_SYMBOLS_COUNT] + один символ троеточия. */
private const val MIN_RECIPIENT_SYMBOLS_COUNT_WITH_ELLIPSIZE = MIN_RECIPIENT_SYMBOLS_COUNT + 1
/** Символ троеточия (сокращения). */
private const val ELLIPSIZE_CHAR = "\u2026"