package ru.tensor.sbis.design.text_span.text.masked

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter
import ru.tensor.sbis.design.text_span.text.masked.mask.MaskInputFilter

/**
 * Базовая реализация маскированного поля ввода, к его типу автоматически применяется обязательный
 * флаг [InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS]. Для реализации форматирования можно использовать
 * реализации [Formatter], а для фильтрации ввода - реализации [InputFilter] (напрмер, [MaskInputFilter])
 *
 * @author ma.kolpakov
 * Создан 4/1/2019
 */
abstract class AbstractMaskEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = com.google.android.material.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        inputType = inputType or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }

    override fun setRawInputType(type: Int) {
        super.setRawInputType(type)
        validateInputType()
    }

    /**
     * Проверка типа ввода.
     * Тип ввода должен содержать флаг {@link android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS},
     * иначе маска будет применяться некорректно.
     *
     * При переопределении нужно обязательно вызвать родительскую реализацию
     */
    protected open fun validateInputType() {
        if (inputType and InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS == 0) {
            throw IllegalArgumentException("Flag android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS required")
        }
    }
}