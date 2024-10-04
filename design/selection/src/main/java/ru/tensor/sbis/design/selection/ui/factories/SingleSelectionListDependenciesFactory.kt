package ru.tensor.sbis.design.selection.ui.factories

import android.content.Context
import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper
import java.io.Serializable

/**
 * Сериализуемая фабрика зависимостей для экрана одиночного выбора
 *
 * @author us.bessonov
 */
interface SingleSelectionListDependenciesFactory<SERVICE_RESULT, DATA, ENTITY_FILTER, ANCHOR> : Serializable
        where DATA : SelectorItemModel {

    /** @SelfDocumented */
    fun getServiceWrapper(appContext: Context): ServiceWrapper<SERVICE_RESULT, ENTITY_FILTER>

    /** @SelfDocumented */
    fun getSelectionLoader(appContext: Context): SingleSelectionLoader<DATA>

    /** @SelfDocumented */
    fun getFilterFactory(appContext: Context): FilterFactory<DATA, ENTITY_FILTER, ANCHOR>

    /** @SelfDocumented */
    fun getMapperFunction(appContext: Context): ListMapper<SERVICE_RESULT, DATA>

    /** @SelfDocumented */
    fun getResultHelper(appContext: Context): ResultHelper<ANCHOR, SERVICE_RESULT>
}