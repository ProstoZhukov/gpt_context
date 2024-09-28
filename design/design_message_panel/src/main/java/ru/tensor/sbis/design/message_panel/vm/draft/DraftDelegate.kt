package ru.tensor.sbis.design.message_panel.vm.draft

import kotlinx.coroutines.flow.StateFlow
import java.util.*

/**
 * Внутренний API для работы с черновиком панели ввода
 *
 * @author ma.kolpakov
 */
internal interface DraftDelegate {

    val draftUuid: StateFlow<UUID?>

    fun setDraftUuid(uuid: UUID?)
}