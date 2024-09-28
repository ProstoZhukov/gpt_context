package ru.tensor.sbis.red_button.interactor

import androidx.activity.ComponentActivity
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.red_button.data.RedButtonStubType
import ru.tensor.sbis.red_button.events.RedButtonNeedRefreshApp

/**
 * @author ra.stepanov
 */
@RunWith(MockitoJUnitRunner::class)
class RedButtonStubInteractorTest {

    private lateinit var interactor: RedButtonStubInteractor

    private val needRefreshAppEvent = spy(PublishSubject.create<RedButtonNeedRefreshApp>())
    private var rxBus = mock<RxBus> {
        on { subscribe(RedButtonNeedRefreshApp::class.java) } doReturn needRefreshAppEvent
    }
    private val preferencesInteractor = mock<RedButtonPreferencesInteractor>()
    private val noNeedStubCallback = mock<NoNeedStub>()
    private val needStubCallback = mock<NeedStub>()
    private val activity = mock<ComponentActivity> {
        on { lifecycle } doReturn mock()
    }

    @Before
    fun setup() {
        interactor = RedButtonStubInteractor(preferencesInteractor, rxBus)
        Mockito.clearInvocations(preferencesInteractor, rxBus)
    }

    @Test
    fun `Given stub type OPEN_STUB, when openStubIfNeedOrRunCode called, then needStubCallback called with OPEN_STUB`() {
        val stubType = RedButtonStubType.OPEN_STUB
        prepareStubPreference(stubType)
        //act
        interactor.openStubIfNeedOrRunCode(activity, needStubCallback, noNeedStubCallback)
        //verify
        verify(needStubCallback).invoke(stubType)
    }

    @Test
    fun `Given stub type NO_STUB, when openStubIfNeedOrRunCode called, then noNeedStubCallback called with NO_STUB`() {
        prepareStubPreference(RedButtonStubType.NO_STUB)
        //act
        interactor.openStubIfNeedOrRunCode(activity, needStubCallback, noNeedStubCallback)
        //verify
        verify(noNeedStubCallback).invoke()
    }

    @Test
    fun `Given stub type NO_STUB, when openStubIfNeedOrRunCode called, then subscribe on rxBus`() {
        prepareStubPreference(RedButtonStubType.NO_STUB)
        //act
        interactor.openStubIfNeedOrRunCode(activity, needStubCallback, noNeedStubCallback)
        //verify
        verify(rxBus).subscribe(RedButtonNeedRefreshApp::class.java)
    }

    @Test
    fun `Given stub type NO_STUB, when openStubIfNeedOrRunCode called, then subscribe on rxBus and call `() {
        prepareStubPreference(RedButtonStubType.NO_STUB)
        val newStubType = RedButtonStubType.OPEN_STUB
        //act
        interactor.openStubIfNeedOrRunCode(activity, needStubCallback, noNeedStubCallback)
        needRefreshAppEvent.onNext(RedButtonNeedRefreshApp(newStubType))
        //verify
        verify(needStubCallback).invoke(newStubType)
    }

    private fun prepareStubPreference(type: RedButtonStubType) {
        doReturn(Single.just(type)).`when`(preferencesInteractor).getStubPreference()
    }
}