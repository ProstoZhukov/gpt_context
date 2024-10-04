package ru.tensor.sbis.design.text_span.text.masked

import android.content.Context
import android.util.AttributeSet
import ru.tensor.sbis.design.text_span.R
import ru.tensor.sbis.design.text_span.text.masked.formatter.StaticFormatterImpl
import ru.tensor.sbis.design.text_span.text.masked.watcher.StaticFormatterHolder
import timber.log.Timber

/**
 * Поле ввода со встроенной статической маской.
 *
 * @author ma.kolpakov
 */
class StaticMaskEditText(
    context: Context,
    attrs: AttributeSet
) : AbstractMaskEditText(context, attrs, com.google.android.material.R.attr.editTextStyle) {

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.StaticMaskEditText, com.google.android.material.R.attr.editTextStyle, 0
        )
        val mask = typedArray.getString(R.styleable.StaticMaskEditText_mask)
        typedArray.recycle()

        mask?.run {
            StaticFormatterImpl(mask, text).run(::StaticFormatterHolder).let(::addTextChangedListener)
        } ?: Timber.w(IllegalStateException("You should use EditText for cases without mask"))
    }
}