package ru.tensor.sbis.design.view.input.accesscode

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatEditText

/**
 * Логика ввода символов по фиксированной маске.
 *
 * @property inputView поле ввода.
 * @property decorationHelper помощник для декорирования маски.
 *
 * @author mb.kruglova
 */
internal class AccessCodeInputViewWatcher(
    private val inputView: AppCompatEditText,
    private val decorationHelper: AccessCodeInputViewDecorationHelper,
    private val permanentPart: String
) : TextWatcher {

    /**
     * Позиция курсора в изменяемой части маски.
     */
    internal var cursorPosition: Int = START_POSITION

    /**
     * Позиция символа.
     */
    private var charPosition: Int = START_POSITION

    /**
     * Блокировка рекурсии.
     */
    private var isRecursiveAfterTextChangedCall = false

    /**
     * Выполняется ли операция удаления символа.
     */
    private var isDeleting = false

    /**
     * Входной код
     */
    private var inputCode = ""

    /**
     * Новая введенная последовательность символов.
     */
    private var newText: CharSequence = ""

    /**
     * Старая удаленная последовательность символов.
     */
    private var oldText: CharSequence = ""

    /**
     * Слушатель достижения максимальной длины вводимого текста.
     */
    internal var maxLengthReachedListener: ((String) -> Unit)? = null

    init {
        // Позиция курсора не может быть изменена пользователем,
        // а изменяется в соответствии с количеством введенных/удаленных символов
        inputView.setOnClickListener {
            inputView.setSelection(cursorPosition)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (s == null || isRecursiveAfterTextChangedCall) return

        oldText = s.substring(start, start + count)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s == null || isRecursiveAfterTextChangedCall) return

        newText = s.substring(start, start + count)
        isDeleting = count == 0
        charPosition = start
        cursorPosition = charPosition
    }

    override fun afterTextChanged(s: Editable?) {
        if (s == null || isRecursiveAfterTextChangedCall) return

        isRecursiveAfterTextChangedCall = true

        val newInputText = when {
            isDeleting && s.toString().startsWith(permanentPart) -> {
                if (inputCode.length + getBlankPartLength(charPosition) > CODE_LENGTH) {
                    inputCode.substring(0, CODE_LENGTH - getBlankPartLength(charPosition))
                } else {
                    inputCode
                }
            }

            inputCode.isNotEmpty() && charPosition >= START_POSITION &&
                inputCode.length + getBlankPartLength(charPosition) > CODE_LENGTH -> {
                inputCode.substring(0, CODE_LENGTH - getBlankPartLength(charPosition)) + newText
            }

            s.isEmpty() -> newText.toString()
            else -> inputCode + newText
        }

        updateInputCode(newInputText)

        // приводим к 4 знакам
        var newFormalText = when {
            newInputText.isEmpty() -> BLANK_INPUT_TEXT
            newInputText.length < CODE_LENGTH -> {
                val length = CODE_LENGTH - newInputText.length
                var inputText: String = newInputText
                for (i in 1..length) {
                    inputText += ZERO
                }
                inputText
            }

            newInputText.length == CODE_LENGTH -> newInputText
            else -> newInputText.subSequence(0, CODE_LENGTH)
        }

        // добавляем тире
        newFormalText = newFormalText.substring(0, 2) + DASH + newFormalText.substring(2, 4)

        val inputValue = SpannableStringBuilder(permanentPart + newFormalText)

        val position =
            permanentPart.length + if (inputCode.length > 2) inputCode.length + 1 else inputCode.length

        setDecoration(inputValue, position)
        inputView.text = inputValue

        setCursorPosition(permanentPart.length + if (inputCode.length > 2) inputCode.length + 1 else inputCode.length)

        isRecursiveAfterTextChangedCall = false

        if (cursorPosition == END_POSITION) {
            maxLengthReachedListener?.invoke(
                inputView.text?.substring(START_POSITION, DASH_POSITION - 1) +
                    inputView.text?.substring(DASH_POSITION, END_POSITION)
            )
        }
    }

    /**
     * Метод для настройки цвета текста.
     */
    private fun setDecoration(s: Editable, position: Int) {
        decorationHelper.setPermanentMaskColorSpan(s, 0, START_POSITION)
        decorationHelper.setFilledMaskColorSpan(s, START_POSITION, position)
        decorationHelper.setBlankMaskColorSpan(s, position, END_POSITION)
    }

    /**
     * Метод для настройки позиции курсора.
     */
    private fun setCursorPosition(position: Int) {
        cursorPosition = position
        inputView.setSelection(cursorPosition)
    }

    /**
     * Метод для обновления входных данных.
     */
    private fun updateInputCode(newCode: String) {
        inputCode = if (newCode.length > CODE_LENGTH) newCode.substring(0, CODE_LENGTH) else newCode
    }

    /**
     * Метод для получения длины оставшихся символов по позиции курсора.
     */
    private fun getBlankPartLength(position: Int): Int = when {
        position <= START_POSITION -> CODE_LENGTH
        position < DASH_POSITION -> END_POSITION - position - 1
        position < END_POSITION -> END_POSITION - position
        else -> 0
    }

    companion object {
        const val START_POSITION = 13
        const val DASH_POSITION = 16
        const val END_POSITION = 18
        const val CODE_LENGTH = 4

        const val BLANK_INPUT_TEXT = "0000"
        const val ZERO = "0"
        const val DASH = "-"
    }
}