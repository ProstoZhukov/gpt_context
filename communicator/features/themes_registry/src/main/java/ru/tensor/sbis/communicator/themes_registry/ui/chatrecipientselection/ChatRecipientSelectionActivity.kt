package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.AdjustResizeActivity
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultDelegate
import ru.tensor.sbis.communicator.common.themes_registry.AddChatParticipantsIntentFactory
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.ChatSelectionType.*
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.ui.ChatCreationFragment
import java.io.Serializable
import java.util.*
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Активность выбора получателей для чата
 * работает в 3ех режимах: создание чата, выбор получателей для группового чата и выбор админов
 *
 * @author vv.chekurda
 */
internal class ChatRecipientSelectionActivity : AdjustResizeActivity() {

    private lateinit var selectionType: ChatSelectionType

    private var resultDelegate: RecipientSelectionResultDelegate? = null

    private val dependency get() = themesRegistryDependency

    private val fragmentInstance: Fragment
        get() = when (val type = selectionType) {
            is ChatCreation -> ChatCreationFragment.newInstance()
            is AddChatParticipants -> {
                dependency.getRecipientSelectionFragment(
                    RecipientSelectionConfig(
                        useCase = RecipientSelectionUseCase.AddChatParticipants(type.chatUuid)
                    )
                )
            }
            is AdminSelection -> {
                val fragment = dependency.getRecipientSelectionFragment(
                    RecipientSelectionConfig(
                        useCase = RecipientSelectionUseCase.AddChatAdmins(type.chatUuid),
                        excludeList = dependency.getRecipientSelectionResultManager().preselectedData?.ids
                    )
                )
                dependency.getRecipientSelectionResultManager().clear()
                fragment
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(RDesign.anim.right_in, RDesign.anim.nothing)

        setContentView(RCommunicatorDesign.layout.communicator_activity_fragment_container)
        initLocal()

        if (savedInstanceState == null) {
            placeFragment(fragmentInstance)
        }
    }

    private fun initLocal() {
        with(intent) {
            selectionType = getSerializableExtra(CHAT_RECIPIENT_SELECTION_TYPE) as ChatSelectionType
        }
        resultDelegate = dependency.getRecipientSelectionResultDelegate()
    }

    private fun placeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(contentViewId, fragment, fragment::class.java.simpleName)
            .commit()
    }

    override fun getContentViewId(): Int =
        RCommunicatorDesign.id.communicator_fragment_content_container

    override fun finish() {
        super.finish()
        overridePendingTransition(RDesign.anim.nothing, RDesign.anim.right_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        resultDelegate = null
    }

    override fun swipeBackEnabled(): Boolean = true

    override fun onViewGoneBySwipe() {
        resultDelegate?.onCancel()
        super.onViewGoneBySwipe()
    }

    companion object : AddChatParticipantsIntentFactory {

        private const val CHAT_RECIPIENT_SELECTION_TYPE = "chat_type_for_recipients_selection"

        /**
         * Созать интент для активности выбора получаталей чата
         *
         * @param context   контекст
         * @param type      тип выбора получателей: для создания чата/для группового чата/выбора админов
         */
        fun newIntent(context: Context, type: ChatSelectionType): Intent =
            Intent(context, ChatRecipientSelectionActivity::class.java).apply {
                putExtra(CHAT_RECIPIENT_SELECTION_TYPE, type)
            }

        override fun createAddChatParticipantsIntent(context: Context, chatUuid: UUID): Intent =
            newIntent(context, AddChatParticipants(chatUuid))
    }
}

/**
 * Тип выбора получателей для чата
 *
 * @author vv.chekurda
 */
internal sealed class ChatSelectionType : Serializable {

    /**
     * Создание чата
     */
    object ChatCreation : ChatSelectionType() {
        private fun readResolve(): Any = ChatCreation
    }

    /**
     * Добавление учатсников в канал
     */
    class AddChatParticipants(val chatUuid: UUID) : ChatSelectionType()

    /**
     * Выбор администраторов
     */
    class AdminSelection(val chatUuid: UUID) : ChatSelectionType()
}