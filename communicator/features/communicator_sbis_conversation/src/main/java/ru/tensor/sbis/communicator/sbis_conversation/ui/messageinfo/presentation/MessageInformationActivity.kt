package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import ru.tensor.sbis.base_components.AdjustResizeActivity
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.MessageInformationFeatureFacade
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.contract.MessageInformationIntentFactory
import java.util.*
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.base_components.R as RBaseComponents

/**
 * Активность экрана информации о сообщении.
 *
 * @author vv.chekurda
 */
internal class MessageInformationActivity : AdjustResizeActivity() {

    companion object : MessageInformationIntentFactory {

        override fun createIntent(
            context: Context,
            dialogUuid: UUID,
            messageUuid: UUID,
            isGroupDialog: Boolean,
            isChannel: Boolean
        ): Intent = Intent(context, MessageInformationActivity::class.java).apply {
            putExtra(MESSAGE_INFORMATION_DIALOG_UUID_ARG, dialogUuid)
            putExtra(MESSAGE_INFORMATION_MESSAGE_UUID_ARG, messageUuid)
            putExtra(MESSAGE_INFORMATION_IS_GROUP_DIALOG_ARG, isGroupDialog)
            putExtra(MESSAGE_INFORMATION_IS_CHANNEL_ARG, isChannel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO https://online.sbis.ru/opendoc.html?guid=5d6aa3ce-3498-4362-9eff-b28624eeb6b4&client=3
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        overridePendingTransition(RDesign.anim.right_in, RDesign.anim.nothing)
        setContentView(RBaseComponents.layout.base_components_activity_fragment_container)
        if (savedInstanceState == null) {
            placeMessageInformationFragment()
        }
    }

    @SuppressLint("CommitTransaction")
    private fun placeMessageInformationFragment() {
        val fragment = MessageInformationFeatureFacade.createMessageInformationFragment(intent.extras!!)
        supportFragmentManager
            .beginTransaction()
            .replace(contentViewId, fragment, fragment::class.java.simpleName)
            .commit()
    }

    override fun getContentViewId(): Int =
        RBaseComponents.id.base_components_content_container

    override fun swipeBackEnabled(): Boolean = false

    override fun finish() {
        super.finish()
        overridePendingTransition(RDesign.anim.nothing, RDesign.anim.right_out)
    }
}