package ru.tensor.sbis.design.view.input.selection.api

import android.util.AttributeSet
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.utils.UpdateState
import ru.tensor.sbis.design.view.input.selection.ValueSelectionInputView
import ru.tensor.sbis.design.view.input.selection.utils.style.ValueSelectionStyleHolder
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewController
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewControllerApi

/**
 * Класс для управления состоянием и внутренними компонентами поля ввода с выбором значения.
 *
 * @author ps.smirnyh
 */
internal class ValueSelectionInputViewController(
    private val singleLineInputViewController: SingleLineInputViewController = SingleLineInputViewController()
) : SingleLineInputViewControllerApi by singleLineInputViewController, ValueSelectionInputViewApi {

    private val valueSelectionStyleHolder = ValueSelectionStyleHolder()

    override var onListIconClickListener: ((ValueSelectionInputView) -> Unit)? = null
        set(value) {
            field = value
            value?.let { iconClickListener ->
                iconView.setOnClickListener { _, _ ->
                    iconClickListener.invoke(
                        baseInputView as ValueSelectionInputView
                    )
                }
            } ?: iconView.setOnClickListener(null)
        }

    override var iconText: CharSequence
        get() = iconView.text
        set(value) {
            iconView.buildLayout {
                text = value
            }
            baseInputView.invalidate()
        }

    override var isIconVisible = true
        set(value) {
            field = value
            updatePropertyCallback.onChange()
        }

    override var onHideKeyboard: Boolean = true
        set(_) = Unit // Никогда не показываем клавиатуру

    override fun attach(
        baseInputView: BaseInputView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        singleLineInputViewController.attach(baseInputView, attrs, defStyleAttr, defStyleRes)
        updatePropertyCallback = UpdateState(::updateOnPropertiesChanged)
        valueSelectionStyleHolder.loadStyle(baseInputView.context, attrs, defStyleAttr, defStyleRes)
        iconText = valueSelectionStyleHolder.property.iconText
        isIconVisible = valueSelectionStyleHolder.property.isIconVisible
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
        isVisible = !(isProgressVisible || !isIconVisible || isReadOnly)
    }
}