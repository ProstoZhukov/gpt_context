package ru.tensor.sbis.richtext.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat

/**
 * Представление массива цветов для удобного получения значений.
 * Массив может содержать цвета в виде hex и атрибутов темы.
 * Инициализация массива цветов происходит лениво при первом обращении.
 * Пример:
 * <array name="my_color_array">
 *     <item>?textColor</item>
 *     <item>#117df3</item>
 *     <item>@color/palette_color_red3</item>
 * </array>
 *
 * @author am.boldinov
 */
class ColorIntArray(
    private val context: Context,
    @ArrayRes
    private val arrayResId: Int
) {

    private val array by lazy(LazyThreadSafetyMode.NONE) {
        toIntArray()
    }

    val size: Int get() = array.size

    @ColorInt
    fun getColor(index: Int): Int? {
        return array.getOrNull(index)?.takeIf {
            it != ResourcesCompat.ID_NULL
        }
    }

    fun toIntArray(): IntArray {
        val array: IntArray
        context.resources.obtainTypedArray(arrayResId).apply {
            val typedValue = TypedValue()
            array = IntArray(length()) { index ->
                val type = getType(index)
                if (type == TypedValue.TYPE_ATTRIBUTE) {
                    getValue(index, typedValue)
                    context.theme.resolveAttribute(typedValue.data, typedValue, true)
                    typedValue.data
                } else {
                    getInt(index, ResourcesCompat.ID_NULL)
                }
            }
            recycle()
        }
        return array
    }
}