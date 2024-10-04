package ru.tensor.sbis.design.additional_fields_views.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель параметров view для выравнивания.
 * @property backgroundRadius  Радиус скругления облачка, в котором находятся доп. поля
 * @property verticalPadding  Вертикальный паддинг для элемента доп. полей
 * @property horizontalMargin  Горизонтальный отступ для элемент доп. полей
 *
 * @author au.aleksikov
 */
@Parcelize
data class AdditionalFieldsViewParams(
    val backgroundRadius: Int,
    val verticalPadding: Int = 0,
    val horizontalMargin: Int = 0
) : Parcelable