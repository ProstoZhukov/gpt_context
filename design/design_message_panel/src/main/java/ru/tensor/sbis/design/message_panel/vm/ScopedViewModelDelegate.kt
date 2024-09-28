package ru.tensor.sbis.design.message_panel.vm

import kotlinx.coroutines.CoroutineScope

/**
 * Интерфейс делегатов для работы которых требуется [CoroutineScope]
 *
 * @author ma.kolpakov
 */
internal interface ScopedViewModelDelegate {

    fun attachToScope(vmScope: CoroutineScope)
}