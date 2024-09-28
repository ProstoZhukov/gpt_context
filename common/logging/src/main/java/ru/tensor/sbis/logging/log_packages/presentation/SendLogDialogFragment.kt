package ru.tensor.sbis.logging.log_packages.presentation

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.view.ContextThemeWrapper
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState.DISABLED
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState.ENABLED
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValue.CHECKED
import ru.tensor.sbis.logging.R
import ru.tensor.sbis.logging.databinding.LoggingSendLogDialogBinding
import ru.tensor.sbis.modalwindows.dialogalert.BaseAlertDialogFragment
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.modalwindows.R as RModal

/**
 * Фрагмент диалога для подтверждения отправки диагностических данных.
 *
 * @author av.krymov
 */
internal class SendLogDialogFragment private constructor() : PopupConfirmation() {

    override fun addContent(container: View) {
        val contentContainer = container.findViewById<FrameLayout>(RModal.id.modalwindows_alert_content_container)
        LoggingSendLogDialogBinding
            .inflate(getThemedInflater(requireActivity().layoutInflater), contentContainer, true)
            .apply {
                loggingSuccessButton.state = DISABLED
                val totalSize = arguments?.getString(LOGGING_TOTAL_SIZE) ?: ""
                loggingTotalSize.text = getString(R.string.logging_confirmation_total_size_text, totalSize)
                loggingSuccessButton.setOnClickListener {
                    getListener<DialogYesNoWithTextListener>()?.onYes(LOGGING_CONFIRM_REQUEST_CODE, null)
                    dismiss()
                }
                loggingDismissButton.setOnClickListener {
                    dismiss()
                }
                loggingConfirmationCheckbox.setOnClickListener {
                    loggingSuccessButton.state = if (loggingConfirmationCheckbox.value == CHECKED) ENABLED
                    else DISABLED
                }
            }
    }

    private fun getThemedInflater(inflater: LayoutInflater) = inflater.cloneInContext(
        ContextThemeWrapper(
            context,
            RDesign.style.DefaultLightTheme
        )
    )

    companion object {
        /**
         * Создать инстанс [SendLogDialogFragment]
         *
         * @param totalSize отображаемое значение размера выбранных файлов
         */
        fun newInstance(totalSize: String): BaseAlertDialogFragment {
            return SendLogDialogFragment().apply {
                getOrCreateArguments().apply {
                    putString(LOGGING_TOTAL_SIZE, totalSize)
                }
            }
        }
    }
}

const val LOGGING_CONFIRM_REQUEST_CODE = 500
private const val LOGGING_TOTAL_SIZE = "LOGGING_TOTAL_SIZE"
