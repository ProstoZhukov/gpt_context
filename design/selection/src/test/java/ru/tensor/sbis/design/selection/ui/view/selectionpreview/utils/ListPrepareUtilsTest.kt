package ru.tensor.sbis.design.selection.ui.view.selectionpreview.utils

import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.ui.utils.CounterFormat
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.DefaultSelectionPreviewItem
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.DefaultSelectionSuggestionItem
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionPreviewListData
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionSuggestionListData
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.vm.*

/**
 * Тест утилит, формирующих списки вьюмоделей для компонента превью выбора
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ListPrepareUtilsTest {

    private val mockStringProvider = mock<StringProvider> {
        on { getFormattedString(anyInt(), any()) } doReturn "formatted string"
        on { getString(anyInt()) } doReturn "string"
        on { getText(anyInt()) } doReturn "text"
    }

    @Test
    fun `When preview list size doesn't exceed maximum displayed count, returns viewmodel list with all items and dividers`() {
        val data = createPreviewListData(itemCount = 3, maxDisplayedEntries = 3)

        val result = prepareSelectionPreviewList(data, mockStringProvider)

        assertEquals(5, result.size)
        assertTrue(result[0] is SelectionPreviewItemVm)
        assertTrue(result[1] is SelectionPreviewDividerItemVm)
        assertTrue(result[2] is SelectionPreviewItemVm)
        assertTrue(result[3] is SelectionPreviewDividerItemVm)
        assertTrue(result[4] is SelectionPreviewItemVm)
    }

    @Test
    fun `When preview list size exceeds maximum displayed count, returns viewmodel list with displayed items, dividers and total count item`() {
        val data = createPreviewListData(itemCount = 3, maxDisplayedEntries = 2)

        val result = prepareSelectionPreviewList(data, mockStringProvider)

        assertEquals(4, result.size)
        assertTrue(result[0] is SelectionPreviewItemVm)
        assertTrue(result[1] is SelectionPreviewDividerItemVm)
        assertTrue(result[2] is SelectionPreviewItemVm)
        assertTrue(result[3] is SelectionPreviewTotalCountItemVm)
    }

    @Test
    fun `When suggestion list size doesn't exceed maximum displayed count, returns viewmodel list with header, all items and dividers`() {
        val data = createSuggestionListData(itemCount = 3, maxDisplayedEntries = 3)

        val result = prepareSuggestionList(data, mockStringProvider)

        assertEquals(6, result.size)
        assertTrue(result[0] is SelectionSuggestionHeaderItemVm)
        assertTrue(result[1] is SelectionSuggestionItemVm)
        assertTrue(result[2] is SelectionPreviewDividerItemVm)
        assertTrue(result[3] is SelectionSuggestionItemVm)
        assertTrue(result[4] is SelectionPreviewDividerItemVm)
        assertTrue(result[5] is SelectionSuggestionItemVm)
    }

    @Test
    fun `When header title is not set, returns viewmodel list without header`() {
        val data = createSuggestionListData(itemCount = 3, maxDisplayedEntries = 3, showHeader = false)

        val result = prepareSuggestionList(data, mockStringProvider)

        assertEquals(5, result.size)
        assertTrue(result[0] is SelectionSuggestionItemVm)
        assertTrue(result[1] is SelectionPreviewDividerItemVm)
        assertTrue(result[2] is SelectionSuggestionItemVm)
        assertTrue(result[3] is SelectionPreviewDividerItemVm)
        assertTrue(result[4] is SelectionSuggestionItemVm)
    }

    @Test
    fun `When suggestion list size exceeds maximum displayed count, returns viewmodel list with header, footer, displayed items and dividers`() {
        val data = createSuggestionListData(itemCount = 3, maxDisplayedEntries = 2)

        val result = prepareSuggestionList(data, mockStringProvider)

        assertEquals(5, result.size)
        assertTrue(result[0] is SelectionSuggestionHeaderItemVm)
        assertTrue(result[1] is SelectionSuggestionItemVm)
        assertTrue(result[2] is SelectionPreviewDividerItemVm)
        assertTrue(result[3] is SelectionSuggestionItemVm)
        assertTrue(result[4] is SelectionSuggestionMoreItemVm)
    }

    @Test
    fun `When showMoreItem parameter set to true, then suggestion viewmodel list contains 'more' item, even though its size doesn't exceed maximum displayed count`() {
        val data = createSuggestionListData(itemCount = 2, maxDisplayedEntries = 3, alwaysShowMoreItem = true)

        val result = prepareSuggestionList(data, mockStringProvider)

        assertEquals(5, result.size)
        assertTrue(result[0] is SelectionSuggestionHeaderItemVm)
        assertTrue(result[1] is SelectionSuggestionItemVm)
        assertTrue(result[2] is SelectionPreviewDividerItemVm)
        assertTrue(result[3] is SelectionSuggestionItemVm)
        assertTrue(result[4] is SelectionSuggestionMoreItemVm)
    }

    @Test
    fun `Selection preview item mapper should return correct viewmodel`() {
        val item = DefaultSelectionPreviewItem("title", true, false)
        val itemClickAction: (DefaultSelectionPreviewItem) -> Unit = mock()
        val removeClickAction: (DefaultSelectionPreviewItem) -> Unit = mock()

        val result = item.toItemVm(itemClickAction, removeClickAction)
        result.onClick?.invoke()
        result.onRemoveClick?.invoke()

        assertEquals(item.title, result.title)
        assertEquals(item.isAcceptable, result.isMarkIconVisible)
        assertEquals(item.isCancellable, result.isRemoveIconVisible)
        verify(itemClickAction).invoke(item)
        verify(removeClickAction).invoke(item)
    }

    @Test
    fun `Selection suggestion item mapper should return correct viewmodel`() {
        val item = DefaultSelectionSuggestionItem("title", 123456)
        val itemClickAction: (DefaultSelectionSuggestionItem) -> Unit = mock()

        val result = item.toItemVm(itemClickAction)
        result.onClick?.invoke()

        assertEquals(item.title, result.title)
        assertEquals(CounterFormat.THOUSANDS_DECIMAL_FORMAT.format(item.count), result.count)
        verify(itemClickAction).invoke(item)
    }

    private fun createPreviewListData(itemCount: Int, maxDisplayedEntries: Int): SelectionPreviewListData<*> {
        val items = (0 until itemCount).map { DefaultSelectionPreviewItem(it.toString(), true, true) }
        return SelectionPreviewListData(items, mock(), maxDisplayedEntries)
    }

    private fun createSuggestionListData(
        itemCount: Int,
        maxDisplayedEntries: Int,
        alwaysShowMoreItem: Boolean = false,
        showHeader: Boolean = true
    ): SelectionSuggestionListData<*> {
        val items = (0 until itemCount).map { DefaultSelectionSuggestionItem(it.toString(), 0) }
        val headerTitle = if (showHeader) 0 else null
        return if (alwaysShowMoreItem) {
            SelectionSuggestionListData(headerTitle, items, mock(), maxDisplayedEntries, showMoreItem = true)
        } else {
            SelectionSuggestionListData(headerTitle, items, mock(), maxDisplayedEntries)
        }
    }
}