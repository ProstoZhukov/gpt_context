package ru.tensor.sbis.marks.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Перечисление возможных типов списка пометок.
 *
 * @param isCheckboxVisible Наличие чекбоксов в конфигурации списка пометок
 * @param isDefaultTextStyle Применяется ли стандартная стилизация заголовков для списка пометок
 * @author ra.geraskin
 */

@Parcelize
enum class SbisMarksComponentType(
    val isCheckboxVisible: Boolean,
    val isDefaultTextStyle: Boolean
) : Parcelable {

    /**
     * Пометки только цветом.
     */
    COLOR(false, true),

    /**
     * Пометки цветным кружком, а так же цветным и стилизованным текстом заголовка.
     */
    COLOR_STYLE(false, false),

    /**
     * Пометки цветным кружком, а так же с помощью платформенных пометок "Важно" и "Плюс".
     * Есть возможность выбрать несколько пометок.
     */
    WITH_ADDITIONAL_MARKS(true, true)
}