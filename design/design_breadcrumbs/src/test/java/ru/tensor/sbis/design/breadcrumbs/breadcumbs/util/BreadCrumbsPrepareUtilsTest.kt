package ru.tensor.sbis.design.breadcrumbs.breadcumbs.util

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.text.getSpans
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.utils.ELLIPSIS
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.BreadCrumbsView
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model.BreadCrumb
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model.BreadCrumbViewData
import ru.tensor.sbis.design.breadcrumbs.breadcrumbs.util.prepareBreadCrumbs

private const val ARROW_WIDTH = 2
private const val AVAILABLE_WIDTH = 20
private const val HIGHLIGHT_COLOR = Color.YELLOW
private const val ELLIPSIS_WIDTH = 1

/**
 * Тесты формирования фактически отображаемых в [BreadCrumbsView] хлебных крошек
 *
 * @author us.bessonov
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], shadows = [ShadowTextUtils::class])
class BreadCrumbsPrepareUtilsTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private val textPaint = TextPaint()

    @Test
    fun `When all items fit completely, then width will be set to wrap_content for all titles`() {
        val rawItems = createItems("One", "Two", "Three")
        val result = testPrepareBreadCrumbsViewData(rawItems)

        val expected = rawItems.mapIndexed { i, it ->
            BreadCrumbViewData(SpannableString(it.title), it.id, i < rawItems.lastIndex, WRAP_CONTENT)
        }
        assertEquals(expected, result)
    }

    @Test
    fun `When there are two items that don't fit completely, and last title requires less than 50% of available space, then first title will occupy remaining space`() {
        val rawItems = createItems(
            "1234567890123",
            "123456"
        )
        val firstTitleWidth = AVAILABLE_WIDTH -
                ARROW_WIDTH -
                rawItems.last().title.length

        val result = testPrepareBreadCrumbsViewData(rawItems)

        assertEquals(rawItems.size, result.size)
        assertEquals(firstTitleWidth, result.first().width)
        assertEquals(WRAP_CONTENT, result.last().width)
    }

    @Test
    fun `When there are two items that don't fit completely, and first title requires less than 50% of available space, then last title will occupy remaining space`() {
        val rawItems = createItems(
            "123456",
            "1234567890123"
        )
        val lastTitleWidth = AVAILABLE_WIDTH -
                ARROW_WIDTH -
                rawItems.first().getDesiredWidth()

        val result = testPrepareBreadCrumbsViewData(rawItems)

        assertEquals(rawItems.size, result.size)
        assertEquals(WRAP_CONTENT, result.first().width)
        assertEquals(lastTitleWidth, result.last().width)
    }

    @Test
    fun `When there are two items that don't fit completely, and each title requires more than 50% of available space, then each will occupy 50% of space`() {
        val rawItems = createItems(
            "12345678901",
            "1234567890"
        )
        val titleWidth = (AVAILABLE_WIDTH - ARROW_WIDTH) / 2

        val result = testPrepareBreadCrumbsViewData(rawItems)

        assertEquals(rawItems.size, result.size)
        assertEquals(titleWidth, result.first().width)
        assertEquals(titleWidth, result.last().width)
    }

    @Test
    fun `When there are three items that don't fit completely, there is no room for second title, and last title requires less than 50% of available space, then first title will occupy remaining space`() {
        val rawItems = createItems(
            "1234567890123",
            "Collapsed title",
            "123456"
        )
        val firstTitleWidth = AVAILABLE_WIDTH -
                ELLIPSIS_WIDTH -
                ARROW_WIDTH * 2 -
                rawItems.last().getDesiredWidth()

        val result = testPrepareBreadCrumbsViewData(rawItems)

        assertEquals(3, result.size)
        assertEquals(SpannableString(ELLIPSIS), result[1].title)
        assertEquals(firstTitleWidth, result.first().width)
        assertEquals(WRAP_CONTENT, result.last().width)
    }

    @Test
    fun `When there are three items that don't fit completely, there is no room for second title, and first title requires less than 50% of available space, then last title will occupy remaining space`() {
        val rawItems = createItems(
            "123456",
            "Collapsed title",
            "1234567890123"
        )
        val lastTitleWidth = AVAILABLE_WIDTH -
                ELLIPSIS_WIDTH -
                ARROW_WIDTH * 2 -
                rawItems.first().getDesiredWidth()

        val result = testPrepareBreadCrumbsViewData(rawItems)

        assertEquals(3, result.size)
        assertEquals(SpannableString(ELLIPSIS), result[1].title)
        assertEquals(WRAP_CONTENT, result.first().width)
        assertEquals(lastTitleWidth, result.last().width)
    }

    @Test
    fun `When there are three items that don't fit completely, there is no room for second title, and each title requires more than 50% of available space, then each will occupy 50% of space`() {
        val rawItems = createItems(
            "12345678901",
            "Collapsed title",
            "1234567890"
        )
        val availableWidth = AVAILABLE_WIDTH -
                ELLIPSIS_WIDTH -
                ARROW_WIDTH * 2
        val firstTitleWidth = availableWidth / 2
        val lastTitleWidth = availableWidth - firstTitleWidth

        val result = testPrepareBreadCrumbsViewData(rawItems)

        assertEquals(3, result.size)
        assertEquals(SpannableString(ELLIPSIS), result[1].title)
        assertEquals(firstTitleWidth, result.first().width)
        assertEquals(lastTitleWidth, result.last().width)
    }

    @Test
    fun `When there are four items that don't fit completely, but second to last title can be ellipsized, then it's width will be restricted, and all the rest will be fully visible`() {
        val rawItems = createItems(
            "01234",
            "01",
            "Ellipsized title",
            "012"
        )
        val expectedWidth = AVAILABLE_WIDTH -
                rawItems[0].getDesiredWidth() -
                rawItems[1].getDesiredWidth() -
                rawItems[3].getDesiredWidth() -
                ARROW_WIDTH * 3

        val result = testPrepareBreadCrumbsViewData(rawItems)

        assertEquals(rawItems.size, result.size)
        assertEquals(WRAP_CONTENT, result[0].width)
        assertEquals(WRAP_CONTENT, result[1].width)
        assertEquals(expectedWidth, result[2].width)
        assertEquals(WRAP_CONTENT, result[3].width)
    }

    @Test
    fun `When there are five items that don't fit completely, and third and fourth can not be displayed, but second can be ellipsized, then its width will be restricted and third and fourth titles will be collapsed`() {
        val rawItems = createItems(
            "012",
            "Ellipsized title",
            "Collapsed title 1",
            "Collapsed title 2",
            "01234"
        )
        val expectedWidth = AVAILABLE_WIDTH -
                rawItems[0].getDesiredWidth() -
                rawItems[4].getDesiredWidth() -
                ELLIPSIS_WIDTH -
                ARROW_WIDTH * 3

        val result = testPrepareBreadCrumbsViewData(rawItems)

        assertEquals(4, result.size)
        assertEquals(SpannableString(rawItems[0].title), result[0].title)
        assertEquals(SpannableString(rawItems[1].title), result[1].title)
        assertEquals(SpannableString(ELLIPSIS), result[2].title)
        assertEquals(SpannableString(rawItems[4].title), result[3].title)

        assertEquals(WRAP_CONTENT, result[0].width)
        assertEquals(expectedWidth, result[1].width)
        assertEquals(WRAP_CONTENT, result[2].width)
        assertEquals(WRAP_CONTENT, result[3].width)
    }

    @Test
    fun `When highlighted regions specified, then they are applied as BackgroundSpan`() {
        val firstTitleHighlights = listOf(IntRange(0, 11))
        val collapsedTitleHighlights = listOf(IntRange(3, 6))
        val expectedCollapsedTitleHighlights = listOf(IntRange(0, ELLIPSIS.length))
        val lastTitleHighlights = listOf(IntRange(0, 1), IntRange(1, 5), IntRange(5, 10))
        val rawItems = createItems(
            "First title" to firstTitleHighlights,
            "Collapsed title" to collapsedTitleHighlights,
            "Last title" to lastTitleHighlights
        )

        val result = testPrepareBreadCrumbsViewData(rawItems)

        assertTrue(checkHasOnlyCorrespondingSpansForAllHighlights(result.first().title, firstTitleHighlights))
        assertTrue(checkHasOnlyCorrespondingSpansForAllHighlights(result[1].title, expectedCollapsedTitleHighlights))
        assertTrue(checkHasOnlyCorrespondingSpansForAllHighlights(result.last().title, lastTitleHighlights))
    }

    @Suppress("RemoveExplicitTypeArguments")
    @Test
    fun `When title is ellipsized, but highlighted region starts after ellipsis, then ellipsis character is also highlighted`() {
        val titleHighlights = listOf(IntRange(0, 2), IntRange(11, 16))
        val expectedHighlights: List<IntRange> = titleHighlights.plus<IntRange>(IntRange(4, 5))
        val rawItems = createItems(
            "12345" to emptyList(),
            "Ellipsized title" to titleHighlights,
            "123456" to emptyList()
        )

        val result = testPrepareBreadCrumbsViewData(rawItems)

        assertTrue(checkHasOnlyCorrespondingSpansForAllHighlights(result[1].title, expectedHighlights))
    }


    private fun checkHasOnlyCorrespondingSpansForAllHighlights(
        spannable: Spannable,
        highlights: List<IntRange>
    ): Boolean {
        val spans = spannable.getSpans<BackgroundColorSpan>()
        if (spans.size != highlights.size) return false

        return highlights.all {
            spans.any { span ->
                spannable.getSpanStart(span) == it.first && spannable.getSpanEnd(span) == it.last
            }
        }
    }

    private fun createItems(vararg titles: String): List<BreadCrumb> {
        return titles.mapIndexed { i, it -> BreadCrumb(it, i.toString()) }
    }

    private fun createItems(vararg titles: Pair<String, List<IntRange>>): List<BreadCrumb> {
        return titles.mapIndexed { i, it -> BreadCrumb(it.first, i.toString(), it.second) }
    }

    private fun testPrepareBreadCrumbsViewData(items: List<BreadCrumb>, availableWidth: Int = AVAILABLE_WIDTH) =
        prepareBreadCrumbs(
            items,
            textPaint,
            ARROW_WIDTH,
            availableWidth,
            HIGHLIGHT_COLOR
        )

    private fun BreadCrumb.getDesiredWidth() = title.length
}
