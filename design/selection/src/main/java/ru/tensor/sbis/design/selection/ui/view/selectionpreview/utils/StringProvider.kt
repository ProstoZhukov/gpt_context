package ru.tensor.sbis.design.selection.ui.view.selectionpreview.utils

import android.content.Context
import androidx.annotation.StringRes

/**
 * Позволяет получить строку с заданным id ресурса
 *
 * @author us.bessonov
 */
internal class StringProvider(context: Context) {

    private val context = context.applicationContext

    /**
     * Возвращает строку с форматированием для заданного ресурса
     */
    fun getFormattedString(@StringRes resId: Int, vararg formatArgs: Any) = context.getString(resId, *formatArgs)

    /**
     * Возвращает строку для заданного ресурса
     */
    fun getString(@StringRes resId: Int) = context.getString(resId)

    /**
     * Возвращает текст для заданного ресурса.
     * В отличии от [getString] возвращает [CharSequence]
     */
    fun getText(@StringRes resId: Int) = context.getText(resId)
}