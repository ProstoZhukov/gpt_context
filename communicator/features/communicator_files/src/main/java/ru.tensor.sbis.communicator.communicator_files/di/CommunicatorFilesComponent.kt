package ru.tensor.sbis.communicator.communicator_files.di

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesController
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesView
import ru.tensor.sbis.communicator.communicator_files.utils.CommunicatorFilesAttachmentViewPool
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import java.util.UUID

/**
 * DI компонент модуля файлов переписки.
 * @author da.zhukov
 */
@CommunicatorFilesScope
@Component(
    modules = [CommunicatorFilesModule::class],
    dependencies = [CommonSingletonComponent::class]
)
internal interface CommunicatorFilesComponent {

    fun injector(): Injector

    val themedContext: SbisThemedContext

    val viewFactory: (View) -> CommunicatorFilesView

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance scope: LifecycleCoroutineScope,
            @BindsInstance viewModelStoreOwner: ViewModelStoreOwner,
            commonSingletonComponent: CommonSingletonComponent,
            @BindsInstance themeId: UUID,
            @BindsInstance viewPool: CommunicatorFilesAttachmentViewPool
        ): CommunicatorFilesComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(
            fragment: Fragment,
            viewFactory: (View) -> CommunicatorFilesView
        ): CommunicatorFilesController
    }
}