package ru.tensor.sbis.design.text_span.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.TypefaceManager
import kotlin.math.min
import ru.tensor.sbis.design.text_span.R
import ru.tensor.sbis.design.text_span.span.util.BreadCrumbsAttributes
import ru.tensor.sbis.design.R as RDesign

/**
 * Реализация [ReplacementSpan] для отображения многосегментного списка одной строкой
 * с поддержкой отображения совпадений при поиске. Пример использования в справочнике
 * сотрудников. Применяется к любой строке, например, пустой (поскольку впоследствии)
 * вместо неё будет отрисован многосегментный путь, содержащийся в [sections].
 *
 * @param context Android-контекст для доступа к ресурсам
 * @param sections многосегментный путь
 * @param highlights диапазоны совпадений при поиске (имеет значение по умолчанию - пустой список,
 * поскольку данная возможность является опциональной)
 *
 * @author us.bessonov
 */
class BreadCrumbsSpan(
    context: Context,
    private val sections: List<String>,
    private val highlights: List<List<Int>> = emptyList(),
    attributes: BreadCrumbsAttributes = BreadCrumbsAttributes()
) : ReplacementSpan() {

    @Deprecated(
        "В связи с переходом на глобальные переменные, не нужно явно указывать цвета в конструкторе; " +
            "будет удалён по https://dev.sbis.ru/opendoc.html?guid=12ff9583-2db7-4c2a-8d42-2f0890f329e7&client=3"
    )
    constructor(
        context: Context,
        sections: List<String>,
        highlights: List<List<Int>> = emptyList(),
        @ColorRes paletteColorHeader: Int = RDesign.color.palette_colorHeader,
        @ColorRes textSearchHighlightColor: Int = RDesign.color.text_search_highlight_color,
        @DimenRes breadCrumbsHorizontalMargin: Int = R.dimen.text_span_horizontal_margin,
        @DimenRes textSize: Int = RDesign.dimen.size_body2_scaleOff,
        @DimenRes iconsSize: Int = RDesign.dimen.size_caption2_scaleOff
    ) : this(context, sections, highlights)

    private val styleHolder = BreadCrumbsStyleHolder(attributes)
        .apply { loadStyle(context) }

    private val ellipsisSymbol: String = "\u2026"
    private val dividerSymbol: String = context.resources.getString(RDesign.string.design_mobile_icon_video_play)
    private val bounds = Rect()

    private var ellipsisSymbolWidth: Float
    private var dividerWidth: Float
    private val margin: Int = styleHolder.horizontalMargin
    private var dividerWidthWithMargins: Float

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val symbolPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val highlightPaint = TextPaint(textPaint)

    private val firstOrEmptyHighlightsEntry: List<Int>
        get() = highlights.firstOrNull() ?: ArrayList()

    private val lastOrEmptyHighlightsEntry: List<Int>
        get() = highlights.lastOrNull() ?: ArrayList()

    init {
        textPaint.typeface = TypefaceManager.getRobotoRegularFont(context)
        textPaint.color = styleHolder.textColor
        textPaint.textSize = styleHolder.textSize

        symbolPaint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
        symbolPaint.color = styleHolder.textColor
        symbolPaint.textSize = styleHolder.iconSize

        highlightPaint.color = styleHolder.highlightsColor

        ellipsisSymbolWidth = textPaint.measureText(ellipsisSymbol)
        dividerWidth = symbolPaint.measureText(dividerSymbol)
        dividerWidthWithMargins = dividerWidth + margin * 2
    }

    // TODO https://online.sbis.ru/opendoc.html?guid=289db0c1-01cc-4ca8-94cd-426dd0d26334&client=3
    @Deprecated("Временное решение, см. TODO")
    fun setTextColor(@ColorInt color: Int) {
        textPaint.color = color
        symbolPaint.color = color
    }

    // TODO https://online.sbis.ru/opendoc.html?guid=289db0c1-01cc-4ca8-94cd-426dd0d26334&client=3
    @Deprecated("Временное решение, см. TODO")
    fun setTextSize(size: Float) {
        textPaint.textSize = size
    }

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?) = 0

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {

        canvas.getClipBounds(bounds)
        val availableWidth: Float = bounds.right - x

        when (sections.size) {
            0 -> return
            1 -> drawSinglePath(canvas, x, top.toFloat(), y.toFloat(), bottom.toFloat(), availableWidth)
            2 -> drawDoublePath(canvas, x, top.toFloat(), y.toFloat(), bottom.toFloat(), availableWidth)
            else -> drawMultiplePath(canvas, x, top.toFloat(), y.toFloat(), bottom.toFloat(), availableWidth)
        }
    }

    /**
     * Метод для отрисовки односегментного пути.
     *
     * @param canvas canvas, на котором будет нарисован односигментный путь
     * @param x x-координата левого края сегмента пути
     * @param top верхняя граница для отрисовки области совпадения при поиске
     * @param y y-координата базовой линии текста
     * @param bottom нижняя граница для отрисовки области совпадения при поиске
     * @param availableWidth доступная ширина для отрисовки пути
     */
    private fun drawSinglePath(
        canvas: Canvas,
        x: Float,
        top: Float,
        y: Float,
        bottom: Float,
        availableWidth: Float
    ) {

        val segment = Segment(sections.first(), textPaint.measureText(sections.first()), SegmentPosition.FIRST)

        if (segment.width > availableWidth) {
            // если не всё помещается, то эллипсайзим
            ellipsizeSegment(segment, textPaint, availableWidth)
        }
        highlightSegment(segment, firstOrEmptyHighlightsEntry, canvas, textPaint, highlightPaint, x, top, bottom)
        canvas.drawText(segment.text, x, y, textPaint)
    }

    /**
     * Метод для отрисовки двухсегментного пути.
     * Разделителем выступает [dividerSymbol].
     *
     * Аргументы метода аналогичны аргументам метода [drawSinglePath].
     */
    private fun drawDoublePath(
        canvas: Canvas,
        x: Float,
        top: Float,
        y: Float,
        bottom: Float,
        availableWidth: Float
    ) {

        val availableWidthForText = availableWidth - dividerWidthWithMargins
        val segment1 = Segment(sections.first(), textPaint.measureText(sections.first()), SegmentPosition.FIRST)
        val segment2 = Segment(sections.last(), textPaint.measureText(sections.last()), SegmentPosition.LAST)

        if (segment1.width + segment2.width <= availableWidthForText) {

            // 1. если всё помещается, то отрисовываем всё целиком
            highlightSegment(segment1, firstOrEmptyHighlightsEntry, canvas, textPaint, highlightPaint, x, top, bottom)
            canvas.drawText(segment1.text, x, y, textPaint)

            val dividerStart = x + segment1.width + margin
            drawPathDivider(canvas, dividerStart, y)

            val segment2TextStart = dividerStart + dividerWidth + margin
            highlightSegment(
                segment2, lastOrEmptyHighlightsEntry, canvas, textPaint, highlightPaint, segment2TextStart, top, bottom
            )
            canvas.drawText(segment2.text, segment2TextStart, y, textPaint)

        } else {

            // 2. если всё не помещается, находим меньшую ширину сегмента
            val smallerSegment = getSmallerSegment(segment1, segment2)
            if (smallerSegment.width <= availableWidthForText / 2) {

                // 2.1. если меньший сегмент меньше или равен половине доступного для текста пространства
                // (доступное пространство минус ширина разделителя и отступов), то рисуем его целиком,
                // а оставшееся пространство отдаем большему сегменту

                // меньший сегмент рисуем целиком, больший - эллипсайзим
                if (smallerSegment.position != SegmentPosition.FIRST) {
                    ellipsizeSegment(segment1, textPaint, availableWidthForText - segment2.width)
                }
                highlightSegment(
                    segment1, firstOrEmptyHighlightsEntry, canvas, textPaint, highlightPaint, x, top, bottom
                )
                canvas.drawText(segment1.text, x, y, textPaint)

                val dividerStart = x + segment1.width + margin
                drawPathDivider(canvas, dividerStart, y)

                val segment2TextStart = dividerStart + dividerWidth + margin
                if (smallerSegment.position != SegmentPosition.LAST) {
                    ellipsizeSegment(segment2, textPaint, availableWidthForText - segment1.width)
                }
                highlightSegment(
                    segment2,
                    lastOrEmptyHighlightsEntry,
                    canvas,
                    textPaint,
                    highlightPaint,
                    segment2TextStart,
                    top,
                    bottom
                )
                canvas.drawText(segment2.text, segment2TextStart, y, textPaint)

            } else {

                // 2.2. если меньший сегмент больше половины доступного для текста пространства,
                // то делим доступное пространство между сегментами пополам
                ellipsizeSegment(segment1, textPaint, availableWidthForText / 2)
                highlightSegment(
                    segment1,
                    firstOrEmptyHighlightsEntry,
                    canvas,
                    textPaint,
                    highlightPaint,
                    x,
                    top,
                    bottom
                )
                canvas.drawText(segment1.text, x, y, textPaint)

                val dividerStart = x + segment1.width + margin
                drawPathDivider(canvas, dividerStart, y)

                val segment2TextStart = dividerStart + dividerWidth + margin
                ellipsizeSegment(segment2, textPaint, availableWidthForText / 2)
                highlightSegment(
                    segment2,
                    lastOrEmptyHighlightsEntry,
                    canvas,
                    textPaint,
                    highlightPaint,
                    segment2TextStart,
                    top,
                    bottom
                )
                canvas.drawText(segment2.text, segment2TextStart, y, textPaint)
            }
        }
    }

    /**
     * Метод для отрисовки многосегментного пути.
     * В данной реализации отображаются только первый и последний сегменты,
     * а между ними хлебные крошки [ellipsisSymbol] и разделители [dividerSymbol].
     *
     * Аргументы метода аналогичны аргументам метода [drawSinglePath].
     */
    private fun drawMultiplePath(
        canvas: Canvas,
        x: Float,
        top: Float,
        y: Float,
        bottom: Float,
        availableWidth: Float
    ) {

        val availableWidthForText = availableWidth - 2 * dividerWidthWithMargins - ellipsisSymbolWidth
        val segment1 = Segment(sections.first(), textPaint.measureText(sections.first()), SegmentPosition.FIRST)
        val segment3 = Segment(sections.last(), textPaint.measureText(sections.last()), SegmentPosition.LAST)

        if (segment1.width + segment3.width <= availableWidthForText) {

            // 1. если всё помещается, то отрисовываем всё целиком
            highlightSegment(segment1, firstOrEmptyHighlightsEntry, canvas, textPaint, highlightPaint, x, top, bottom)
            canvas.drawText(segment1.text, x, y, textPaint)

            val divider1Start = x + segment1.width + margin
            drawPathDivider(canvas, divider1Start, y)

            val ellipsisSymbolStart = divider1Start + dividerWidth + margin
            if (highlights.size > 1 && highlights[1].isNotEmpty()) {
                canvas.drawRect(
                    ellipsisSymbolStart, top,
                    ellipsisSymbolStart + ellipsisSymbolWidth,
                    bottom, highlightPaint
                )
            }
            canvas.drawText(ellipsisSymbol, ellipsisSymbolStart, y, textPaint)

            val divider2Start = ellipsisSymbolStart + ellipsisSymbolWidth + margin
            drawPathDivider(canvas, divider2Start, y)

            val segment3TextStart = divider2Start + dividerWidth + margin
            highlightSegment(
                segment3, lastOrEmptyHighlightsEntry,
                canvas, textPaint,
                highlightPaint, segment3TextStart,
                top, bottom
            )
            canvas.drawText(segment3.text, segment3TextStart, y, textPaint)

        } else {

            // 2. если всё не помещается, находим меньшую ширину сегмента
            val smallerSegment = getSmallerSegment(segment1, segment3)
            if (smallerSegment.width <= availableWidthForText / 2) {
                // 2.1. если меньший сегмент меньше или равен половине доступного для текста пространства
                // (доступное пространство минус ширина разделителя и отступов), то рисуем его целиком,
                // а оставшееся пространство отдаем большему сегменту

                // меньший сегмент рисуем целиком, больший - эллипсайзим
                if (smallerSegment.position != SegmentPosition.FIRST) {
                    ellipsizeSegment(segment1, textPaint, availableWidthForText - segment3.width)
                }
                highlightSegment(
                    segment1, firstOrEmptyHighlightsEntry,
                    canvas, textPaint,
                    highlightPaint, x,
                    top, bottom
                )
                canvas.drawText(segment1.text, x, y, textPaint)

                val divider1Start = x + segment1.width + margin
                drawPathDivider(canvas, divider1Start, y)

                val ellipsisSymbolStart = divider1Start + dividerWidth + margin
                if (highlights.size > 1 && highlights[1].isNotEmpty()) {
                    canvas.drawRect(
                        ellipsisSymbolStart, top,
                        ellipsisSymbolStart + ellipsisSymbolWidth, bottom,
                        highlightPaint
                    )
                }
                canvas.drawText(ellipsisSymbol, ellipsisSymbolStart, y, textPaint)

                val divider2Start = ellipsisSymbolStart + ellipsisSymbolWidth + margin
                drawPathDivider(canvas, divider2Start, y)

                val segment3TextStart = divider2Start + dividerWidth + margin
                if (smallerSegment.position != SegmentPosition.LAST) {
                    ellipsizeSegment(segment3, textPaint, availableWidthForText - segment1.width)
                }
                highlightSegment(
                    segment3, lastOrEmptyHighlightsEntry,
                    canvas, textPaint, highlightPaint, segment3TextStart, top, bottom
                )
                canvas.drawText(segment3.text, segment3TextStart, y, textPaint)

            } else {
                // 2.2. если меньший сегмент больше половины доступного для текста пространства,
                // то делим доступное пространство между сегментами пополам
                ellipsizeSegment(segment1, textPaint, availableWidthForText / 2)
                highlightSegment(
                    segment1, firstOrEmptyHighlightsEntry,
                    canvas, textPaint, highlightPaint, x, top, bottom
                )
                canvas.drawText(segment1.text, x, y, textPaint)

                val divider1Start = x + segment1.width + margin
                drawPathDivider(canvas, divider1Start, y)

                val ellipsisSymbolStart = divider1Start + dividerWidth + margin
                if (highlights.size > 1 && highlights[1].isNotEmpty()) {
                    canvas.drawRect(
                        ellipsisSymbolStart, top,
                        ellipsisSymbolStart + ellipsisSymbolWidth, bottom, highlightPaint
                    )
                }
                canvas.drawText(ellipsisSymbol, ellipsisSymbolStart, y, textPaint)

                val divider2Start = ellipsisSymbolStart + ellipsisSymbolWidth + margin
                drawPathDivider(canvas, divider2Start, y)

                val segment3TextStart = divider2Start + dividerWidth + margin
                ellipsizeSegment(segment3, textPaint, availableWidthForText / 2)
                highlightSegment(
                    segment3, lastOrEmptyHighlightsEntry, canvas,
                    textPaint, highlightPaint, segment3TextStart, top, bottom
                )
                canvas.drawText(segment3.text, segment3TextStart, y, textPaint)
            }
        }
    }

    /**
     * Метод для рисования разделителя сегментов пути с отступами.
     * @param canvas canvas, на котором будет нарисован разделитель сегментов пути
     * @param x x-координата для отрисовки
     * @param y y-координата для отрисовки
     */
    private fun drawPathDivider(canvas: Canvas, x: Float, y: Float) {
        canvas.drawText(dividerSymbol, x, y, symbolPaint)
    }

    /**
     * Метод для эллипсайза сегмента пути.
     * Обратите внимание, что метод принимает на вход объект [segment] и модифицирует его
     * свойства; а после вызова данного метода во внешнем scope используется изменённый объект.
     */
    private fun ellipsizeSegment(segment: Segment, textPaint: TextPaint, availableWidthForSegment: Float) {
        val ellipsizedSegment =
            TextUtils.ellipsize(segment.text, textPaint, availableWidthForSegment, TextUtils.TruncateAt.END)
        segment.text = ellipsizedSegment.toString()
        segment.width = availableWidthForSegment
        segment.ellipsized = true
    }

    /**
     * Отрисовывает области совпадения при поиске.
     * @param segment сегмент пути для отрисовки
     * @param highlights диапазоны совпадений текста при поиске
     * @param canvas canvas, на котором будут подсвечены области совпадения при поиске в сегменте пути
     * @param textPaint paint для рисования текста
     * @param highlightPaint paint для рисования совпадений текста при поиске
     * @param x x-координата левого края сегмента пути
     * @param top верхняя граница для отрисовки области совпадения при поиске
     * @param bottom нижняя граница для отрисовки области совпадения при поиске
     */
    private fun highlightSegment(
        segment: Segment,
        highlights: List<Int>,
        canvas: Canvas,
        textPaint: TextPaint,
        highlightPaint: TextPaint,
        x: Float,
        top: Float,
        bottom: Float
    ) {

        var ellipsizedHighlightStart: Int
        var ellipsizedHighlightEnd: Int

        var ellipsizedHighlightStartX: Float
        var ellipsizedHighlightEndX: Float

        for (i in highlights.indices step 2) {
            if (segment.ellipsized) {
                ellipsizedHighlightStart = min(highlights[i], segment.text.lastIndex)
                ellipsizedHighlightEnd = min(highlights[i + 1], segment.text.lastIndex)
                ellipsizedHighlightStartX = x + textPaint.measureText(segment.text, 0, ellipsizedHighlightStart)
                ellipsizedHighlightEndX =
                    x + textPaint.measureText(segment.text, 0, ellipsizedHighlightEnd + 1)
            } else {
                ellipsizedHighlightStartX = x + textPaint.measureText(segment.text, 0, highlights[i])
                ellipsizedHighlightEndX = x + textPaint.measureText(segment.text, 0, highlights[i + 1] + 1)
            }

            canvas.drawRect(ellipsizedHighlightStartX, top, ellipsizedHighlightEndX, bottom, highlightPaint)
        }
    }

    /**
     * Метод для определения самого короткого сегмента пути; если сегменты равны,
     * то каждый из них является самым коротким.
     *
     * @return сегмент с меньшей шириной из [segment1] и [segment2]. Если ширина сегментов
     * одинакова, то возвращает [segment1].
     */
    private fun getSmallerSegment(segment1: Segment, segment2: Segment) = when {
        segment1.width <= segment2.width -> segment1
        else -> segment2
    }

    private class Segment(
        var text: String,
        var width: Float,
        val position: SegmentPosition,
        var ellipsized: Boolean = false
    )

    private enum class SegmentPosition {
        FIRST,
        LAST
    }

}