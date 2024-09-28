package ru.tensor.sbis.list.view.utils.layout_manager

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import junit.framework.TestCase
import ru.tensor.sbis.list.view.section.DataInfo

class IsFirstGroupInSectionTest : TestCase() {

    private val spanCount = 3
    fun testInvoke() {

        /**
         * Цифра - абсолютный индекс элемента в [DataInfo], схема показывает занимаемые места в Grid с учетом
         * размера(spanSize) элемента.
         * Цифрой '5' отмечен элемент вне секций карточек.
         *
         * |0,0|1|
         * |2|3,3|
         * |4,4,4|
         * <  5  >
         * |6,6|7|
         * |8|9,9|
         */

        val info = mock<DataInfo> {
            on { getIndexOfSection(0) } doReturn 0
            on { getIndexOfSection(1) } doReturn 0
            on { getIndexOfSection(2) } doReturn 0
            on { getIndexOfSection(3) } doReturn 0
            on { getIndexOfSection(4) } doReturn 0
            on { getIndexOfSection(5) } doReturn 1
            on { getIndexOfSection(6) } doReturn 2
            on { getIndexOfSection(7) } doReturn 2
            on { getIndexOfSection(8) } doReturn 2
            on { getIndexOfSection(9) } doReturn 2
        }

        val sizeLookup = mock<ItemSpanSizeLookup> {
            on { getSpanGroupIndex(0, spanCount) } doReturn 0
            on { getSpanGroupIndex(1, spanCount) } doReturn 0
            on { getSpanGroupIndex(2, spanCount) } doReturn 1
            on { getSpanGroupIndex(3, spanCount) } doReturn 1
            on { getSpanGroupIndex(4, spanCount) } doReturn 2
            on { getSpanGroupIndex(5, spanCount) } doReturn 3
            on { getSpanGroupIndex(6, spanCount) } doReturn 4
            on { getSpanGroupIndex(7, spanCount) } doReturn 4
            on { getSpanGroupIndex(8, spanCount) } doReturn 5
            on { getSpanGroupIndex(9, spanCount) } doReturn 5
        }

        val isFirstGroupInSection = IsFirstGroupInSection(info, sizeLookup)

        assertTrue(isFirstGroupInSection(0, spanCount))
        assertTrue(isFirstGroupInSection(1, spanCount))

        assertFalse(isFirstGroupInSection(2, spanCount))
        assertFalse(isFirstGroupInSection(3, spanCount))
        assertFalse(isFirstGroupInSection(4, spanCount))

        assertTrue(isFirstGroupInSection(5, spanCount))
        assertTrue(isFirstGroupInSection(6, spanCount))
        assertTrue(isFirstGroupInSection(7, spanCount))

        assertFalse(isFirstGroupInSection(8, spanCount))
        assertFalse(isFirstGroupInSection(9, spanCount))
    }
}