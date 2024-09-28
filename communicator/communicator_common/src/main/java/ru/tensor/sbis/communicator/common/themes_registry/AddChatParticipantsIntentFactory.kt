package ru.tensor.sbis.communicator.common.themes_registry

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Фабрика для создания интента добавления участников в канал.
 *
 * @author da.zhukov
 */
interface AddChatParticipantsIntentFactory : Feature {

    /**
     * Создать интент для открытия активити добавления участников в канал.
     */
    fun createAddChatParticipantsIntent(context: Context, chatUuid: UUID): Intent
}