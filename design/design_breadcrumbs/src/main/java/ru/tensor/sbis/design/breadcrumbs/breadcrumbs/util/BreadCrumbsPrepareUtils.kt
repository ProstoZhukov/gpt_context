/**
 * Методы, отвечающие за бизнес-логику по формированию хлебных крошек, используемую в BreadCrumbsView
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.breadcrumbs.breadcrumbs.util

import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.tensor.sbis.design.utils.ELLIPSIS
import ru.tensor.sbis.design.utils.getExpectedTextWidth
import ru.tensor.sbis.design.utils.getMinTextWidth
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model.BreadCrumb
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model.BreadCrumbViewData

/**
 * Идентификатор специального элемента хлебных крошек ([BreadCrumb]) для отображения троеточия вместо скрытых элементов.
 * Нажатия на элемент с этим идентификатором не обрабатываются
 */
internal const val ELLIPSIS_ID = "ELLIPSIS_ID"
// при модификации нужно обратить внимание на ru.tensor.sbis.design.utils.MIN_VISIBLE_CHARACTERS
private const val MIN_VISIBLE_CHARACTERS = 3

private data class VisibleBreadCrumb(val item: BreadCrumb, @Px val width: Int = WRAP_CONTENT)

/**
 * Создаёт данные хлебных крошек, которые фактически будут отображаться на экране
 */
internal fun prepareBreadCrumbs(
    items: List<BreadCrumb>,
    titlePaint: TextPaint,
    @Px
    arrowWidth: Int,
    @Px
    availableWidth: Int,
    @ColorInt
    highlightColor: Int
): List<BreadCrumbViewData> {
    if (items.isEmpty()) return emptyList()

    val visibleBreadCrumbs = createVisibleBreadCrumbs(items, titlePaint, arrowWidth, availableWidth)
    return createBreadCrumbsViewData(visibleBreadCrumbs, titlePaint, highlightColor)
}

/**
 * Метод для формирования хлебных крошек по [правилам](http://axure.tensor.ru/standarts/v7/хлебные_крошки__версия_02_.html)
 */
private fun createVisibleBreadCrumbs(
    items: List<BreadCrumb>,
    titlePaint: Paint,
    @Px
    arrowWidth: Int,
    @Px
    availableWidth: Int
): List<VisibleBreadCrumb> {
    var ellipsizedIndex = (items.lastIndex - 1).coerceAtLeast(0)
    while (true) {
        val visibleTitles = items.take(ellipsizedIndex)
            .map { it.title }
            .toMutableList()
            .apply {
                if (ellipsizedIndex < items.lastIndex - 1) add(ELLIPSIS)
                if (items.size > 1) add(items.last().title)
            }

        val visibleTitlesWidth = calculateWidth(visibleTitles, titlePaint, arrowWidth)
        val probablyEllipsizedTitle = items[ellipsizedIndex].title
        val desiredTitleWidth = getExpectedTextWidth(probablyEllipsizedTitle, titlePaint)
        val desiredWidth = visibleTitlesWidth + desiredTitleWidth + if (items.size > 1) arrowWidth else 0

        if (desiredWidth <= availableWidth) {
            // все элементы поместятся целиком
            return getVisibleBreadCrumbs(items, ellipsizedIndex)
        }

        if (ellipsizedIndex > 0) {
            // пробуем сократить предыдущий элемент
            val availableForTitleWidth = availableWidth - visibleTitlesWidth - arrowWidth
            val minTitleWidth = getMinTextWidth(probablyEllipsizedTitle, titlePaint, MIN_VISIBLE_CHARACTERS)
            if (minTitleWidth <= availableForTitleWidth) {
                // элемент отобразится сокращённо, элементы правее, кроме последнего, будут скрыты
                return getVisibleBreadCrumbs(items, ellipsizedIndex, availableForTitleWidth)
            }
            // не удастся отобразить мин. число символов, элемент будет скрыт
            ellipsizedIndex--
        } else {
            // все элементы кроме первого и последнего будут скрыты
            if (items.size == 1) {
                // единственный элемент занимает всё доступное пространство
                return getVisibleBreadCrumbs(items, ellipsizedIndex, availableWidth)
            }

            val desiredLastTitleWidth = getExpectedTextWidth(items.last().title, titlePaint)
            val leftWidth = availableWidth - arrowWidth - if (items.size > 2) {
                getExpectedTextWidth(visibleTitles.first(), titlePaint) + arrowWidth
            } else {
                0
            }
            return when {
                desiredLastTitleWidth <= leftWidth / 2 -> {
                    // последний элемент займёт меньше 50% доступного места, оставшееся занимает первый
                    getVisibleBreadCrumbs(items, ellipsizedIndex, leftWidth - desiredLastTitleWidth)
                }
                desiredTitleWidth <= leftWidth / 2     -> {
                    // первый элемент займёт меньше 50% доступного места, оставшееся занимает последний
                    getVisibleBreadCrumbs(items, ellipsizedIndex, lastTitleWidth = leftWidth - desiredTitleWidth)
                }
                else                                   -> {
                    val firstTitleWidth = leftWidth / 2
                    val lastTitleWidth = leftWidth - firstTitleWidth
                    // первому и последнему элементам отведено по 50% доступного места, оба будут сокращены
                    getVisibleBreadCrumbs(items, ellipsizedIndex, firstTitleWidth, lastTitleWidth)
                }
            }
        }
    }
}

