package ru.tensor.sbis.richtext.converter.handler

import android.os.Build
import android.text.Editable
import android.text.Spanned
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.date.DateFormatTemplate
import ru.tensor.sbis.common.util.date.DateFormatUtils
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.richtext.converter.MarkSpan
import ru.tensor.sbis.richtext.converter.TagAttributes
import ru.tensor.sbis.richtext.converter.cfg.BlockQuoteSenderConfiguration
import ru.tensor.sbis.richtext.converter.handler.base.SimpleTagHandler
import timber.log.Timber

/**
 * Обработчик тегов автора цитаты, автор по умолчанию выводится перед содержимым цитаты в виде заголовка
 *
 * @author am.boldinov
 */
class BlockQuoteSenderTagHandler(
    context: SbisThemedContext,
    configuration: BlockQuoteSenderConfiguration
) : SimpleTagHandler() {

    private val style by lazy(LazyThreadSafetyMode.NONE) {
        BlockQuoteSenderStyle(context, configuration)
    }

    private val dateFormatter by lazy(LazyThreadSafetyMode.NONE) {
        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DATE_FORMAT_TARGET_API_24
        } else {
            DATE_FORMAT
        }
        DateFormatUtils.getFormatter(format)
    }

    override fun onStartTag(stream: Editable, attributes: TagAttributes) {
        val name = attributes.getValue("name")
        if (name != null) {
            val date = attributes.getValue("date")
            val start = stream.length
            stream.append(name)
            if (date != null) {
                try {
                    val formattedDate = DateFormatUtils.format(
                        dateFormatter.parse(date),
                        DateFormatTemplate.WITHOUT_YEAR
                    )
                    stream.append(", ")
                    stream.append(formattedDate)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            stream.append(StringUtils.LF)
            stream.setSpan(
                MarkSpan.ForegroundColor(style.senderTextColor).realSpan,
                start,
                stream.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            stream.setSpan(
                MarkSpan.FontSize(style.senderTextSize).realSpan,
                start,
                stream.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    override fun onEndTag(stream: Editable) {
        // ignore
    }

    companion object {
        private const val DATE_FORMAT_TARGET_API_24 = "yyyy-MM-dd HH:mm:ss.SSSX"
        private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ"
    }

    private class BlockQuoteSenderStyle(
        context: SbisThemedContext,
        configuration: BlockQuoteSenderConfiguration
    ) {

        val senderTextColor = configuration.senderTextColor.getColor(context)
        val senderTextSize = configuration.senderTextSize.getDimenPx(context)
    }
}
