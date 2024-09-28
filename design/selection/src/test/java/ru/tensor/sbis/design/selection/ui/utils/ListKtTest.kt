package ru.tensor.sbis.design.selection.ui.utils

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ListKtTest {

    @Test
    fun `Item list shouldn't contain selected items`() {
        // подготовим оригинальный список элементов
        val items = (0..101).map { mock<SelectorItemModel>() }
        val selection: List<SelectorItemModel> = mutableSetOf<SelectorItemModel>()
            // выберем случайно 10 элементов
            .apply {
                do {
                    add(items.random())
                } while (size < 10)
            }
            // и назначим выбранным элементам идентификаторы для процедуры исключения
            .apply { forEachIndexed { index, model -> whenever(model.id).thenReturn("ID=$index") } }
            .toList()

        val result = items.minusItems(selection)

        // базовая проверка: все выбранные элементы исключены
        assertTrue("Selection is $selection. Source list is $items", result.intersect(selection).isEmpty())
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=f35b844b-f693-403f-b1ab-2ed7f64648a4
     */
    @Test
    fun `When all items selected, then function should return empty list`() {
        val itemA: SelectorItemModel = mock { on { id } doReturn "Data id A" }
        val itemB: SelectorItemModel = mock { on { id } doReturn "Data id B" }

        val result = listOf(itemA, itemB).minusItems(listOf(itemB, itemA))

        assertTrue(result.isEmpty())
    }
}