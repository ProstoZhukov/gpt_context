package ru.tensor.sbis.communicator.crm.conversation.di.singleton

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService

/**
 * Singleton DI компонент сабмодуля сообщений CRM.
 *
 * @author da.zhukov
 */
@Module
class CRMConversationSingletonModule {

    @Provides
    @PerApp
    internal fun provideLocalFeatureToggleService(
        context: Context,
    ): LocalFeatureToggleService =
        LocalFeatureToggleService(context)
}