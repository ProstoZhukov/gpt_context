package ru.tensor.sbis.communication_decl.selection.universal.data

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.communication_decl.selection.SelectionItemId

/**
 * ID элемента в компоненте универсального выбора.
 *
 * @author vv.chekurda
 */
sealed interface UniversalItemId : SelectionItemId {
    val value: String
}

/**
 * ID базового элемента в компоненте универсального выбора.
 */
@Parcelize
data class BaseUniversalItemId(override val value: String) : UniversalItemId