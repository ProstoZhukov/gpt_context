package ru.tensor.sbis.design.view.input.password

import android.text.method.PasswordTransformationMethod
import android.view.View

/**
 * Задает скрытие ввода текста точками
 *
 * @author ps.smirnyh
 */
internal class HidePasswordTransformationMethod : PasswordTransformationMethod() {

    override fun getTransformation(source: CharSequence, view: View) =
        PasswordCharSequence(source)

    inner class PasswordCharSequence(private val source: CharSequence) : CharSequence {

        override val length: Int
            get() = source.length

        override fun get(index: Int): Char = '•'

        override fun subSequence(startIndex: Int, endIndex: Int) =
            source.subSequence(startIndex, endIndex)
    }
}