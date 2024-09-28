package ru.tensor.sbis.design_selection.ui.content.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.design_selection.ui.content.vm.SelectionContentViewModel
import ru.tensor.sbis.design_selection.ui.main.di.SelectionComponent
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.ui.main.router.SelectionRouter
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract
import ru.tensor.sbis.design_selection.domain.list.SelectionComponentSettings
import ru.tensor.sbis.design_selection.domain.list.SelectionListComponent
import ru.tensor.sbis.design_selection.ui.content.utils.SelectionContentStackHelper
import ru.tensor.sbis.design_selection.ui.content.vm.search.SelectionSearchViewModel
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionRulesHelper

/**
 * DI компонент области контента компонента выбора.
 *
 * @author vv.chekurda
 */
@SelectionContentScope
@Component(
    dependencies = [SelectionComponent::class],
    modules = [SelectionContentModule::class]
)
internal interface SelectionContentComponent {

    val searchVM: SelectionSearchViewModel
    val contentVM: SelectionContentViewModel<SelectionItem>
    val listVM: SelectionListComponent
    val config: SelectionConfig

    val router: SelectionRouter
    val folderItem: SelectionFolderItem?
    val stackHelper: SelectionContentStackHelper
    val headerButtonContract: HeaderButtonContract<SelectionItem, FragmentActivity>?
    val rulesHelper: SelectionRulesHelper

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance fragment: Fragment,
            @BindsInstance folderItem: SelectionFolderItem?,
            @BindsInstance listSettings: SelectionComponentSettings,
            selectionComponent: SelectionComponent
        ): SelectionContentComponent
    }
}