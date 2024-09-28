package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import ru.tensor.sbis.base_components.TrackingActivity
import ru.tensor.sbis.communicator.common.themes_registry.ChatSettingsIntentFactory
import ru.tensor.sbis.communicator.themes_registry.R
import java.util.*
import ru.tensor.sbis.design.R as RDesign

/**
 * Активити экрана настроек чата
 *
 * @author vv.chekurda
 */
internal class ChatSettingsActivity : TrackingActivity() {

    companion object : ChatSettingsIntentFactory {

        /** Константы экрана настроек чата */
        internal const val CHAT_SETTINGS_NEW_CHAT_BOOLEAN = "chat_settings_new_chat_boolean"
        internal const val CHAT_SETTINGS_UUID = "chat_settings_uuid"
        internal const val CHAT_SETTINGS_DRAFT_BOOLEAN = "chat_settings_draft_boolean"

        override fun createChatSettingsIntent(
            context: Context,
            conversationUuid: UUID?,
            isNewChat: Boolean,
            isDraft: Boolean
        ): Intent =
            Intent(context, ChatSettingsActivity::class.java).apply {
                putExtra(CHAT_SETTINGS_NEW_CHAT_BOOLEAN, isNewChat)
                putExtra(CHAT_SETTINGS_DRAFT_BOOLEAN, isDraft)
                putExtra(CHAT_SETTINGS_UUID, conversationUuid)
            }
    }

    /**@SelfDocumented */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(RDesign.anim.right_in, RDesign.anim.nothing)
        setContentView(R.layout.communicator_activity_chat_settings)

        var chatSettingsFragment: ChatSettingsFragment? =
            supportFragmentManager.findFragmentByTag(ChatSettingsFragment::class.java.simpleName) as? ChatSettingsFragment

        if (chatSettingsFragment == null) {
            chatSettingsFragment = ChatSettingsFragment.newInstance(
                intent.getBooleanExtra(CHAT_SETTINGS_NEW_CHAT_BOOLEAN, true),
                intent.getSerializableExtra(CHAT_SETTINGS_UUID) as UUID?,
                intent.getBooleanExtra(CHAT_SETTINGS_DRAFT_BOOLEAN, true)
            )
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.communicator_chat_settings_fragment_container,
                    chatSettingsFragment,
                    ChatSettingsFragment::class.java.simpleName
                )
                .commit()
        }
    }

    /**@SelfDocumented */
    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
        overridePendingTransition(RDesign.anim.nothing, RDesign.anim.right_out)
    }

    override fun swipeBackEnabled(): Boolean = true

    /**@SelfDocumented */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            overridePendingTransition(RDesign.anim.nothing, RDesign.anim.right_out)
        }
        return super.onOptionsItemSelected(item)
    }
}
