package ru.tensor.sbis.pin_code

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.pin_code.decl.*
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureImpl

/**
 * Тест для [PinCodeViewModel].
 *
 * @author as.stafeev
 */
@RunWith(AndroidJUnit4::class)
class PinCodeViewModelTest {

    @get:Rule
    val rxRule = TrampolineSchedulerRule()

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val header = "Test header"
    private val headerIcon = SbisMobileIcon.Icon.smi_Email
    private val codeLength = 5
    private val comment = "Test comment"
    private val theme = 0
    private val isMaskedCode = true
    private val isDefaultField = false
    private val closeBtnVisible = false
    private val errorMsg = "Test error"
    private val strResult = "Test result"

    private val mockRepository = mock<PinCodeRepository<String>>()
    private val mockContext = mock<Context>()
    private val spyFeature = spy(PinCodeFeatureImpl<String>(mock()))
    private val mockCloseObserver = mock<Observer<Boolean>>()
    private val mockErrorObserver = mock<Observer<String>>()
    private val mockDigitsObserver = mock<Observer<String>>()
    private val mockConfirmBtnVisibleObserver = mock<Observer<Boolean>>()
    private val mockProgressVisibleObserver = mock<Observer<Boolean>>()
    private val mockConfirmBtnEnabledObserver = mock<Observer<Boolean>>()
    private val mockRetryBtnVisibleObserver = mock<Observer<Boolean>>()
    private val mockTimerVisibleObserver = mock<Observer<Boolean>>()
    private val mockTimeObserver = mock<Observer<Long>>()
    private val mockOnRequestCheckCodeResultObserver = mock<Observer<PinCodeSuccessResult<String>>>()

    private lateinit var viewModel: PinCodeViewModel<String>

    @Test
    fun `Given confirmationType button, when code entered, then confirm button is visible`() {
        createVm(confirmationType = ConfirmationType.BUTTON)

        viewModel.codeInputConfirmAction()

        verify(mockConfirmBtnVisibleObserver).onChanged(true)
    }

    @Test
    fun `Given confirmationType input completion, when code entered, then confirm click action is called`() {
        whenever(mockRepository.getConfirmationFlowAction(any())).thenAnswer { ConfirmationFlowAction.SEND_CODE }
        createVm(confirmationType = ConfirmationType.INPUT_COMPLETION)

        viewModel.codeInputConfirmAction()

        verify(viewModel).confirmClickAction()
    }

    /**
     * Fix https://online.sbis.ru/doc/84cd5737-0e01-4a5e-acf1-116d54db928a
     */
    @Test
    fun `When there are no digits yet, then confirm button is disabled`() {
        createVm()

        assertFalse(viewModel.confirmBtnEnabled.value!!)
    }

    /**
     * Fix https://online.sbis.ru/doc/84cd5737-0e01-4a5e-acf1-116d54db928a
     */
    @Test
    fun `When digits are changed, then confirm button state is changed accordingly`() {
        createVm()
        val confirmBtnEnabledCaptor = argumentCaptor<Boolean>()

        viewModel.digits.value = "1234"
        viewModel.digits.value = ""

        verify(mockConfirmBtnEnabledObserver, times(2)).onChanged(confirmBtnEnabledCaptor.capture())
        assertThat(confirmBtnEnabledCaptor.allValues, equalTo(listOf(true, false)))
    }

    @Test
    fun `When confirm click action is called, then progress and confirm button are changed state`() {
        whenever(mockRepository.getConfirmationFlowAction(any())).thenAnswer { ConfirmationFlowAction.SEND_CODE }
        whenever(mockRepository.onCodeEntered(any())).thenReturn(strResult)
        createVm()
        val progressVisibleCaptor = argumentCaptor<Boolean>()
        val confirmBtnEnabledCaptor = argumentCaptor<Boolean>()

        viewModel.digits.value = "1234"
        viewModel.confirmClickAction()

        verify(mockProgressVisibleObserver, times(2)).onChanged(progressVisibleCaptor.capture())
        verify(mockConfirmBtnEnabledObserver, times(3)).onChanged(confirmBtnEnabledCaptor.capture())
        assertThat(progressVisibleCaptor.allValues, equalTo(listOf(true, false)))
        assertThat(confirmBtnEnabledCaptor.allValues, equalTo(listOf(true, false, true)))
    }

    @Test
    fun `Given onCodeEntered result success, when confirm click action is called, then close and feature result events are called`() {
        whenever(mockRepository.getConfirmationFlowAction(any())).thenAnswer { ConfirmationFlowAction.SEND_CODE }
        whenever(mockRepository.onCodeEntered(any())).thenReturn(strResult)
        createVm()

        viewModel.confirmClickAction()

        verify(mockCloseObserver).onChanged(any())
        verify(mockOnRequestCheckCodeResultObserver).onChanged(argThat { data == strResult })
    }

    @Test
    fun `Given onCodeEntered result throw exception and needCleanCode true, when confirm click action is called, then error event is called and digits is cleaned`() {
        whenever(mockRepository.getConfirmationFlowAction(any())).thenAnswer { ConfirmationFlowAction.SEND_CODE }
        whenever(mockRepository.onCodeEntered(any())).thenAnswer { error(errorMsg) }
        whenever(mockRepository.needCleanCode(any())).thenAnswer { true }
        createVm()

        viewModel.confirmClickAction()

        verify(mockErrorObserver).onChanged(errorMsg)
        verify(mockDigitsObserver).onChanged("")
    }

