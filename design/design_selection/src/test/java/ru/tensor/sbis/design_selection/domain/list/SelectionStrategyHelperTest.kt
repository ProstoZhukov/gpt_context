package ru.tensor.sbis.design_selection.domain.list

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.apache.commons.lang3.StringUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.communication_decl.selection.SelectionMode
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.ui.content.vm.search.SelectionSearchViewModel
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.SelectionLiveData

/**
 * Тесты вспомогательной реализацию для определения стратегии обработки команды выбора [SelectionStrategyHelper].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionStrategyHelperTest {

    private lateinit var helper: SelectionStrategyHelper<SelectionItem>

    @Mock
    private lateinit var selectionModeProvider: SelectionModeProvider

    @Mock
    private lateinit var searchVM: SelectionSearchViewModel

    @Mock
    private lateinit var liveData: SelectionLiveData<SelectionItem>

    @Test
    fun `When selection mode is ALWAYS_ADD and item isn't appended, then strategy is SELECT`() {
        whenever(selectionModeProvider.selectionMode).thenReturn(SelectionMode.ALWAYS_ADD)
        whenever(searchVM.searchQuery).thenReturn(StringUtils.EMPTY)
        prepareHelper()

        val strategy = helper.produceSelectStrategy(mock(), false)

        assertEquals(SelectionStrategy.SELECT, strategy)
    }

    @Test
    fun `When selection mode is ALWAYS_ADD and item is appended, then strategy is SELECT`() {
        whenever(selectionModeProvider.selectionMode).thenReturn(SelectionMode.ALWAYS_ADD)
        whenever(searchVM.searchQuery).thenReturn(StringUtils.EMPTY)
        prepareHelper()

        val strategy = helper.produceSelectStrategy(mock(), true)

        assertEquals(SelectionStrategy.SELECT, strategy)
    }

    @Test
    fun `When selection mode is ALWAYS_ADD and search isn't empty, then strategy is SELECT_AND_CANCEL_SEARCH`() {
        whenever(selectionModeProvider.selectionMode).thenReturn(SelectionMode.ALWAYS_ADD)
        val query = "123"
        whenever(searchVM.searchQuery).thenReturn(query)
        prepareHelper()

        val strategy = helper.produceSelectStrategy(mock(), false)

        assertEquals(SelectionStrategy.SELECT_AND_CANCEL_SEARCH, strategy)
        assertTrue(query.isNotEmpty())
    }

    @Test
    fun `When selection mode is REPLACE_ALL_IF_FIRST, then selection mode become ALWAYS_ADD`() {
        whenever(selectionModeProvider.selectionMode).thenReturn(SelectionMode.REPLACE_ALL_IF_FIRST)
        whenever(searchVM.searchQuery).thenReturn(StringUtils.EMPTY)
        prepareHelper()

        helper.produceSelectStrategy(mock(), true)

        verify(selectionModeProvider).selectionMode = SelectionMode.ALWAYS_ADD
    }

    @Test
    fun `When selection mode is REPLACE_ALL_IF_FIRST and append, then strategy is SELECT`() {
        whenever(selectionModeProvider.selectionMode).thenReturn(SelectionMode.REPLACE_ALL_IF_FIRST)
        whenever(searchVM.searchQuery).thenReturn(StringUtils.EMPTY)
        prepareHelper()

        val strategy = helper.produceSelectStrategy(mock(), true)

        assertEquals(SelectionStrategy.SELECT, strategy)
    }

    @Test
    fun `When mode is REPLACE_ALL_IF_FIRST, query isn't empty and append, then strategy is SELECT_AND_CANCEL_SEARCH`() {
        whenever(selectionModeProvider.selectionMode).thenReturn(SelectionMode.REPLACE_ALL_IF_FIRST)
        val query = "123"
        whenever(searchVM.searchQuery).thenReturn(query)
        prepareHelper()

        val strategy = helper.produceSelectStrategy(mock(), true)

        assertEquals(SelectionStrategy.SELECT_AND_CANCEL_SEARCH, strategy)
        assertTrue(query.isNotEmpty())
    }

    @Test
    fun `When selection mode is REPLACE_ALL_IF_FIRST and not has selected items, then strategy is COMPLETE`() {
        whenever(selectionModeProvider.selectionMode).thenReturn(SelectionMode.REPLACE_ALL_IF_FIRST)
        val selectedData = SelectedData<SelectionItem>(hasSelectedItems = false)
        whenever(liveData.selectedData).thenReturn(selectedData)
        prepareHelper()

        val strategy = helper.produceSelectStrategy(mock(), false)

        assertEquals(SelectionStrategy.COMPLETE, strategy)
    }

    @Test
    fun `When selection mode is REPLACE_ALL_IF_FIRST and has selected items, then strategy is REPLACE_SELECTED`() {
        whenever(selectionModeProvider.selectionMode).thenReturn(SelectionMode.REPLACE_ALL_IF_FIRST)
        val selectedData = SelectedData<SelectionItem>(hasSelectedItems = true)
        whenever(liveData.selectedData).thenReturn(selectedData)
        whenever(searchVM.searchQuery).thenReturn("")
        prepareHelper()

        val strategy = helper.produceSelectStrategy(mock(), false)

        assertEquals(SelectionStrategy.REPLACE_SELECTED, strategy)
    }

    @Test
    fun `When selection mode is SINGLE and append is true, then strategy is COMPLETE`() {
        whenever(selectionModeProvider.selectionMode).thenReturn(SelectionMode.SINGLE)
        prepareHelper()

        val strategy = helper.produceSelectStrategy(mock(), true)

        assertEquals(SelectionStrategy.COMPLETE, strategy)
    }

    @Test
    fun `When selection mode is SINGLE and append is false, then strategy is COMPLETE`() {
        whenever(selectionModeProvider.selectionMode).thenReturn(SelectionMode.SINGLE)
        prepareHelper()

        val strategy = helper.produceSelectStrategy(mock(), false)

        assertEquals(SelectionStrategy.COMPLETE, strategy)
    }

    @Test
    fun `When selected item is openable folder and append is false, then strategy is OPEN_FOLDER`() {
        prepareHelper()
        val selectionFolder = mock<SelectionFolderItem> {
            on { openable } doReturn true
        }

        val strategy = helper.produceSelectStrategy(selectionFolder, false)

        assertEquals(SelectionStrategy.OPEN_FOLDER, strategy)
    }

    @Test
    fun `When selected item is openable folder and append is true, then strategy is SELECT`() {
        whenever(selectionModeProvider.selectionMode).thenReturn(SelectionMode.ALWAYS_ADD)
        whenever(searchVM.searchQuery).thenReturn(StringUtils.EMPTY)
        prepareHelper()
        val selectionFolder = mock<SelectionFolderItem> {
            on { openable } doReturn true
        }

        val strategy = helper.produceSelectStrategy(selectionFolder, true)

        assertEquals(SelectionStrategy.SELECT, strategy)
    }

    @Test
    fun `When selected item isn't openable folder, then strategy is SELECT`() {
        whenever(selectionModeProvider.selectionMode).thenReturn(SelectionMode.ALWAYS_ADD)
        whenever(searchVM.searchQuery).thenReturn(StringUtils.EMPTY)
        prepareHelper()
        val selectionFolder = mock<SelectionFolderItem> {
            on { openable } doReturn false
        }

        val strategy = helper.produceSelectStrategy(selectionFolder, false)

        assertEquals(SelectionStrategy.SELECT, strategy)
    }

    @Test
    fun `When unselect, then selection mode become ALWAYS_ADD`() {
        prepareHelper()

        helper.onUnselect()

        verify(selectionModeProvider).selectionMode = SelectionMode.ALWAYS_ADD
    }

    private fun prepareHelper() {
        helper = SelectionStrategyHelper(
            selectionModeProvider = selectionModeProvider,
            searchVM = searchVM,
            liveData = liveData,
            rulesHelper = mock { on { isFinalComplete } doReturn true }
        )
    }
}