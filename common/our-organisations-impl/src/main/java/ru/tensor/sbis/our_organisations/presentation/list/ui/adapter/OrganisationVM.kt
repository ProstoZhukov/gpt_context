package ru.tensor.sbis.our_organisations.presentation.list.ui.adapter

import android.util.SparseArray
import androidx.databinding.ObservableBoolean
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.our_organisations.BR
import ru.tensor.sbis.our_organisations.feature.data.Organisation

const val TYPE_ORGANISATION_ITEM = 0

/**
 * Вью модель организации.
 *
 * @author mv.ilin
 */
data class OrganisationVM(
    val organisation: Organisation,
    val searchText: String?
) : UniversalBindingItem(OrganisationVM::class.java.name) {

    val isSelected = ObservableBoolean()

    override fun getViewType() = TYPE_ORGANISATION_ITEM

    override fun createBindingVariables() = SparseArray<Any>(1).apply {
        try {
            put(BR.viewModel, this@OrganisationVM)
        } catch (_: Throwable) {
        }
    }
}
