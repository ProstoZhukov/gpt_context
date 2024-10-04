package ru.tensor.sbis.pin_code

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.pin_code.decl.ConfirmationFlowAction
import ru.tensor.sbis.pin_code.decl.ConfirmationFlowAction.CONFIRM_CODE
import ru.tensor.sbis.pin_code.decl.ConfirmationFlowAction.CREATE_AGAIN
import ru.tensor.sbis.pin_code.decl.ConfirmationFlowAction.SEND_CODE
import ru.tensor.sbis.pin_code.decl.ConfirmationFlowAction.TRY_CONFIRM_AGAIN
import ru.tensor.sbis.pin_code.decl.PinCodeAnchor
import ru.tensor.sbis.pin_code.decl.PinCodeRepository
import ru.tensor.sbis.pin_code.decl.PinCodeSuccessResult
import ru.tensor.sbis.pin_code.decl.PinCodeUseCase
import ru.tensor.sbis.pin_code.feature.PinCodeFeatureImpl
import ru.tensor.sbis.pin_code.util.runVibration
import timber.log.Timber

/**
 * Реализация репозитория которая поддерживает подтверждение ввода. Используется при создании пин-кода.
 * @author mb.kruglova
 */
internal class ConfirmationFlowRepository<RESULT>(
    private val repository: PinCodeRepository<RESULT>,
    private val attemptsCount: Int = 3
) : PinCodeRepository<RESULT> {
    private var changeCaseSubject = PublishSubject.create<PinCodeUseCase>()
    private var disposer = SerialDisposable()
    private var currentDigits = ""
    private var currentAttempt = 0
    private var creationEnd = false
    private var showAlert = false
    private var vibrate = false
    internal var onFinishFlow: (() -> Unit)? = null
    lateinit var startCase: PinCodeUseCase

    lateinit var confirmCase: PinCodeUseCase

    override fun getConfirmationFlowAction(digits: String): ConfirmationFlowAction {
        if (creationEnd) return SEND_CODE

        return if (currentDigits.isEmpty()) {
            currentDigits = digits
            changeCaseSubject.onNext(confirmCase)
            CONFIRM_CODE
        } else {
            if (currentDigits == digits) {
                finishConfirmationFlow()
                SEND_CODE
            } else {
                repository.onConfirmationError()
                currentAttempt++
                if (currentAttempt >= attemptsCount) {
                    showAlert = true
                    vibrate = true
                    initAndShowStartCase()
                    CREATE_AGAIN
                } else {
                    TRY_CONFIRM_AGAIN
                }
            }
        }
    }

    override fun onCodeEntered(digits: String): RESULT = repository.onCodeEntered(digits)

    override fun needCleanCode(error: Throwable): Boolean = repository.needCleanCode(error)

    internal fun attach(
        activity: FragmentActivity,
        fragmentManager: FragmentManager,
        pinCodeFeature: PinCodeFeatureImpl<*>,
        popoverAnchor: PinCodeAnchor?,
        onCancel: (() -> Unit)?,
        onResult: ((PinCodeSuccessResult<*>) -> Unit)?
    ) {
        pinCodeFeature.cancelPinCodeEntering.observe(activity) {
            finishConfirmationFlow()
        }
        disposer.set(
            changeCaseSubject
                .subscribe(
                    {
                        pinCodeFeature.createPinCodeFragment(
                            activity,
                            fragmentManager,
                            it,
                            popoverAnchor,
                            onCancel,
                            onResult
                        )
                        if (showAlert) {
                            showAlert = false
                            Toast.makeText(
                                activity,
                                R.string.pin_code_creation_confirmation_alert,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        if (vibrate) {
                            vibrate = false
                            activity.runVibration()
                        }
                    },
                    {
                        Timber.e(it)
                    }
                )
        )
        initAndShowStartCase()
    }

    private fun initAndShowStartCase() {
        creationEnd = false
        currentAttempt = 0
        currentDigits = ""
        changeCaseSubject.onNext(startCase)
    }

    /** Завершить подтверждение ввода пин-кода. */
    private fun finishConfirmationFlow() {
        onFinishFlow?.invoke()
        clear()
    }

    private fun clear() {
        creationEnd = true
        currentAttempt = 0
        currentDigits = ""
        disposer.set(null)
        onFinishFlow = null
    }
}