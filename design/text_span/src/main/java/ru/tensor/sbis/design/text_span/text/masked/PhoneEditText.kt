package ru.tensor.sbis.design.text_span.text.masked

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.util.AttributeSet
import ru.tensor.sbis.design.text_span.R

import ru.tensor.sbis.design.text_span.text.masked.formatter.phone.COMMON_PHONE_MASK
import ru.tensor.sbis.design.text_span.text.masked.formatter.phone.asTransformationMethod
import ru.tensor.sbis.design.text_span.text.masked.formatter.phone.createPhoneFormatter
import ru.tensor.sbis.design.text_span.text.masked.mask.MaskInputFilter
import ru.tensor.sbis.design.text_span.text.masked.mask.MaskParser
import ru.tensor.sbis.design.text_span.text.masked.phone.PhoneLengthFilter
import ru.tensor.sbis.design.text_span.text.masked.phone.RU_FORMAT
import ru.tensor.sbis.design.text_span.text.util.NonEditableTextWordWrapFix

/**
 * Поле ввода для номера телефона.
 * Текст маскируется динамически по стандарту ["Поле для ввода телефонного номера"](http://axure.tensor.ru/standarts/v7/%D0%BF%D0%BE%D0%BB%D0%B5_%D0%B2%D0%B2%D0%BE%D0%B4%D0%B0__%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D1%8F_3_1_.html).
 * К типу поля ввода автоматически применяется обязательный флаг [InputType.TYPE_CLASS_PHONE]
 *
 * Перечень отступлений от стандарта:
 * - TODO: 2/26/2019 [Форматируются не только российские номера, которые начинаются с 8](https://online.sbis.ru/opendoc.html?guid=a6e2d5f6-f738-488b-81c2-e2b7ff61f12a)
 * - [Номер с ограничением длины](https://online.sbis.ru/opendoc.html?guid=874c4ff5-8543-441c-8ba4-24e43e8b1384)
 *
 * @author ma.kolpakov
 * Создан 2/26/2019
 */
class PhoneEditText(
    context: Context,
    attrs: AttributeSet
) : AbstractMaskEditText(context, attrs, com.google.android.material.R.attr.editTextStyle) {

    private var decorationAttr = RU_FORMAT

    init {
        if (isEnabled) {
            inputType = inputType or InputType.TYPE_CLASS_PHONE
        }

        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.PhoneEditText, 0, 0)

        /**
         * получим флаг декорирования отображения содержимого поля ввода, для совместимости по умолчанию форматрирование для
         * российских телефонов
         */
        decorationAttr = attributes.getInteger(R.styleable.PhoneEditText_PhoneEditText_decoration, RU_FORMAT)

        attributes.recycle()

        // подготовка форматтера
        setFormatter()

        // установка фильтров ввода
        filters = arrayOf(
            PhoneLengthFilter(decorationAttr),
            MaskInputFilter(MaskParser(COMMON_PHONE_MASK).types),
            FirstSymbolInputFilter()
        )

        // предотвращение некорректного переноса номера на новую строку при отображении без возможности редактирования
        NonEditableTextWordWrapFix().preventNonEditableTextWordWrap(this)
    }

    /**
     * Задаёт формат для ввода рабочего телефона. Отличается от используемого по умолчанию тем, что номера короче пяти
     * символов не форматируются
     *
     * @param isWorkPhone должно ли использоваться форматирование для рабочего телефона
     */
    @Suppress("unused")
    fun setWorkPhoneFormat(isWorkPhone: Boolean) {
        setFormatter(isWorkPhone)
    }

    override fun validateInputType() {
        // при отсутствии возможности редактирования допускается произвольный inputType
        if (!isEnabled) return
        super.validateInputType()
        if (inputType and InputType.TYPE_CLASS_PHONE == 0) {
            throw IllegalArgumentException("Flag android.text.InputType.TYPE_CLASS_PHONE required")
        }
    }

    private fun setFormatter(isWorkPhone: Boolean = false) {
        val formatter = createPhoneFormatter(decorationAttr, isWorkPhone)

        // установка подписки на ввод для форматирования
        transformationMethod = formatter.asTransformationMethod()
    }

    /**
     * Фильтр применяется только на первый элемент в поле ввода. Он разрешает вводить цифру или +
     */
    private class FirstSymbolInputFilter : InputFilter {

        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            return when {
                source == null || dstart != 0 -> null
                else -> source.getOrNull(0)?.let {
                    if (it.isDigit() || it == '+') null else source.subSequence(1..source.lastIndex)
                }
            }
        }
    }
}