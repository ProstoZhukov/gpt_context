package ru.tensor.sbis.pin_code.feature

import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.common.util.SingleLiveEvent
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.pin_code.ConfirmationFlowRepository
import ru.tensor.sbis.pin_code.PinCodeFragment
import ru.tensor.sbis.pin_code.PinCodeHostFragment
import ru.tensor.sbis.pin_code.decl.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Реализация фичи компонента ввода пин-кода.
 * Тут важно понимать, что скоуп у фичи отличается от скоупа экрана ввода пин-кода.
 * Фича живет в родительском хосте и не уничтожается при закрытии экрана ввода пин-кода.
 *
 * @param repositoryProducer поставщик внешнего репозитория. Инициализация репозитория произойдет только после открытия фрагмента с вводом пин-кода.
 *
 * @author mb.kruglova
 */
internal class PinCodeFeatureImpl<RESULT>(internal var repositoryProducer: () -> PinCodeRepository<RESULT>?) :
    ViewModel(),
    PinCodeFeature<RESULT> {

    override val onRequestCheckCodeResult = SingleLiveEvent<PinCodeSuccessResult<RESULT>>()
    override val onCanceled = SingleLiveEvent<Unit>()
    override val onCustomLinkButtonAction = SingleLiveEvent<Unit>()
    override val onPeriodChanged = SingleLiveEvent<PinCodePeriod?>()

    private val disposer = CompositeDisposable()
    private var confirmationFlowRepository: PinCodeRepository<RESULT>? = null
    private var resultDelay: Long = RESULT_DELAY

    /** Является ли текущий сценарий подтверждающим. */
    internal var isConfirmationFlow: Boolean = false

    /** Событие завершения ввода пинкода. */
    internal val completePinCodeEntering = SingleLiveEvent<Unit>()

    /** Событие отмены ввода пинкода. */
    internal val cancelPinCodeEntering = SingleLiveEvent<Unit>()

    /** Событие скрытия окна ввода пинкода. */
    internal val hidePinCodeFragment = SingleLiveEvent<Boolean>()

    companion object {
        internal const val TAG_PIN_CODE = "PIN_CODE"
        internal const val ARG_USE_CASE = "USE_CASE"
        internal const val ARG_POPOVER_ANCHOR = "POPOVER_ANCHOR"
        internal const val ARG_CONFIRMATION_USE_CASE = "CONFIRMATION_USE_CASE"
        internal const val ARG_ON_CANCEL = "ON_CANCEL"
        internal const val ARG_ON_RESULT = "ON_RESULT"
    }

    override fun show(
        owner: AppCompatActivity,
        useCase: PinCodeUseCase,
        popoverAnchor: PinCodeAnchor?,
        confirmationUseCase: PinCodeUseCase?,
        onCancel: (() -> Unit)?,
        onResult: ((PinCodeSuccessResult<RESULT>) -> Unit)?,
        resultDelayMillis: Long
    ) {
        resultDelayMillis.setResultDelay()
        if (owner.checkState()) {
            showHostFragment(
                owner.supportFragmentManager,
                useCase,
                popoverAnchor,
                confirmationUseCase,
                onCancel,
                onResult
            )
        }
    }

    override fun show(
        owner: Fragment,
        useCase: PinCodeUseCase,
        popoverAnchor: PinCodeAnchor?,
        confirmationUseCase: PinCodeUseCase?,
        onCancel: (() -> Unit)?,
        onResult: ((PinCodeSuccessResult<RESULT>) -> Unit)?,
        resultDelayMillis: Long
    ) {
        resultDelayMillis.setResultDelay()
        if (owner.requireActivity().checkState()) {
            showHostFragment(
                owner.childFragmentManager,
                useCase,
                popoverAnchor,
                confirmationUseCase,
                onCancel,
                onResult
            )
        }
    }

    override fun hide(owner: AppCompatActivity) {
        hidePinCodeFragment()
    }

    override fun hide(owner: Fragment) {
        hidePinCodeFragment()
    }

    override fun isShown(owner: AppCompatActivity): Boolean {
        return isShownPinCodeFragment(owner.supportFragmentManager)
    }

    override fun isShown(owner: Fragment): Boolean {
        return isShownPinCodeFragment(owner.childFragmentManager)
    }

    override fun onCleared() {
        confirmationFlowRepository = null
        disposer.dispose()
    }

    /**
     * Инициализирует внешний репозиторий и возвращает его.
     */
    fun provideRepository() = repositoryProducer()

    /**
     * Отправит результат с небольшой задержкой. Нужно чтобы потребитель выполнил необходимое действие после закрытия диалога ввода пин-кода.
     */
    fun setResultWithDelay(
        result: PinCodeSuccessResult<RESULT>,
        onResultEvent: ((PinCodeSuccessResult<RESULT>) -> Unit)?
    ) {
        sendData(true) {
            onRequestCheckCodeResult.value = result
            if (onResultEvent != null) {
                onResultEvent(result)
            }
        }
    }

    /**
     * Уведомит потребителя с небольшой задержкой о закрытии диалога ввода пин-кода пользователем.
     */
    fun notifyCancellation(onCancelEvent: (() -> Unit)? = null) {
        sendData(true) {
            onCanceled.value = Unit
            if (onCancelEvent != null) {
                onCancelEvent()
            }
        }
    }

    /**
     * Уведомит потребителя о нажатии на кастомную кнопку-ссылку.
     */
    fun notifyCustomLinkButtonClicked() {
        sendData {
            onCustomLinkButtonAction.value = Unit
        }
    }

    /**
     * Уведомит потребителя об изменении периода действия пин-кода.
     * @param period Период действия. null, если для данной операции не требуется период действия.
     */
    fun notifyPeriodChanged(period: PinCodePeriod?) {
        sendData {
            onPeriodChanged.value = period
        }
    }

    private fun showHostFragment(
        fragmentManager: FragmentManager,
        useCase: PinCodeUseCase,
        popoverAnchor: PinCodeAnchor?,
        confirmationUseCase: PinCodeUseCase?,
        onCancel: (() -> Unit)?,
        onResult: ((PinCodeSuccessResult<RESULT>) -> Unit)?
    ) {
        val checkPinCodFragment = fragmentManager.findFragmentByTag(TAG_PIN_CODE)
        if (checkPinCodFragment != null) {
            fragmentManager.beginTransaction().remove(checkPinCodFragment).performCommit()
        }

        val hostFragment = PinCodeHostFragment<RESULT>().apply {
            arguments = bundleOf(
                ARG_USE_CASE to useCase,
                ARG_POPOVER_ANCHOR to popoverAnchor,
                ARG_CONFIRMATION_USE_CASE to confirmationUseCase,
                ARG_ON_CANCEL to onCancel,
                ARG_ON_RESULT to onResult
            )
        }
        val transaction = fragmentManager.beginTransaction()
        transaction.add(hostFragment, TAG_PIN_CODE)
        transaction.performCommit()
    }

    @Suppress("UNCHECKED_CAST")
    internal fun showPinCodeFragment(
        activity: FragmentActivity,
        fragmentManager: FragmentManager,
        useCase: PinCodeUseCase?,
        popoverAnchor: PinCodeAnchor?,
        confirmationUseCase: PinCodeUseCase?,
        onCancel: (() -> Unit)?,
        onResult: ((PinCodeSuccessResult<RESULT>) -> Unit)?
    ) {
        useCase?.let {
            if (confirmationUseCase == null) {
                createPinCodeFragment(activity, fragmentManager, it, popoverAnchor, onCancel, onResult)
            } else {
                repositoryProducer()?.let { repo ->
                    val userRepository = repo
                    confirmationFlowRepository = ConfirmationFlowRepository(userRepository).apply {
                        startCase = it
                        confirmCase = confirmationUseCase
                        attach(
                            activity,
                            fragmentManager,
                            this@PinCodeFeatureImpl,
                            popoverAnchor,
                            onCancel,
                            onResult as ((PinCodeSuccessResult<*>) -> Unit)?
                        )
                        onFinishFlow = {
                            repositoryProducer = { userRepository }
                        }
                    }
                    repositoryProducer = { confirmationFlowRepository as ConfirmationFlowRepository<RESULT> }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun createPinCodeFragment(
        activity: FragmentActivity,
        fragmentManager: FragmentManager,
        useCase: PinCodeUseCase,
        popoverAnchor: PinCodeAnchor?,
        onCancel: (() -> Unit)? = null,
        onResult: ((PinCodeSuccessResult<RESULT>) -> Unit)? = null
    ) {
        PinCodeFragment.create(
            activity,
            fragmentManager,
            useCase.configuration,
            popoverAnchor,
            onCancel,
            onResult as ((PinCodeSuccessResult<*>) -> Unit)?
        )
    }

    private fun hidePinCodeFragment() {
        hidePinCodeFragment.value = true
    }

    private fun isShownPinCodeFragment(fragmentManager: FragmentManager): Boolean {
        val hostFragment = fragmentManager.findFragmentByTag(TAG_PIN_CODE)
        return hostFragment?.childFragmentManager?.let { PinCodeFragment.isShown(it) } ?: false
    }

    private fun Long.setResultDelay() {
        resultDelay = this
    }

    private fun getCompletable() = Completable.complete()

    private fun getCompletableWithDelay() = getCompletable().delay(resultDelay, TimeUnit.MILLISECONDS)

    private fun sendData(hasDelay: Boolean = false, action: () -> Unit) {
        val completable = if (hasDelay) getCompletableWithDelay() else getCompletable()
        completable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                action()
            }
            .storeIn(disposer)
    }

    private fun FragmentActivity.checkState() = this.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

    private fun FragmentTransaction.performCommit() {
        try {
            this.commit()
        } catch (ex: IllegalStateException) {
            Timber.e(ex)
        }
    }
}