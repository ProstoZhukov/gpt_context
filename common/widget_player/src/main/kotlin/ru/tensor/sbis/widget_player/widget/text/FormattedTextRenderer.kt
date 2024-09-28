package ru.tensor.sbis.widget_player.widget.text

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.text_span.span.CustomTypefaceSpan
import ru.tensor.sbis.richtext.R
import ru.tensor.sbis.richtext.span.LinkUrlSpan
import ru.tensor.sbis.richtext.span.background.TextBackgroundColorSpan
import ru.tensor.sbis.richtext.util.HtmlHelper
import ru.tensor.sbis.widget_player.converter.element.decor.TextHighlight
import ru.tensor.sbis.widget_player.converter.style.FontStyle
import ru.tensor.sbis.widget_player.converter.style.FontWeight
import ru.tensor.sbis.widget_player.converter.style.FormattedTextColor
import ru.tensor.sbis.widget_player.converter.style.FormattedTextStyle
import ru.tensor.sbis.widget_player.converter.style.TextDecoration
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.layout.widget.WidgetRenderer
import ru.tensor.sbis.widget_player.res.array.IntArrayRes
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams
import ru.tensor.sbis.widget_player.widget.link.DecoratedLinkOptions
import timber.log.Timber
import java.util.regex.Pattern

/**
 * @author am.boldinov
 */
internal class FormattedTextRenderer(
    private val context: WidgetContext,
    private val textOptions: TextOptions,
    private val linkOptions: DecoratedLinkOptions
) : WidgetRenderer<FormattedTextElement> {

    override val view = FormattedTextView(context).apply {
        setDefaultWidgetLayoutParams()
        linksClickable = textOptions.linksClickable
    }

    override fun render(element: FormattedTextElement) {
        val spannable = SpannableString(element.text)
        element.textAttributes.formats.forEach { range ->
            range.style.toSpanList(context) { span ->
                val start = range.location
                val end = start + range.length
                if (start < end) {
                    spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        with(view) {
            with(element.style) {
                setTextSizePx(fontSize.getValue(context))
                setTextColor(textColor.getValue(context))
                setLinkTextColor(linkTextColor.getValue(context))
                setTextDecoration(textDecoration)
                setFontStyle(fontStyle, fontWeight)
                spannable.highlight(context, element.textHighlight)
                configure {
                    text = spannable
                    alignment = element.textAlign
                }
            }
        }
    }

    private inline fun FormattedTextStyle.toSpanList(context: Context, consumer: (CharacterStyle) -> Unit) {
        font?.apply {
            relativeFontWeight?.apply {
                when (this) {
                    "bold" -> consumer(CustomTypefaceSpan(FontWeight.BOLD.getTypeface(context)))
                }
            }
            style?.apply {
                when (this) {
                    "italic" -> consumer(CustomTypefaceSpan(TypefaceManager.getRobotoItalicFont(context)))
                }
            }
        }
        decoration?.apply {
            line?.forEach {
                when (it) {
                    "underline" -> consumer(UnderlineSpan())
                    "line-through" -> consumer(StrikethroughSpan())
                }
            }
        }
        color?.toColorInt(context, textOptions.textColorPalette)?.let { color ->
            consumer(ForegroundColorSpan(color))
        }
        backgroundColor?.toColorInt(context, textOptions.backgroundColorPalette)?.let { color ->
            val cornerRadius = context.resources.getDimension(R.dimen.richtext_background_corner_radius)
            consumer(TextBackgroundColorSpan(color, cornerRadius))
        }
        href?.apply {
            consumer(LinkUrlSpan(this, linkOptions.linkOpener))
        }
    }

    private fun FormattedTextColor.toColorInt(context: Context, palette: IntArrayRes): Int? {
        rgbColor?.let {
            parseRgbColorPalette(context, it, palette)?.let { color ->
                return color
            }
        }
        hexColor?.let {
            HtmlHelper.parseColor(it)?.let { color ->
                return color
            }
        }
        absoluteColor?.let {
            return it.getValue(context)
        }
        return null
    }

    private fun FormattedTextView.setTextDecoration(decoration: TextDecoration) {
        when (decoration) {
            TextDecoration.NORMAL -> {
                setStrikeText(false)
                setUnderlineText(false)
            }

            TextDecoration.UNDERLINE -> {
                setStrikeText(false)
                setUnderlineText(true)
            }

            TextDecoration.LINE_THROUGH -> {
                setUnderlineText(false)
                setStrikeText(true)
            }
        }
    }

    private fun FormattedTextView.setFontStyle(fontStyle: FontStyle, fontWeight: FontWeight) {
        when (fontStyle) {
            FontStyle.NORMAL -> {
                setTypeface(fontWeight.getTypeface(context))
            }

            FontStyle.ITALIC -> {
                setTypeface(TypefaceManager.getRobotoItalicFont(context))
            }

            FontStyle.MONO -> {
                setTypeface(TypefaceManager.getRobotoMonoRegularFont(context))
            }
        }
        if (fontStyle != FontStyle.NORMAL) {
            configure {
                when (fontWeight) {
                    FontWeight.NORMAL -> {
                        paint.isFakeBoldText = false
                    }

                    FontWeight.BOLD -> {
                        paint.isFakeBoldText = true // TODO add bold italic
                    }
                }
            }
        }
    }

    private fun Spannable.highlight(context: Context, highlight: TextHighlight?) {
        highlight?.let {
            val color = it.backgroundColor.getValue(context)
            it.words.forEach { query ->
                if (query.isNotEmpty()) {
                    // Превращающаем метасимволы в литералы-части паттерна
                    val searchPattern = Pattern.compile(
                        query.replace("(", "\\(").replace(")", "\\)"),
                        Pattern.CASE_INSENSITIVE
                    )
                    val matchResults = searchPattern.matcher(this)
                    while (matchResults.find()) {
                        setSpan(
                            BackgroundColorSpan(color),
                            matchResults.start(),
                            matchResults.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
        }

    }

    @ColorInt
    private fun parseRgbColorPalette(
        context: Context,
        relativeColor: String,
        colorPalette: IntArrayRes
    ): Int? {
        try {
            val colorNumber = relativeColor.replace("[^0-9]".toRegex(), "").toInt() - 1 // на вебе счет от единицы
            if (colorNumber >= 0) {
                val palette = colorPalette.getValue(context)
                if (colorNumber < palette.size) {
                    return palette[colorNumber]
                }
            }
        } catch (e: NumberFormatException) {
            Timber.e(e)
        }
        return null
    }
}