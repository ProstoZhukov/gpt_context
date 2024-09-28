package ru.tensor.sbis.design_dialogs.multipicker

import android.graphics.Color
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.design_dialogs.R

import java.io.Serializable

/**
 * Модель для создания объекта для мультипикера (вьюхи с барабанами)
 *
 * @param tag тэг для идентифицирования барабана в контейнере
 * @param values данные для барабана
 * @param valuesTextStyle стиль текста в барабане.
 *         Размер по умолчанию 18sp. При необходимости изменения создать стиль и изменить android:textSize
 *         Цвет по умолчанию @color/text_color_black_1. При необходимости изменения изменить android:editTextColor
 * @param rightText текст справа от барабана, по умолчанию ничего не добавляется
 * @param rightTextSize размер текста справа от барабана, по умолчанию 18sp
 * @param rightTextColor цвет текста справа от барабана, по умолчанию черный
 * @param selectedPosition индекс выбранного значения, по-умолчанию 0
 * @param isWrapSelectorWheel бесконечная прокрутка, по умолчанию включена
 * @param isAddMarginLeft добавлять ли отступ слева
 * @param isAddMarginRight добавлять ли отступ справа
 */
class MultiPickerDataItem(
    val tag: String,
    val values: List<String>,
    @StyleRes val valuesTextStyle: Int = R.style.MultiPicker_Item,
    val rightText: String = "",
    val rightTextSize: Int = 18,
    val rightTextColor: Int = Color.BLACK,
    var selectedPosition: Int = 0,
    val isWrapSelectorWheel: Boolean = true,
    val isAddMarginLeft: Boolean = false,
    val isAddMarginRight: Boolean = false
) : Serializable