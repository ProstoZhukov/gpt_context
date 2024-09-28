package ru.tensor.sbis.red_button.ui.settings_item

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.red_button.R
import ru.tensor.sbis.red_button.data.RedButtonActions
import ru.tensor.sbis.red_button.data.RedButtonError
import ru.tensor.sbis.red_button.data.RedButtonOpenAction
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button.events.RedButtonStateRefresh
import ru.tensor.sbis.red_button.interactor.RedButtonStatesInteractor
import ru.tensor.sbis.common.R as RCommon

/**
 * @author ra.stepanov
 */
@RunWith(JUnitParamsRunner::class)
class RedButtonItemViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: RedButtonItemViewModel

    private var rxBus = mock<RxBus> {
        on { subscribe(RedButtonStateRefresh::class.java) } doReturn spy(PublishSubject.create())
    }
    private val statesInteractor = mock<RedButtonStatesInteractor>()
    private val resourceProvider = mock<ResourceProvider> {
        on { getString(R.string.red_button_title_open) } doReturn "red_button_title_open"
        on { getString(R.string.red_button_title_open_progress) } doReturn "red_button_title_open_progress"
        on { getString(R.string.red_button_title_close_progress) } doReturn "red_button_title_close_progress"
        on { getString(R.string.red_button_title_close) } doReturn "red_button_title_close"
        on { getString(RCommon.string.common_no_permission_error) } doReturn "common_no_permission_error"
        on { getString(RCommon.string.common_access_error) } doReturn "common_access_error"
        on { getString(R.string.red_button_network_error) } doReturn "red_button_network_error"
    }

    @Test
    fun `When viewModel initialized, then button is visible`() {
        initViewModelWithState(RedButtonState.CLICK)
        //act
        val testObserver = viewModel.isViewVisible.test()
        //verify
        testObserver.assertValues(true)
            .assertNever(false)
            .dispose()
    }

    @Test
    fun `When viewModel initialized, then button is invisible`() {
        initViewModelWithState(RedButtonState.ACCESS_DENIED)
        //act
        val testObserver = viewModel.isViewVisible.test()
        //verify
        testObserver.assertValues(false)
            .assertNever(true)
            .dispose()
    }

    @Test
    @Parameters(
        "ACCESS_DENIED,common_access_error",
        "ACCESS_LOCK,common_no_permission_error",
        "NOT_CLICK,red_button_title_close",
        "CLOSE_IN_PROGRESS,red_button_title_close_progress",
        "OPEN_IN_PROGRESS,red_button_title_open_progress",
        "CLICK,red_button_title_open"
    )
    fun `When viewModel initialized, then title is equals param`(state: RedButtonState, string: String) {
        initViewModelWithState(state)
        //act
        val testObserver = viewModel.title.test()
        //verify
        testObserver.assertValuesOnly(string).dispose()
    }

    @Test
    fun `Given CLICK state, when onPreferenceClick called, then openAction emits OPEN_FRAGMENT`() {
        initViewModelWithState(RedButtonState.CLICK)
        //act
        val testObserver = viewModel.openAction.test()
        viewModel.onPreferenceClick()
        //verify
        testObserver.assertValues(RedButtonOpenAction.OPEN_FRAGMENT)
            .assertNever(RedButtonOpenAction.OPEN_DIALOG_EMPTY_CABINET)
            .assertNever(RedButtonOpenAction.OPEN_DIALOG_MANAGEMENT)
            .dispose()
    }

    @Test
    fun `Given NOT_CLICK state, when onPreferenceClick called, then openAction emits OPEN_DIALOG_MANAGEMENT`() {
        initViewModelWithState(RedButtonState.NOT_CLICK)
        //act
        doReturn(Single.just(RedButtonActions.HIDE_MANAGEMENT)).`when`(statesInteractor).getAction()
        val testObserver = viewModel.openAction.test()
        viewModel.onPreferenceClick()
        //verify
        testObserver.assertValues(RedButtonOpenAction.OPEN_DIALOG_MANAGEMENT)
            .assertNever(RedButtonOpenAction.OPEN_DIALOG_EMPTY_CABINET)
            .assertNever(RedButtonOpenAction.OPEN_FRAGMENT).dispose()
    }

    @Test
    fun `Given NOT_CLICK state, when onPreferenceClick called, then openAction emits OPEN_DIALOG_EMPTY_CABINET`() {
        initViewModelWithState(RedButtonState.NOT_CLICK)
        //act
        doReturn(Single.just(RedButtonActions.EMPTY_CABINET)).`when`(statesInteractor).getAction()
        val testObserver = viewModel.openAction.test()
        viewModel.onPreferenceClick()
        //verify
        testObserver.assertValues(RedButtonOpenAction.OPEN_DIALOG_EMPTY_CABINET)
            .assertNever(RedButtonOpenAction.OPEN_DIALOG_MANAGEMENT)
            .assertNever(RedButtonOpenAction.OPEN_FRAGMENT).dispose()
    }

    @Test
    @Parameters("ACCESS_DENIED", "ACCESS_LOCK", "CLOSE_IN_PROGRESS", "OPEN_IN_PROGRESS")
    fun `Given red button non-clickable state, when onPreferenceClick called, then openAction not emits anything`(state: RedButtonState) {
        initViewModelWithState(state)
        //act
        val testObserver = viewModel.openAction.test()
        viewModel.onPreferenceClick()
        //verify
        testObserver.assertEmpty().dispose()
    }

    @Test
    fun `Given NOT_CLICK state, when onPreferenceClick called, then openAction not emits anything and emit no network error`() {
        initViewModelWithState(RedButtonState.NOT_CLICK)
        doReturn(Single.error<RedButtonState>(RedButtonError.NoInternet)).`when`(statesInteractor).getStateDirectly()
        //act
        val openActionObserver = viewModel.openAction.test()
        val errorsObserver = viewModel.errors.test()
        viewModel.onPreferenceClick()
        //verify
        openActionObserver.assertEmpty().dispose()
        errorsObserver.assertValues("red_button_network_error").dispose()
    }

    @Test
    fun `Given NOT_CLICK state, when onPreferenceClick called, then openAction not emits anything and errors emit 'error'`() {
        val errorMessage = "error"
        initViewModelWithState(RedButtonState.NOT_CLICK)
        doReturn(Single.error<RedButtonState>(RedButtonError.General(errorMessage))).`when`(statesInteractor)
            .getStateDirectly()
        //act
        val openActionObserver = viewModel.openAction.test()
        val errorsObserver = viewModel.errors.test()
        viewModel.onPreferenceClick()
        //verify
        openActionObserver.assertEmpty().dispose()
        errorsObserver.assertValues(errorMessage).dispose()
    }

    @Test
    fun `Given NOT_CLICK state, when onPreferenceClick called, then openAction not emits anything and errors emit RedButtonError#General`() {
        val errorMessage = "error"
        initViewModelWithState(RedButtonState.NOT_CLICK)
        doReturn(Single.error<RedButtonState>(RedButtonError.General(errorMessage))).`when`(statesInteractor)
            .getStateDirectly()
        //act
        val openActionObserver = viewModel.openAction.test()
        val errorsObserver = viewModel.errors.test()
        viewModel.onPreferenceClick()
        //verify
        openActionObserver.assertEmpty().dispose()
        errorsObserver.assertValues(errorMessage).dispose()
    }

    private fun initViewModelWithState(state: RedButtonState) {
        doReturn(Single.just(state)).`when`(statesInteractor).getStateDirectly()
        doReturn(state).`when`(statesInteractor).getState()
        viewModel = RedButtonItemViewModel(rxBus, statesInteractor, resourceProvider)
        Mockito.clearInvocations(rxBus, statesInteractor, resourceProvider)
    }
}