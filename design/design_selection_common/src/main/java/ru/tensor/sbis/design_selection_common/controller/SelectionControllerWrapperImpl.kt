package ru.tensor.sbis.design_selection_common.controller

import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design_selection.contract.controller.SelectionControllerWrapper
import ru.tensor.sbis.design_selection.contract.controller.SelectionDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionItemMapper
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.recipients.generated.CollectionObserverOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.CollectionOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.ItemWithIndexOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.PaginationOfRecipientAnchor
import ru.tensor.sbis.recipients.generated.RecipientControllerProvider
import ru.tensor.sbis.recipients.generated.RecipientFilter
import ru.tensor.sbis.recipients.generated.RecipientId
import ru.tensor.sbis.recipients.generated.RecipientViewModel

/**
 * Реализация обертки контроллера компонента выбора.
 *
 * @see SelectionDelegate
 * @see SelectionControllerWrapper
 *
 * @property adapter адаптер контроллера.
 * @property config конфигурация компонента выбора.
 * @property mapper маппер списка контроллера в список ui моделей.
 * @property folderItem папка, с которой будет работать контроллер.
 *
 * @author vv.chekurda
 */
class SelectionControllerWrapperImpl<ITEM : SelectionItem> internal constructor(
    private val adapter: SelectionControllerAdapter<ITEM>,
    private val config: SelectionConfig,
    private val mapper: SelectionItemMapper<RecipientViewModel, RecipientId, ITEM, SelectionItemId>,
    private val folderItem: SelectionFolderItem? = null
) : SelectionControllerWrapper<CollectionOfRecipientViewModel, CollectionObserverOfRecipientViewModel,
        RecipientFilter, PaginationOfRecipientAnchor, ItemWithIndexOfRecipientViewModel, RecipientViewModel, ITEM>,
    SelectionDelegate<ITEM> by adapter {

    constructor(
        controllerProvider: Lazy<RecipientControllerProvider>,
        config: SelectionConfig,
        mapper: SelectionItemMapper<RecipientViewModel, RecipientId, ITEM, SelectionItemId>,
        folderItem: SelectionFolderItem? = null,
        sourcesImportHelper: SelectionSourcesImportHelper? = null
    ) : this(
        SelectionControllerAdapter(controllerProvider, mapper, sourcesImportHelper),
        config,
        mapper,
        folderItem
    )

    override fun getSelectionCollectionWrapper() =
        SelectionCollectionWrapperImpl(adapter, config, mapper, folderItem)
}