package ru.tensor.sbis.design.view.input.mask

import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.CallSuper

/**
 * Базовый тип для логики ввода символов по маске, где 0 - число, А - буква, * - любой символ. Могут отличаться нюансы
 * применения маски к тексту, поэтому этот тип существует, и в подклассах необходимо реализовать свой алгоритм
 * замены/добавления символов в [afterTextChanged].
 * @property mask маска.
 *
 * @author ps.smirnyh
 */
abstract class BaseMaskInputViewTextWatcher internal constructor(
    var mask: String
) : TextWatcher {

    /**
     * Блокировка рекурсии.
     */
    protected var isRecursiveAfterTextChangedCall = false

    protected var isDeleting = false

    protected var oldText: CharSequence = ""
    protected var newText: CharSequence = ""
    protected var replacementStart = 0

    final override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        doIfNotApplyingFormat {
            isDeleting = count > after
            oldText = s?.substring(start, start + count) ?: ""
        }
    }

    final override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        doIfNotApplyingFormat {
            val newText = s?.substring(start, start + count) ?: ""
            onTextBlockChanged(start, oldText, newText)
        }
    }

    /**
     * Изменить существующую маску на новую.
     *
     * @param newMask новый шаблон маски.
     * @param editable [Editable], на котором надо маску применить.
     */
    @CallSuper
    internal open fun changeMask(newMask: String, editable: Editable) {
        if (mask == newMask) return
        mask = newMask
        replacementStart = 0
        newText = editable.toString()
    }

    protected fun Char.isSpecialForMask() =
        this == MASK_LETTER_CHAR || this == MASK_DIGIT_CHAR || this == MASK_ANY_CHAR

    protected inline fun doIfNotApplyingFormat(action: () -> Unit) {
        if (!isRecursiveAfterTextChangedCall) action()
    }

    private fun onTextBlockChanged(start: Int, old: CharSequence, new: CharSequence) {
        replacementStart = start
        oldText = old
        newText = new
    }

    protected companion object {
        const val MASK_ANY_CHAR = '*'
        const val MASK_DIGIT_CHAR = '0'
        const val MASK_LETTER_CHAR = 'A'
    }
}