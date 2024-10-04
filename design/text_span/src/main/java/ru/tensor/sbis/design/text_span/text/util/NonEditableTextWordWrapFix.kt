package ru.tensor.sbis.design.text_span.text.util

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.widget.EditText
import ru.tensor.sbis.design.text_span.span.CharSequenceSpan
import ru.tensor.sbis.design.text_span.text.masked.PhoneEditText

/**
 * Позволяет исправить проблему, специфичную для длинного номера телефона в многострочном [PhoneEditText], для которого
 * [CharSequenceSpan] применяется только в начале строки.
 * Чтобы текст при использовании для отображения номера без возможности редактирования не переносился на новую
 * строку, если место для него ещё есть, для каждого символа до конца строки применяется пустой [CharSequenceSpan],
 * после последнего уже имеющегося применения
 *
 * @author ve.arefev
 */
internal class NonEditableTextWordWrapFix : TextWatcher {

    private lateinit var editText: EditText

    /**
     * Предотвращение некорректного переноса номера на новую строку при отображении без возможности редактирования.
     */
    fun preventNonEditableTextWordWrap(editText: EditText) {
        this.editText = editText
        editText.addTextChangedListener(this)
    }

    override fun afterTextChanged(p0: Editable?) {
        if (editText.isEnabled) return
        editText.removeTextChangedListener(this)
        with(p0 as SpannableStringBuilder) {
            val charSequenceSpans = getSpans(0, length, CharSequenceSpan::class.java)
            charSequenceSpans
                .map { getSpanEnd(it) }
                .maxOrNull()
                ?.let { lastSpanEnd ->
                    (lastSpanEnd until length).forEach { i ->
                        setSpan(CharSequenceSpan(""), i, i + 1, Spanned.SPAN_POINT_MARK)
                    }
                }
        }
        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}