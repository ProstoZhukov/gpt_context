package ru.tensor.sbis.design.message_panel.domain.common

import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel

/**
 * Общая логика по очистке панели ввода
 *
 * @author ma.kolpakov
 */
internal class CleanupUseCase(
    private val vm: MessagePanelViewModel
) {

    operator fun invoke() {
        vm.setText("")
        vm.clearAttachments()
        vm.clearQuote()
    }
}