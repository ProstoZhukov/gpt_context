package ru.tensor.sbis.design_selection.contract.customization

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * Комплект строк для компонента выбора, которые специфичны для предметной области.
 *
 * @property searchHint Подсказка в поисковой строке.
 *
 * @author vv.chekurda
 */
@Parcelize
data class SelectionStrings(
    @StringRes val searchHint: Int
) : Parcelable, Serializable