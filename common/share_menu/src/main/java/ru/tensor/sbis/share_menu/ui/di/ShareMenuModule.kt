package ru.tensor.sbis.share_menu.ui.di

import android.content.Context
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import ru.tensor.sbis.share_menu.contract.ShareMenuDependency
import ru.tensor.sbis.share_menu.ui.store.domain.ShareHandlersProvider
import ru.tensor.sbis.share_menu.utils.ShareAnalyticsHelper
import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * DI модуль меню для "поделиться".
 *
 * @author vv.chekurda
 */
@Module
internal class ShareMenuModule {

    @Provides
    @ShareMenuScope
    fun provideStoreFactory(): StoreFactory =
        AndroidStoreFactory(DefaultStoreFactory())

    @Provides
    @ShareMenuScope
    fun provideResourceProvider(context: Context): ResourceProvider =
        ResourceProvider(context)

    @Provides
    @ShareMenuScope
    fun provideLoginInterface(dependency: ShareMenuDependency): LoginInterface =
        dependency.loginInterface

    @Provides
    @ShareMenuScope
    fun provideShareHandlersProvider(
        dependency: ShareMenuDependency
    ): ShareHandlersProvider =
        ShareHandlersProvider(
            appShareHandlers = dependency.shareHandlers,
            navigationService = dependency.navigationService,
            loginInterface = dependency.loginInterface,
            permissionFeature = dependency.permissionFeature
        )

    @Provides
    @ShareMenuScope
    fun provideFileUriUtil(context: Context): FileUriUtil =
        FileUriUtil(context)

    @Provides
    @ShareMenuScope
    fun provideShareAnalyticsHelper(): ShareAnalyticsHelper = ShareAnalyticsHelper()
}