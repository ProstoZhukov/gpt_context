package ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates

import androidx.annotation.StringRes
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesPresenter.Companion.PHONE_VERIFICATION_CONFIRMATION_DIALOG_TAG
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.PhoneNumberSelectionItemListener
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.PhoneNumberVerificationErrorHandler
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.cloud_view.content.phone_number.PhoneNumberClickListener
import ru.tensor.sbis.design.confirmation_dialog.ButtonModel
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import timber.log.Timber
import java.util.UUID

/**
 * Делегат для работы с номером телефона.
 *
 * @author da.zhukov
 */
internal class ConversationPhoneNumberDelegate(
    private val clipboardManager: ClipboardManager
) : ConversationMessagesBaseDelegate(),
    PhoneNumberSelectionItemListener,
    PhoneNumberClickListener,
    PhoneNumberVerificationErrorHandler {

    private var phoneNumberActionList: List<Int>? = null
    private var phoneNumber: String? = null

    override fun onPhoneNumberActionClick(actionOrder: Int) {
        @StringRes val action = phoneNumberActionList?.getOrNull(actionOrder) ?: kotlin.run {
            Timber.w("There is no selected action in actions list")
            return
        }
        phoneNumberActionList = null
        performPhoneNumberAction(action)
    }

    override fun onPhoneNumberClicked(phoneNumber: String) {
        router?.dialPhoneNumber(phoneNumber)
    }

    override fun onPhoneNumberLongClicked(phoneNumber: String, messageUUID: UUID?) {
        this.phoneNumber = phoneNumber
        phoneNumberActionList = preparedPhoneNumberActionList()
        view?.showPhoneNumberActionsList(messageUUID, phoneNumberActionList!!)
    }

    override fun onPhoneVerificationRequired(message: CharSequence?) {
        view?.forceHideKeyboard()
        showPhoneVerificationDialog()
    }

    private fun preparedPhoneNumberActionList(): List<Int> {
        return listOf(
            R.string.communicator_selected_phone_number_action_copy,
            R.string.communicator_selected_phone_number_action_call,
            R.string.communicator_selected_phone_number_action_add
        )
    }

    /**
     * Обработать выбранный элемент списка действий с номером телефона.
     *
     * @param actionString строковый ресурс выбранного элемента из предложенного списка действий.
     */
    private fun performPhoneNumberAction(@StringRes actionString: Int) {
        when (actionString) {
            R.string.communicator_selected_phone_number_action_copy -> {
                copySelectedPhoneNumberToClipboard()
            }

            R.string.communicator_selected_phone_number_action_call -> {
                phoneNumber?.let { router?.callTheNumber(it) }
            }

            R.string.communicator_selected_phone_number_action_add -> {
                phoneNumber?.let { router?.addNumberToPhoneBook(it) }
            }
        }
    }

    private fun showPhoneVerificationDialog() {
        view?.showConfirmationDialog(
            text = view?.getStringRes(R.string.communicator_no_verified_phone_number),
            buttons = getPhoneVerificationDialogButtons(),
            tag = PHONE_VERIFICATION_CONFIRMATION_DIALOG_TAG
        )
    }

    private fun getPhoneVerificationDialogButtons(): List<ButtonModel<ConfirmationButtonId>> =
        listOf(
            ButtonModel(
                id = ConfirmationButtonId.OK,
                labelRes = R.string.communicator_confirmation_dialog_error_send_ok,
                viewId = ru.tensor.sbis.design.design_confirmation.R.id.confirmation_dialog_button_cancel
            ),
            ButtonModel(
                id = ConfirmationButtonId.YES,
                labelRes = R.string.communicator_alert_confirmation_number_yes,
                style = PrimaryButtonStyle,
                isPrimary = true,
                viewId = ru.tensor.sbis.design.design_confirmation.R.id.confirmation_dialog_button_yes
            )
        )

    private fun copySelectedPhoneNumberToClipboard() {
        val text = phoneNumber ?: StringUtils.EMPTY
        clipboardManager.copyToClipboard(text)
        if (text.isNotEmpty()) {
            view?.showToast(R.string.communicator_phone_number_copied)
        }
    }
}