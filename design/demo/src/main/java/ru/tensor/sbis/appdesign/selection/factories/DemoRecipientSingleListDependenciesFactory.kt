package ru.tensor.sbis.appdesign.selection.factories

import android.content.Context
import ru.tensor.sbis.appdesign.selection.data.DemoRecipientFilter
import ru.tensor.sbis.appdesign.selection.data.DemoRecipientServiceResult
import ru.tensor.sbis.appdesign.selection.datasource.*
import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.factories.SingleSelectionListDependenciesFactory
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultRecipientSelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper

/**
 * @author ma.kolpakov
 */
class DemoRecipientSingleListDependenciesFactory : SingleSelectionListDependenciesFactory<DemoRecipientServiceResult, DefaultRecipientSelectorItemModel, DemoRecipientFilter, Int> {

    override fun getServiceWrapper(appContext: Context): ServiceWrapper<DemoRecipientServiceResult, DemoRecipientFilter> =
        DemoRecipientServiceWrapper(appContext.run { DemoRecipientController /* Получение контроллера из DI */ })

    override fun getSelectionLoader(appContext: Context): SingleSelectionLoader<DefaultRecipientSelectorItemModel> =
        DemoRecipientSingleSelectionLoader(DemoRecipientController, DemoRecipientDataMapper())

    override fun getFilterFactory(appContext: Context): FilterFactory<DefaultRecipientSelectorItemModel, DemoRecipientFilter, Int> =
        DemoRecipientFilterFactory()

    override fun getMapperFunction(appContext: Context): ListMapper<DemoRecipientServiceResult, DefaultRecipientSelectorItemModel> =
        DemoRecipientDataMapper()

    override fun getResultHelper(appContext: Context): ResultHelper<Int, DemoRecipientServiceResult> =
        DemoRecipientResultHelper()
}