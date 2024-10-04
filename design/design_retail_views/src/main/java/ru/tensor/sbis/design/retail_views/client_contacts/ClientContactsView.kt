package ru.tensor.sbis.design.retail_views.client_contacts

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import ru.tensor.sbis.design.checkbox.SbisCheckboxView
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.databinding.ClientContactsBinding
import ru.tensor.sbis.design.retail_views.tooltip.Tooltip
import ru.tensor.sbis.design.retail_views.utils.applyStyle
import ru.tensor.sbis.design.theme.Position

/** @SelfDocumented */
private typealias ErrorMessage = String

/** Компонент для редактирования контактной информации о клиенте для отправки чека. */
class ClientContactsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.retail_views_client_contacts_theme,
    @StyleRes defStyleRes: Int = R.style.RetailViewsClientContactsViewStyle_Light,
) : ConstraintLayout(context.applyStyle(defStyleAttr, defStyleRes), attrs, defStyleAttr, defStyleRes) {

    private val binding = ClientContactsBinding.inflate(LayoutInflater.from(getContext()), this).apply {
        retailViewsClientPhoneInputField.onValueChanged = { _, value ->
            handleCheckboxVisibility(value, retailViewsClientCheckboxPhoneSelected)
            handleCheckboxTitleVisibility()
        }

        retailViewsClientEmailInputField.onValueChanged = { _, value ->
            handleCheckboxVisibility(value, retailViewsClientCheckboxEmailSelected)
            handleCheckboxTitleVisibility()
        }

        initCheckboxes(
            retailViewsClientCheckboxEmailSelected,
            retailViewsClientCheckboxPhoneSelected
        )

        retailViewsSaveButton.setOnClickListener {
            handleSaveClick()
        }
    }
    private var errorTooltip: Tooltip? = null

    /** Слушатель, который вызывается по нажатию на кнопку "Сохранить". */
    var onSaveClick: ((ClientContacts) -> Unit)? = null

    /** Установить имя клиента. */
    fun setClient(clientName: String) {
        binding.retailViewsClientNameInputField.value = clientName
    }

    /** Установить email. */
    fun setEmail(email: String, checked: Boolean = false) {
        binding.retailViewsClientEmailInputField.value = email
        binding.retailViewsClientCheckboxEmailSelected.isCheckBoxChecked = checked
    }

    /** Установить телефон. */
    fun setPhone(phone: String, checked: Boolean = false) {
        binding.retailViewsClientPhoneInputField.value = phone
        binding.retailViewsClientCheckboxPhoneSelected.isCheckBoxChecked = checked
    }

    /** Инициализирует поведение чекбоксов при котором выделяется только единственный. */
    private fun initCheckboxes(vararg checkboxes: SbisCheckboxView) {
        fun unselectCheckoxes(excludedCheckBox: View) {
            checkboxes.forEach {
                if (it != excludedCheckBox) {
                    it.isCheckBoxChecked = false
                }
            }
        }

        checkboxes.forEach { checkbox ->
            checkbox.setOnClickListener { currentCheckbox ->
                unselectCheckoxes(currentCheckbox)
            }
        }
    }

    private fun handleCheckboxVisibility(value: String, checkBox: View) {
        checkBox.isVisible = value.isNotBlank()
    }

    private fun handleCheckboxTitleVisibility() {
        binding.retailViewsSendReceiptText.isInvisible = !binding.retailViewsClientCheckboxPhoneSelected.isVisible &&
            !binding.retailViewsClientCheckboxEmailSelected.isVisible
    }

    private fun handleSaveClick() {
        if (!validateData()) return

        val clientContacts = with(binding) {
            ClientContacts(
                clientName = retailViewsClientNameInputField.value.toString(),
                phoneField = ClientContacts.SendReceiptField(
                    value = retailViewsClientPhoneInputField.value.toString(),
                    checked = retailViewsClientCheckboxPhoneSelected.isCheckBoxChecked
                ),
                emailField = ClientContacts.SendReceiptField(
                    value = retailViewsClientEmailInputField.value.toString(),
                    checked = retailViewsClientCheckboxEmailSelected.isCheckBoxChecked
                )
            )
        }

        onSaveClick?.invoke(clientContacts)
    }

    private fun validateData(): Boolean {
        errorTooltip?.hide()

        val phoneValue = binding.retailViewsClientPhoneInputField.value.toString()
        val phoneError = validatePhone(phoneValue)
        if (phoneValue.isNotEmpty() && phoneError != null) {
            errorTooltip = createErrorTooltip(binding.retailViewsClientPhoneInputField, phoneError)
            errorTooltip?.show()
            return false
        }

        val emailValue = binding.retailViewsClientEmailInputField.value.toString()
        val emailError = validateEmail(emailValue)
        if (emailValue.isNotEmpty() && emailError != null) {
            errorTooltip = createErrorTooltip(binding.retailViewsClientEmailInputField, emailError)
            errorTooltip?.show()
            return false
        }

        return true
    }

    private fun validatePhone(phone: String): ErrorMessage? {
        val phoneDigitsCount = phone.count(Char::isDigit)
        if (phoneDigitsCount !in MIN_PHONE_LENGTH..MAX_PHONE_LENGTH) {
            return context.getString(
                R.string.retail_views_client_data_error_phone_length,
                phoneDigitsCount,
                MIN_PHONE_LENGTH,
                MAX_PHONE_LENGTH
            )
        }

        if (phone.length > MAX_ZEROS_STARTING_PHONE_LENGTH && phone.startsWith("000")) {
            return context.getString(
                R.string.retail_views_client_data_error_phone_zeros_starting,
                MAX_ZEROS_STARTING_PHONE_LENGTH
            )
        }

        if (phone.all { symbol -> symbol == '0' }) {
            return context.getString(R.string.retail_views_client_data_error_phone_zeros_only)
        }

        return null
    }

    private fun validateEmail(email: String): ErrorMessage? {
        if (email.length < MIN_EMAIL_LENGTH) {
            return context.getString(R.string.retail_views_client_data_error_email_length, MIN_EMAIL_LENGTH)
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return context.getString(R.string.retail_views_client_data_error_email_format)
        }

        return null
    }

    private fun createErrorTooltip(view: View, errorText: String) = Tooltip.on(view, false)
        .setState(Tooltip.State.ERROR)
        .setText(errorText)
        .setPosition(Position.TOP)
        .setPointerPosition(Tooltip.PointerPosition.START)

    companion object {
        private const val MIN_PHONE_LENGTH = 5
        private const val MAX_PHONE_LENGTH = 15
        private const val MAX_ZEROS_STARTING_PHONE_LENGTH = 11
        private const val MIN_EMAIL_LENGTH = 5
    }
}
