package ru.tensor.sbis.design_selection.contract.customization.selected.panel.utils

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.communication_decl.selection.DefaultSelectionItemId
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectionPanel
import java.util.UUID

/**
 * Добавление данных для [SelectionPanel] отображения в превью студии.
 *
 * @author vv.chekurda
 */
internal fun SelectionPanel.showPreview() {
    setData(
        SelectedData(
            items = (0..100).map {
                DemoSelectionFolderItemModel(
                    id = DefaultSelectionItemId(UUID.randomUUID()),
                    title = "Demo item $it"
                )
            },
            isUserSelection = true
        )
    )
}

/**
 * Реализация модели папки компонента выбора.
 * @see SelectionFolderItem
 */
@Parcelize
private data class DemoSelectionFolderItemModel @JvmOverloads constructor(
    override val id: SelectionItemId,
    override val photoData: PhotoData? = null,
    override val title: String,
    override val subtitle: String? = null,
    override val counter: Int? = null,
    override val openable: Boolean = true,
    override val selectable: Boolean = true,
    override val titleHighlights: List<SearchSpan> = emptyList()
) : SelectionFolderItem
