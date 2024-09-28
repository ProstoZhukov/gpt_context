package ru.tensor.sbis.widget_player.layout.inline

import android.content.Context
import android.os.Build
import android.view.ViewGroup
import ru.tensor.sbis.widget_player.layout.VerticalBlockLayout

/**
 * Создает [ViewGroup] с совместимостью с разными версиями Android.
 * - Для версий SDK < 23 отсутствует возможность задавать отступы для строк текста, виджеты будут
 *   расположены вертикально друг под другом.
 *
 * @author am.boldinov
 */
object InlineLayoutCompatFactory {

    fun create(context: Context): ViewGroup {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            InlineLayout(context)
        } else {
            VerticalBlockLayout(context)
        }
    }
}