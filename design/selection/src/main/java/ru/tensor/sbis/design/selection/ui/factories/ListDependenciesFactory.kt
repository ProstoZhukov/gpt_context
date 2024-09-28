package ru.tensor.sbis.design.selection.ui.factories

import android.content.Context
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper
import java.io.Serializable

/**
 * Сериализуемая фабрика зависимостей для компонента выбора
 *
 * @author ma.kolpakov
 */
interface ListDependenciesFactory<SERVICE_RESULT, DATA, ENTITY_FILTER, ANCHOR> : Serializable
        where DATA : SelectorItemModel {

    fun getServiceWrapper(appContext: Context): ServiceWrapper<SERVICE_RESULT, ENTITY_FILTER>

    fun getSelectionLoader(appContext: Context): MultiSelectionLoader<DATA>

    fun getFilterFactory(appContext: Context): FilterFactory<DATA, ENTITY_FILTER, ANCHOR>

    fun getMapperFunction(appContext: Context): ListMapper<SERVICE_RESULT, DATA>

    fun getResultHelper(appContext: Context): ResultHelper<ANCHOR, SERVICE_RESULT>
}