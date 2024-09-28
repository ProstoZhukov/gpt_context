package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.di

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
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.CrmReassignCommentController
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.CrmReassignCommentView
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.store.CrmReassignCommentStoreFactory
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorParams

/**
 * Компонент для предоставления зависимостей фрагменту.
 */
@CrmReassignCommentScope
@Component(
    modules = [(CrmReassignCommentModule::class)]
)
internal interface CrmReassignCommentComponent {

    fun injector(): Injector

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance viewFactory: (View) -> CrmReassignCommentView,
            @BindsInstance params: CRMAnotherOperatorParams,
            @BindsInstance context: Context
        ): CrmReassignCommentComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: Fragment): CrmReassignCommentController
    }
}

/**
 * Модуль для разрешения зависимостей [CrmReassignCommentComponent].
 */
@Module
internal class CrmReassignCommentModule {

    @Provides
    @CrmReassignCommentScope
    fun provideStoreFactory(): StoreFactory {
        return DefaultStoreFactory()
    }

    @Provides
    @CrmReassignCommentScope
    fun provideCrmReassignCommentStoreFactory(
        storeFactory: StoreFactory,
        params: CRMAnotherOperatorParams
    ): CrmReassignCommentStoreFactory {
        return CrmReassignCommentStoreFactory(
            storeFactory,
            params
        )
    }
}