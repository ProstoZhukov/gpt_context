package ru.tensor.sbis.design.view.input.password.api

import android.util.AttributeSet
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.utils.UpdateState
import ru.tensor.sbis.design.view.input.password.HidePasswordTransformationMethod
import ru.tensor.sbis.design.view.input.password.ShowHideToggleClickListener
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewController
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewControllerApi

/**
 * Класс для управления состоянием и внутренними компонентами поля ввода пароля.
 *
 * @author ps.smirnyh
 */
internal class PasswordInputViewController(
    private val singleLineInputViewController: SingleLineInputViewController = SingleLineInputViewController()
) : SingleLineInputViewControllerApi by singleLineInputViewController,
    PasswordInputViewControllerApi {

    override lateinit var showHideToggleClickListener: ShowHideToggleClickListener

    override var isSecureMode = false
        set(value) {
            if (field == value) return
            field = value
            if (value) {
                val startSelection = inputView.selectionStart
                inputView.transformationMethod = HidePasswordTransformationMethod()
                inputView.setSelection(startSelection)
            } else {
                showHideToggleClickListener.updateVisibility(iconView)
            }
            updatePropertyCallback.onChange()
        }

    override fun attach(
        baseInputView: BaseInputView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        singleLineInputViewController.attach(baseInputView, attrs, defStyleAttr, defStyleRes)
        updatePropertyCallback = UpdateState(::updateOnPropertiesChanged)
        updateFocusCallback = UpdateState {
            updateOnFocusChanged(inputView.isFocused)
        }
        iconView.configure {
            text = SbisMobileIcon.Icon.smi_unread.character.toString()
            isVisible = false
        }
        showHideToggleClickListener = ShowHideToggleClickListener(baseInputView)
        // изначальная синхронизация состояния
        showHideToggleClickListener.updateVisibility(iconView)
        iconView.setOnClickListener(showHideToggleClickListener)
    }

    override fun updateOnPropertiesChanged(): Boolean {
        val visibilityPropertiesChanged = updateInternalVisibility(isProgressVisible, isClearVisible, readOnly)
        val superPropertyChanged = singleLineInputViewController.updateOnPropertiesChanged()
        return if (visibilityPropertiesChanged || superPropertyChanged) {
            baseInputView.safeRequestLayout()
            true
        } else {
            false
        }
    }

    override fun updateInternalVisibility(
        isProgressVisible: Boolean,
        isClearVisible: Boolean,
        isReadOnly: Boolean
    ) = iconView.configure(checkDiffs = true) {
        isVisible = !isProgressVisible && !isSecureMode && value.isNotEmpty()
    }

    override fun updateOnFocusChanged(isFocus: Boolean) {
        singleLineInputViewController.updateOnFocusChanged(isFocus)
        if (isFocus && !isSecureMode) {
            showHideToggleClickListener.updateVisibility(iconView)
        }
    }
}