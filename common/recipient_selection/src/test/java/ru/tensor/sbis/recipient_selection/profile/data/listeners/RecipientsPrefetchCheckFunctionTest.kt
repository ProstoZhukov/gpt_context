package ru.tensor.sbis.recipient_selection.profile.data.listeners

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchMode.RELOAD
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.recipient_selection.profile.ui.RECIPIENT_SELECTION_LIST_SIZE
import kotlin.math.roundToInt

private const val SELECTED_ID = "0"

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class RecipientsPrefetchCheckFunctionTest {

    private val checkFunction = RecipientsPrefetchCheckFunction()

    @Test
    fun `When available count less than minimal selection size, then list should be reloaded`() {
        val availableItems = mockItems(MINIMAL_RECIPIENT_COUNT.roundToInt() - 1)

        assertEquals(RELOAD, checkFunction.needToPrefetch(emptyList(), availableItems))
    }

    @Test
    fun `When available count is equal to minimal selection size, then list should not be reloaded`() {
        val availableItems = mockItems(RECIPIENT_SELECTION_LIST_SIZE)

        assertNull(checkFunction.needToPrefetch(emptyList(), availableItems))
    }

    @Test
    fun `When available count more than minimal selection size, then list should not be reloaded`() {
        val availableItems = mockItems(RECIPIENT_SELECTION_LIST_SIZE + 1)

        assertNull(checkFunction.needToPrefetch(emptyList(), availableItems))
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=6d70f89f-58e4-4945-9a4c-bb06e10b8451
     */
    @Test
    fun `Given required count of available items, when selection count is decreased, then list should be reloaded`() {
        val availableItems = mockItems(RECIPIENT_SELECTION_LIST_SIZE /* достаточно, чтобы не влиять на тест */)

        // начальное состояние, было выбрано 3 элемента
        assertNull(checkFunction.needToPrefetch(mockItems(3, SELECTED_ID), availableItems))
        assertEquals(RELOAD, checkFunction.needToPrefetch(mockItems(2, SELECTED_ID), availableItems))
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=00f65ad9-2d81-428e-8460-0e76064dd5eb
     */
    @Test
    fun `When available count, excluding containing selected, is less than minimal selection size, then list should be reloaded`() {
        val availableItems = mockItems(RECIPIENT_SELECTION_LIST_SIZE)
        val selectedItems = mockItems(RECIPIENT_SELECTION_LIST_SIZE)

        assertEquals(RELOAD, checkFunction.needToPrefetch(selectedItems, availableItems))
    }

    @Test
    fun `Given required count of available items, when selection count is increased, then list should not be reloaded`() {
        val availableItems = mockItems(RECIPIENT_SELECTION_LIST_SIZE)

        assertNull(checkFunction.needToPrefetch(mockItems(1, SELECTED_ID), availableItems))
        assertNull(checkFunction.needToPrefetch(mockItems(2, SELECTED_ID), availableItems))
    }

    private fun mockItems(count: Int, itemId: String = "") = (0 until count).map {
        mock<RecipientSelectorItemModel> {
            on { id } doReturn itemId
        }
    }
}