package ru.tensor.sbis.design.selection.bl.vm.completion

import org.mockito.kotlin.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.vm.TestData

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionChangeFunctionTest {

    private val dataAId = "Test dataA id"
    private val dataBId = "Test dataB id"
    private val dataCId = "Test dataC id"

    @Mock
    private lateinit var initialDataA: TestData

    @Mock
    private lateinit var initialDataB: TestData

    @Mock
    private lateinit var dataA: TestData

    @Mock
    private lateinit var dataB: TestData

    @Mock
    private lateinit var dataC: TestData

    private lateinit var initialSelection: List<TestData>

    private val function = SelectionChangeFunction()

    @Before
    fun setUp() {
        whenever(initialDataA.id).thenReturn(dataAId)
        whenever(initialDataB.id).thenReturn(dataBId)

        initialSelection = listOf(initialDataA, initialDataB)

        whenever(dataA.id).thenReturn(dataAId)
        whenever(dataB.id).thenReturn(dataBId)
        whenever(dataC.id).thenReturn(dataCId)
    }

    @Test
    fun `When selection size isn't equal to initial data size, then button should be visible`() {
        assertTrue(function.apply(listOf(dataA), initialSelection))
    }

    @Test
    fun `When selection size is equal to initial but content is different, then button should be visible`() {
        assertTrue(function.apply(listOf(dataA, dataC), initialSelection))
    }

    @Test
    fun `When selection is empty but initial selection not, then button should be visible`() {
        assertTrue(function.apply(emptyList(), initialSelection))
    }

    @Test
    fun `When selection has the same elements as initial data, then button should be hidden`() {
        assertFalse(function.apply(listOf(dataA, dataB), initialSelection))
    }

    @Test
    fun `When selection has the same elements as initial data but in different order, then button should be hidden`() {
        assertFalse(function.apply(listOf(dataB, dataA), initialSelection))
    }
}