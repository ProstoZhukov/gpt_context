package ru.tensor.sbis.red_button.ui.host

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.pin_code.decl.ConfirmationType
import ru.tensor.sbis.pin_code.decl.PinCodeConfiguration
import ru.tensor.sbis.pin_code.decl.PinCodeSuccessResult
import ru.tensor.sbis.pin_code.decl.PinCodeTransportType
import ru.tensor.sbis.red_button.R
import ru.tensor.sbis.red_button.data.RedButtonError
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button.repository.RedButtonRepository
import ru.tensor.sbis.red_button.ui.host.data.PinConfirmationResult
import ru.tensor.sbis.red_button.ui.host.data.WorkMode
import ru.tensor.sbis.red_button.ui.host.data.WorkStep

/**
 * @author ra.stepanov
 */
@RunWith(JUnitParamsRunner::class)
internal class HostViewModelTest {

    private lateinit var viewModel: HostViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val defaultPhone = "phone"
    private val networkUtils = mock<NetworkUtils>()
    private val resourceProvider = mock<ResourceProvider> {
        on { getString(R.string.red_button_network_error) } doReturn "red_button_network_error"
        on {
            getString(
                R.string.red_button_password_sent_on_phone_template,
                defaultPhone
            )
        } doReturn "red_button_password_sent_on_phone_template"
        on { getString(R.string.red_button_message_pin_close) } doReturn "red_button_message_pin_close"
        on { getString(R.string.red_button_message_pin_open) } doReturn "red_button_message_pin_open"
    }
    private val repository = mock<RedButtonRepository> {
        on { getPhone() } doReturn defaultPhone
        on { turnOn(any()) } doReturn Completable.fromSingle(Single.just(Unit))
        on { turnOff(any()) } doReturn Completable.fromSingle(Single.just(Unit))
        on { confirmOn(any()) } doReturn Completable.fromSingle(Single.just(Unit))
    }

    //region initialization tests
    @Test
    @Parameters(
        "ACCESS_DENIED",
        "ACCESS_LOCK",
        "CLOSE_IN_PROGRESS",
        "OPEN_IN_PROGRESS",
    )
    fun `Given viewModel, when repository return invalid state, then refreshConfiguration not called`(state: RedButtonState) {
        initVm(state)

        //verify
        assertEquals(null, viewModel.onConfigurationChanged.value)
    }

    @Test
    fun `Given viewModel, when repository return valid state, then refreshConfiguration called with open config`() {
        initVm(RedButtonState.CLICK)

        //verify
        checkConfigIsOpenWithStepPin(viewModel.onConfigurationChanged.value)
        assertEquals(WorkMode.MODE_OFF, getWorkMode())
    }

    @Test
    fun `Given viewModel, when repository return valid state, then refreshConfiguration called with close config`() {
        initVm(RedButtonState.NOT_CLICK)

        //verify
        checkConfigIsCloseWithStepPin(viewModel.onConfigurationChanged.value)
        assertEquals(WorkMode.MODE_ON, getWorkMode())
    }
    //endregion

    //region onCodeEntered tests
    @Test
    fun `Given viewModel with RedButtonState#CLICK state, when onCodeEntered called with empty string, then close panel`() {
        initVm(RedButtonState.CLICK)

        //verify
        assertEquals(PinConfirmationResult.CLOSE, viewModel.onCodeEntered(""))
    }

    @Test
    fun `Given viewModel with RedButtonState#NOT_CLICK state, when onCodeEntered called with not empty value, then return PinConfirmationResult#NAVIGATE_NEXT`() {
        initVm(RedButtonState.NOT_CLICK)

        //act
        doReturn(true).`when`(networkUtils).isConnected
        val code = "1111"
        val result = viewModel.onCodeEntered(code)

        //verify
        verify(repository).turnOn(code)
        assertEquals(WorkMode.MODE_ON, getWorkMode())
        assertEquals(PinConfirmationResult.NAVIGATE_NEXT, result)
    }

    @Test
    fun `Given viewModel with RedButtonState#NOT_CLICK state, when onCodeEntered called with not empty value and internet, then return PinConfirmationResult#CLOSE`() {
        initVm(RedButtonState.NOT_CLICK)

        //act
        doReturn(true).`when`(networkUtils).isConnected
        setWorkStep(WorkStep.STEP_SMS)
        val code = "1111"
        val result = viewModel.onCodeEntered(code)

        //verify
        verify(repository).confirmOn(code)
        assertEquals(WorkMode.MODE_ON, getWorkMode())
        assertEquals(PinConfirmationResult.CLOSE, result)
    }

