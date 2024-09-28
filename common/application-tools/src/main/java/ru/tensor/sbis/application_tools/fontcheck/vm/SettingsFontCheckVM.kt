package ru.tensor.sbis.application_tools.fontcheck.vm

import android.util.SparseArray
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import ru.tensor.sbis.application_tools.BR
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem


/**
 * @author du.bykov
 *
 * Вью-модель списка на экране проверки шрифтов
 *
 * @param [fontSizeAndColorText] Отформатированная строка, содержащая информацию о размере шрифта и цвете текста
 * @param [fontSize] ссылка на ресурс размера шрифта
 * @param [color] ссылка на ресурс цвета
 */
internal class SettingsFontCheckVM(
    val fontSizeAndColorText: String,
    @DimenRes val fontSize: Int,
    @ColorRes val color: Int
) : UniversalBindingItem(SettingsFontCheckVM::class.java.name) {

    override fun getViewType(): Int {
        return TYPE_FONT_ITEM
    }

    override fun createBindingVariables(): SparseArray<Any> {
        val variables = SparseArray<Any>(1)
        variables.put(BR.SettingsFontCheckVM, this)
        return variables
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SettingsFontCheckVM

        if (fontSizeAndColorText != other.fontSizeAndColorText) return false
        if (fontSize != other.fontSize) return false
        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fontSizeAndColorText.hashCode()
        result = 31 * result + fontSize
        result = 31 * result + color
        return result
    }
}

private const val TYPE_FONT_ITEM = 0