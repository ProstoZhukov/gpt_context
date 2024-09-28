package ru.tensor.sbis.communicator.declaration.communicator_support_channel_list

import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.settings_screen_decl.Item
import java.util.UUID

/**
 * Фабрика хост фрагмента реестра чатов техподдержки.
 *
 * @author ra.petrov
 */
interface SupportChannelListFragmentFactory : Feature {

    /**
     * Возвращает элемент для экрана настроек для открытия службы поддержки.
     */
    fun createSupportChatListFragmentSettingsItem(): Item

    /**
     * Возвращает элемент для экрана настроек для открытия службы поддержки с КЛИЕНТСКИМИ каналами.
     * Используется в SabyDisk.
     * В нем отображается список клиентских каналов с закрепленной "Поддержкой СБИС".
     */
    fun createClientsSupportChatListFragmentSettingsItem(): Item

    /**
     * Доступна ли служба поддержки.
     * @return Доступна ли служба поддержки.
     */
    suspend fun isSupportAvailable(): Boolean

    /**
     * Получить Intent для открытия СП по пушу.
     */
    fun getOpenSupportConversationIntent(dialogUuid: UUID, conversationTitle: String? = null) : Intent

    /**
     * Получить Intent для открытия СП по пушу.
     */
    fun getOpenSabySupportConversationIntent(dialogUuid: UUID, conversationTitle: String? = null) : Intent
}