package ru.tensor.sbis.design.toolbar.appbar.color

import android.view.View
import androidx.appcompat.widget.Toolbar
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.CollapsingToolbarLayout

/**
 * @author ma.kolpakov
 * Создан 9/29/2019
 */
@RunWith(JUnitParamsRunner::class)
internal class ColorFunctionUtilTest {

    @Test(expected = IllegalArgumentException::class)
    fun `Exception if ColorUpdateFunction not found for view type`() {
        val unexpectedView = object : View(mock()) {}

        getColorUpdateFunction(unexpectedView)
    }

    @Test
    @Parameters(method = "parametersForValidationTest")
    fun `Verify ColorUpdateFunction resolving`(view: View, expected: ColorUpdateFunction<*>) {
        val actual = getColorUpdateFunction(view)

        assertEquals(expected, actual)
    }

    @Suppress("unused") // используется в тестовых методах выше.
    private fun parametersForValidationTest() = listOf(
        listOf(mock<CollapsingToolbarLayout>(), CollapsingLayoutColorUpdateFunction),
        listOf(mock<SbisTextView>(), TextViewColorUpdateFunction),
        listOf(mock<Toolbar>(), TOOLBAR_COLOR_UPDATE_FUNCTION)
    )
}