package ru.tensor.sbis.business_card_list.di.view

import android.text.SpannableString
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.business.card.mobile.generated.BusinessCardCollectionProvider
import ru.tensor.sbis.business_card_domain.BusinessCardDomainCommandProvider
import ru.tensor.sbis.business_card_list.contract.BusinessCardListDependency
import ru.tensor.sbis.business_card_list.contract.BusinessCardListInteractor
import ru.tensor.sbis.business_card_list.contract.internal.list.BusinessCardListRouter
import ru.tensor.sbis.business_card_list.domain.command.BusinessCardListFilter
import ru.tensor.sbis.business_card_list.domain.command.BusinessCardListInteractorImpl
import ru.tensor.sbis.business_card_list.domain.command.BusinessCardListMapper
import ru.tensor.sbis.business_card_list.domain.command.BusinessCardListStubFactory
import ru.tensor.sbis.business_card_list.domain.command.BusinessCardListWrapper
import ru.tensor.sbis.business_card_list.domain.command.BusinessCardListWrapperImpl
import ru.tensor.sbis.business_card_list.presentation.router.BusinessCardListRouterImpl
import ru.tensor.sbis.business_card_list.presentation.view.ClicksWrapper
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCardLink
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCardStyle
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCardStyleProperties
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.crud3.ItemWithSection
import ru.tensor.sbis.crud3.ListComponentViewViewModel
import ru.tensor.sbis.crud3.createListComponentViewViewModel
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.person_decl.profile.model.ProfileContact
import ru.tensor.sbis.person_decl.profile.model.ProfileContactType
import ru.tensor.sbis.profiles.generated.PersonContactModel
import ru.tensor.sbis.profiles.generated.PersonContactType
import ru.tensor.sbis.profiles.generated.VisibilityStatus
import java.util.UUID
import ru.tensor.business.card.mobile.generated.BusinessCard as ControllerBusinessCard
import ru.tensor.business.card.mobile.generated.BusinessCardLink as ControllerBusinessCardLink
import ru.tensor.business.card.mobile.generated.BusinessCardStyle as ControllerBusinessCardStyle
import ru.tensor.business.card.mobile.generated.BusinessCardStyleProperties as ControllerBusinessCardStyleProperties
import ru.tensor.sbis.person_decl.profile.model.VisibilityStatus as ProfileVisibilityStatus

/**@SelfDocumented*/
@Module
internal class BusinessCardListViewModule {

    /**@SelfDocumented*/
    @BusinessCardListViewScope
    @Provides
    internal fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()

    /**@SelfDocumented*/
    @BusinessCardListViewScope
    @Provides
    fun provideBusinessCardListComponentViewModel(
        viewModelStoreOwner: ViewModelStoreOwner,
        collectionWrapper: BusinessCardListWrapper,
        itemMapper: BusinessCardListMapper,
        stubFactory: BusinessCardListStubFactory
    ): BusinessCardListViewModel =
        createListComponentViewViewModel(
            viewModelStoreOwner = viewModelStoreOwner,
            wrapper = lazy { collectionWrapper },
            mapper = lazy { itemMapper },
            stubFactory = lazy { stubFactory }
        )

    /**@SelfDocumented*/
    @BusinessCardListViewScope
    @Provides
    fun provideBusinessCardListWrapper(filter: BusinessCardListFilter): BusinessCardListWrapper =
        BusinessCardListWrapperImpl(
            DependencyProvider.create { BusinessCardCollectionProvider.instance() },
            mapper = { it.map() },
            indexedMapper = { IndexedValue(it.index.toInt(), it.item.map()) },
            filter
        )

    /**@SelfDocumented*/
    @BusinessCardListViewScope
    @Provides
    fun provideBusinessCardListMapper(
        clicksWrapper: ClicksWrapper, scope: LifecycleCoroutineScope
    ): BusinessCardListMapper =
        BusinessCardListMapper(
            clicksWrapper = clicksWrapper,
            scope = scope
        )

    /**@SelfDocumented*/
    @BusinessCardListViewScope
    @Provides
    fun provideBusinessCardListFilter(personUuid: UUID): BusinessCardListFilter =
        BusinessCardListFilter(personUuid = personUuid)

    /**@SelfDocumented*/
    @BusinessCardListViewScope
    @Provides
    fun provideClicksWrapper(): ClicksWrapper = ClicksWrapper()

