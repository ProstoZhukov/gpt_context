/**
 * Инструменты для преобразования информации о персонах из слоя данных (declaration) в сой представления (design)
 *
 * @author ma.kolpakov
 */
@file:JvmName("PersonViewMapper")

package ru.tensor.sbis.common.util.person

import android.view.View
import androidx.annotation.CheckResult
import ru.tensor.sbis.common.util.PreviewerUrlUtil
import ru.tensor.sbis.common.util.PreviewerUrlUtil.PREVIEWER_UNKNOWN_SIZES
import timber.log.Timber

/**
 * Проверяет необходимость установить размеры в URL и устанавливает их на основе [view]
 *
 * @return та же самая строка, если размеры устанавливать не нужно или не из чего (`view == null`)
 */
@CheckResult
private fun <V : View?> String.setViewSize(view: V, getSize: V.() -> Pair<Int, Int>): String {
    val sizesShouldBeInserted = contains(PREVIEWER_UNKNOWN_SIZES)
    if (sizesShouldBeInserted && view == null) {
        Timber.w(IllegalStateException("Unable to insert sizes. View is not provided"))
    }
    return when {
        view == null          -> this
        sizesShouldBeInserted -> view.getSize().let { (width, height) ->
            PreviewerUrlUtil.replacePreviewerUrlPartWithCheck(this, width, height, PreviewerUrlUtil.ScaleMode.RESIZE)
        }
        else                  -> this
    }
}

