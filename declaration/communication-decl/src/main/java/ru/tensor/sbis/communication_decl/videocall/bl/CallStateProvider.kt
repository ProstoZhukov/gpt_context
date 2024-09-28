package ru.tensor.sbis.communication_decl.videocall.bl

import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Interface which provides Video Call State
 *
 * @author is.mosin
 */
interface CallStateProvider : Feature {

    /**
     * Состояние: запущен звонок или нет.
     */
    val isCallRunningFlow: StateFlow<Boolean>

    /**
     * Used to check if any active call exists
     */
    fun isCallRunning(): Boolean
}