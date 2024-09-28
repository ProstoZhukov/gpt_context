package ru.tensor.sbis.recipient_selection.profile.data.factory_models.single

import android.content.Context
import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.factories.SingleSelectionListDependenciesFactory
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.ProfilesFoldersResult
import ru.tensor.sbis.recipient_selection.profile.di.profile_component.RecipientSelectionComponentDelegate
import ru.tensor.sbis.recipient_selection.profile.di.profile_component.RecipientSelectionComponentHelper

/**
 * Реализация сериализуемой фабрики зависимостей для компнента выбора получателей
 *
 * @author vv.chekurda
 */
internal class SingleRecipientSelectionDependencyFactory(
    filter: RecipientsSearchFilter
) : SingleSelectionListDependenciesFactory<ProfilesFoldersResult, RecipientSelectorItemModel, RecipientsSearchFilter, Int>,
    RecipientSelectionComponentDelegate by RecipientSelectionComponentHelper(filter) {

    override fun getServiceWrapper(appContext: Context): ServiceWrapper<ProfilesFoldersResult, RecipientsSearchFilter> =
        getComponent(appContext).getRecipientSelectionServiceWrapper()

    override fun getSelectionLoader(appContext: Context): SingleSelectionLoader<RecipientSelectorItemModel> =
        getComponent(appContext).getSingleRecipientSelectionLoader()

    override fun getFilterFactory(appContext: Context): FilterFactory<RecipientSelectorItemModel, RecipientsSearchFilter, Int> =
        getComponent(appContext).getSingleSelectionFilterFactory()

    override fun getMapperFunction(appContext: Context): ListMapper<ProfilesFoldersResult, RecipientSelectorItemModel> =
        getComponent(appContext).getSingleSelectionMapper()

    override fun getResultHelper(appContext: Context): ResultHelper<Int, ProfilesFoldersResult> =
        getComponent(appContext).getRecipientSelectionResultHelper()
}