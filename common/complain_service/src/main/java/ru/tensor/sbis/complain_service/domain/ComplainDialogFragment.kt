package ru.tensor.sbis.complain_service.domain

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.complain.ComplainDialogFragmentFeature
import ru.tensor.sbis.communication_decl.complain.data.*
import ru.tensor.sbis.complain_service.ComplainServiceFeatureFacade
import ru.tensor.sbis.complain_service.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation

internal class ComplainDialogFragment : DialogFragment(),
    PopupConfirmation.DialogYesNoWithTextListener,
    PopupConfirmation.DialogCancelListener,
    PopupConfirmation.DialogDismissListener {

    companion object : ComplainDialogFragmentFeature {

        private const val MAX_LENGTH = 256
        private const val COMPLAIN_USE_CASE_KEY = "COMPLAIN_USE_CASE_KEY"
        private const val COMPLAIN_DIALOG_CODE = 65

        override fun showComplainDialogFragment(
            fragmentManager: FragmentManager,
            useCase: ComplainUseCase
        ) {
            val fragment = ComplainDialogFragment().withArgs {
                putSerializable(COMPLAIN_USE_CASE_KEY, useCase)
            }
            fragment.show(fragmentManager, fragment.javaClass.canonicalName)
        }
    }

    private val complainParams by lazy { arguments?.getSerializable(COMPLAIN_USE_CASE_KEY) as ComplainUseCase }
    private val complainService = ComplainServiceFeatureFacade.getComplainService()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        showComplainDialog()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onYes(requestCode: Int, text: String?) {
        if (requestCode == COMPLAIN_DIALOG_CODE) {
            val complainParams = complainParams.buildParams(text)
            val result = complainService.complain(complainParams)
            showNotification(result)
            dismissAllowingStateLoss()
        }
    }

    override fun onNo(requestCode: Int, text: String?) {
        dismissAllowingStateLoss()
    }

    override fun onDismiss(requestCode: Int) {
        dismissAllowingStateLoss()
    }

    private fun showComplainDialog() {
        val context = this.context ?: return
        PopupConfirmation.newEditTextInstance(
            requestCode = COMPLAIN_DIALOG_CODE,
            hint = context.getString(R.string.complain_service_report_hint_text),
            maxLength = MAX_LENGTH,
            message = context.getString(R.string.complain_service_report_message_text),
            inputType = InputType.TYPE_CLASS_TEXT,
            canNotBeBlank = true,
            mustChangeInitialText = false,
            initialText = null
        ).also {
            it.requestPositiveButton(context.getString(R.string.complain_service_dialog_positive))
            it.requestNegativeButton(context.getString(R.string.complain_service_dialog_negative))
            it.setEventProcessingRequired(true)
        }.show(this.childFragmentManager, this.javaClass.canonicalName)
    }

    private fun showNotification(complainResult: ComplainResult) {
        val context = this.context ?: return
        val icon = when (complainResult.status) {
            ComplainStatus.NETWORK_ERROR -> SbisMobileIcon.Icon.smi_WiFiNone.character.toString()
            ComplainStatus.SUCCESS -> SbisMobileIcon.Icon.smi_Successful.character.toString()
            else -> null
        }
        val type = if (complainResult.status == ComplainStatus.SUCCESS) {
            SbisPopupNotificationStyle.SUCCESS
        } else {
            SbisPopupNotificationStyle.ERROR
        }

        SbisPopupNotification.push(
            context = context,
            type = type,
            message = complainResult.message,
            icon = icon
        )
    }

    private fun ComplainUseCase.buildParams(comment: String?): ComplainParams =
        when (this) {
            is ComplainUseCase.ConversationMessage -> ComplainParams(
                entityType = ComplainEntityType.MESSAGE,
                entityUUID = messageUuid,
                entityParentType = if (isChat) ComplainEntityType.CHAT else ComplainEntityType.DIALOG,
                entityParentUuid = conversationUuid,
                comment = comment
            )
            is ComplainUseCase.Conversation -> ComplainParams(
                entityType = if (isChat) ComplainEntityType.CHAT else ComplainEntityType.DIALOG,
                entityUUID = uuid,
                comment = comment
            )
            is ComplainUseCase.Comment -> ComplainParams(
                entityType = ComplainEntityType.MESSAGE,
                entityUUID = commentUuid,
                entityParentType = ComplainEntityType.NEWS,
                entityParentUuid = documentUuid,
                comment = comment
            )
            is ComplainUseCase.User -> ComplainParams(
                entityType = ComplainEntityType.USER,
                entityUUID = uuid,
                comment = comment
            )
            is ComplainUseCase.Group -> ComplainParams(
                entityType = ComplainEntityType.GROUP,
                entityUUID = uuid,
                comment = comment
            )
            is ComplainUseCase.Forum -> ComplainParams(
                entityType = ComplainEntityType.FORUM,
                entityUUID = forumUuid,
                entityParentType = ComplainEntityType.GROUP,
                entityParentUuid = groupUuid,
                comment = comment
            )
            is ComplainUseCase.News -> ComplainParams(
                entityType = ComplainEntityType.NEWS,
                entityUUID = uuid,
                comment = comment
            )
            is ComplainUseCase.SabyReview -> ComplainParams(
                entityType = ComplainEntityType.SABYGET_REVIEW,
                entityUUID = reviewUuid,
                entityParentType = ComplainEntityType.SABYGET,
                entityParentUuid = placeUuid,
                comment = comment
            )
            is ComplainUseCase.SabyPlace -> ComplainParams(
                entityType = ComplainEntityType.SABYGET,
                entityUUID = placeUuid,
                comment = comment
            )
        }
}