    @Test
    fun `Given viewModel with RedButtonState#CLICK state, when onCodeEntered called with not empty value, then return PinConfirmationResult#CLOSE`() {
        initVm(RedButtonState.CLICK)

        //act
        doReturn(true).`when`(networkUtils).isConnected
        val code = "1111"
        val result = viewModel.onCodeEntered(code)

        //verify
        verify(repository).turnOff(code)
        assertEquals(WorkMode.MODE_OFF, getWorkMode())
        assertEquals(PinConfirmationResult.CLOSE, result)
    }

    @Test
    fun `Given viewModel with RedButtonState#CLICK state, when onCodeEntered called with not empty value and disabled internet, then close panel and show error`() {
        initVm(RedButtonState.CLICK)

        //act
        doReturn(false).`when`(networkUtils).isConnected
        val result = viewModel.onCodeEntered("1111")

        //verify
        assertEquals(resourceProvider.getString(R.string.red_button_network_error), viewModel.onError.value)
        assertEquals(PinConfirmationResult.CLOSE, result)
    }

    @Test
    fun `Given viewModel with RedButtonState#NOT_CLICK state, when onCodeEntered called with not empty value, then return RedButtonEx`() {
        initVm(RedButtonState.NOT_CLICK)

        //act
        val errorMessage = "error"
        val code = "1111"
        doReturn(Completable.create { throw RedButtonError.General(errorMessage) }).`when`(repository).turnOn(code)
        doReturn(true).`when`(networkUtils).isConnected
        val result = viewModel.onCodeEntered(code)

        //verify
        assertEquals(errorMessage, viewModel.onError.value)
        assertEquals(PinConfirmationResult.CLOSE, result)
    }

    @Test
    fun `Given viewModel with RedButtonState#NOT_CLICK state, when onCodeEntered called with not empty value, then return RedButtonMobilePhoneEx`() {
        initVm(RedButtonState.NOT_CLICK)

        //act
        val errorMessage = "error"
        val code = "1111"
        doReturn(Completable.create { throw RedButtonError.MobilePhone(errorMessage) }).`when`(repository).turnOn(code)
        doReturn(true).`when`(networkUtils).isConnected
        val result = viewModel.onCodeEntered(code)

        //verify
        assertEquals(null, viewModel.onError.value)
        assertEquals(Unit, viewModel.onNeedVerifyPhone.value)
        assertEquals(PinConfirmationResult.IGNORE, result)
    }

    @Test
    fun `Given viewModel with RedButtonState#NOT_CLICK state, when onCodeEntered called with not empty value, then return SbisException`() {
        initVm(RedButtonState.NOT_CLICK)

        //act
        val errorMessage = "error"
        val code = "1111"
        doReturn(Completable.create { throw RedButtonError.General(errorMessage) }).`when`(repository).turnOn(code)
        doReturn(true).`when`(networkUtils).isConnected
        val result = viewModel.onCodeEntered(code)

        //verify
        assertEquals(errorMessage, viewModel.onError.value)
        assertEquals(PinConfirmationResult.CLOSE, result)
    }

    @Test(expected = RedButtonError.Pin::class)
    fun `Given viewModel with RedButtonState#NOT_CLICK state, when onCodeEntered called with not empty value, then return RedButtonPinEx`() {
        initVm(RedButtonState.NOT_CLICK)

        //act
        val errorMessage = "error"
        val code = "1111"
        doReturn(Completable.create { throw RedButtonError.Pin(errorMessage) }).`when`(repository).turnOn(code)
        doReturn(true).`when`(networkUtils).isConnected
        viewModel.onCodeEntered(code)

        //verify
        assertEquals(null, viewModel.onError.value)
        assertEquals(null, viewModel.onNeedVerifyPhone.value)
    }

    @Test(expected = RedButtonError.ConfirmCode::class)
    fun `Given viewModel with RedButtonState#NOT_CLICK state, when onCodeEntered called with not empty value, then return RedButtonConfirmCodeEx`() {
        initVm(RedButtonState.NOT_CLICK)

        //act
        val errorMessage = "error"
        val code = "1111"
        doReturn(Completable.create { throw RedButtonError.ConfirmCode(errorMessage) }).`when`(repository).turnOn(code)
        doReturn(true).`when`(networkUtils).isConnected
        viewModel.onCodeEntered(code)

        //verify
        assertEquals(null, viewModel.onError.value)
        assertEquals(null, viewModel.onNeedVerifyPhone.value)
    }
    //endregion

