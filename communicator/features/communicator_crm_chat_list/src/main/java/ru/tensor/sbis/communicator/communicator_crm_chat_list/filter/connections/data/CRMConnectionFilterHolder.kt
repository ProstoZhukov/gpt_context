package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.data

import ru.tensor.sbis.consultations.generated.ConnectionFilter

/**
 * Холдер фильтра источников CRM.
 *
 * @author da.zhukov
 */
class CRMConnectionFilterHolder(private val filter: ConnectionFilter) : () -> ConnectionFilter {

    override fun invoke(): ConnectionFilter {
        return filter
    }

    /** @SelfDocumented */
    fun setQuery(query: String?) {
        filter.searchStr = query
    }
}