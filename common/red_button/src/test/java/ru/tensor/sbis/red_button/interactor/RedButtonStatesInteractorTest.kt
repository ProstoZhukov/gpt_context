package ru.tensor.sbis.red_button.interactor

import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.red_button.data.RedButtonActions
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button.repository.data_source.RedButtonDataSource

/**
 * @author ra.stepanov
 */
@RunWith(MockitoJUnitRunner::class)
class RedButtonStatesInteractorTest {

    private lateinit var interactor: RedButtonStatesInteractor
    private val preferencesInteractor = mock<RedButtonPreferencesInteractor>()
    private val dataSource = mock<RedButtonDataSource> {
        on { getState() } doReturn Single.just(RedButtonState.CLICK)
        on { getAction() } doReturn Single.just(RedButtonActions.EMPTY_CABINET)
    }

    @Before
    fun setup() {
        interactor = RedButtonStatesInteractor(preferencesInteractor, dataSource)
        Mockito.clearInvocations(preferencesInteractor, dataSource)
    }

    @Test
    fun `When getState called, then get state from preferences`() {
        //act
        interactor.getState()
        //verify
        verify(preferencesInteractor, atLeastOnce()).getStatePreference()
    }

    @Test
    fun `Given click state in datasource, when getStateDirectly called, then only click state coming`() {
        //act
        val testObserver = interactor.getStateDirectly().test()
        //verify
        testObserver.assertValues(RedButtonState.CLICK)
            .assertNever(RedButtonState.NOT_CLICK)
            .assertNever(RedButtonState.CLOSE_IN_PROGRESS)
            .assertNever(RedButtonState.OPEN_IN_PROGRESS)
            .assertNever(RedButtonState.ACCESS_DENIED)
            .assertNever(RedButtonState.ACCESS_LOCK).dispose()
    }

    @Test
    fun `When getStateDirectly called, then preferencesInteractor#putState called`() {
        //act
        interactor.getStateDirectly().subscribe()
        //verify
        verify(preferencesInteractor).putState(RedButtonState.CLICK)
    }

    @Test
    fun `Given empty cabinet action in datasource, when getAction called, then only empty cabinet action coming`() {
        //act
        val testObserver = interactor.getAction().test()
        //verify
        testObserver.assertValues(RedButtonActions.EMPTY_CABINET)
            .assertNever(RedButtonActions.HIDE_MANAGEMENT)
            .dispose()
    }
}