    //region onRequestCheckCodeHandler
    @Test
    fun `Given viewModel with RedButtonState#NOT_CLICK state, when onRequestCheckCodeHandler called with PinConfirmationResult#IGNORE, then do nothing`() {
        initVm(RedButtonState.NOT_CLICK)

        //act
        setWorkStep(WorkStep.STEP_PIN)
        viewModel.onRequestCheckCodeHandler.onChanged(PinCodeSuccessResult(PinConfirmationResult.IGNORE))

        //verify
        checkConfigIsCloseWithStepPin(viewModel.onConfigurationChanged.value)
        assertEquals(WorkMode.MODE_ON, getWorkMode())
        assertEquals(WorkStep.STEP_PIN, getWorkStep())
    }

    @Test
    fun `Given viewModel with RedButtonState#CLICK state, when onRequestCheckCodeHandler called with PinConfirmationResult#IGNORE, then do nothing`() {
        initVm(RedButtonState.CLICK)

        //act
        setWorkStep(WorkStep.STEP_PIN)
        viewModel.onRequestCheckCodeHandler.onChanged(PinCodeSuccessResult(PinConfirmationResult.IGNORE))

        //verify
        checkConfigIsOpenWithStepPin(viewModel.onConfigurationChanged.value)
        assertEquals(WorkMode.MODE_OFF, getWorkMode())
        assertEquals(WorkStep.STEP_PIN, getWorkStep())
    }

    @Test
    fun `Given viewModel with RedButtonState#NOT_CLICK state, when onRequestCheckCodeHandler called with PinConfirmationResult#CLOSE, then close panel`() {
        initVm(RedButtonState.NOT_CLICK)

        //act
        setWorkStep(WorkStep.STEP_PIN)
        viewModel.onRequestCheckCodeHandler.onChanged(PinCodeSuccessResult(PinConfirmationResult.CLOSE))

        //verify
        checkConfigIsCloseWithStepPin(viewModel.onConfigurationChanged.value)
        assertEquals(WorkMode.MODE_ON, getWorkMode())
        assertEquals(WorkStep.STEP_PIN, getWorkStep())
        assertEquals(Unit, viewModel.onCloseFragment.value)
    }

    @Test
    fun `Given viewModel with RedButtonState#NOT_CLICK state, when onRequestCheckCodeHandler called with PinConfirmationResult#NAVIGATE_NEXT, then refresh work mode and config`() {
        initVm(RedButtonState.NOT_CLICK)

        //act
        setWorkStep(WorkStep.STEP_PIN)
        viewModel.onRequestCheckCodeHandler.onChanged(PinCodeSuccessResult(PinConfirmationResult.NAVIGATE_NEXT))

        //verify
        checkConfigIsCloseWithStepSms(viewModel.onConfigurationChanged.value)
        assertEquals(WorkMode.MODE_ON, getWorkMode())
        assertEquals(WorkStep.STEP_SMS, getWorkStep())
        assertEquals(null, viewModel.onCloseFragment.value)
    }

    @Test
    fun `Given viewModel with RedButtonState#CLICK state, when onRequestCheckCodeHandler called with PinConfirmationResult#NAVIGATE_NEXT, then refresh config`() {
        initVm(RedButtonState.CLICK)

        //act
        setWorkStep(WorkStep.STEP_PIN)
        viewModel.onRequestCheckCodeHandler.onChanged(PinCodeSuccessResult(PinConfirmationResult.NAVIGATE_NEXT))

        //verify
        assertEquals(WorkMode.MODE_OFF, getWorkMode())
        assertEquals(WorkStep.STEP_PIN, getWorkStep())
        assertEquals(null, viewModel.onCloseFragment.value)
    }
    //endregion

    //region onRetry tests
    @Test
    fun `Given viewModel with RedButtonState#CLICK state, when onRetry called, then close old panel and open new`() {
        initVm(RedButtonState.CLICK)
        setWorkStep(WorkStep.STEP_SMS)

        //act
        viewModel.onRetry()

        //verify
        checkConfigIsOpenWithStepPin(viewModel.onConfigurationChanged.value)
        assertEquals(WorkMode.MODE_OFF, getWorkMode())
        assertEquals(WorkStep.STEP_PIN, getWorkStep())
        assertEquals(Unit, viewModel.onCloseContent.value)
    }

