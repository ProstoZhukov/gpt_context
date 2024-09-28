package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.ui

import android.view.View.OnFocusChangeListener
import com.arkivanov.mvikotlin.core.view.BaseMviView
import ru.tensor.sbis.communicator.themes_registry.databinding.CommunicatorFragmentLinkAdditionBinding
import ru.tensor.sbis.design.header.BaseHeader
import ru.tensor.sbis.design.utils.KeyboardUtils

/**
 * Реализация View содержимого экрана добавления ссылки.
 *
 * @author dv.baranov
 */
internal class LinkAdditionViewImpl(
    binding: CommunicatorFragmentLinkAdditionBinding,
    private val header: BaseHeader?
) : BaseMviView<LinkAdditionView.Model, LinkAdditionView.Event>(),
    LinkAdditionView {

    init {
        binding.communicatorAdditionLinkInput.apply {
            header?.setAcceptButtonEnabled(isEnabled(value))
            onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
                view?.let {
                    if (hasFocus) {
                        KeyboardUtils.showKeyboard(it)
                    } else {
                        KeyboardUtils.hideKeyboard(it)
                    }
                }
            }
            onValueChanged = { _, value ->
                dispatch(LinkAdditionView.Event.InputValueChanged(value))
                header?.setAcceptButtonEnabled(isEnabled(value))
            }
            requestFocus()
        }
        header?.addAcceptListener {
            dispatch(LinkAdditionView.Event.SaveLink)
        }
    }

    private fun isEnabled(value: CharSequence) = value.isNotEmpty() && value.isNotBlank()
}