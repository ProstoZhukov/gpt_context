package ru.tensor.sbis.message_panel.view.mentions

import android.content.Context
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import java.util.UUID

/**
 * Базовая реализация Span для выделения упоминаний в тексте панели сообщений.
 *
 * @author dv.baranov
 */
internal abstract class BaseMentionSpan(
    private val context: Context // TODO
) : ForegroundColorSpan(context.getColorFromAttr(R.attr.secondaryTextColor)) {

    /** @SelfDocumented */
    var data: MentionData = MentionData(UUIDUtils.NIL_UUID, 0, 0)

    /** @SelfDocumented */
    fun setBounds(start: Int, end: Int) {
        data = data.copy(start = start, end = end)
    }

    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.textSize = FontSize.M.getScaleOnDimenPx(context).toFloat()
        super.updateDrawState(textPaint)
    }
}

/**
 * Span для уже созданных упоминаний, к которым прикреплена информация о персоне.
 */
internal class MentionSpan(context: Context) : BaseMentionSpan(context) {

    /** @SelfDocumented */
    fun setPersonUuid(uuid: UUID) {
        data = data.copy(personUuid = uuid)
    }
}

/**
 * Span для ещё не завершенных упоминаний.
 */
internal class EnterMentionSpan(context: Context) : BaseMentionSpan(context)

/**
 * Модель данных упоминаний.
 */
data class MentionData(
    val personUuid: UUID,
    val start: Int,
    val end: Int
)