    /**@SelfDocumented*/
    @BusinessCardListViewScope
    @Provides
    internal fun provideRouter(
        dependency: BusinessCardListDependency,
        containerId: Int
    ): BusinessCardListRouter = BusinessCardListRouterImpl(dependency, containerId)

    /**@SelfDocumented*/
    @BusinessCardListViewScope
    @Provides
    fun provideStubFactory(resourceProvider: ResourceProvider): BusinessCardListStubFactory =
        BusinessCardListStubFactory(resourceProvider)

    /**@SelfDocumented*/
    @BusinessCardListViewScope
    @Provides
    fun provideQueueInteractor(provider: BusinessCardDomainCommandProvider): BusinessCardListInteractor =
        BusinessCardListInteractorImpl(provider.getWrapper())
}

/**@SelfDocumented*/
internal typealias BusinessCardListViewModel =
    ListComponentViewViewModel<ItemWithSection<AnyItem>, BusinessCardListFilter, BusinessCard>

/**
 * Маппер для преобразование модели визитки контроллера во вью модель
 */
fun ControllerBusinessCard.map(): BusinessCard = BusinessCard(
    id,
    createdTs,
    title,
    personName,
    personRole,
    personPhoto,
    snapshotUrl,
    ArrayList(links.map { it.map() }),
    pinned,
    style?.map(),
    contacts.map { mapProfileContactToDataModel(it) }
)

private fun ControllerBusinessCardStyle.map(): BusinessCardStyle = BusinessCardStyle(
    id,
    name,
    selector,
    parent,
    parentName,
    authorChanged,
    createdTime,
    updatedTime,
    enabled,
    clientId,
    type,
    themeType,
    uuid,
    scopeType,
    scope,
    properties?.let { mapControllerBusinessCardStyleProperties(it) }
)

private fun mapControllerBusinessCardStyleProperties(businessCardStyleProperties: ControllerBusinessCardStyleProperties): BusinessCardStyleProperties {
    return BusinessCardStyleProperties(
        businessCardStyleProperties.accent,
        businessCardStyleProperties.background,
        businessCardStyleProperties.buttonTextColor,
        businessCardStyleProperties.dominantColorRGB,
        businessCardStyleProperties.headers,
        businessCardStyleProperties.text,
        businessCardStyleProperties.logo,
        businessCardStyleProperties.picture,
        businessCardStyleProperties.texture
    )
}

//TODO договориться с маппером EmployeeMapper.kt открыть маппинг
private fun mapProfileContactToDataModel(profileContact: PersonContactModel): ProfileContact {
    return ProfileContact(
        profileContact.uuid,
        profileContact.personUuid,
        mapVisibilityStatusToDataModel(profileContact.visibility),
        mapVisibilityStatusToDataModel(profileContact.minVisibility),
        mapProfileContactTypeToDataModel(profileContact.type),
        SpannableString(profileContact.info),
        profileContact.verified,
        profileContact.editable,
        profileContact.personal,
        profileContact.bindedMessengers ?: "",
    )
}

private fun mapVisibilityStatusToDataModel(visibilityStatus: VisibilityStatus): ProfileVisibilityStatus {
    return when (visibilityStatus) {
        VisibilityStatus.NONE -> ProfileVisibilityStatus.NONE
        VisibilityStatus.FOR_COLLEAGUES -> ProfileVisibilityStatus.FOR_COLLEAGUES
        VisibilityStatus.FOR_ALL -> ProfileVisibilityStatus.FOR_ALL
    }
}

private fun mapProfileContactTypeToDataModel(profileContactType: PersonContactType): ProfileContactType {
    return when (profileContactType) {
        PersonContactType.WORK_PHONE -> ProfileContactType.WORK_PHONE
        PersonContactType.MOBILE_PHONE -> ProfileContactType.MOBILE_PHONE
        PersonContactType.HOME_PHONE -> ProfileContactType.HOME_PHONE
        PersonContactType.EMAIL -> ProfileContactType.EMAIL
        PersonContactType.SKYPE -> ProfileContactType.SKYPE
        PersonContactType.TELEGRAM -> ProfileContactType.TELEGRAM
        else -> ProfileContactType.OTHER
    }
}

/**
 * Маппер для преобразование модели ссылки контроллера во вью модель
 */
fun ControllerBusinessCardLink.map(): BusinessCardLink = BusinessCardLink(title, url)