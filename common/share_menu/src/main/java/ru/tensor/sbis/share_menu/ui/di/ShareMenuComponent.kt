package ru.tensor.sbis.share_menu.ui.di

import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.share_menu.contract.ShareMenuDependency
import ru.tensor.sbis.share_menu.ui.view.ShareMenuController
import ru.tensor.sbis.toolbox_decl.share.ShareData
import javax.inject.Scope

/**
 * DI компонент меню для "поделиться".
 *
 * @author vv.chekurda
 */
@Component(modules = [ShareMenuModule::class])
@ShareMenuScope
internal interface ShareMenuComponent {

    fun injector(): Injector

    /**
     * Фабрика для создания di компонента [ShareMenuComponent].
     */
    @Component.Factory
    interface Factory {

        /**
         * Создать di компонент.
         *
         * @param appContext контекст приложения.
         * @param contentContainerId идентификатор контейнера для размещения контента.
         * @param shareData данные для "поделиться".
         * @param quickShareKey строковый идентификатор для быстрого шаринга.
         * @param dependency зависимости компонента меню.
         */
        fun create(
            @BindsInstance appContext: Context,
            @BindsInstance @IdRes contentContainerId: Int,
            @BindsInstance shareData: ShareData,
            @BindsInstance quickShareKey: String?,
            @BindsInstance dependency: ShareMenuDependency
        ): ShareMenuComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: Fragment) : ShareMenuController
    }
}

@Scope
@Retention
internal annotation class ShareMenuScope