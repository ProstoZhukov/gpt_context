package ru.tensor.sbis.design.text_span.span

import android.text.style.ClickableSpan
import android.view.View
import ru.tensor.sbis.design.utils.DebounceActionHandler

/**
 * Span для обработки повторяющихся кликов пользователя.
 * Обрабатывает только первый клик за промежуток времени, остальные пропускает
 *
 * @author am.boldinov
 */
abstract class DebounceClickableSpan : ClickableSpan() {

    private var actionHandler: DebounceActionHandler? = null

    final override fun onClick(widget: View) {
        if (actionHandler == null) {
            actionHandler = DebounceActionHandler()
        }
        if (actionHandler!!.enqueue()) {
            onDebounceClick(widget)
        }
    }

    abstract fun onDebounceClick(widget: View)
}