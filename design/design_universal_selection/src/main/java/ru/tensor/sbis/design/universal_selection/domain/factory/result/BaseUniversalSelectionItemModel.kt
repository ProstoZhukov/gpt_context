/**
 * Модели результата универсального выбора.
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.design.universal_selection.domain.factory.result

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalSelectionItem

/**
 * Модель результата для базового элемента компонента универсального выбора.
 *
 * @see UniversalSelectionItem
 *
 * @author vv.chekurda
 */
@Parcelize
internal data class BaseUniversalSelectionItemModel(
    override val id: String,
    override val title: String,
    override val subtitle: String? = null,
    override val isFolder: Boolean = false
) : UniversalSelectionItem