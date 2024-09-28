package ru.tensor.sbis.mvi_extension.router.navigator

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.mvi_extension.router.buffer.BufferFactory
import ru.tensor.sbis.mvi_extension.router.buffer.BufferStatePolicy

/**
 * Created by Aleksey Boldinov on 23.08.2022.
 */
class WeakLifecycleNavigator<ENTITY : LifecycleOwner>(
    entity: ENTITY,
    private val bufferStatePolicy: BufferStatePolicy = BufferStatePolicy.Lifecycle,
    private val navigatorPolicy: NavigatorLifecyclePolicy = NavigatorLifecyclePolicy.RESUME_PAUSE
) : Navigator<ENTITY> {

    private var weakEntity: ENTITY? = null
    private val buffer = BufferFactory.create<(ENTITY) -> Unit>(bufferStatePolicy)

    init {
        weakEntity = entity
        entity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                executePendingCommands()
                if (event == Lifecycle.Event.ON_DESTROY) {
                    weakEntity = null
                    if (bufferStatePolicy == BufferStatePolicy.Lifecycle) {
                        buffer.clear()
                    }
                    source.lifecycle.removeObserver(this)
                }
            }
        })
    }

    override fun execute(command: (ENTITY) -> Unit) {
        weakEntity?.apply {
            if (ensureLifecycleState()) {
                command.invoke(this)
            } else {
                buffer.add(command)
            }
        }
    }

    private fun executePendingCommands() {
        weakEntity?.apply {
            if (ensureLifecycleState()) {
                while (buffer.isNotEmpty()) {
                    buffer.poll()?.invoke(this)
                }
            }
        }
    }

    private fun ENTITY.ensureLifecycleState(): Boolean {
        lifecycle.currentState.apply {
            return if (isAtLeast(Lifecycle.State.RESUMED)) {
                true
            } else if (isAtLeast(Lifecycle.State.STARTED) && navigatorPolicy <= NavigatorLifecyclePolicy.START_STOP) {
                true
            } else isAtLeast(Lifecycle.State.CREATED) && navigatorPolicy <= NavigatorLifecyclePolicy.CREATE_DESTROY
        }
    }
}