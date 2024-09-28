package ru.tensor.sbis.design.selection.ui.utils

import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ListKtGetQueryRangeTest {

    @Mock
    private lateinit var data: SelectorItemModel

    @Before
    fun setUp() {
        whenever(data.title).thenReturn("Test data title")
    }

    @Test
    fun `When title contains search query, then range should be returned`() {
        assertEquals(listOf(5..9), data.getQueryRangeList("data"))
    }

    @Test
    fun `When title does not contain search query, then empty range's list should be returned`() {
        assertEquals(emptyList<IntRange>(), data.getQueryRangeList("Testing"))
    }

    @Test
    fun `When title contains words from search query in opposite order, then ranges list should contain all of them`() {
        assertEquals(listOf(0..4, 5..9, 10..15), data.getQueryRangeList("data title test"))
    }

    @Test
    fun `When title contains only part of words from search query, then ranges list should contain ranges for the parts`() {
        assertEquals(listOf(0..4, 5..9, 11..13), data.getQueryRangeList("It test data"))
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=debd24fb-5b25-4484-96bd-10cea04acdf2
    @Test
    fun `When title contains all of search query words, then query range list should not be empty`() {
        assertEquals(listOf(0..4, 10..15), data.getQueryRangeList("Test title"))
    }

    @Test
    fun `When title contains all of search query words, but in different order, then query range list should not be empty`() {
        assertEquals(listOf(5..9, 10..15), data.getQueryRangeList("title data"))
    }

    @Test
    fun `When title contains some of search query words, then query range list should be empty`() {
        assertEquals(emptyList<IntRange>(), data.getQueryRangeList("Another data title"))
    }

    @Test
    fun `Given search query with one word contains another, when title contains only longest of search query word, then query range list should be empty`() {
        // te только в составе Test, за пределами больше нет в предложении - считается как часть одного слова
        assertEquals(emptyList<IntRange>(), data.getQueryRangeList("Test te"))
    }

    @Test
    fun `Given search query with one word contains another, when title contains the shortest word out of the longest word's range, then query range list should not be empty`() {
        // t есть не только в составе Test, но и в daTa TiTle - считается как часть другого слова
        assertEquals(listOf(0..4, 7..8, 10..11, 12..13), data.getQueryRangeList("Test t"))
    }
    //endregion
}