package ru.tensor.sbis.red_button.interactor

import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import io.reactivex.Completable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.red_button.repository.data_source.RedButtonDataSource

/**
 * @author ra.stepanov
 */
@RunWith(MockitoJUnitRunner::class)
class RedButtonReRunAppInteractorTest {

    private lateinit var interactor: RedButtonReRunAppInteractor
    private val preferencesInteractor = mock<RedButtonPreferencesInteractor> {
        on { clearStubPreference() } doReturn Completable.fromAction {}
    }
    private val rxBus = mock<RxBus>()
    private val dataSource = mock<RedButtonDataSource>()

    @Before
    fun setup() {
        interactor = RedButtonReRunAppInteractor(preferencesInteractor, rxBus, dataSource)
        Mockito.clearInvocations(preferencesInteractor, rxBus, dataSource)
    }

    @Test
    fun `When subscribeOnAppReRun called, then preferencesInteractor#clearStubPreference called`() {
        interactor.subscribeOnAppReRun()
        //verify
        verify(preferencesInteractor).clearStubPreference()
    }


    @Test
    fun `When subscribeOnAppReRun called, then dataSource#subscribeOnRefreshApp called`() {
        interactor.subscribeOnAppReRun()
        preferencesInteractor.clearStubPreference().subscribe()
        //verify
        verify(dataSource).subscribeOnRefreshApp(any())
    }
}