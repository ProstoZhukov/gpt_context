package ru.tensor.sbis.red_button.ui.host

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.disposables.Disposables
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.SingleLiveEvent
import ru.tensor.sbis.pin_code.decl.*
import ru.tensor.sbis.red_button.R
import ru.tensor.sbis.red_button.data.RedButtonError
import ru.tensor.sbis.red_button.data.RedButtonState
import ru.tensor.sbis.red_button.repository.RedButtonRepository
import ru.tensor.sbis.red_button.ui.host.data.PinConfirmationResult
import ru.tensor.sbis.red_button.ui.host.data.WorkMode
import ru.tensor.sbis.red_button.ui.host.data.WorkStep

/**
 * Вью модель фрагмента [HostFragment], реализует работу с репозиторием и навигацию между фрагментами
 * @property resourceProvider провайдер ресурсов
 * @property repository репозиторий модуля
 * @property networkUtils хелпер для работы с состоянием сети девайса
 * @property workStep шаг работы вью модели
 *
 * @author ra.stepanov
 */
internal class HostViewModel(
    private val resourceProvider: ResourceProvider,
    private val repository: RedButtonRepository,
    private val networkUtils: NetworkUtils
) : ViewModel(), PinCodeRepository<PinConfirmationResult>, LifecycleObserver {


    private var disposable = Disposables.empty()
    private var workMode = WorkMode.MODE_ON
    private var workStep = WorkStep.STEP_PIN
    private val _onConfigurationChanged = SingleLiveEvent<PinCodeConfiguration>()
    private val _onCloseContent = SingleLiveEvent<Unit>()
    private val _onCloseFragment = SingleLiveEvent<Unit>()
    private val _onNeedVerifyPhone = SingleLiveEvent<Unit>()
    private val _onError = SingleLiveEvent<String>()

    internal val isPinCodeCreation: Boolean
        get() {
            return workStep == WorkStep.STEP_PIN && workMode == WorkMode.MODE_ON
        }

    internal val pinCodeCreationConfirmationCase: PinCodeUseCase by lazy {
        PinCodeUseCase.Custom(
            PinCodeConfiguration(
                header = R.string.red_button_title_pin_confirmation,
                description = resourceProvider.getString(R.string.red_button_message_pin_confirmation),
                codeLength = 5,
                transportType = PinCodeTransportType.NONE,
                confirmationType = ConfirmationType.INPUT_COMPLETION
            )
        )
    }

    //region view data
    val onConfigurationChanged: LiveData<PinCodeConfiguration> get() = _onConfigurationChanged
    val onCloseFragment: LiveData<Unit> get() = _onCloseFragment
    val onCloseContent: LiveData<Unit> get() = _onCloseContent
    val onNeedVerifyPhone: LiveData<Unit> get() = _onNeedVerifyPhone
    val onError: LiveData<String> get() = _onError

    // Вызывается после завершения анимации закрытия окна
    val onRequestCheckCodeHandler = Observer<PinCodeSuccessResult<PinConfirmationResult>> {
        if (it.data == PinConfirmationResult.CLOSE) {
            _onCloseFragment.postValue(Unit)
            return@Observer
        }

        if (it.data == PinConfirmationResult.NAVIGATE_NEXT) {
            if (workStep == WorkStep.STEP_PIN && workMode == WorkMode.MODE_ON) workStep = WorkStep.STEP_SMS
            refreshConfiguration()
        }
    }
    //endregion

    //region lifecycle
    init {
        /**
         * Инициализация вью модели.
         * Устанавливает режим работы вью модели: открытие или закрытие данных
         * и вызывает обновление конфигурации окна
         */
        disposable = repository.getState()
            .filter { it == RedButtonState.CLICK || it == RedButtonState.NOT_CLICK }
            .subscribe {
                workMode = if (it != RedButtonState.CLICK) WorkMode.MODE_ON else WorkMode.MODE_OFF
                refreshConfiguration()
            }
    }

    /**
     * Отписка от репозитория и [RxBus]
     */
    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
    //endregion

    //region PinCodeRepository
    /** @SelfDocumented */
    override fun onCodeEntered(digits: String): PinConfirmationResult {
        if (digits.isEmpty()) return PinConfirmationResult.CLOSE
        return when (val result = confirm(digits).blockingGet()) {
            is RedButtonError.General -> {
                _onError.postValue(result.errorMessage)
                PinConfirmationResult.CLOSE
            }
            // Если ошибка определения номера телефона - выводим тост и прячем окно
            is RedButtonError.MobilePhone -> {
                _onNeedVerifyPhone.postValue(Unit)
                PinConfirmationResult.IGNORE
            }
            // Если ошибка введения неверного вводе pin-кода пользователем или вводе неверного кода подтверждения из смс - выбрасывваем исключение
            is RedButtonError.Pin,
            is RedButtonError.ConfirmCode -> {
                throw result
            }
            is Throwable -> {
                _onError.postValue(result.localizedMessage ?: "")
                PinConfirmationResult.CLOSE
            }
            else -> getResultByStepAndMode()
        }
    }

    /**
     * По клику на запрос повторной отправки CMC устанавливаем режим работы,
     * отправляем новый конфиг и скрываем старое окно
     */
    override fun onRetry() {
        workStep = WorkStep.STEP_PIN
        refreshConfiguration()
        _onCloseContent.postValue(Unit)
    }

    /** @SelfDocumented */
    override fun needCleanCode(error: Throwable): Boolean {
        return error is RedButtonError.Pin || error is RedButtonError.ConfirmCode
    }
    //endregion

    /**
     * Отправить новое событие изменения конфигурации окна ввода пин кода (Показать новое)
     */
    private fun refreshConfiguration() {
        _onConfigurationChanged.postValue(getConfiguration(workStep, workMode))
    }

    private fun getResultByStepAndMode() =
        if ((workStep == WorkStep.STEP_SMS && workMode == WorkMode.MODE_ON) || (workStep == WorkStep.STEP_PIN && workMode == WorkMode.MODE_OFF)) {
            PinConfirmationResult.CLOSE
        } else {
            PinConfirmationResult.NAVIGATE_NEXT
        }

    /**
     * Обработчик для клика по кнопке confirm.
     * Проверяет наличие сети, при отсутствии выдаёт ошибку
     * Если режим работы [workStep] == [WorkStep.STEP_PIN] и [workMode] == [WorkMode.MODE_ON], то начинает процедуру закрытия данных
     * Если режим работы [workStep] == [WorkStep.STEP_PIN] и [workMode] == [WorkMode.MODE_OFF], то открывает данные
     * Если режим работы [workStep] == [WorkStep.STEP_SMS], то закрывает данныекода
     * @param codeString код с поля ввода
     */
    private fun confirm(codeString: String): Completable {
        //Проверка подключения к сети, если сети нет ошибка отобразится в тосте
        if (!networkUtils.isConnected) {
            return Completable.error(Throwable(resourceProvider.getString(R.string.red_button_network_error)))
        }
        return if (workStep == WorkStep.STEP_PIN) {
            if (workMode == WorkMode.MODE_ON) {
                repository.turnOn(codeString)
            } else {
                repository.turnOff(codeString)
            }
        } else {
            repository.confirmOn(codeString)
        }
    }

    //region ui refresh

    /**
     * Получить новую конфигурацию для окна ввода пин-кода
     * @param workStep шаг работы
     * @param workMode режим работы
     */
    private fun getConfiguration(workStep: WorkStep, workMode: WorkMode) =
        PinCodeConfiguration(
            header = getTitle(workStep, workMode),
            description = if (workStep == WorkStep.STEP_PIN) getMessagePinString() else getMessageSmsString(),
            codeLength = if (workStep == WorkStep.STEP_PIN) 5 else 4,
            transportType = if (workStep == WorkStep.STEP_PIN) PinCodeTransportType.NONE else PinCodeTransportType.SMS,
            confirmationType = ConfirmationType.INPUT_COMPLETION
        )

    /** @SelfDocumented */
    private fun getTitle(workStep: WorkStep, workMode: WorkMode): Int {
        return if (workStep == WorkStep.STEP_SMS) {
            R.string.red_button_title_sms
        } else {
            if (workMode == WorkMode.MODE_ON) R.string.red_button_title_pin_on else R.string.red_button_title_pin_off
        }
    }

    /** @SelfDocumented */
    private fun getMessageSmsString(): String {
        return resourceProvider.getString(
            R.string.red_button_password_sent_on_phone_template,
            repository.getPhone()
        )
    }

    /** @SelfDocumented */
    private fun getMessagePinString(): String {
        return resourceProvider.getString(
            if (workMode == WorkMode.MODE_ON) {
                R.string.red_button_message_pin_close
            } else {
                R.string.red_button_message_pin_open
            }
        )
    }
    //endregion
}