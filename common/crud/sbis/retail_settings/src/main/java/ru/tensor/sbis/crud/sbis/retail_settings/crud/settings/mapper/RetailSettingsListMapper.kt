package ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.mapper

import android.content.Context
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sbis.retail_settings.model.RetailSettings
import ru.tensor.sbis.crud.sbis.retail_settings.model.map
import ru.tensor.sbis.retail_settings.generated.ListResultOfSettingsMapOfStringString

internal class RetailSettingsListMapper(context: Context) :
        BaseModelMapper<ListResultOfSettingsMapOfStringString, PagedListResult<RetailSettings>>(context) {

    override fun apply(rawList: ListResultOfSettingsMapOfStringString): PagedListResult<RetailSettings> =
            PagedListResult(rawList.result.map { it.map() }, rawList.haveMore)
}