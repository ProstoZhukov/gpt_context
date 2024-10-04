package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientMultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoSerializableRecipientMultiSelectorLoader(
    controller: DemoRecipientController,
    mapper: DemoRecipientDataMapper
) :
    RecipientMultiSelectionLoader,
    MultiSelectionLoader<RecipientSelectorItemModel> by DemoRecipientMultiSelectionLoader(controller, mapper)