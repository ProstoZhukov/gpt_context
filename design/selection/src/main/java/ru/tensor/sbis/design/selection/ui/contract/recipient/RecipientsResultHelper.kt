package ru.tensor.sbis.design.selection.ui.contract.recipient

import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import java.io.Serializable

/**
 * Сериализуемая реализация [ResultHelper] для загрузки моделей типа [RecipientListModel]
 *
 * @author ma.kolpakov
 */
interface RecipientsResultHelper<ANCHOR, DATA : RecipientSelectorItemModel> :
    ResultHelper<ANCHOR, RecipientListModel<DATA>>,
    Serializable