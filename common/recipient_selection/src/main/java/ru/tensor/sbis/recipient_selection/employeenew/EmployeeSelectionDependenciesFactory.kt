package ru.tensor.sbis.recipient_selection.employeenew

import android.content.Context
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.factories.ListDependenciesFactory
import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.utils.getFilterPageStartIndexForMeta
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper
import ru.tensor.sbis.profile_service.models.employee.EmployeeSearchFilter
import ru.tensor.sbis.profile_service.models.employee.EmployeeSearchResult
import ru.tensor.sbis.profile_service.models.employee.EmployeeType
import ru.tensor.sbis.profile_service.models.employee.EmployeesControllerWrapper
import ru.tensor.sbis.recipient_selection.employee.di.EmployeeSelectionComponentProvider.getEmployeesSelectionSingletonComponent
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeeSelectionItem
import ru.tensor.sbis.recipient_selection.employeenew.data.*
import java.util.*

/**
 * Фабрика зависимостей для экрана выбора получателей из сотрудников.
 *
 * @author sr.golovkin on 31.07.2020
 */

private const val OUR_COMPANY_IDENTIFIER = -2L

class EmployeeSelectionDependenciesFactory : ListDependenciesFactory<EmployeeSearchResult, BaseEmployeeSelectorItemModel, EmployeeSearchFilter, Int> {

    override fun getServiceWrapper(appContext: Context): ServiceWrapper<EmployeeSearchResult, EmployeeSearchFilter> {
        return EmployeeServiceWrapper(provideControllerDependency(appContext))
    }

    override fun getSelectionLoader(appContext: Context): MultiSelectionLoader<BaseEmployeeSelectorItemModel> {
        return createSelectionLoader {
            val currentSelectionResult = getEmployeesSelectionSingletonComponent(appContext)
                .getEmployeeSelectionResultManager()
                .selectionResult

            val items = currentSelectionResult.fullList

            currentSelectionResult
                .getEmployeesAndFoldersDataForRepost()
                .mapIndexed { index, item ->
                    if (item.hasNestedItems) {
                        EmployeeFolderSelectorItemModel(
                            item.title,
                            item.subtitle,
                            item.uuid.toString(),
                            item.hasNestedItems,
                            item.counter,
                            items[index]
                        )
                    } else {
                        val oldItem = items[index] as EmployeeSelectionItem
                        EmployeeSelectorItemModel(
                            PersonData(oldItem.contact.uuid, oldItem.contact.rawPhoto, oldItem.contact.initialsStubData),
                            mapEmployeeName(item.title),
                            item.title,
                            item.subtitle,
                            (oldItem.contact.uuid ?: item.uuid).toString(),
                            oldItem
                        )
                    }
                }
        }
    }

    override fun getFilterFactory(appContext: Context): FilterFactory<BaseEmployeeSelectorItemModel, EmployeeSearchFilter, Int> {
        return object : FilterFactory<BaseEmployeeSelectorItemModel, EmployeeSearchFilter, Int> {

            override fun createFilter(meta: FilterMeta<BaseEmployeeSelectorItemModel, Int>): EmployeeSearchFilter {
                return EmployeeSearchFilter(
                    OUR_COMPANY_IDENTIFIER,
                    meta.parent?.let {
                        UUID.fromString(meta.parent)
                    },
                    if (meta.query.isEmpty()) null else meta.query,
                    EmployeeType.WORKING,
                    getFilterPageStartIndexForMeta(meta),
                    meta.itemsOnPage,
                    startRegularSync = false,
                    onlyWithAccess = false,
                    null
                )
            }
        }
    }

    override fun getMapperFunction(appContext: Context): ListMapper<EmployeeSearchResult, BaseEmployeeSelectorItemModel> {
        return EmployeeResultMapper()
    }

    override fun getResultHelper(appContext: Context): ResultHelper<Int, EmployeeSearchResult> {
        return EmployeesResultHelper()
    }

    private fun provideDependency(appContext: Context) =
        getEmployeesSelectionSingletonComponent(appContext).dependency

    private fun provideControllerDependency(context: Context): DependencyProvider<EmployeesControllerWrapper> {
        return provideDependency(context).getEmployeesControllerWrapper()
    }
}

private inline fun <DATA : SelectorItem> createSelectionLoader(crossinline func: () -> List<DATA>): MultiSelectionLoader<DATA> =
        object : MultiSelectionLoader<DATA> {
            override fun loadSelectedItems(): List<DATA> = func.invoke()
        }