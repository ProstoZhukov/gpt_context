package ru.tensor.sbis.message_panel.view

import android.content.res.Resources
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.view.updateMargins
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.recorder.decl.RecorderView
import ru.tensor.sbis.design.R as RDesign

/**
 * Инструмент для управления отступами в панели сообщений.
 *
 * @author vv.chekurda
 * @since 01/23/2020
 */
internal class MessagePanelBindingHelper(resources: Resources) {

    private val messageContainerLeftMarginWithoutAttachButton = resources.getDimensionPixelSize(R.dimen.message_container_left_margin_without_attach_button)
    private val editTextRightPadding = resources.getDimensionPixelSize(RDesign.dimen.input_text_field_right_padding)
    private val editTextRightPaddingWithRecorder = resources.getDimensionPixelSize(R.dimen.message_panel_input_field_right_padding_with_recorder)

    fun setMessageContainerMargins(attachButton: View?, messageContainer: View) {
        val leftMargin = if (attachButton?.isVisible == true) {
            0
        } else {
            messageContainerLeftMarginWithoutAttachButton
        }
        with(messageContainer.layoutParams as RelativeLayout.LayoutParams) {
            updateMargins(left = leftMargin)
        }
    }

    /**
     * Задаёт внутренний отступ справа у поля ввода, в зависимости от наличия [RecorderView]
     */
    fun setEditTextPadding(editText: EditText, hasRecorder: Boolean) {
        val paddingRight = if (hasRecorder) editTextRightPaddingWithRecorder else editTextRightPadding
        editText.updatePadding(right = paddingRight)
    }
}