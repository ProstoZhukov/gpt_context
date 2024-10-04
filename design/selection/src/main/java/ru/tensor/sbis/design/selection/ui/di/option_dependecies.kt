/**
 * Фабрики, которые предоставляют объекты в DI в зависимости от настроек компонента
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.selection.ui.di

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.bl.contract.listener.OpenHierarchyListener
import ru.tensor.sbis.design.selection.bl.vm.completion.*
import ru.tensor.sbis.design.selection.ui.contract.SelectorDoneButtonVisibilityMode
import ru.tensor.sbis.design.selection.ui.contract.SelectorStrings
import ru.tensor.sbis.design.selection.ui.contract.SelectorStubContentProvider
import ru.tensor.sbis.design.selection.ui.contract.listeners.NewGroupClickListener
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenEntity
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenEntityFactory
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenVM
import ru.tensor.sbis.design.selection.ui.list.recipients.RecipientRepository
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.*
import ru.tensor.sbis.design.selection.ui.utils.fixed_button.ChooseAllFixedButtonListener
import ru.tensor.sbis.design.selection.ui.utils.fixed_button.FixedButtonListener
import ru.tensor.sbis.design.selection.ui.utils.fixed_button.FixedButtonType
import ru.tensor.sbis.design.selection.ui.utils.stub.DefaultSelectorStubContentProvider
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.*
import ru.tensor.sbis.list.base.data.CrudRepository
import ru.tensor.sbis.list.base.data.ServiceWrapper
import ru.tensor.sbis.list.base.domain.boundary.Repository
import ru.tensor.sbis.common.R as CommonR
import ru.tensor.sbis.design.R as DesignR

/**
 * Предоставление реализации [FixedButtonViewModel]
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечивается параметрами инициализации и отладкой */)
internal fun createFixedButtonViewModel(arguments: Bundle): FixedButtonViewModel<Any> =
    when (arguments.fixedButtonType) {
        FixedButtonType.CHOOSE_ALL -> ChooseAllFixedButtonViewModelImpl()
        FixedButtonType.CREATE_GROUP -> FixedButtonViewModelImpl(
            FixedButtonData(DesignR.string.design_mobile_icon_create_new_chat, CommonR.string.create_chat_button_text)
        )
        null -> EmptyFixedButtonViewModelImpl()
    } as FixedButtonViewModel<Any>

/**
 * Предоставление реализации [FixedButtonListener]
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечивается параметрами инициализации и отладкой */)
internal fun createFixedButtonListener(
    arguments: Bundle,
    chooseAllAction: (SelectorItemModel) -> Unit
): FixedButtonListener<Any, FragmentActivity>? =
    when (arguments.fixedButtonType) {
        FixedButtonType.CHOOSE_ALL -> ChooseAllFixedButtonListener(chooseAllAction)
        FixedButtonType.CREATE_GROUP -> createNewGroupListener(arguments.newGroupListener)
        null -> null
    } as FixedButtonListener<Any, FragmentActivity>?

/**
 * Предоставление реализации [OpenHierarchyListener]
 */
internal fun createOpenHierarchyListener(
    arguments: Bundle,
    listViewModel: SelectionListScreenVM
): OpenHierarchyListener<SelectorItemModel>? =
    if (arguments.isHierarchicalData)
        listViewModel::onItemClicked
    else
        null

/**
 * Предоставление реализации [DoneButtonViewModel] в зависимости от параметров инициализации
 */
internal fun createDoneButtonViewModel(arguments: Bundle): DoneButtonViewModel =
    when (arguments.doneButtonMode) {
        SelectorDoneButtonVisibilityMode.VISIBLE -> VisibleDoneButtonViewModel()
        SelectorDoneButtonVisibilityMode.HIDDEN -> HiddenDoneButtonViewModel()
        SelectorDoneButtonVisibilityMode.AUTO_HIDDEN -> AutoHiddenDoneButtonViewModel(SelectionChangeFunction())
        SelectorDoneButtonVisibilityMode.AT_LEAST_ONE -> AutoHiddenDoneButtonViewModel(AtLeastOneChangeFunction())
        SelectorDoneButtonVisibilityMode.AUTO_DISABLE -> AutoDisableDoneButtonViewModel()
    }

/**
 * Возвращает и настраивает [customStubProvider]. Если его нет, создаётся [DefaultSelectorStubContentProvider] на основе
 * [selectorStrings]
 */
internal fun resolveStubContentProvider(
    customStubProvider: SelectorStubContentProvider<Any>?,
    selectorStrings: SelectorStrings
): SelectorStubContentProvider<Any> {
    return customStubProvider ?: DefaultSelectorStubContentProvider(selectorStrings)
}

/**
 * Создаёт [Repository] на основе предоставленной информации об окружении в [arguments]
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент конфигурации компонента */)
internal fun createRepository(
    entityFactory: SelectionListScreenEntityFactory<Any, Any, Any>,
    serviceWrapper: ServiceWrapper<Any, Any>?,
    arguments: Bundle
): Repository<SelectionListScreenEntity<Any, Any, Any>, Any> =
    if (arguments.isRecipientCommonAPI)
        RecipientRepository<Any>(arguments.recipientDataProvider) as
            Repository<SelectionListScreenEntity<Any, Any, Any>, Any>
    else
        CrudRepository(
            entityFactory, checkNotNull(serviceWrapper) { "ServiceWrapper required for selector with CrudRepository" }
        )

/**
 * Создание обёртки над подпиской, для которой не важны входящие данные. Только действие
 */
private fun createNewGroupListener(
    listener: NewGroupClickListener<FragmentActivity>
): FixedButtonListener<Any, FragmentActivity> = object : FixedButtonListener<Any, FragmentActivity> {

    override fun onButtonClicked(activity: FragmentActivity, result: Any) {
        listener.onButtonClicked(activity)
    }
}
