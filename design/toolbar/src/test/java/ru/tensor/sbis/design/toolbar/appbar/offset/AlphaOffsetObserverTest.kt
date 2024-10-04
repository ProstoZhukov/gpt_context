package ru.tensor.sbis.design.toolbar.appbar.offset

import android.view.View
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.verify
import org.mockito.quality.Strictness

private const val MAX_POSITION = MAX_ALPHA
private const val MIN_POSITION = MIN_ALPHA
private const val SMALLEST_STEP = 0.01

/**
 * Unit тесты для [AlphaOffsetObserver]
 *
 * @author ma.kolpakov
 * @since 01/10/2020
 */
@RunWith(JUnitParamsRunner::class)
class AlphaOffsetObserverTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private lateinit var alphaOffsetObserver: AlphaOffsetObserver

    @Mock
    private lateinit var view: View

    @Test
    @Parameters(
        "$MIN_POSITION",
        "${MIN_POSITION_LIMIT - SMALLEST_STEP}"
    )
    fun `When view is shown on expanded appbar and position is less than min position limit then view will be hidden`(
        position: Float
    ) {
        alphaOffsetObserver = AlphaOffsetObserver(view to true)

        alphaOffsetObserver.onOffsetChanged(position)

        verify(view).alpha = MIN_ALPHA
    }

    @Test
    @Parameters(
        "$MAX_POSITION",
        "${MAX_POSITION_LIMIT + SMALLEST_STEP}"
    )
    fun `When view is shown on expanded appbar and position is greater than max position limit then view will be shown`(
        position: Float
    ) {
        alphaOffsetObserver = AlphaOffsetObserver(view to true)

        alphaOffsetObserver.onOffsetChanged(position)

        verify(view).alpha = MAX_ALPHA
    }

    @Test
    @Parameters(
        "${MIN_POSITION_LIMIT + SMALLEST_STEP}",
        "${MAX_POSITION_LIMIT - SMALLEST_STEP}"
    )
    fun `When view is shown on expanded appbar and position is between min position limit and max position limit then view's alpha will be equal to position`(
        position: Float
    ) {
        alphaOffsetObserver = AlphaOffsetObserver(view to true)

        alphaOffsetObserver.onOffsetChanged(position)

        verify(view).alpha = position
    }

    @Test
    @Parameters(
        "$MIN_POSITION",
        "${MAX_POSITION - MAX_POSITION_LIMIT - SMALLEST_STEP}"
    )
    fun `When view is hidden on expanded appbar and position is less than (MAX_POSITION - max position limit) then view will be shown`(
        position: Float
    ) {
        alphaOffsetObserver = AlphaOffsetObserver(view to false)

        alphaOffsetObserver.onOffsetChanged(position)

        verify(view).alpha = MAX_ALPHA
    }

    @Test
    @Parameters(
        "$MAX_POSITION",
        "${MAX_POSITION - MIN_POSITION_LIMIT + SMALLEST_STEP}"
    )
    fun `When view is hidden on expanded appbar and position is greater than max position limit then view will be hidden`(
        position: Float
    ) {
        alphaOffsetObserver = AlphaOffsetObserver(view to false)

        alphaOffsetObserver.onOffsetChanged(position)

        verify(view).alpha = MIN_ALPHA
    }

    @Test
    @Parameters(
        "${MIN_POSITION_LIMIT + SMALLEST_STEP}",
        "${MAX_POSITION_LIMIT - SMALLEST_STEP}"
    )
    fun `When view is hidden on expanded appbar and position is between min position limit and max position limit then view's alpha will be equal to (MAX_POSITION - position)`(
        position: Float
    ) {
        alphaOffsetObserver = AlphaOffsetObserver(view to false)

        alphaOffsetObserver.onOffsetChanged(position)

        verify(view).alpha = MAX_ALPHA - position
    }
}