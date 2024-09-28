package ru.tensor.sbis.manage_features.data.di

import android.content.Context
import dagger.Component
import ru.tensor.sbis.verification_decl.account.PersonalAccount
import ru.tensor.sbis.verification_decl.account.UserAccount
import ru.tensor.sbis.manage_features.ManageFeaturePlugin
import ru.tensor.sbis.manage_features.contract.ManageFeaturesDependency

/**
 * Основной компонент модуля. несёт в себе зависимости необходимые для работы модуля
 */
@Component(dependencies = [ManageFeaturesDependency::class])
interface ManageFeaturesComponent {

    /** @SelfDocumented */
    val currentAccount: UserAccount?

    /** @SelfDocumented */
    val personalAccount: PersonalAccount

    @Component.Factory
    interface Factory {

        /** @SelfDocumented */
        fun create(dependency: ManageFeaturesDependency): ManageFeaturesComponent
    }

    companion object {

        fun get(context: Context): ManageFeaturesComponent {
            /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
            return ManageFeaturePlugin.singletonComponent
        }

    }
}