    @Test
    fun `Given onCodeEntered result throw exception and needCleanCode false, when confirm click action is called, then only error event is called`() {
        whenever(mockRepository.getConfirmationFlowAction(any())).thenAnswer { ConfirmationFlowAction.SEND_CODE }
        whenever(mockRepository.onCodeEntered(any())).thenAnswer { error(errorMsg) }
        whenever(mockRepository.needCleanCode(any())).thenAnswer { false }
        createVm()

        viewModel.confirmClickAction()

        verify(mockErrorObserver).onChanged(errorMsg)
        verifyNoMoreInteractions(mockDigitsObserver)
    }

    @Test
    fun `When retry is clicked, then progress and confirm button are changed state`() {
        createVm()
        val progressVisibleCaptor = argumentCaptor<Boolean>()
        val confirmBtnEnabledCaptor = argumentCaptor<Boolean>()

        viewModel.digits.value = "1234"
        viewModel.retryAction()

        verify(mockProgressVisibleObserver, times(2)).onChanged(progressVisibleCaptor.capture())
        verify(mockConfirmBtnEnabledObserver, times(3)).onChanged(confirmBtnEnabledCaptor.capture())
        assertThat(progressVisibleCaptor.allValues, equalTo(listOf(true, false)))
        assertThat(confirmBtnEnabledCaptor.allValues, equalTo(listOf(true, false, false)))
    }

    @Test
    fun `Given onRetry result throw exception, when retry is clicked, then error event is called`() {
        whenever(mockRepository.onRetry()).thenAnswer { error(errorMsg) }
        createVm()

        viewModel.retryAction()

        verify(mockErrorObserver).onChanged(errorMsg)
    }

    @Test
    fun `Given countDownTime not zero, when retry is clicked, then timer is initialized`() {
        createVm(
            transportType = mock<PinCodeTransportType.CALL> {
                on { countDownTime }.thenAnswer { 2L }
            }
        )
        val retryVisibleCaptor = argumentCaptor<Boolean>()
        val timerVisibleCaptor = argumentCaptor<Boolean>()
        val timesCaptor = argumentCaptor<Long>()

        viewModel.retryAction()

        verify(mockTimeObserver, atLeastOnce()).onChanged(timesCaptor.capture())
        verify(mockRetryBtnVisibleObserver, times(2)).onChanged(retryVisibleCaptor.capture())
        verify(mockTimerVisibleObserver, times(2)).onChanged(timerVisibleCaptor.capture())
        assertThat(retryVisibleCaptor.allValues, equalTo(listOf(false, true)))
        assertThat(timerVisibleCaptor.allValues, equalTo(listOf(true, false)))
        assertThat(timesCaptor.allValues, equalTo(listOf(2L, 1L, 0L)))
    }

    @Test
    fun `Given countDownTime zero, when init view model, then timer is hidden`() {
        createVm(transportType = PinCodeTransportType.NONE)

        assertThat(viewModel.timerVisible.value, equalTo(false))
    }

    private fun createVm(
        confirmationType: ConfirmationType = ConfirmationType.BUTTON,
        transportType: PinCodeTransportType = PinCodeTransportType.NONE
    ) {
        viewModel = spy(
            PinCodeViewModel(
                spyFeature,
                mockRepository,
                mockContext,
                createHandle(confirmationType, transportType)
            )
        ).apply {
            closeEvent.observeForever(mockCloseObserver)
            confirmBtnVisible.observeForever(mockConfirmBtnVisibleObserver)
            progressVisible.observeForever(mockProgressVisibleObserver)
            confirmBtnEnabled.observeForever(mockConfirmBtnEnabledObserver)
            errorEvent.observeForever(mockErrorObserver)
            retryBtnVisible.observeForever(mockRetryBtnVisibleObserver)
            timerVisible.observeForever(mockTimerVisibleObserver)
            time.observeForever(mockTimeObserver)
            digits.observeForever(mockDigitsObserver)
        }

        spyFeature.onRequestCheckCodeResult.observeForever(mockOnRequestCheckCodeResultObserver)

        clearInvocations(
            mockCloseObserver,
            mockConfirmBtnVisibleObserver,
            mockProgressVisibleObserver,
            mockConfirmBtnEnabledObserver,
            mockOnRequestCheckCodeResultObserver,
            mockErrorObserver,
            mockRetryBtnVisibleObserver,
            mockTimerVisibleObserver,
            mockTimeObserver,
            mockDigitsObserver
        )
    }

    private fun createHandle(
        confirmationType: ConfirmationType,
        transportType: PinCodeTransportType
    ) = SavedStateHandle().apply {
        set(ARG_CONFIRMATION_TYPE, confirmationType)
        set(ARG_IS_DEFAULT_FIELD, isDefaultField)
        set(ARG_IS_MASKED_CODE, isMaskedCode)
        set(ARG_HEADER, header)
        set(ARG_HEADER_ICON, headerIcon)
        set(ARG_CODE_LENGTH, codeLength)
        set(ARG_DESCRIPTION, comment)
        set(ARG_THEME, theme)
        set(ARG_TRANSPORT_TYPE, transportType)
        set(ARG_CLOSE_BTN_VISIBLE, closeBtnVisible)
        set(ARG_HAS_PERIOD, false)
    }
}