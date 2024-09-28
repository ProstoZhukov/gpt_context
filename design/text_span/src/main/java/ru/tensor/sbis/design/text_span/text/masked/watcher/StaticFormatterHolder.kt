package ru.tensor.sbis.design.text_span.text.masked.watcher

import android.text.Editable
import android.text.TextWatcher
import ru.tensor.sbis.design.text_span.text.masked.formatter.StaticFormatter

/**
 * Реализация [TextWatcher], которая применяет форматирование [formatter] с заданной статической маской во время
 * пользовательского ввода
 *
 * @param formatter отвечает за форматирование пользовательского ввода
 *
 * @author us.bessonov
 */
class StaticFormatterHolder(
    private val formatter: StaticFormatter
) : TextWatcher {

    private var isApplyingFormat = false

    private var oldText: CharSequence = ""

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        doIfNotApplyingFormat {
            oldText = s.substring(start, start + count)
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        doIfNotApplyingFormat {
            val newText = s.substring(start, start + count)
            formatter.onTextBlockChanged(start, oldText, newText)
        }
    }

    override fun afterTextChanged(s: Editable?) = applyMask(s)

    private fun applyMask(s: Editable?) {
        s ?: return
        doIfNotApplyingFormat {
            isApplyingFormat = true
            formatter.apply(s)
            isApplyingFormat = false
        }
    }

    private inline fun doIfNotApplyingFormat(action: () -> Unit) {
        if (!isApplyingFormat) action()
    }
}