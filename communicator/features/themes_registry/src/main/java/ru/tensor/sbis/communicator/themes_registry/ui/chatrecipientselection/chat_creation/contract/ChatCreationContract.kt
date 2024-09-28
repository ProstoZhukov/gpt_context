package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.contract

import androidx.lifecycle.LifecycleObserver
import ru.tensor.sbis.mvp.presenter.BasePresenter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsFragment
import java.util.*

/**
 * Интерфейс view экрана создания нового чата
 *
 * @author vv.chekurda
 */
internal interface ChatCreationView {

    /**
     * Показать создание настроек чата
     */
    fun showCreationChatSettings()

    /**
     * Открыть приватный чат
     *
     * @param recipient идентификатор получателя
     */
    fun openPrivateChat(recipient: UUID)

    /**
     * Открыть новый групповой чат
     *
     * @param chatUuid идентификатор чата
     */
    fun openNewGroupChat(chatUuid: UUID)

    /**
     * Закрыть экран создания чата
     */
    fun closeChatCreation()
}

/**
 * Презентер экрана создания нового чата
 */
internal interface ChatCreationPresenter : BasePresenter<ChatCreationView>,
    ChatSettingsFragment.ResultListener,
    LifecycleObserver