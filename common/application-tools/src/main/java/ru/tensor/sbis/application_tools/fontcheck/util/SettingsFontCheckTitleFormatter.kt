package ru.tensor.sbis.application_tools.fontcheck.util

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import org.apache.commons.lang3.StringUtils
import java.util.Locale

/**
 * @author du.bykov
 *
 * Утилита для форматирования текста для экрана проверки шрифтов
 * */
internal object SettingsFontCheckTitleFormatter {

    /**
     * Отформатировать строку для ячейки списка экрана "проверка шрифтов"
     *
     * @param [context] Контекст
     * @param [fontSize] Строковый ресурс, хранящий первую часть строки с описанием размера шрифта
     * @param [color] Ресурс цвета, который необходимо преобразовать в строку и конкатенировать с основным текстом
     * @return отформатированная строка формата *тип и размер шрифта (краткое название стиля) #цвет строки*
     */
    @JvmStatic
    fun format(context: Context?, @StringRes fontSize: Int, @ColorRes color: Int? = null): String? {
        val resultString = context?.getString(fontSize)
        color?.let { colorRes ->
            return resultString
                .plus(StringUtils.SPACE)
                .plus(
                    String.format("#%06x", ContextCompat.getColor(context!!, colorRes) and 0xffffff)
                        .uppercase(Locale.getDefault())
                ) //Получение кода цвета без учёта альфа-канала
        } ?: return resultString
    }
}