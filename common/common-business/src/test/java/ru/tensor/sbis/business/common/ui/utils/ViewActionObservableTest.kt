package ru.tensor.sbis.business.common.ui.utils

import android.view.View
import org.mockito.kotlin.mock
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ViewActionObservableTest {

    @Test
    fun `Do clean observable value after first usage`() {
        val observableAction = ViewActionObservable<View>()
        observableAction.set { Unit }

        assertNotNull(observableAction.get())
        observableAction.perform(mock())
        assertNull(observableAction.get())
    }
}