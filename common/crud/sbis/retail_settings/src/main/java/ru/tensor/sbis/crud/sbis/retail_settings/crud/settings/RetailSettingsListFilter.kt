package ru.tensor.sbis.crud.sbis.retail_settings.crud.settings

import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.retail_settings.generated.SettingsFilter
import java.io.Serializable

class RetailSettingsListFilter : Serializable, ListFilter() {

    override fun queryBuilder(): Builder<*, *> =
            RetailSettingsListFilterBuilder()

    private class RetailSettingsListFilterBuilder
    internal constructor() : AnchorPositionQueryBuilder<Any, SettingsFilter>() {

        override fun build(): SettingsFilter = SettingsFilter()
    }
}