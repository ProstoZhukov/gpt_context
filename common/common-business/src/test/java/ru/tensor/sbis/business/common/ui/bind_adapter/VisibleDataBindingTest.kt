package ru.tensor.sbis.business.common.ui.bind_adapter

import android.os.Build
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class VisibleDataBindingTest {

    private val view = View(ApplicationProvider.getApplicationContext())

    @Test
    fun `setIsGone true to GONE`() {
        view.setIsGone(true)

        assertEquals(View.GONE, view.visibility)
    }

    @Test
    fun `setIsGone false to VISIBLE`() {
        view.setIsGone(false)

        assertEquals(View.VISIBLE, view.visibility)
    }
}
