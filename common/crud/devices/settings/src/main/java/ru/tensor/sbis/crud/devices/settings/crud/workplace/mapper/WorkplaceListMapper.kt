package ru.tensor.sbis.crud.devices.settings.crud.workplace.mapper

import android.content.Context
import ru.tensor.devices.settings.generated.ListResultOfWorkplaceMapOfStringString
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.R
import ru.tensor.sbis.crud.devices.settings.model.Workplace
import ru.tensor.sbis.crud.devices.settings.model.map

/**
 * Маппер для преобразования нескольких рабочих мест, пришедших с контроллера, в Android модели
 */
internal class WorkplaceListMapper(context: Context) :
        BaseModelMapper<ListResultOfWorkplaceMapOfStringString, PagedListResult<BaseItem<Workplace>>>(context) {

    override fun apply(rawList: ListResultOfWorkplaceMapOfStringString): PagedListResult<BaseItem<Workplace>> =
            PagedListResult(rawList.result.mapTo(arrayListOf()) {
                BaseItem(R.id.settings_workplace_item, data = it.map())
            }.apply {
                add(BaseItem(R.id.settings_workplace_stub_item, data = Workplace.stub()))
            }, rawList.haveMore)
}