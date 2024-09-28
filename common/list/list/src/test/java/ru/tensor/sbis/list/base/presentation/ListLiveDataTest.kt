package ru.tensor.sbis.list.base.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.list.view.utils.Plain

class ListLiveDataTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `Is empty initially`() {
        assertTrue((ListLiveData().value as Plain).data.isEmpty())
    }
}