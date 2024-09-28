package ru.tensor.sbis.business.common.ui.bind_adapter

import android.content.res.Resources
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.BoolRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.common.testing.doReturn
import ru.tensor.sbis.common.testing.params

@RunWith(JUnitParamsRunner::class)
internal class VisibleDataBindingKtTest {

    private val mockResources = mock<Resources> {
        on { getBoolean(any()) } doReturn true
    }
    private val mockView = mock<View> {
        on { resources } doReturn mockResources
    }
    private val testForciblyGoneRes = 111

    @Test
    @Parameters(method = "generateParams")
    fun `Verify view visibility`(
        isVisible: Boolean?,
        @BoolRes isForciblyGoneRes: Int,
        @AttrRes isForciblyGoneAttr: Int,
        isInvisibleIfNotGone: Boolean,
        expected: Int
    ) {
        mockView.isNotGone(isVisible, isForciblyGoneRes, isForciblyGoneAttr, isInvisibleIfNotGone)

        verify(mockView).visibility = expected
    }

    @Suppress("unused")
    private fun generateParams() = params {
        add(true, ID_NULL, ID_NULL, false, View.VISIBLE)
        add(true, testForciblyGoneRes, ID_NULL, false, View.GONE)
        add(true, ID_NULL, ID_NULL, true, View.INVISIBLE)
        add(false, ID_NULL, ID_NULL, true, View.GONE)
    }
}