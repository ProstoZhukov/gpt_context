package ru.tensor.sbis.communicator.common.themes_registry

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Фабрика для создания интента на запуск экрана настроек чата
 *
 * @author da.zhukov
 */
interface ChatSettingsIntentFactory : Feature {

    /**
     * Создать интент на запуск экрана настроек чата
     */
    fun createChatSettingsIntent(context: Context, conversationUuid: UUID?, isNewChat: Boolean, isDraft: Boolean): Intent
}