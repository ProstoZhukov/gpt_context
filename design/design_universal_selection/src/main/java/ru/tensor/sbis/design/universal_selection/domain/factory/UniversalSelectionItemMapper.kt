package ru.tensor.sbis.design.universal_selection.domain.factory

import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.communication_decl.selection.universal.UniversalSelectionConfig
import ru.tensor.sbis.communication_decl.selection.universal.data.BaseUniversalItemId
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalItemId
import ru.tensor.sbis.design_selection.contract.data.SelectionItemMapper
import ru.tensor.sbis.recipients.generated.GeneralSelectionStringId
import ru.tensor.sbis.recipients.generated.RecipientId
import ru.tensor.sbis.recipients.generated.RecipientViewModel
import javax.inject.Inject

/**
 * Маппер компонента универсального выбора.
 * Производит маппинг моделей контроллера в соответствующие модели списка UI.
 *
 * @property config настрока компонента универсального выбора.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectionItemMapper @Inject constructor(
    private val config: UniversalSelectionConfig
) : SelectionItemMapper<RecipientViewModel, RecipientId, UniversalItem, UniversalItemId> {

    override fun map(item: RecipientViewModel): UniversalItem =
        if (requireNotNull(item.data.fieldGeneralSelectionString).isFolder) {
            mapFolderItem(item)
        } else {
            mapItem(item)
        }

    private fun mapItem(item: RecipientViewModel): UniversalSelectionItem =
        with(requireNotNull(item.data.fieldGeneralSelectionString)) {
            UniversalSelectionItem(
                id = BaseUniversalItemId(id),
                title = item.displayTitle,
                subtitle = subtitle,
                titleHighlights = item.nameHighlight.map { SearchSpan(it.start, it.end) }
            )
        }

    private fun mapFolderItem(item: RecipientViewModel): UniversalSelectionFolderItem =
        with(requireNotNull(item.data.fieldGeneralSelectionString)) {
            UniversalSelectionFolderItem(
                id = BaseUniversalItemId(id),
                title = item.displayTitle,
                subtitle = subtitle,
                titleHighlights = item.nameHighlight.map { SearchSpan(it.start, it.end) },
                selectable = config.isFoldersSelectable,
                openable = isFolder
            )
        }

    override fun getId(id: UniversalItemId): RecipientId =
        RecipientId().apply { fieldGeneralSelectionStringId = GeneralSelectionStringId(id.value) }
}