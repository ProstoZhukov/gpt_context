package ru.tensor.sbis.list.view.utils

import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * @author ma.kolpakov
 */
class ListDataPlainTest {

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=f95a9a49-dd1e-4efc-8d6b-36b9014cef5a
     */
    @Test
    fun `When plain list created, then it doesn't have top margin of it section`() {
        assertFalse(Plain().getSections().single().hasTopMargin)
    }
}