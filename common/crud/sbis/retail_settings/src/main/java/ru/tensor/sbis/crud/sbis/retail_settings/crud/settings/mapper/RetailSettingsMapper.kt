package ru.tensor.sbis.crud.sbis.retail_settings.crud.settings.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sbis.retail_settings.model.RetailSettings
import ru.tensor.sbis.crud.sbis.retail_settings.model.map
import ru.tensor.sbis.retail_settings.generated.Settings as ControllerRetailSettings

internal class RetailSettingsMapper(context: Context) :
        BaseModelMapper<ControllerRetailSettings, RetailSettings>(context) {

    override fun apply(rawData: ControllerRetailSettings): RetailSettings =
            rawData.map()
}