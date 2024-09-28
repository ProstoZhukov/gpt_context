package ru.tensor.sbis.recipient_selection.profile.data.factory_models.repost

import android.content.Context
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.factories.ListDependenciesFactory
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.ProfilesFoldersResult
import ru.tensor.sbis.recipient_selection.profile.di.profile_component.RecipientSelectionComponentDelegate
import ru.tensor.sbis.recipient_selection.profile.di.profile_component.RecipientSelectionComponentHelper

/**
 * Реализация сериализуемой фабрики зависимостей для компнента множественного выбора контактов для репоста
 *
 * @author vv.chekurda
 */
internal class ContactSelectionForRepostDependencyFactory(
    filter: RecipientsSearchFilter
) : ListDependenciesFactory<ProfilesFoldersResult, RecipientSelectorItemModel, RecipientsSearchFilter, Int>,
    RecipientSelectionComponentDelegate by RecipientSelectionComponentHelper(filter) {

    override fun getServiceWrapper(appContext: Context): ServiceWrapper<ProfilesFoldersResult, RecipientsSearchFilter> =
        getComponent(appContext).getRecipientSelectionServiceWrapper()

    override fun getSelectionLoader(appContext: Context): MultiSelectionLoader<RecipientSelectorItemModel> =
        getComponent(appContext).getContactSelectionForRepostLoader()

    override fun getFilterFactory(appContext: Context): FilterFactory<RecipientSelectorItemModel, RecipientsSearchFilter, Int> =
        getComponent(appContext).getMultiSelectionFilterFactory()

    override fun getMapperFunction(appContext: Context): ListMapper<ProfilesFoldersResult, RecipientSelectorItemModel> =
        getComponent(appContext).getMultiSelectionMapper()

    override fun getResultHelper(appContext: Context): ResultHelper<Int, ProfilesFoldersResult> =
        getComponent(appContext).getRecipientSelectionResultHelper()
}