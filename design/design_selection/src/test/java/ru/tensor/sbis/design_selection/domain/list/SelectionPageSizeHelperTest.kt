package ru.tensor.sbis.design_selection.domain.list

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.crud3.defaultPageSize
import ru.tensor.sbis.crud3.defaultViewPostSize

/**
 * Тесты на вспомогательную реализацию [SelectionPageSizeHelper] для определения размера страницы
 * и количества элементов для начала пагинации.
 *
 * @author vv.chekurda
 */
class SelectionPageSizeHelperTest {

    @Test
    fun `When items is unlimited, then pageSize and viewPostSize is default`() {
        val helper = SelectionPageSizeHelper(itemsLimit = null)

        assertEquals(defaultPageSize, helper.pageSize)
        assertEquals(defaultViewPostSize, helper.viewPostSize)
    }

    @Test
    fun `When items limited, then pageSize and viewPostSize is equals bigger size for disable pagination`() {
        val itemsLimit = 25
        val helper = SelectionPageSizeHelper(itemsLimit = itemsLimit)

        assertEquals(itemsLimit, helper.pageSize)
        assertEquals(itemsLimit, helper.viewPostSize)
    }
}