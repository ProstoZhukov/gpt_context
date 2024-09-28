package ru.tensor.sbis.message_panel.viewModel.livedata.hint

import androidx.annotation.StringRes
import ru.tensor.sbis.design.message_panel.R as RMPDesign

/**
 * Настройка вариантов подсказок для различных состояний панели ввода. Оформлено отдельным объектом по причинам:
 * - значения получаются из стилей панели ввода
 * - значения могут получаться из интегрируемых компонентов (панель аудиозаписи)
 * - не должно быть жёсткой связи с машиной состояний
 *
 * @author vv.chekurda
 */
data class MessagePanelHintConfig(
    @StringRes val disabledStateHint: Int = DEFAULT_HINT,
    @StringRes val enabledStateHint: Int = DEFAULT_HINT
) {

    internal companion object {
        @StringRes
        val DEFAULT_HINT = RMPDesign.string.design_message_panel_enter_message_hint
    }
}
