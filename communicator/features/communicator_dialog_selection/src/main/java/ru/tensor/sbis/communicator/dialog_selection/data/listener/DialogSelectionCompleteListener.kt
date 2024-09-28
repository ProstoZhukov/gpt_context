package ru.tensor.sbis.communicator.dialog_selection.data.listener

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.communicator.common.dialog_selection.SelectedDialogResult
import ru.tensor.sbis.communicator.common.dialog_selection.SelectedParticipantsResult
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.share.ConversationUtils.DATA_INTENT_ACTION
import ru.tensor.sbis.communicator.dialog_selection.di.getDialogSelectionComponent
import ru.tensor.sbis.communicator.dialog_selection.presentation.DialogSelectionActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.MultiSelectionListener
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.share.dialog.DialogSelectorItemModel
import java.util.*

/**
 * Реализация слушателя подтверждения выбора диалога/участников
 *
 * @author vv.chekurda
 */
internal class DialogSelectionCompleteListener : MultiSelectionListener<SelectorItemModel, FragmentActivity> {

    override fun onComplete(activity: FragmentActivity, result: List<SelectorItemModel>) {
        val dialogModel: DialogSelectorItemModel? = result[0].castTo()
        val (text: String?, files: List<Uri>?) = activity.intent?.let {
            val textToShare = it.getStringExtra(Intent.EXTRA_TEXT)
            val filesToShare: List<Uri>? = if (it.getStringExtra(DATA_INTENT_ACTION) == Intent.ACTION_SEND) {
                listOfNotNull(it.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)?.castTo())
            } else {
                it.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)?.castTo<List<Uri>>()
            }
            textToShare to filesToShare
        } ?: null to null

        val resultData =
            dialogModel?.run {
                SelectedDialogResult(
                    UUID.fromString(id),
                    messageUuid,
                    isChatForOperations,
                    documentUuid,
                    documentType,
                    isForMe,
                    isSocnetEvent,
                    text,
                    files
                )
            }
                ?: SelectedParticipantsResult(
                    result.filterIsInstance<PersonSelectorItemModel>().map { UUID.fromString(it.id) },
                    result.filterIsInstance<DepartmentSelectorItemModel>().map { UUID.fromString(it.id) },
                    text,
                    files
                )
        activity.getDialogSelectionComponent().resultManager.run {
            clearSelectionResult()
            putNewData(resultData)
        }
        closeScreen(activity)
    }

    private fun closeScreen(activity: FragmentActivity) {
        activity.run {
            if (this is DialogSelectionActivity) {
                finish()
            } else {
                onBackPressed()
            }
        }
    }
}