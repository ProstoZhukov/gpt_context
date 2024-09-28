package ru.tensor.sbis.design.navigation

import android.widget.TextView
import androidx.lifecycle.LiveData
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.quality.Strictness
import ru.tensor.sbis.design.navigation.view.model.NavigationItemState
import ru.tensor.sbis.design.navigation.view.model.SelectedByUserState
import ru.tensor.sbis.design.navigation.view.model.SelectedState
import ru.tensor.sbis.design.navigation.view.model.UnselectedState
import ru.tensor.sbis.design.navigation.view.setActivateByState

/**
 * @author ma.kolpakov
 */
@Suppress("JUnitMalformedDeclaration")
@RunWith(JUnitParamsRunner::class)
internal class BindingKtTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var view: TextView

    @Suppress("JUnitMalformedDeclaration")
    @Test
    @Parameters(source = BindingKtTest::class)
    fun `When selection state changed, then view should be in correct state`(
        state: NavigationItemState,
        isActive: Boolean
    ) {
        view.setActivateByState(state)

        verify(view, only()).isSelected = isActive
    }

    /**
     * При биндинге метод [LiveData.getValue] может вызываться даже до установки данных в [LiveData]
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=fb7d3ad4-8d3a-4dca-a3e9-b9d88009881a
     */
    @Test
    fun `When item state undefined, then view should not be changed`() {
        view.setActivateByState(null)

        verifyNoMoreInteractions(view)
    }

    companion object TestParams {

        @Suppress("unused")
        @JvmStatic
        fun provideNavigationItemState() = arrayOf(
            arrayOf(UnselectedState, false),
            arrayOf(SelectedState, true),
            arrayOf(mock<SelectedByUserState>(), true)
        )
    }
}