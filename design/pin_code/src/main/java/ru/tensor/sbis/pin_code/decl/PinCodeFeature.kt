package ru.tensor.sbis.pin_code.decl

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

/**
 * Задержка отправки результата ввода пин-кода. Эмпирическая величина.
 * Нужна для того, чтобы анимация закрытия шторки и клавиатуры отработали,
 * а затем можно было отобразить прикладной контент.
 */
internal const val RESULT_DELAY = 200L

/**
 * Фича компонента ввода пин-кода.
 * @param RESULT тип успешного результата выполнения проверки введенного пин-кода.
 *
 * @author mb.kruglova
 */
interface PinCodeFeature<RESULT> {

    /**
     * Результат выполнения запроса проверки пин-кода после ввода.
     *
     * Наблюдатель будет получать события, только если прикладной фрагмент
     * находится в состоянии Lifecycle.State.STARTED или Lifecycle.State.RESUMED.
     *
     * Если владелец переходит в состояние Lifecycle.State.DESTROYED, наблюдатель будет автоматически удален.
     * Например, как в случае со сменой конфигурации устройства.
     * В этом случае лучше использовать метод [show]
     */
    val onRequestCheckCodeResult: LiveData<PinCodeSuccessResult<RESULT>>

    /**
     * Компонент был закрыт пользователем.
     *
     * Наблюдатель будет получать события, только если прикладной фрагмент
     * находится в состоянии Lifecycle.State.STARTED или Lifecycle.State.RESUMED.
     *
     * Если владелец переходит в состояние Lifecycle.State.DESTROYED, наблюдатель будет автоматически удален.
     * Например, как в случае со сменой конфигурации устройства.
     * В этом случае лучше использовать метод [show]
     */
    val onCanceled: LiveData<Unit>

    /**
     * Была нажата кастомная кнопка-ссылка.
     *
     * Наблюдатель будет получать события, только если прикладной фрагмент
     * находится в состоянии Lifecycle.State.STARTED или Lifecycle.State.RESUMED.
     */
    val onCustomLinkButtonAction: LiveData<Unit>

    /**
     * Период действия пин-кода был изменен.
     */
    val onPeriodChanged: LiveData<PinCodePeriod?>

    /**
     * Отобразить компонент с заданным сценарием использования.
     * @param owner [AppCompatActivity] в котором будет отображаться фрагмент с вводом пин-кода.
     * @param useCase сценарий использования.
     * @param popoverAnchor якорь [PinCodeAnchor] вью к которому должна быть прикреплена форма ввода пин-кода. Используется только для планшетов. Если якорь не задан, то размещение на планшете будет происходить по центру.
     * @param confirmationUseCase сценарий подтверждения ввода (используется при создании пин-кода)
     * @param onCancel коллбэк закрытия фрагмента с вводом пинкода
     * @param onResult коллбэк результата ввода данных во фрагменте с вводом пинкода
     * @param resultDelayMillis задержка отправки результата в прикладной код. Нужна для того, чтобы анимация закрытия
     * шторки и клавиатуры отработали, а затем можно было отобразить прикладной контент. Если нет прикладного контента,
     * который нужно отобразить, как только результат будет передан, можно величину выставить в 0.
     */
    fun show(
        owner: AppCompatActivity,
        useCase: PinCodeUseCase,
        popoverAnchor: PinCodeAnchor? = null,
        confirmationUseCase: PinCodeUseCase? = null,
        onCancel: (() -> Unit)? = null,
        onResult: ((PinCodeSuccessResult<RESULT>) -> Unit)? = null,
        resultDelayMillis: Long = RESULT_DELAY
    )

    /**
     * Отобразить компонент с заданным сценарием использования.
     * @param owner [Fragment] в котором будет отображаться фрагмент с вводом пин-кода.
     * @param useCase сценарий использования.
     * @param popoverAnchor якорь [PinCodeAnchor] вью к которому должна быть прикреплена форма ввода пин-кода. Используется только для планшетов. Если якорь не задан, то размещение на планшете будет происходить по центру.
     * @param confirmationUseCase сценарий подтверждения ввода (используется при создании пин-кода)
     * @param onCancel коллбэк закрытия фрагмента с вводом пинкода
     * @param onResult коллбэк результата ввода данных во фрагменте с вводом пинкода
     * @param resultDelayMillis задержка отправки результата в прикладной код. Нужна для того, чтобы анимация закрытия
     * шторки и клавиатуры отработали, а затем можно было отобразить прикладной контент. Если нет прикладного контента,
     * который нужно отобразить, как только результат будет передан, можно величину выставить в 0.
     */
    fun show(
        owner: Fragment,
        useCase: PinCodeUseCase,
        popoverAnchor: PinCodeAnchor? = null,
        confirmationUseCase: PinCodeUseCase? = null,
        onCancel: (() -> Unit)? = null,
        onResult: ((PinCodeSuccessResult<RESULT>) -> Unit)? = null,
        resultDelayMillis: Long = RESULT_DELAY
    )

    /**
     * Скрыть шторку пин-кода без результата.
     * @param owner [AppCompatActivity] в котором отображается фрагмент с вводом пин-кода.
     */
    fun hide(owner: AppCompatActivity)

    /**
     * Скрыть шторку пин-кода без результата.
     * @param owner [Fragment] в котором отображается фрагмент с вводом пин-кода.
     */
    fun hide(owner: Fragment)

    /**
     * Проверить, отображается ли сейчас шторка с пин-кодом или нет.
     * @param owner [AppCompatActivity] в котором отображается фрагмент с вводом пин-кода.
     */
    fun isShown(owner: AppCompatActivity): Boolean

    /**
     * Проверить, отображается ли сейчас шторка с пин-кодом или нет.
     * @param owner [Fragment] в котором отображается фрагмент с вводом пин-кода.
     */
    fun isShown(owner: Fragment): Boolean
}