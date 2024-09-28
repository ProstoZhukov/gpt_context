package ru.tensor.sbis.list.view.utils

import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.list.view.adapter.SbisAdapter

internal class TopLoadMoreProgressHelperTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)
    private val itemProgress = mock<ProgressItem>()
    private val mockAdapter = mock<SbisAdapter>()
    private val helper = TopLoadMoreProgressHelper(itemProgressTop = itemProgress)

    @Test
    fun setSbisList() {
        TopLoadMoreProgressHelper().setAdapter(mockAdapter)
    }

    @Test
    fun `Add progress item`() {
        helper.setAdapter(mockAdapter)
        helper.hasLoadMore(true)
        helper.hasLoadMore(true)

        verify(mockAdapter).addFirst(itemProgress)
    }

    @Test
    fun `Remove progress item`() {
        helper.setAdapter(mockAdapter)
        helper.hasLoadMore(false)

        verify(mockAdapter).removeFirst(itemProgress)
    }
}