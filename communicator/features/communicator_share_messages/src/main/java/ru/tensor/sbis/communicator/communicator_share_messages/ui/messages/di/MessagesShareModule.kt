package ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.di

import android.content.Context
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communicator.common.data.theme.ConversationMapper
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelper
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelperImpl
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.ui.MessagesShareRouter
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.ui.MessagesShareRouterImpl
import ru.tensor.sbis.communicator.communicator_share_messages.utils.ContactsInfoUtil
import ru.tensor.sbis.communicator.communicator_share_messages.utils.OfflineLinksUtil
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
/**
 * Di-модуль экрана шаринга в сообщения.
 *
 * @author dv.baranov
 */
@Module
internal class MessagesShareModule {

    @Provides
    @MessagesShareScope
    fun provideStoreFactory(): StoreFactory {
        return AndroidStoreFactory(DefaultStoreFactory())
    }

    @Provides
    @MessagesShareScope
    fun provideMessagesShareRouter(): MessagesShareRouter =
        MessagesShareRouterImpl()

    @Provides
    @MessagesShareScope
    fun provideQuickShareHelper(context: Context): QuickShareHelper =
        QuickShareHelperImpl(context)

    @Provides
    @MessagesShareScope
    fun provideConversationMapper(context: Context): ConversationMapper =
        CommunicatorCommonComponent.getInstance(context).conversationMapper

    @Provides
    @MessagesShareScope
    fun provideContactsInfoUtil(context: Context): ContactsInfoUtil =
        ContactsInfoUtil(context)

    @Provides
    @MessagesShareScope
    fun provideOfflineLinksUtil(context: Context): OfflineLinksUtil =
        OfflineLinksUtil(context)
}
