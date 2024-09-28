package ru.tensor.sbis.entrypoint_guard.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.entrypoint_guard.activity.contract.controllers.KeyEventController
import ru.tensor.sbis.entrypoint_guard.activity.contract.controllers.MotionEventController


/** @SelfDocumented */
internal class FlexibleActivityControllersHolder : ViewModel() {
    /** @SelfDocumented */
    var keyEventController: KeyEventController? = null

    /** @SelfDocumented */
    var motionEventController: MotionEventController? = null

    override fun onCleared() {
        reset()
        super.onCleared()
    }

    /** @SelfDocumented */
    fun reset() {
        keyEventController?.setInterceptor(null)
        keyEventController = null
        motionEventController?.setInterceptor(null)
        motionEventController = null
    }

}

/** Найти [KeyEventController] в текущей активности. */
fun <T> T.findKeyEventController(): KeyEventController? where T : AppCompatActivity =
    ViewModelProvider(this)[FlexibleActivityControllersHolder::class.java].keyEventController

/** Найти [MotionEventController] в текущей активности. */
fun <T> T.findMotionEventController(): MotionEventController? where T : AppCompatActivity =
    ViewModelProvider(this)[FlexibleActivityControllersHolder::class.java].motionEventController