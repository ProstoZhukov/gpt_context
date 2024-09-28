package ru.tensor.sbis.design.retail_views.common.text_watcher

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import ru.tensor.sbis.design.retail_views.utils.format

/**
 * Реализация TextWatcher для RetailInputFieldView
 */
@SuppressLint("SetTextI18n")
open class DecimalTextWatcher(
    private val editText: EditText,
    private val decimalFormat: DecimalFormat,
    private val decimalPartSize: Int,
    private val integerPartMaxLength: Int?
) : TextWatcher {
    /**
     * Флаг чтобы предотвратить дальнейшие проверки в TextWatcher,
     * на случай если эти изменения были переписаны в ходе предыдущих этапов этой же проверки
     */
    private var ignoreChanges = false

    /**
     * Флаг, определяющий имеет ли поле какое - нибудь значение по умолчанию.
     * Используется в ситуации, когда значения по умолчанию нет, иначе игнорируется.
     * Используется, чтобы установить курсор на разделитель при первом использовании поля(иначе курсор "перепрыгнет" в конец строки).
     */
    private var isFirstInput = editText.text.isEmpty()

    /**
     * Флаг, определяющий произошло ли форматирование текста после очередного пользовательского ввода.
     * Нужен, чтобы проигнорировать вызовы [beforeTextChanged] и [onTextChanged], а так же служит индикатором,
     * что нужно пересчитать позицию курсора(форматирование добавляет в строку символы, и нужно это учесть)
     */
    private var isTextFormattingHappened = false

    /**
     * Позиция, на которую будет установлен курсор после того как было применено форматирование.
     * Если после форматирования длинна строки не изменилась, то [position] == editText.selectionStart.
     * В ином случае [position] изменяется на разницу между новой и старой длинной строки.
     */
    private var position: Int = 0

    /**
     * @SelfDocumented
     */
    private fun String.indexOfPoint() = indexOf('.')

    /**
     * @SelfDocumented
     */
    private fun String.indexOfPoint(startIndex: Int) = indexOf('.', startIndex)

    /**
     * @SelfDocumented
     */
    private fun String.startWithPoint() = startsWith('.')

    /**
     * @SelfDocumented
     */
    private fun String.isIndexOfPoint(index: Int) = this[index] == '.'

    /**
     * @SelfDocumented
     */
    private fun String.isIndexOfWhiteSpace(index: Int) = this[index] == ' '

    /**
     * Проверяет, есть ли в начале цены не несущий смысла "0"
     */
    private fun String.startsWithUnusedZero() = startsWith("0") && indexOfPoint() > 1

    /**
     * Удаляет не несущий смысла "0"
     */
    private fun String.removeUnusedZero() = replaceFirst(Regex("0"), "")

    /**
     * Удаляет не несущий смысла "0"
     */
    private fun String.removeExtraSymbolsFromDecimalPart(start: Int) =
        replaceRange(IntRange(start, start), "")

    /**
     * Возвращает true, если введённый пользователем символ попадает в decimalPartSize диапозон
     * Нужен для определения ситуации, когда пользователь пытается ввести недопустимое количество символов после разделителя
     */
    private fun String.isNewCharInDecimalPartSize(start: Int, count: Int) = length > start + count

    /**
     * Возвращает количество точек в строке
     */
    private fun String.countPoints() = count { it == '.' }

    /**
     * Восстанавливает точку в строке по индексу точки
     */
    private fun String.restorePointByPointIndex(pointIndex: Int) =
        "${substring(0, pointIndex)}.${substring(pointIndex + 1, length)}"

    /**
     * Восстанавливает "0", если пользователь стёр символ, стоящий после разделителя
     * indexOfPoint - позиция разделителя
     */
    private fun String.restoreDecimal(indexOfPoint: Int): String {
        var updatedText = "${substring(0, indexOfPoint)}0"

        // Если после удаленного символа есть еще символы то прибавляем их тоже
        if (length >= indexOfPoint + 1) {
            updatedText = "$updatedText${substring(indexOfPoint, length)}"
        }

        return updatedText
    }

    /**
     * Проверяет, отформатирован ли текст посредством сравнения с отформатированной строкой
     */
    private fun String.isTextFormatted(formattedPrice: String): Boolean = equals(formattedPrice)

    /**
     * Установка курсора.
     * В случае с [isFirstInput] == true, устанавливает курсор перед разделителем.
     */
    private fun setSelectionOnFormattedText() {
        if (isFirstInput) {
            editText.setSelection(1)
            isFirstInput = false
            return
        }

        // При быстром пользовательском вводе редко, но возможна ситуация
        // когда position занулится
        if (position >= 0) {
            editText.setSelection(position)
        }
    }

    override fun afterTextChanged(editableText: Editable?) {
        if (editableText.isNullOrEmpty() || ignoreChanges) return

        val text = editableText.toString()

        if (isTextFormattingHappened) {
            setSelectionOnFormattedText()
        }

        isTextFormattingHappened = false

        if (text.startsWithUnusedZero()) {
            editText.setText(text.removeUnusedZero())
            editText.setSelection(1)
            return
        }

        if (text.startWithPoint()) {
            editText.setText("0$editableText")
            return
        }

        val formattedPrice = format(text, decimalFormat)
        if (!text.isTextFormatted(formattedPrice)) {
            isTextFormattingHappened = true
            editText.setText(formattedPrice)
        }

        // Чистим флаг, если он не понадобился при первом прогоне
        isFirstInput = false
    }

    override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
        ignoreChanges = false

        if (isTextFormattingHappened) {
            val diff = when {
                count != after -> after - count
                else -> 0
            }
            position = editText.selectionStart + diff
            return
        }

        if (charSequence.isNullOrEmpty()) isFirstInput = true

        if (charSequence.isNullOrEmpty() || count <= after) return

        val text = charSequence.toString()

        // Восстанавливаем разделитель если пользователь пытается его удалить.
        // Срабатывает при условии, что в поле присутствует только один разделитель
        if (text.isIndexOfPoint(start) && text.countPoints() <= 1) {
            editText.setText(text.restorePointByPointIndex(start))
            editText.setSelection(start)
            ignoreChanges = true
            return
        }

        // Если жмем "backspace" перед разделителем разряда, то удаляем цифру слева от разделителя.
        // Если разделитель стоит первым символом в строке, то просто удаляем его
        if (start != 0 && text.isIndexOfWhiteSpace(start)) {
            val updatedText = text.removeRange(start - 1, start + 1)
            editText.setText(updatedText)
            editText.setSelection(start - 1)
            ignoreChanges = true
        }
    }

    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
        if (charSequence.isNullOrEmpty() || ignoreChanges || isTextFormattingHappened) return

        val text = charSequence.toString()

        // text.countPoints() > 1 означает, что юзер добавляет разделитель.
        // Убираем введённый разделитель и переносим курсор текстового поля к уже имеющемуся "правильному" разделителю
        if (text.countPoints() > 1) {
            editText.setText(text.removeRange(start, start + 1))

            var indexOfAllowedPoint = text.indexOfPoint()
            if (indexOfAllowedPoint == start) {
                indexOfAllowedPoint = text.indexOfPoint(indexOfAllowedPoint + 1)
            }

            val newSelectionIndex = if (indexOfAllowedPoint > start) {
                indexOfAllowedPoint
            } else {
                indexOfAllowedPoint + 1
            }
            editText.setSelection(newSelectionIndex)
            ignoreChanges = true
            return
        }

        // Определяем, принадлежит ли введённый символ decimalPartSize части поля(символы после точки)
        // Если да, то мы должны заменить уже имеющийся символ(который находится на позиции start + count) новым символом, чтобы не выйти за границы decimalPartSize.
        // В случае если введённый символ оказывается за пределами decimalPartSize части, мы должны просто удалить его.
        if (text.indexOfPoint() != -1 &&
            start > text.indexOfPoint() && text.length - 1 > text.indexOfPoint() + decimalPartSize
        ) {
            if (text.isNewCharInDecimalPartSize(start, count)) {
                editText.setText(text.removeExtraSymbolsFromDecimalPart(start + count))
                editText.setSelection(start + count)
            } else {
                editText.setText(text.removeExtraSymbolsFromDecimalPart(start))
                editText.setSelection(start)
            }
            ignoreChanges = true
            return
        }

        // Если пользователь удалил символ после разделителя, надо на место символа восстановить "0"
        if (text.indexOfPoint() != -1 && before > 0 && start > text.indexOfPoint()) {
            editText.setText(text.restoreDecimal(start))
            editText.setSelection(start)
            ignoreChanges = true
            return
        }

        // Если был выделен весь текст, введенная цифра заменяет текущее значение
        // Если был введен разделитель, то значение в поле ввода сбрасывается и курсор устанавливается после разделителя
        val isAllTextSelected = editText.text.length == editText.selectionEnd - start
        if (isAllTextSelected) {
            val formattedText: String
            val cursorPosition: Int
            if (text.startWithPoint()) {
                formattedText = format("0", decimalFormat)
                cursorPosition = 2
            } else {
                formattedText = format(text, decimalFormat)
                cursorPosition = 1
            }
            editText.setText(formattedText)
            editText.setSelection(cursorPosition)
            ignoreChanges = true
            return
        }

        // Если указано максимальное значение длины целой части числа, ввод цифр в целой части ограничивается этим значением.
        // При переполнении целой части введённая цифра удаляется, курсор возвращается в прежнюю позицию
        integerPartMaxLength?.let {
            val indexOfPoint = text.indexOfPoint()
            if (indexOfPoint != -1 && indexOfPoint > integerPartMaxLength) {
                editText.setText(text.removeRange(start, start + 1))
                editText.setSelection(start)
                ignoreChanges = true
                return
            }
        }
    }
}