    @Test
    fun `Given viewModel with RedButtonState#NOT_CLICK state, when onRetry called, then close old panel and open new`() {
        initVm(RedButtonState.NOT_CLICK)
        setWorkStep(WorkStep.STEP_SMS)

        //act
        viewModel.onRetry()

        //verify
        checkConfigIsCloseWithStepPin(viewModel.onConfigurationChanged.value)
        assertEquals(WorkStep.STEP_PIN, getWorkStep())
        assertEquals(Unit, viewModel.onCloseContent.value)
    }
    //endregion

    //region needCleanCode tests
    @Test
    fun `Given viewModel, when needCleanCode called with RedButtonPinEx, then return true`() {
        //act
        initVm(RedButtonState.NOT_CLICK)
        //verify
        assertEquals(true, viewModel.needCleanCode(mock<RedButtonError.Pin>()))
    }

    @Test
    fun `Given viewModel, when needCleanCode called with RedButtonConfirmCodeEx, then return true`() {
        //act
        initVm(RedButtonState.NOT_CLICK)
        //verify
        assertEquals(true, viewModel.needCleanCode(mock<RedButtonError.ConfirmCode>()))
    }

    /**
     * Проверяет, что конфигурация соответствует закрытию данных и шагу ввода пин-кода
     */
    private fun checkConfigIsCloseWithStepPin(config: PinCodeConfiguration?) {
        assertEquals(R.string.red_button_title_pin_on, config?.header)
        assertEquals(resourceProvider.getString(R.string.red_button_message_pin_close), config?.description)
        assertEquals(5, config?.codeLength)
        assertEquals(PinCodeTransportType.NONE, config?.transportType)
        assertEquals(ConfirmationType.INPUT_COMPLETION, config?.confirmationType)
    }

    /**
     * Проверяет, что конфигурация соответствует открытию данных и шагу ввода пин-кода
     */
    private fun checkConfigIsOpenWithStepPin(config: PinCodeConfiguration?) {
        assertEquals(R.string.red_button_title_pin_off, config?.header)
        assertEquals(resourceProvider.getString(R.string.red_button_message_pin_open), config?.description)
        assertEquals(5, config?.codeLength)
        assertEquals(PinCodeTransportType.NONE, config?.transportType)
        assertEquals(ConfirmationType.INPUT_COMPLETION, config?.confirmationType)
    }

    /**
     * Проверяет, что конфигурация соответствует закрытию данных и шагу ввода смс-кода
     */
    private fun checkConfigIsCloseWithStepSms(config: PinCodeConfiguration?) {
        assertEquals(R.string.red_button_title_sms, config?.header)
        assertEquals(
            resourceProvider.getString(R.string.red_button_password_sent_on_phone_template, defaultPhone),
            config?.description
        )
        assertEquals(4, config?.codeLength)
        assertEquals(PinCodeTransportType.SMS, config?.transportType)
        assertEquals(ConfirmationType.INPUT_COMPLETION, config?.confirmationType)
    }

    /**
     * Получение данных режима работы через Kotlin Reflection API
     */
    private fun getWorkMode(): WorkMode {
        val field = viewModel::class.java.getDeclaredField("workMode")
        field.isAccessible = true
        return field.get(viewModel) as WorkMode
    }

    /**
     * Установка нового шага работы во вью модель
     */
    private fun setWorkStep(workStep: WorkStep) {
        val field = viewModel::class.java.getDeclaredField("workStep")
        field.isAccessible = true
        field.set(viewModel, workStep)
    }

    /**
     * Получение данных шага работы через Kotlin Reflection API
     */
    private fun getWorkStep(): WorkStep {
        val field = viewModel::class.java.getDeclaredField("workStep")
        field.isAccessible = true
        return field.get(viewModel) as WorkStep
    }

    /**
     * Инициализация вью модели с переданным состоянием Красной Кнопки
     */
    private fun initVm(state: RedButtonState) {
        doReturn(Observable.just(state)).`when`(repository).getState()
        viewModel = HostViewModel(resourceProvider, repository, networkUtils)
        Mockito.clearInvocations(resourceProvider, repository, networkUtils)
    }
}