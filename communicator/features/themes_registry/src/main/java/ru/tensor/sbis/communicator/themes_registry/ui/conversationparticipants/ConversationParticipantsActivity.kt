package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ParcelUuid
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.AdjustResizeActivity
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationProvider
import ru.tensor.sbis.communicator.common.themes_registry.ConversationParticipantsFactory
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information.DialogInformationFragment
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.view.ChatParticipantsFragment
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.view.ChatParticipantsHostFragment
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.presentation.view.DialogParticipantsFragment
import java.util.UUID
import ru.tensor.sbis.design.R as RDesign

/**
 * Активити-контейнер для последующего открытия экрана участников чата [ChatParticipantsFragment] или диалога [DialogParticipantsFragment], [DialogInformationFragment]
 */
internal class ConversationParticipantsActivity : AdjustResizeActivity(), ConversationParticipantsResultListener {

    companion object : ConversationParticipantsFactory {
        const val KEY_DIALOG_UUID = "dialog_uuid"
        const val KEY_NEW_DIALOG = "new_dialog"
        const val EXTRA_UUID_KEY = ConversationProvider.CONVERSATION_PARTICIPANTS_ACTIVITY_EXTRA_UUID_KEY
        const val EXTRA_STRING_KEY = ConversationProvider.CONVERSATION_PARTICIPANTS_ACTIVITY_EXTRA_STRING_KEY
        const val KEY_IS_CHAT = "key_is_chat"
        const val KEY_CHAT_PERMISSIONS = "chat_permissions"
        const val KEY_IS_FROM_COLLAGE = "from_collage"
        const val KEY_IS_DIALOG_INFO = "is_dialog_info"
        const val KEY_CONVERSATION_NAME = "conversation_name"
        const val KEY_DIALOG_INFORMATION_PARTICIPANTS_UUIDS = "participants_uuids"
        const val KEY_IS_GROUP_CONVERSATION = "is_group_conversation"

        /** @SelfDocumented */
        override fun createConversationParticipantsIntent(
            context: Context,
            conversationUuid: UUID,
            isNewDialog: Boolean,
            isChat: Boolean,
            isDialogInfo: Boolean,
            conversationName: String?,
            permissions: Permissions?,
            participantsUuids: ArrayList<UUID>?
        ): Intent =
            Intent(context, ConversationParticipantsActivity::class.java).apply {
                putExtra(KEY_DIALOG_UUID, conversationUuid)
                putExtra(KEY_NEW_DIALOG, isNewDialog)
                putExtra(KEY_IS_CHAT, isChat)
                putExtra(KEY_CHAT_PERMISSIONS, permissions ?: Permissions())
                putExtra(KEY_IS_DIALOG_INFO, isDialogInfo)
                putExtra(KEY_CONVERSATION_NAME, conversationName)
                putParcelableArrayListExtra(
                    KEY_DIALOG_INFORMATION_PARTICIPANTS_UUIDS,
                    UUIDUtils.toParcelUuids(participantsUuids) as ArrayList<ParcelUuid>
                )
            }
    }

    private val isSwipeBackEnabled
        get() = intent?.extras?.getBoolean(KEY_IS_FROM_COLLAGE, false) ?: false ||
            intent?.extras?.getBoolean(KEY_IS_DIALOG_INFO, false) ?: false

    /** @SelfDocumented */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isSwipeBackEnabled) {
            overridePendingTransition(RDesign.anim.right_in, RDesign.anim.nothing)
        } else {
            overridePendingTransition(RDesign.anim.nothing, RDesign.anim.nothing)
        }
        setContentView(R.layout.communicator_activity_dialog_participants)
        if (savedInstanceState == null) {
            showParticipantsFragment()
        }
        setupCallbacks()
    }

    private fun showParticipantsFragment() {
        var fragment: Fragment? = null

        intent.extras?.run {
            if (containsKey(KEY_IS_CHAT)) {
                fragment = when {
                    getBoolean(KEY_IS_CHAT) -> {
                        ChatParticipantsHostFragment.newInstance(this)
                    }
                    getBoolean(KEY_IS_DIALOG_INFO) -> {
                        DialogInformationFragment.newInstance(this)
                    }
                    getBoolean(KEY_NEW_DIALOG) -> DialogParticipantsFragment.newInstance(this)
                    else -> DialogParticipantsFragment.newInstance(this)
                }
            }
        }

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.communicator_conversation_members_fragment_container,
                fragment!!,
                fragment!!::class.java.canonicalName
            )
            .commit()
    }

    /** @SelfDocumented */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    /** @SelfDocumented */
    override fun onResultOk(result: UUID) {
        val intent = Intent()
        intent.putExtra(EXTRA_UUID_KEY, result)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /** @SelfDocumented */
    override fun onResultOk(result: String) {
        intent.putExtra(EXTRA_STRING_KEY, result)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /** @SelfDocumented */
    override fun onResultCancel() {
        setResult(Activity.RESULT_CANCELED, null)
        finish()
    }

    /** @SelfDocumented */
    override fun getContentViewId(): Int {
        return R.id.communicator_conversation_members_fragment_container
    }

    /** @SelfDocumented */
    override fun finish() {
        super.finish()
        if (isSwipeBackEnabled) {
            overridePendingTransition(RDesign.anim.nothing, RDesign.anim.right_out)
        } else {
            overridePendingTransition(RDesign.anim.nothing, RDesign.anim.nothing)
        }
    }

    /** @SelfDocumented */
    override fun swipeBackEnabled(): Boolean {
        return isSwipeBackEnabled
    }

    private fun setupCallbacks() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (supportFragmentManager.fragments.lastOrNull()?.castTo<FragmentBackPress>()?.onBackPressed() != true) {
                        this@ConversationParticipantsActivity.finish()
                    }
                }
            }
        )
    }
}
