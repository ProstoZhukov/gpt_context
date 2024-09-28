package ru.tensor.sbis.consignment_decl.registry.view

import androidx.lifecycle.LiveData
import ru.tensor.sbis.consignment_decl.base.util.ConfigChangeResistant
import ru.tensor.sbis.consignment_decl.registry.model.ConsignmentRegistryElement
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.list.view.item.AnyItem
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Тип [ItemInSectionMapper] для моделей ЭТРН реестра.
 *
 * @author kv.martyshenko
 */
@ConfigChangeResistant
interface ConsignmentRegistryMapper : ItemInSectionMapper<ConsignmentRegistryElement, AnyItem> {

    /**
     * Фабрика, для созания [ConsignmentRegistryMapper].
     */
    interface Factory {

        /**
         * Метод для создания [ConsignmentRegistryMapper].
         *
         * @param searchQuery поисковый запрос.
         * @param onClick действие по клику.
         * @param onReassign действие по смене фазы.
         * @param onRead действие пометить прочитанным.
         */
        fun create(
            searchQuery: LiveData<String?>,
            onClick: (ConsignmentRegistryElement) -> Unit,
            onReassign: (ConsignmentRegistryElement) -> Unit,
            onRead: (ConsignmentRegistryElement) -> Unit
        ): ConsignmentRegistryMapper

    }
}

/**
 * Метод для формирования строкового представления даты.
 */
fun ConsignmentRegistryElement.dateString(): CharSequence {
    return dateFormatter.format(date)
}

private val dateFormatter: DateFormat by lazy {
    SimpleDateFormat("dd.MM.yy", Locale.getDefault())
}