/**
 * Набор расширений для работы с аргументами для выбора получателей
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.selection.ui.utils

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectorItemListeners
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientMultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientSelectorDataProvider
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientsResultHelper
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper

private const val RECIPIENT_SELECTION_DATA_PROVIDER = "RECIPIENT_SELECTION_DATA_PROVIDER"
private const val RECIPIENTS_MULTI_SELECTION_LOADER = "RECIPIENTS_MULTI_SELECTION_LOADER"
private const val RECIPIENTS_RESULT_HELPER = "RECIPIENTS_RESULT_HELPER"
private const val SELECTOR_ITEM_LISTENERS = "SELECTOR_ITEM_LISTENERS"

/**
 * Признак того, то необходимо подготовить окружение для работы с "общим API выбора получателей"
 *
 * @see ru.tensor.sbis.design.selection.ui.contract.recipient
 */
internal val Bundle.isRecipientCommonAPI: Boolean
    get() = containsKey(RECIPIENT_SELECTION_DATA_PROVIDER)

/**
 * Объект для постраничной загрузки получателей
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент конфигурации компонента */)
internal var Bundle.recipientDataProvider: RecipientSelectorDataProvider<RecipientSelectorItemModel>
    get() = getSerializable(RECIPIENT_SELECTION_DATA_PROVIDER) as
        RecipientSelectorDataProvider<RecipientSelectorItemModel>
    set(value) = putSerializable(RECIPIENT_SELECTION_DATA_PROVIDER, value)

/**
 * Объект для загрузки выбранных получателей при инициализации компонента
 *
 * @see MultiSelectionLoader
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент конфигурации компонента */)
internal var Bundle.recipientsMultiSelectionLoader: RecipientMultiSelectionLoader
    get() = getSerializable(RECIPIENTS_MULTI_SELECTION_LOADER) as RecipientMultiSelectionLoader
    set(value) = putSerializable(RECIPIENTS_MULTI_SELECTION_LOADER, value)

/**
 * Объект для получения информации о загруженных данных
 *
 * @see ResultHelper
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент конфигурации компонента */)
internal var Bundle.recipientsResultHelper: RecipientsResultHelper<Any, RecipientSelectorItemModel>
    get() = getSerializable(RECIPIENTS_RESULT_HELPER) as RecipientsResultHelper<Any, RecipientSelectorItemModel>
    set(value) = putSerializable(RECIPIENTS_RESULT_HELPER, value)

/**
 * Объект для получения пользовательских клик листенеров
 */
internal var Bundle.selectorItemListeners: SelectorItemListeners<SelectorItemModel, FragmentActivity>
    get() = getParcelable(SELECTOR_ITEM_LISTENERS) ?: SelectorItemListeners()
    set(value) = putParcelable(SELECTOR_ITEM_LISTENERS, value)
