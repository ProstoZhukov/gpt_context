package ru.tensor.sbis.appdesign.combined_multiselection.factories

import android.content.Context
import ru.tensor.sbis.appdesign.combined_multiselection.data.DemoCombinedRecipientServiceResult
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.DemoCombinedMultiSelectionLoader
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.DemoCombinedRecipientHelper
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.DemoCombinedRecipientMapper
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.DemoCombinedRecipientServiceWrapper
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.contact.DemoContactController
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.contact.DemoContactDataMapper
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.contact.DemoContactServiceWrapper
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.dialog.DemoDialogController
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.dialog.DemoDialogDataMapper
import ru.tensor.sbis.appdesign.combined_multiselection.datasource.dialog.DemoDialogServiceWrapper
import ru.tensor.sbis.appdesign.selection.data.DemoRecipientFilter
import ru.tensor.sbis.appdesign.selection.datasource.DemoRecipientFilterFactory
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.factories.ListDependenciesFactory
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper

/**
 * В реальном приложение контроллеры и мапперы нужно заинджектить через di
 *
 * @author ma.kolpakov
 */
class DemoCombinedRecipientFactory(
    private val contactsController: DemoContactController = DemoContactController,
    private val dialogsController: DemoDialogController = DemoDialogController,
    private val contactsMapper: DemoContactDataMapper = DemoContactDataMapper(),
    private val dialogsMapper: DemoDialogDataMapper = DemoDialogDataMapper()
) : ListDependenciesFactory<DemoCombinedRecipientServiceResult, SelectorItemModel, DemoRecipientFilter, Int> {

    override fun getServiceWrapper(appContext: Context): ServiceWrapper<DemoCombinedRecipientServiceResult, DemoRecipientFilter> =
        DemoCombinedRecipientServiceWrapper(
            DemoContactServiceWrapper(contactsController),
            DemoDialogServiceWrapper(dialogsController)
        )

    override fun getSelectionLoader(appContext: Context): MultiSelectionLoader<SelectorItemModel> =
        DemoCombinedMultiSelectionLoader()

    override fun getFilterFactory(appContext: Context): FilterFactory<SelectorItemModel, DemoRecipientFilter, Int> =
        DemoRecipientFilterFactory()

    override fun getMapperFunction(appContext: Context): ListMapper<DemoCombinedRecipientServiceResult, SelectorItemModel> =
        DemoCombinedRecipientMapper(
            contactsMapper = contactsMapper,
            dialogsMapper = dialogsMapper,
        )

    override fun getResultHelper(appContext: Context): ResultHelper<Int, DemoCombinedRecipientServiceResult> {
        return DemoCombinedRecipientHelper()
    }
}
