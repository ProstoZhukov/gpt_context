package ru.tensor.sbis.design.list_header

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.list_header.format.FormattedDateTime

/**
 * @author ra.petrov
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ItemDateViewTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        context.theme.applyStyle(R.style.BaseAppTheme, false)
    }

    @Test
    fun `When call setFormattedDateTime then set text to view`() {
        val dateView = ItemDateView(context)

        dateView.dateViewMode = DateViewMode.DATE_TIME
        dateView.setFormattedDateTime(FormattedDateTime("01.05.2021", "13:54"))
        Assert.assertEquals("01.05.2021 13:54", dateView.text)
    }
}