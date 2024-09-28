package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.di

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.BuildConfig
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.data.CRMConnectionFilterHolder
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.data.CRMConnectionListCollectionWrapper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.data.CRMConnectionListCollectionWrapperImpl
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.mapper.CRMConnectionItemClickHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.mapper.CRMConnectionListMapper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui.CRMConnectionListComponentFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui.CRMConnectionListView
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui.CRMConnectionListViewImpl
import ru.tensor.sbis.consultations.generated.ConnectionCollectionProvider
import ru.tensor.sbis.consultations.generated.ConnectionFilter
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.databinding.CommunicatorCrmConnectionFragmentBinding
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.HighlightsColorProvider
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import java.util.UUID

/**
 * DI модуль.
 *
 * @author da.zhukov
 */
@Module
internal class CRMConnectionListModule {

    @CRMConnectionListScope
    @Provides
    fun provideViewFactory(
        listComponentFactory: CRMConnectionListComponentFactory,
        crmConnectionItemClickHelper: CRMConnectionItemClickHelper
    ): (View) -> CRMConnectionListView {
        return {
            CRMConnectionListViewImpl(
                CommunicatorCrmConnectionFragmentBinding.bind(it),
                listComponentFactory,
                crmConnectionItemClickHelper
            )
        }
    }


    @CRMConnectionListScope
    @Provides
    fun provideStoreFactory(): StoreFactory {
        return return if (BuildConfig.DEBUG) {
            LoggingStoreFactory(AndroidStoreFactory.timeTravel())
        } else {
            AndroidStoreFactory.default()
        }
    }

    @CRMConnectionListScope
    @Provides
    fun provideCRMConnectionFilterHolder(): CRMConnectionFilterHolder {
        return CRMConnectionFilterHolder(ConnectionFilter().apply {
            operatorVisibility = true
        })
    }

    @CRMConnectionListScope
    @Provides
    fun provideCRMConnectionListCollectionWrapper(
        connectionCollectionProvider: DependencyProvider<ConnectionCollectionProvider>,
        crmConnectionFilterHolder: CRMConnectionFilterHolder
    ): CRMConnectionListCollectionWrapper {
        return CRMConnectionListCollectionWrapperImpl(connectionCollectionProvider, crmConnectionFilterHolder)
    }

    @CRMConnectionListScope
    @Provides
    fun provideConnectionCollectionProvider(): DependencyProvider<ConnectionCollectionProvider> =
        DependencyProvider.create(ConnectionCollectionProvider::instance)

    @CRMConnectionListScope
    @Provides
    fun provideCRMConnectionListMapper(
        context: SbisThemedContext,
        highlightsColorProvider: HighlightsColorProvider,
        crmConnectionItemClickHelper: CRMConnectionItemClickHelper,
        selectedItems: MutableLiveData<List<UUID>>
    ): CRMConnectionListMapper {
        return CRMConnectionListMapper(
            context,
            highlightsColorProvider,
            crmConnectionItemClickHelper.onItemCheckboxClick,
            selectedItems
        )
    }

    @CRMConnectionListScope
    @Provides
    fun provideCRMConnectionItemClickHelper(
        scope: LifecycleCoroutineScope
    ): CRMConnectionItemClickHelper {
        return CRMConnectionItemClickHelper(scope)
    }

    @CRMConnectionListScope
    @Provides
    fun provideCRMConnectionListComponentFactory(
        viewModelStoreOwner: ViewModelStoreOwner,
        wrapper: CRMConnectionListCollectionWrapper,
        mapper: CRMConnectionListMapper
    ): CRMConnectionListComponentFactory {
        return CRMConnectionListComponentFactory(
            viewModelStoreOwner,
            wrapper,
            mapper
        )
    }

    @CRMConnectionListScope
    @Provides
    fun provideHighlightsColorProvider(context: SbisThemedContext) =
        HighlightsColorProvider(context)
}        