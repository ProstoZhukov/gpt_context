package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.di

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.CrmChatFilterController
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.CrmChatFilterView
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.store.CrmChatFilterStoreFactory
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.communicator.declaration.crm.model.CRMRadioButtonFilterType
import ru.tensor.sbis.consultations.generated.ConsultationService
import javax.inject.Named

/**
 * Компонент для предоставления зависимостей фрагменту.
 */
@CrmChatFilterScope
@Component(
    modules = [(CrmChatFilterModule::class)]
)
internal interface CrmChatFilterComponent {

    fun injector(): Injector

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance viewFactory: (View) -> CrmChatFilterView,
            @Named(INITIAL_FILTER)
            @BindsInstance filterModel: CRMChatFilterModel,
            @BindsInstance context: Context
        ): CrmChatFilterComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: Fragment): CrmChatFilterController
    }
}

/**
 * Модуль для разрешения зависимостей [CrmChatFilterComponent].
 */
@Module
internal class CrmChatFilterModule {

    @Provides
    @CrmChatFilterScope
    fun provideStoreFactory(): StoreFactory {
        return DefaultStoreFactory()
    }

    @Provides
    @CrmChatFilterScope
    fun provideCrmChatFilterStoreFactory(
        storeFactory: StoreFactory,
        @Named(INITIAL_FILTER)initFilterModel: CRMChatFilterModel,
        @Named(DEFAULT_FILTER)defFilterModel: CRMChatFilterModel,
        context: Context
    ): CrmChatFilterStoreFactory {
        return CrmChatFilterStoreFactory(
            storeFactory,
            initFilterModel,
            defFilterModel,
            context
        )
    }

    @Provides
    @CrmChatFilterScope
    @Named(DEFAULT_FILTER)
    fun provideDefFilter(): CRMChatFilterModel{
        ConsultationService.instance()
        val isCurrentUserOperator = ConsultationService.instance().getIsCurrentUserOperator()
        return CRMChatFilterModel(
            type = if (isCurrentUserOperator) {
                CRMRadioButtonFilterType.MY
            } else {
                CRMRadioButtonFilterType.ALL
            }
        )
    }
}
// фильтр с которым был инициализирован экран.
const val INITIAL_FILTER = "INITIAL_FILTER"
// фильтр к которому может сброситься экран.
const val DEFAULT_FILTER = "DEFAULT_FILTER"