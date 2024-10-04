package ru.tensor.sbis.design_dialogs.dialogs.container.util.immersive_mode

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import ru.tensor.sbis.design_dialogs.dialogs.container.HasImmersiveMode

private const val IMMERSIVE_MODE_ACTIVATION_DELAY = 2000L

/**
 * Обработчик режима погружения
 *
 * @property autoActivationSupported Поддерживается ли автоматическая активация режима погружения
 * Автоматическая активация - переход в режим погружения, если пользователь не взаимодействует как-либо с экраном
 *
 * @author sa.nikitin
 */
class ImmersiveModeHandler(private val autoActivationSupported: Boolean = true) : HasImmersiveMode {

    private val uiHandler = Handler(Looper.getMainLooper())
    private val delayedActivation =
        Runnable { changeImmersiveModeState(isImmersiveModeEnabled = true, withAnim = true) }

    private var isImmersiveModeSupported: Boolean = true
    private var isImmersiveModeEnabled: Boolean = false

    private var autoActivationBlockers: MutableSet<String> = mutableSetOf()
    private var autoActivationStopped: Boolean = false

    private var lastLifecycleEvent: Lifecycle.Event? = null

    private var view: ImmersiveModeView? = null

    override fun changeImmersiveModeSupport(isImmersiveModeSupported: Boolean) {
        if (this.isImmersiveModeSupported != isImmersiveModeSupported) {
            this.isImmersiveModeSupported = isImmersiveModeSupported
            if (isImmersiveModeSupported) {
                updateDelayedActivation()
            } else {
                changeImmersiveModeState(isImmersiveModeEnabled = false, withAnim = true)
            }
        }
    }

    override fun changeImmersiveModeState(isImmersiveModeEnabled: Boolean) {
        if (this.isImmersiveModeEnabled != isImmersiveModeEnabled) {
            changeImmersiveModeState(isImmersiveModeEnabled, withAnim = true)
        }
        if (!isImmersiveModeEnabled) {
            autoActivationStopped = true
            updateDelayedActivation()
        }
    }

    override fun switchImmersiveModeState() {
        changeImmersiveModeState(!isImmersiveModeEnabled)
    }

    /** @SelfDocumented */
    fun onViewCreated(view: ImmersiveModeView) {
        this.view = view
        updateControlsVisibility(false)
        updateDelayedActivation()
        lastLifecycleEvent = Lifecycle.Event.ON_CREATE
    }

    /** @SelfDocumented */
    fun onViewStarted() {
        if (lastLifecycleEvent == Lifecycle.Event.ON_STOP) {
            changeImmersiveModeState(isImmersiveModeEnabled = false, withAnim = false)
            updateDelayedActivation()
        }
        lastLifecycleEvent = Lifecycle.Event.ON_START
    }

    /** @SelfDocumented */
    fun onViewStopped() {
        autoActivationStopped = false
        lastLifecycleEvent = Lifecycle.Event.ON_STOP
    }

    /** @SelfDocumented */
    fun onViewDestroyed() {
        view = null
        lastLifecycleEvent = Lifecycle.Event.ON_DESTROY
        cancelDelayedActivation()
    }

    /**
     * Добавить блокировщик автоматической активации режима погружения по его идентификатору [blockerId].
     * Автоматическая активация - переход в режим погружения, если пользователь не взаимодействует как-либо с экраном.
     * Если есть хотя бы один блокировщик, то автоматическая активация не доступна.
     * Повторные вызовы этого метода с одним и тем же [blockerId] ни к чему не приведут, используется Set
     */
    fun addAutoActivationBlocker(blockerId: String) {
        if (autoActivationBlockers.add(blockerId)) {
            updateDelayedActivation()
        }
    }

    /**
     * Удалить блокировщик автоматической активации режима погружения по его идентификатору [blockerId].
     * Автоматическая активация - переход в режим погружения, если пользователь не взаимодействует как-либо с экраном.
     * Если есть хотя бы один блокировщик, то автоматическая активация не доступна.
     * Не гарантирует запуск автоматической активации, т.к. она зависит и от других параметров
     */
    fun removeAutoActivationBlocker(blockerId: String) {
        if (autoActivationBlockers.remove(blockerId)) {
            updateDelayedActivation()
        }
    }

    /**
     * Перезапустить автоматическую активацию.
     * Автоматическая активация - переход в режим погружения, если пользователь не взаимодействует как-либо с экраном.
     * Не гарантирует запуск автоматической активации, т.к. она зависит и от других параметров
     */
    fun restartAutoActivation() {
        cancelDelayedActivation()
        updateDelayedActivation()
    }

    private fun changeImmersiveModeState(isImmersiveModeEnabled: Boolean, withAnim: Boolean) {
        if (this.isImmersiveModeEnabled != isImmersiveModeEnabled) {
            if (isImmersiveModeSupported || !isImmersiveModeEnabled) {
                this.isImmersiveModeEnabled = isImmersiveModeEnabled
                updateControlsVisibility(withAnim)
            }
        }
    }

    private fun updateDelayedActivation() {
        if (isImmersiveModeSupported &&
            !isImmersiveModeEnabled &&
            autoActivationSupported &&
            autoActivationBlockers.isEmpty() &&
            !autoActivationStopped
        ) {
            runDelayedActivation()
        } else {
            cancelDelayedActivation()
        }
    }

    private fun runDelayedActivation() {
        uiHandler.postDelayed(delayedActivation, IMMERSIVE_MODE_ACTIVATION_DELAY)
    }

    private fun cancelDelayedActivation() {
        uiHandler.removeCallbacks(delayedActivation)
    }

    private fun updateControlsVisibility(withAnim: Boolean) {
        view?.changeControlsVisibility(!isImmersiveModeEnabled, withAnim)
    }
}