private fun getVisibleBreadCrumbs(
    items: List<BreadCrumb>,
    ellipsizedTitleIndex: Int,
    @Px
    ellipsizedTitleWidth: Int = WRAP_CONTENT,
    @Px
    lastTitleWidth: Int = WRAP_CONTENT
): List<VisibleBreadCrumb> {
    val count = ellipsizedTitleIndex.coerceAtMost(items.size - 1)
    return items.take(count)
        .mapTo(ArrayList(count)) { VisibleBreadCrumb(it) }
        .apply {
            if (ellipsizedTitleIndex < items.lastIndex) {
                add(VisibleBreadCrumb(items[ellipsizedTitleIndex], ellipsizedTitleWidth))
            }
            if (ellipsizedTitleIndex < items.lastIndex - 1) {
                add(VisibleBreadCrumb(createEllipsisBreadCrumb(items, ellipsizedTitleIndex)))
            }
            items.last().run {
                add(VisibleBreadCrumb(this, lastTitleWidth))
            }
        }
}

private fun createBreadCrumbsViewData(
    items: List<VisibleBreadCrumb>,
    titlePaint: TextPaint,
    @ColorInt
    highlightColor: Int
): List<BreadCrumbViewData> {
    return items.mapIndexed { i, it ->
        val highlightedEllipsisIndex = getHighlightedEllipsisIndex(it.item, titlePaint, it.width)
        BreadCrumbViewData(
            createSpannableWithHighlights(it.item, highlightColor, highlightedEllipsisIndex),
            it.item.id,
            i < items.lastIndex,
            it.width
        )
    }
}

private fun createSpannableWithHighlights(
    item: BreadCrumb,
    @ColorInt
    highlightColor: Int,
    highlightedEllipsisIndex: Int?
) = SpannableString(item.title).apply {
    item.highlights.forEach {
        setSpan(BackgroundColorSpan(highlightColor), it.first, it.last, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    highlightedEllipsisIndex?.let { i ->
        setSpan(BackgroundColorSpan(highlightColor), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

private fun calculateWidth(
    titles: List<String>,
    titlePaint: Paint,
    @Px
    arrowWidth: Int
): Int {
    if (titles.isEmpty()) return 0
    val arrowCount = titles.size - 1
    return titles.sumOf { getExpectedTextWidth(it, titlePaint) } + arrowCount * arrowWidth
}

private fun getHighlightedEllipsisIndex(
    item: BreadCrumb,
    titlePaint: TextPaint,
    @Px
    width: Int
): Int? {
    val title = item.title
    if (title.isEmpty() || width == WRAP_CONTENT || item.highlights.isEmpty()) return null

    val ellipsized = TextUtils.ellipsize(title, titlePaint, width.toFloat(), TextUtils.TruncateAt.END)

    return if (ellipsized.length < title.length &&
        ellipsized.lastOrNull() == ELLIPSIS.first() &&
        item.highlights.any { it.first > ellipsized.lastIndex }
    ) {
        ellipsized.lastIndex
    } else {
        null
    }
}

private fun createEllipsisBreadCrumb(items: List<BreadCrumb>, ellipsizedTitleIndex: Int): BreadCrumb {
    val isHighlighted = items.subList(ellipsizedTitleIndex, items.lastIndex)
        .any { it.highlights.isNotEmpty() }
    val highlights = if (isHighlighted) listOf(IntRange(0, ELLIPSIS.length)) else emptyList()
    return BreadCrumb(ELLIPSIS, ELLIPSIS_ID, highlights)
}