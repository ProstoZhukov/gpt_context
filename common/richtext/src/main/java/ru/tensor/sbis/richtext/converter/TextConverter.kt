package ru.tensor.sbis.richtext.converter

import android.text.Spannable

interface TextConverter {

    /**
     * Конвертирует текст с набором тегов в spannable строку
     */
    fun convert(source: String): Spannable

}