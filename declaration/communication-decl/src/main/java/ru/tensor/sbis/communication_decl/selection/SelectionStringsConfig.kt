package ru.tensor.sbis.communication_decl.selection

import androidx.annotation.StringRes
import java.io.Serializable

/**
 * Комплект строк для компонента выбора, которые специфичны для предметной области.
 *
 * @property searchHint Подсказка в поисковой строке. null - строка компонента по умолчанию.
 *
 * @author vv.chekurda
 */
data class SelectionStringsConfig(
    @StringRes val searchHint: Int? = null
) : Serializable