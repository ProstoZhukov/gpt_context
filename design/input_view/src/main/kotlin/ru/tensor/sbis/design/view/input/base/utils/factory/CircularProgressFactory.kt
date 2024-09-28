package ru.tensor.sbis.design.view.input.base.utils.factory

import android.content.Context
import android.view.View
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

/**
 * Фабрика для создания прогресса для полей ввода.
 *
 * @author ps.smirnyh
 */
internal class CircularProgressFactory {

    /** @SelfDocumented */
    fun create(context: Context, view: View): CircularProgressDrawable =
        CircularProgressDrawable(context).apply {
            callback = view
        }
}