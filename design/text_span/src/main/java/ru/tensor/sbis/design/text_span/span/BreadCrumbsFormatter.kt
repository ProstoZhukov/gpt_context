package ru.tensor.sbis.design.text_span.span

import android.content.Context
import android.graphics.Rect
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import android.text.Spannable
import android.text.SpannableString
import android.text.method.TransformationMethod
import android.view.View
import ru.tensor.sbis.design.text_span.R
import ru.tensor.sbis.design.text_span.span.util.BreadCrumbsAttributes
import ru.tensor.sbis.design.R as RDesign

/**
 * Реализация [TransformationMethod] для отображения многосегментного списка одной строкой
 * с поддержкой отображения совпадений при поиске. Пример использования в справочнике
 * сотрудников. Применяется к EditText [sections].
 * @param sections многосегментный путь
 * @param highlights диапазоны совпадений при поиске (имеет значение по умолчанию - пустой список,
 * поскольку данная возможность является опциональной)
 * @param attributes атрибуты внешнего вида хлебных крошек
 *
 * @author us.bessonov
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class BreadCrumbsFormatter(
    val sections: MutableList<String> = ArrayList(),
    val highlights: MutableList<ArrayList<Int>> = ArrayList(),
    private val attributes: BreadCrumbsAttributes = BreadCrumbsAttributes()
) : TransformationMethod {

    @Deprecated(
        "В связи с переходом на глобальные переменные, не нужно явно указывать цвета в конструкторе; " +
            "будет удалён по https://dev.sbis.ru/opendoc.html?guid=12ff9583-2db7-4c2a-8d42-2f0890f329e7&client=3"
    )
    constructor(
        sections: MutableList<String> = ArrayList(),
        highlights: MutableList<ArrayList<Int>> = ArrayList(),
        @ColorRes paletteColorHeader: Int = RDesign.color.palette_colorHeader,
        @ColorRes textSearchHighlightColor: Int = RDesign.color.text_search_highlight_color,
        @DimenRes breadCrumbsHorizontalMargin: Int = R.dimen.text_span_horizontal_margin,
        @DimenRes textSize: Int = RDesign.dimen.size_body2_scaleOff,
        @DimenRes iconsSize: Int = RDesign.dimen.size_caption2_scaleOff
    ) : this(sections, highlights)

    override fun onFocusChanged(
        view: View?,
        sourceText: CharSequence?,
        focused: Boolean,
        direction: Int,
        previouslyFocusedRect: Rect?
    ) = Unit

    override fun getTransformation(source: CharSequence, view: View): CharSequence =
        if (source is Spannable) {
            setSpans(source, view.context)
            source
        } else {
            val spawn = SpannableString(source)
            setSpans(spawn, view.context)
            spawn
        }

    private fun setSpans(spannable: Spannable, context: Context) {
        spannable.clearSpans()
        val breadCrumbsSpan = BreadCrumbsSpan(context, sections, highlights, attributes)
        spannable.setSpan(breadCrumbsSpan, 0, spannable.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun Spannable.clearSpans() = getSpans(0, this.length, BreadCrumbsSpan::class.java).forEach {
        this.removeSpan(it)
    }
}