package ru.tensor.sbis.common_views

import android.content.Context
import android.graphics.Paint
import android.os.Parcelable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import timber.log.Timber
import ru.tensor.sbis.design.text_span.R as RTextSpan
import ru.tensor.sbis.design.R as RDesign

/**
 * Кастомная реализация [AppCompatTextView] с поддержкой выделения текста.
 * */
class HighlightedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var baseText: Spannable = SpannableString("")
        set(value) {
            field = value
            lastBaseTextPosition = value.lastIndex
        }
    private var fullTextWidth: Float = 0f
    private var maxHighlightPosition: Int? = null
    private val searchColor: Int
    private var lastBaseTextPosition: Int = 0

    private var postfixText: Spannable? = null
    private var postfixPaint: Paint? = null
    private var postfixTextSizeSpan: AbsoluteSizeSpan? = null
    private var postfixTextColorSpan: ForegroundColorSpan? = null
    private var space = StringUtils.SPACE

    private val baseTextWithEllipsis: SpannableStringBuilder
        get() = SpannableStringBuilder(baseText)
            .append(ellipsis)
            .append(postfixText)

    private val needToHighlightEllipsis: Boolean
        get() = (maxHighlightPosition ?: 0) > lastBaseTextPosition + 1

    init {
        var postfixColor = currentTextColor
        var postfixTextSize = textSize.toInt()

        attrs?.let {
            with(context.obtainStyledAttributes(attrs, RTextSpan.styleable.TextViewWithPostFix, defStyleAttr, 0)) {
                try {
                    postfixTextSize =
                        getDimensionPixelSize(RTextSpan.styleable.TextViewWithPostFix_postfix_text_size, postfixTextSize)
                    postfixColor = getColor(RTextSpan.styleable.TextViewWithPostFix_postfix_text_color, postfixColor)
                    space = getString(RTextSpan.styleable.TextViewWithPostFix_space) ?: StringUtils.SPACE
                } finally {
                    recycle()
                }
            }
        }

        this.searchColor = context.getColorFromAttr(RDesign.attr.textBackgroundColorDecoratorHighlight)
        postfixPaint = Paint(paint).apply {
            textSize = postfixTextSize.toFloat()
            color = postfixColor
        }
        postfixTextSizeSpan = AbsoluteSizeSpan(postfixTextSize)
        postfixTextColorSpan = ForegroundColorSpan(postfixColor)
    }

    /**
     * Метод для установки выделенного текста.
     *
     * @param text исходный текст.
     * @param highlights список [SearchSpan], который нужно выделить.
     * @param postfix постфикс.
     * */
    @JvmOverloads
    fun setTextWithHighlight(text: CharSequence?, highlights: List<SearchSpan>? = null, postfix: CharSequence = "") {
        if (highlights.isNullOrEmpty() && postfix.isEmpty()) {
            setSimpleText(text)
            return
        }

        baseText = text?.highlightText(highlights) ?: SpannableString("")
        postfixText = generatePostfix(postfix)
        maxHighlightPosition = highlights?.maxOfOrNull { it.end }

        val textWithPostfix = SpannableStringBuilder(baseText).append(postfixText)
        fullTextWidth = paint.measureText(textWithPostfix.toString())

        this.text = textWithPostfix
    }


    /**
     * Метод для установки выделенного текста.
     *
     * @param text исходный текст.
     * @param highlights список индексов, по которым будет выполнено выделение текста.
     * @param postfix постфикс.
     * */
    @JvmOverloads
    @JvmName(name = "setTextWithHighlightPositions")
    fun setTextWithHighlight(text: CharSequence?, highlights: List<Int>? = null, postfix: CharSequence = "") {
        setTextWithHighlight(text, highlights?.toSearchSpanList(), postfix)
    }

    /**
     * Метод для установки текста.
     *
     * @param text текст.
     * */
    fun setSimpleText(text: CharSequence?) {
        ellipsize = TextUtils.TruncateAt.END
        baseText = SpannableString("")
        this.text = text?.let(::SpannableStringBuilder)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        checkToTrim()
    }

    private fun checkToTrim() {
        if (ellipsize == TextUtils.TruncateAt.END) {
            val measuredWidth = measuredWidth - compoundPaddingRight - compoundPaddingLeft
            val measuredWidthForAllLines = measuredWidth.toFloat() * maxLines
            if (fullTextWidth > measuredWidthForAllLines) {
                trimTextToWidth(measuredWidthForAllLines)
            } else if (lineCount > maxLines) {
                trimOverText()
            }
        }
    }

    private fun trimTextToWidth(viewWidth: Float) {
        if (baseText.isBlank()) return
        text = baseTextWithEllipsis.manualTrim(viewWidth).highlightEllipsisIfNeed()
    }

    private fun trimOverText() {
        if (lastBaseTextPosition > 0
            && text.length - lastBaseTextPosition > 2 // фикс креша возникающего от гонки процессов при быстром листании.
        // по хорошему надо сделать синхронное исполнение методов,
        // ну или не пользоваться нпонятно как меняющимся lastBaseTextPosition вместо параметров.
        ) {
            text = SpannableStringBuilder(text).delete(lastBaseTextPosition, lastBaseTextPosition + 1)
            lastBaseTextPosition--
        }
    }

    private fun SpannableStringBuilder.manualTrim(viewWidth: Float) = apply {
        lastBaseTextPosition = baseText.lastIndex
        if (lastBaseTextPosition <= 1 || fullTextWidth <= viewWidth) return@apply

        val ellipsisCount = (layout.getEllipsisCount(maxLines - 1) - 1).coerceAtLeast(0)
        val deletionStartIndex = baseText.lastIndex - ellipsisCount

        val deletionStart = deletionStartIndex.coerceIn(2, baseText.lastIndex)
        delete(deletionStart, baseText.length)

        fullTextWidth = paint.measureText(this, 0, length)
        lastBaseTextPosition = deletionStart - 1
    }

    private fun generatePostfix(postfix: CharSequence) = SpannableStringBuilder().apply {
        if (postfix.isNotEmpty()) {
            append(postfix)
            setSpan(postfixTextColorSpan, 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(postfixTextSizeSpan, 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun List<Int>.toSearchSpanList() = ArrayList<SearchSpan>().also {
        forEachIndexed { index, item -> if (index % 2 == 0) it.add(SearchSpan(item, 0)) else it.last().end = item + 1 }
    }

    private fun Spannable.highlightEllipsisIfNeed() = apply {
        if (needToHighlightEllipsis) {
            val firstEllipsisPosition = lastBaseTextPosition + 1
            setSpan(
                BackgroundColorSpan(searchColor),
                firstEllipsisPosition,
                firstEllipsisPosition + ellipsis.length,
                SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun CharSequence.highlightText(spanList: List<SearchSpan>?): Spannable =
        (this as? Spannable ?: SpannableString(this)).apply {
            try {
                spanList?.forEach {
                    setSpan(
                        BackgroundColorSpan(searchColor),
                        it.start,
                        it.end,
                        SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            } catch (ex: IndexOutOfBoundsException) {
                // Подстраховка крашей из-за ошибок локализации -
                // последняя позиция выделения не должна превышать последний индекс строки
                Timber.e("Ошибочные позиции выделения текста при поиске: ${ex.message}")
            }
        }
}

@Parcelize
data class SearchSpan(var start: Int = 0, var end: Int = 0) : Parcelable {

    override fun toString(): String = "SearchSpan{start=$start, end=$end}"
}

private const val ellipsis = "\u2026"
