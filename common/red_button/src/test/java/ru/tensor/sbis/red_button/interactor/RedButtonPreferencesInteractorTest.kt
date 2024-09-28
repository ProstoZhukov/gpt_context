package ru.tensor.sbis.red_button.interactor

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button.data.RedButtonStubType
import ru.tensor.sbis.red_button.repository.data_source.RedButtonPreferences

/**
 * @author ra.stepanov
 */
@RunWith(MockitoJUnitRunner::class)
class RedButtonPreferencesInteractorTest {

    private lateinit var interactor: RedButtonPreferencesInteractor
    private val preferences = mock<RedButtonPreferences> {
        on { getRedButtonRefreshApp() } doReturn RedButtonStubType.OPEN_STUB.value
        on { getRedButtonState() } doReturn RedButtonState.CLICK.value
    }

    @Before
    fun setup() {
        interactor = RedButtonPreferencesInteractor(preferences)
        Mockito.clearInvocations(preferences)
    }

    @Test
    fun `Given open stub in preference, when getStubPreference called, then only open stub coming`() {
        //act
        val testObserver = interactor.getStubPreference().test()
        //verify
        testObserver.assertValues(RedButtonStubType.OPEN_STUB)
            .assertNever(RedButtonStubType.CLOSE_STUB)
            .assertNever(RedButtonStubType.NO_STUB).dispose()
    }

    @Test
    fun `When putStubPreference called, then preferences#setRedButtonRefreshApp called`() {
        //act
        interactor.putStubPreference(RedButtonStubType.OPEN_STUB).subscribe()
        //verify
        verify(preferences).setRedButtonRefreshApp(RedButtonStubType.OPEN_STUB.value)
    }

    @Test
    fun `When getStatePreference called, then preferences#clearRedButtonRefreshApp called`() {
        //act
        interactor.clearStubPreference().subscribe()
        //verify
        verify(preferences).clearRedButtonRefreshApp()
    }

    @Test
    fun `When getStatePreference called, then preferences#getRedButtonState called`() {
        //act
        interactor.getStatePreference()
        //verify
        verify(preferences).getRedButtonState()
    }

    @Test
    fun `When getStatePreference called, then click state returned`() {
        assertEquals(RedButtonState.CLICK, interactor.getStatePreference())
    }

    @Test
    fun `When putState called, then preferences#setRedButtonState called`() {
        //act
        interactor.putState(RedButtonState.CLICK)
        //verify
        verify(preferences).setRedButtonState(RedButtonState.CLICK.value)
    }
}