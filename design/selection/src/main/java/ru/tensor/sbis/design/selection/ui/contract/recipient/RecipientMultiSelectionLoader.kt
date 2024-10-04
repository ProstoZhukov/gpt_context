package ru.tensor.sbis.design.selection.ui.contract.recipient

import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import java.io.Serializable

/**
 * Сериализуемая реализация [MultiSelectionLoader] для загрузки моделей типа [RecipientSelectorItemModel]
 *
 * @author ma.kolpakov
 */
interface RecipientMultiSelectionLoader :
    MultiSelectionLoader<RecipientSelectorItemModel>,
    Serializable