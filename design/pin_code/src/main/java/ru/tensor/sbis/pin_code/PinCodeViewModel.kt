package ru.tensor.sbis.pin_code

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.util.SingleLiveEvent
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.mvvm.StatefulViewModel
import ru.tensor.sbis.pin_code.decl.ConfirmationFlowAction.CONFIRM_CODE
import ru.tensor.sbis.pin_code.decl.ConfirmationFlowAction.SEND_CODE
import ru.tensor.sbis.pin_code.decl.ConfirmationFlowAction.TRY_CONFIRM_AGAIN
import ru.tensor.sbis.pin_code.decl.ConfirmationType
import ru.tensor.sbis.pin_code.decl.PinCodePeriod
import ru.tensor.sbis.pin_code.decl.PinCodeRepository
import ru.tensor.sbis.pin_code.decl.PinCodeSuccessResult
import ru.tensor.sbis.pin_code.decl.PinCodeTransportType
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureImpl
import ru.tensor.sbis.pin_code.util.SmsRetrieverLiveData
import ru.tensor.sbis.pin_code.util.createCountDownTimer
import java.util.concurrent.TimeUnit

/**
 * Вью-модель фрагмента ввода пин-кода.
 *
 * @author mb.kruglova
 */
internal class PinCodeViewModel<RESULT>(
    private val feature: PinCodeFeatureImpl<RESULT>?,
    private val repository: PinCodeRepository<RESULT>? = feature?.provideRepository(),
    appContext: Context,
    state: SavedStateHandle
) : StatefulViewModel(state) {

    private val requestDisposable = CompositeDisposable()
    private val timerDisposable = SerialDisposable()
    private val progressSubject = PublishSubject.create<Boolean>()

    /**
     * Меняем состояние видимости прогресса с эмперической задержкой.
     * Необходим, чтобы избежать мерцаний в случае быстрого получения ответа от контроллера.
     */
    private val progressDisposable = progressSubject
        .debounce(PROGRESS_DELAY_MS, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
        .subscribe { progressVisible.value = it }

    private val confirmationType: ConfirmationType by arg(ARG_CONFIRMATION_TYPE)
    private val transportType: PinCodeTransportType by arg(ARG_TRANSPORT_TYPE)
    private val hasPeriod: Boolean by arg(ARG_HAS_PERIOD)
    val initialScreenOrientation: Int by arg(ARG_INITIAL_SCREEN_ORIENTATION)
    val isDefaultField: Boolean by arg(ARG_IS_DEFAULT_FIELD)
    val isMaskedCode: Boolean by arg(ARG_IS_MASKED_CODE)
    val isPhoneCode: Boolean by arg(ARG_IS_PHONE_CODE)
    val headerTitle: String by arg(ARG_HEADER)
    val headerIcon: String by arg(ARG_HEADER_ICON)
    val codeLength: Int by arg(ARG_CODE_LENGTH)
    val isNumericKeyboard: Boolean by arg(ARG_IS_NUMERIC_KEYBOARD)
    val description: String by arg(ARG_DESCRIPTION)
    val theme: Int by arg(ARG_THEME)
    val inputHint: String by arg(ARG_INPUT_HINT)
    val closeAreaVisible: Boolean by arg(ARG_CLOSE_BTN_VISIBLE)
    val onCancel: (() -> Unit)? by arg(ARG_ON_CANCEL)
    val customLinkButtonTitle: String by arg(ARG_CUSTOM_LINK_BUTTON_TITLE)
    private val onResult: ((PinCodeSuccessResult<RESULT>) -> Unit)? by arg(ARG_ON_RESULT)

    private val smsRetrieverLiveData = SmsRetrieverLiveData(appContext, codeLength)
    private val smsRetrieverObserver = Observer<String> { digits.value = it }

    val confirmBtnVisible: MutableLiveData<Boolean> by statefulLiveData(false)
    val digits: MutableLiveData<String> by statefulLiveData("")
    val period: MutableLiveData<PinCodePeriod?> by statefulLiveData(
        if (hasPeriod) PinCodePeriod.Hour() else null
    )
    val time: MutableLiveData<Long> by statefulLiveData(transportType.countDownTime)
    val timerVisible: MutableLiveData<Boolean> by statefulLiveData(false)
    val retryBtnVisible: MutableLiveData<Boolean> by statefulLiveData(false)
    val progressVisible = MutableLiveData(false)
    val confirmBtnEnabled = MutableLiveData(false)

    /** Событие закрытия диалога. true если закрытие происходит после успешной проверки введенного кода */
    val closeEvent = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<String>()
    val longErrorEvent = SingleLiveEvent<String>()
    val confirmationErrorEvent = SingleLiveEvent<Boolean>()
    val retrySuccessEvent = SingleLiveEvent<Unit>()
    val showPeriodPickerEvent = SingleLiveEvent<PinCodePeriod>()

    /** Свойство, определяющее был ли завершен ввод кода. */
    var isCodeInputComplete = false

    /** Время последнего нажатия на кнопку получения кода повторно. */
    private var lastRetryClickTime: Long = 0

    /**
     * Первичное время запуска таймера обратного отсчета.
     * Необходимо для правильного перерасчета времени начала таймера при пересоздании активности после вытеснения.
     */
    private var savedStartTimerTime: Long by stateful(0)

    /**
     * Действие вызываемое при нажатии на кнопку "Получить код повторно"
     */
    fun retryAction() {
        if (System.currentTimeMillis() - lastRetryClickTime < MULTIPLE_CLICKS_DELAY_MS) return

        lastRetryClickTime = System.currentTimeMillis()
        repository?.let {
            Single.fromCallable { repository.onRetry() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    progressSubject.onNext(true)
                    confirmBtnEnabled.value = false
                }
                .doFinally {
                    progressSubject.onNext(false)
                }
                .subscribe(
                    {
                        digits.value = ""
                        initTimer()
                        retrySuccessEvent.call()
                    },
                    { ex ->
                        if (repository.needCloseAndDisplayOnDialogOnError(ex)) {
                            longErrorEvent.value = ex.message
                            closeOnError()
                        } else {
                            errorEvent.value = ex.message
                            if (repository.needCloseOnError(ex)) closeOnError()
                        }
                    }
                ).storeIn(requestDisposable)
        }
    }

    /**
     * Действие, вызываемое при нажатии на поле выбора периода действия кода.
     */
    fun periodFieldClickAction() {
        period.value?.let { showPeriodPickerEvent.value = it }
    }

    /**
     * Действие, вызываемое при выборе периода действия кода.
     */
    fun periodPickerAction(selectedPeriod: PinCodePeriod?) {
        period.value = selectedPeriod
    }

    /**
     * Действие вызываемое при заполнении всех цифр в поле ввода
     */
    fun codeInputConfirmAction() {
        when (confirmationType) {
            ConfirmationType.BUTTON -> confirmBtnVisible.value = true
            ConfirmationType.INPUT_COMPLETION -> confirmClickAction()
        }
    }

    /**
     * Действие вызываемое при нажатии на кнопку подтверждения
     */
    fun confirmClickAction() {
        isCodeInputComplete = true
        digits.value?.let {
            when (repository?.getConfirmationFlowAction(it)) {
                CONFIRM_CODE -> feature?.isConfirmationFlow = true
                TRY_CONFIRM_AGAIN -> confirmationErrorEvent.value = true
                SEND_CODE -> sendCode(it)
                else -> Unit
            }
        }
    }

    /** Закрытие пин-кода по ошибке. */
    fun closeOnError() {
        closeEvent.value = false
    }

    init {
        if (retryBtnVisible.value!!.not()) initTimer()
        confirmBtnVisible.value = isDefaultField
        digits.observeForever {
            confirmBtnEnabled.value = shouldEnableConfirmButton()
        }
    }

    override fun onCleared() {
        super.onCleared()
        requestDisposable.dispose()
        timerDisposable.dispose()
        progressDisposable.dispose()
        smsRetrieverLiveData.removeObserver(smsRetrieverObserver)
    }

    private fun initTimer() {
        if (transportType !is PinCodeTransportType.NONE) {
            retryBtnVisible.value = false
            timerVisible.value = true
            if (transportType is PinCodeTransportType.SMS) smsRetrieverLiveData.observeForever(smsRetrieverObserver)
            createCountDownTimer(savedStartTimerTime, transportType.countDownTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    if (savedStartTimerTime == 0L) savedStartTimerTime = System.currentTimeMillis()
                }
                .subscribe(
                    { seconds ->
                        time.value = seconds
                    },
                    { },
                    {
                        savedStartTimerTime = 0L
                        retryBtnVisible.value = true
                        timerVisible.value = false
                        smsRetrieverLiveData.removeObserver(smsRetrieverObserver)
                    }
                ).storeIn(timerDisposable)
        } else {
            retryBtnVisible.value = false
            timerVisible.value = false
        }
    }

    private fun sendCode(digits: String) {
        repository?.let {
            Single.fromCallable { repository.onCodeEntered(digits) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    progressSubject.onNext(true)
                    confirmBtnEnabled.value = false
                }
                .doFinally {
                    progressSubject.onNext(false)
                    confirmBtnEnabled.value = shouldEnableConfirmButton()
                }
                .subscribe(
                    {
                        closeEvent.value = true
                        feature?.completePinCodeEntering?.value = Unit
                        feature?.setResultWithDelay(PinCodeSuccessResult(it), onResult)
                    },
                    { ex ->
                        if (repository.needCleanCode(ex)) {
                            isCodeInputComplete = false
                            this.digits.value = ""
                        }
                        if (repository.needCloseAndDisplayOnDialogOnError(ex)) {
                            longErrorEvent.value = ex.cause?.message ?: ex.message
                        } else {
                            errorEvent.value = ex.cause?.message ?: ex.message
                            if (repository.needCloseOnError(ex)) closeOnError()
                        }
                    }
                ).storeIn(requestDisposable)
        }
    }

    private fun shouldEnableConfirmButton() = !digits.value.isNullOrEmpty()
}

private const val PROGRESS_DELAY_MS = 200L
private const val MULTIPLE_CLICKS_DELAY_MS = 1000L