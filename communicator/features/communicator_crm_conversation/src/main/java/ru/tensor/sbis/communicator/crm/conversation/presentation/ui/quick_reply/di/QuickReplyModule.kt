package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.di

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.crm.conversation.databinding.CommunicatorCrmQuickReplyBinding
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data.QuickReplyCollectionWrapper
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data.QuickReplyCollectionWrapperImpl
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data.QuickReplyFilterHolder
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data.mappers.QuickReplyMapper
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.QuickReplyListComponentFactory
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.QuickReplyView
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.QuickReplyViewImpl
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.helpers.QuickReplyClickActionHandler
import ru.tensor.sbis.communicator.declaration.crm.model.QuickReplyParams
import ru.tensor.sbis.consultations.generated.QuickReplyCollectionProvider
import ru.tensor.sbis.consultations.generated.QuickReplyFilter
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory

/**
 * @author dv.baranov
 */

/** @SelfDocumented */
@Module
internal class QuickReplyModule {

    @Provides
    @QuickReplyScope
    fun provideViewFactory(
        listComponentFactory: QuickReplyListComponentFactory,
        params: QuickReplyParams,
        quickReplyClickActionHandler: QuickReplyClickActionHandler,
    ): (View) -> QuickReplyView {
        return {
            QuickReplyViewImpl(
                CommunicatorCrmQuickReplyBinding.bind(it),
                listComponentFactory,
                params,
                quickReplyClickActionHandler,
            )
        }
    }

    @Provides
    @QuickReplyScope
    fun provideStoreFactory(): StoreFactory {
        return AndroidStoreFactory(DefaultStoreFactory())
    }

    @Provides
    @QuickReplyScope
    fun provideQuickReplyListComponentFactory(
        viewModelStoreOwner: ViewModelStoreOwner,
        wrapper: QuickReplyCollectionWrapper,
        mapper: QuickReplyMapper,
    ): QuickReplyListComponentFactory = QuickReplyListComponentFactory(
        viewModelStoreOwner,
        wrapper,
        mapper,
    )

    @Provides
    @QuickReplyScope
    fun provideQuickReplyCollectionWrapper(
        quickReplyCollectionProvider: DependencyProvider<QuickReplyCollectionProvider>,
        filterHolder: QuickReplyFilterHolder,
    ): QuickReplyCollectionWrapper = QuickReplyCollectionWrapperImpl(
        quickReplyCollectionProvider,
        filterHolder,
    )

    @Provides
    @QuickReplyScope
    fun provideQuickReplyCollectionProvider(): DependencyProvider<QuickReplyCollectionProvider> =
        DependencyProvider.create(QuickReplyCollectionProvider::instance)

    @Provides
    @QuickReplyScope
    fun provideFilterHolder(
        params: QuickReplyParams,
    ): QuickReplyFilterHolder = QuickReplyFilterHolder(
        QuickReplyFilter(
            isGreeting = false,
            channelId = params.channelUUID,
            parentId = null,
            isEditSearch = params.isEditSearch,
            searchQuery = null,
        ),
    )

    @Provides
    @QuickReplyScope
    fun provideQuickReplyMapper(
        actionHandler: QuickReplyClickActionHandler,
        params: QuickReplyParams,
    ): QuickReplyMapper = QuickReplyMapper(actionHandler, params)

    @Provides
    @QuickReplyScope
    fun provideQuickReplyClickActionHandler(
        scope: LifecycleCoroutineScope,
    ): QuickReplyClickActionHandler = QuickReplyClickActionHandler(scope)
}
