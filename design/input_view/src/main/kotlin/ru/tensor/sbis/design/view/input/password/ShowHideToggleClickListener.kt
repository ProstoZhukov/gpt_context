package ru.tensor.sbis.design.view.input.password

import android.content.Context
import android.view.inputmethod.EditorInfo
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.view.input.base.BaseInputView

/**
 * Слушатель для переключателя показа или скрытия пароля.
 * @property baseInputView базовый класс поле ввода.
 *
 * @author ps.smirnyh
 */
internal class ShowHideToggleClickListener(
    private val baseInputView: BaseInputView
) : TextLayout.OnClickListener {

    /**
     * true если пароль видимый, false - невидимый.
     */
    private var isVisible: Boolean = false

    override fun onClick(context: Context, layout: TextLayout) {
        isVisible = !isVisible
        updateVisibility(layout)
    }

    internal fun updateVisibility(layout: TextLayout) {
        val inputView = baseInputView.inputView
        val (inputType, iconViewText) = if (isVisible) {
            EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD to
                SbisMobileIcon.Icon.smi_unread.character.toString()
        } else {
            EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD to
                SbisMobileIcon.Icon.smi_eyeBlack.character.toString()
        }
        val lastCursorPosition = inputView.selectionStart
        val typeface = inputView.typeface
        inputView.inputType = inputType
        inputView.typeface = typeface
        inputView.setSelection(lastCursorPosition)
        if (layout.configure(checkDiffs = true) {
                text = iconViewText
            }
        ) {
            baseInputView.safeRequestLayout()
        }
    }
}