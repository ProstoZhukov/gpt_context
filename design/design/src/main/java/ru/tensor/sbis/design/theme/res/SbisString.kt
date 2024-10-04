package ru.tensor.sbis.design.theme.res

import android.content.Context

/**
 * Текстовая модель.
 *
 * @author da.zolotarev
 */

interface SbisString {

    /**
     * Получить [CharSequence].
     */
    fun getCharSequence(context: Context): CharSequence

    /**
     * Получить [String].
     */
    fun getString(context: Context): String = getCharSequence(context).toString()
}
