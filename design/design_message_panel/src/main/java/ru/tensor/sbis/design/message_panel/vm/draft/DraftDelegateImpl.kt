package ru.tensor.sbis.design.message_panel.vm.draft

import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import javax.inject.Inject

/**
 * @author ma.kolpakov
 */
internal class DraftDelegateImpl @Inject constructor() : DraftDelegate {

    override val draftUuid = MutableStateFlow<UUID?>(null)

    override fun setDraftUuid(uuid: UUID?) {
        draftUuid.value = uuid
    }
}