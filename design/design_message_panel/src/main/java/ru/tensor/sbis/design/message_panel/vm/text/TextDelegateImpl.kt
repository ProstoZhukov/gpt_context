package ru.tensor.sbis.design.message_panel.vm.text

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

/**
 * @author ma.kolpakov
 */
internal class TextDelegateImpl @Inject constructor() : TextDelegate {

    override val text = MutableStateFlow("")

    override fun setText(newText: String) {
        text.value = newText
    }
}
