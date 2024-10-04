package ru.tensor.sbis.design_selection_common.controller

import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design_selection.contract.controller.SelectionControllerWrapper
import ru.tensor.sbis.design_selection.contract.data.SelectionItemMapper
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection_common.PreselectedDataProvider
import ru.tensor.sbis.design_selection_common.SelectionIntentJsonFactory
import ru.tensor.sbis.recipients.generated.RecipientControllerProvider
import ru.tensor.sbis.recipients.generated.CollectionObserverOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.CollectionOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.ItemWithIndexOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.PaginationOfRecipientAnchor
import ru.tensor.sbis.recipients.generated.RecipientFilter
import ru.tensor.sbis.recipients.generated.RecipientId
import ru.tensor.sbis.recipients.generated.RecipientViewModel

/**
 * Реализация поставщика контроллера компонента выбора.
 *
 * @property config конфигурация компонента выбора.
 * @property mapper маппер списка контроллера в список ui моделей.
 * @property preselectedItemsProvider поставщик предвыбранных элементов.
 * @property sourcesImportHelper помощник для импорта результата выбора в сторонние источники.
 * @property intentFactory фабрика для создания intent_json для конфигурации источников контроллера.
 * Например, при выборе получателей из контроллера контактов - импортировать их в контроллер doc_face.
 *
 * @author vv.chekurda
 */
class SelectionControllerProviderImpl<ITEM : SelectionItem, ITEM_ID : SelectionItemId> @JvmOverloads constructor(
    private val config: SelectionConfig,
    private val mapper: SelectionItemMapper<RecipientViewModel, RecipientId, ITEM, ITEM_ID>,
    private val preselectedItemsProvider: PreselectedDataProvider? = null,
    private val sourcesImportHelper: SelectionSourcesImportHelper? = null,
    private val intentFactory: SelectionIntentJsonFactory = DefaultSelectionIntentJsonFactory(config.useCase),
) : SelectionControllerWrapper.Provider<CollectionOfRecipientViewModel, CollectionObserverOfRecipientViewModel,
        RecipientFilter, PaginationOfRecipientAnchor, ItemWithIndexOfRecipientViewModel, RecipientViewModel, ITEM> {

    @Suppress("UNCHECKED_CAST")
    private val selectionMapper = mapper as SelectionItemMapper<RecipientViewModel, RecipientId, ITEM, SelectionItemId>

    private val controllerProvider: RecipientControllerProvider by lazy {
        val preselectedData = preselectedItemsProvider?.getPreselectedData(config)
        val preselectedItems = preselectedData?.ids ?: listOf()
        RecipientControllerProvider.createControllerProvider(
            preselectedItems.mapTo(ArrayList(), selectionMapper::getId),
            intentFactory.createIntentJson(),
            config.excludeList.orEmpty().mapTo(ArrayList(), selectionMapper::getId)
        )
    }

    override fun createSelectionControllerWrapper(folderItem: SelectionFolderItem?) =
        SelectionControllerWrapperImpl(
            controllerProvider = lazy { controllerProvider },
            config = config,
            mapper = selectionMapper,
            folderItem = folderItem,
            sourcesImportHelper = sourcesImportHelper
        )
}