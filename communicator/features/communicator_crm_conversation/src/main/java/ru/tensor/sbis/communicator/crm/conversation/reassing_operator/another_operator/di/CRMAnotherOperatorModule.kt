package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.di

import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin
import ru.tensor.sbis.communicator.crm.conversation.databinding.CommunicatorCrmAnotherOperatorFragmentBinding
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.data.CRMAnotherOperatorCollectionWrapper
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.data.CRMAnotherOperatorCollectionWrapperImpl
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.data.CRMAnotherOperatorFilterHolder
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.mapper.CRMAnotherOperatorMapper
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.store.CRMAnotherOperatorInteractor
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.store.CRMAnotherOperatorInteractorImpl
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.store.CRMAnotherOperatorStoreFactory
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.ui.CRMAnotherOperatorListComponentFactory
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.ui.CRMAnotherOperatorView
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.ui.CRMAnotherOperatorViewImpl
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorParams
import ru.tensor.sbis.consultations.generated.ConsultationService
import ru.tensor.sbis.consultations.generated.OperatorCollectionFilter
import ru.tensor.sbis.consultations.generated.OperatorCollectionProvider
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import java.util.UUID

/**
 * DI модуль переназначения оператору.
 * @author da.zhukov
 */
@Module
internal class CRMAnotherOperatorModule {

    @Provides
    @CRMAnotherOperatorScope
    fun provideViewFactory(
        listComponentFactory: CRMAnotherOperatorListComponentFactory
    ): (View) -> CRMAnotherOperatorView {
        return {
            CRMAnotherOperatorViewImpl(
                CommunicatorCrmAnotherOperatorFragmentBinding.bind(it),
                listComponentFactory
            )
        }
    }

    @Provides
    @CRMAnotherOperatorScope
    fun provideStoreFactory(): StoreFactory {
        return AndroidStoreFactory(DefaultStoreFactory())
    }

    @Provides
    @CRMAnotherOperatorScope
    fun provideCRMAnotherOperatorListComponentFactory(
        viewModelStoreOwner: ViewModelStoreOwner,
        wrapper: CRMAnotherOperatorCollectionWrapper,
        mapper: CRMAnotherOperatorMapper
    ): CRMAnotherOperatorListComponentFactory {
        return CRMAnotherOperatorListComponentFactory(
            viewModelStoreOwner,
            wrapper,
            mapper
        )
    }

    @Provides
    @CRMAnotherOperatorScope
    fun provideCRMAnotherOperatorCollectionWrapper(
        operatorCollectionProvider: DependencyProvider<OperatorCollectionProvider>,
        filterHolder: CRMAnotherOperatorFilterHolder
    ): CRMAnotherOperatorCollectionWrapper {
        return CRMAnotherOperatorCollectionWrapperImpl(
            operatorCollectionProvider,
            filterHolder
        )
    }

    @Provides
    @CRMAnotherOperatorScope
    fun provideOperatorCollectionProvider(): DependencyProvider<OperatorCollectionProvider> =
        DependencyProvider.create(OperatorCollectionProvider::instance)

    @Provides
    @CRMAnotherOperatorScope
    fun provideCRMAnotherOperatorMapper(): CRMAnotherOperatorMapper =
        CRMAnotherOperatorMapper()

    @Provides
    @CRMAnotherOperatorScope
    fun provideCRMAnotherOperatorStoreFactory(
        storeFactory: StoreFactory,
        listComponentFactory: CRMAnotherOperatorListComponentFactory,
        filterHolder: CRMAnotherOperatorFilterHolder,
        crmAnotherOperatorInteractor: CRMAnotherOperatorInteractor,
        params: CRMAnotherOperatorParams
    ): CRMAnotherOperatorStoreFactory {
        return CRMAnotherOperatorStoreFactory(
            storeFactory,
            listComponentFactory,
            filterHolder,
            crmAnotherOperatorInteractor,
            params
        )
    }

    @Provides
    @CRMAnotherOperatorScope
    fun provideFilterHolder(params: CRMAnotherOperatorParams): CRMAnotherOperatorFilterHolder {
        return CRMAnotherOperatorFilterHolder(
            OperatorCollectionFilter().apply {
                params.operatorId?.let {
                    excludeUuids = arrayListOf(it)
                }
                channelId = params.channelId
            }
        )
    }

    @Provides
    @CRMAnotherOperatorScope
    fun provideConsultationService(): DependencyProvider<ConsultationService> =
        DependencyProvider.create(ConsultationService::instance)

    @Provides
    @CRMAnotherOperatorScope
    fun provideCRMAnotherOperatorInteractor(
        consultationServiceProvider: DependencyProvider<ConsultationService>,
        params: CRMAnotherOperatorParams
    ): CRMAnotherOperatorInteractor {
        return CRMAnotherOperatorInteractorImpl(
            consultationServiceProvider,
            params
        )
    }
}