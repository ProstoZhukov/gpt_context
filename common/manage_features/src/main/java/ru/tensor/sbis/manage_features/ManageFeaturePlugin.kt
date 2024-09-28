package ru.tensor.sbis.manage_features

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.verification_decl.account.PersonalAccount
import ru.tensor.sbis.verification_decl.account.UserAccount
import ru.tensor.sbis.verification_decl.login.CurrentAccount
import ru.tensor.sbis.verification_decl.login.CurrentPersonalAccount
import ru.tensor.sbis.manage_features.contract.ManageFeaturesDependency
import ru.tensor.sbis.manage_features.data.ManageFeaturesFeatureImpl
import ru.tensor.sbis.manage_features.data.di.ManageFeaturesComponentInitializer
import ru.tensor.sbis.manage_features.domain.ManageFeaturesFeature
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин для управления фичами.
 *
 * @author kv.martyshenko
 */
object ManageFeaturePlugin : BasePlugin<Unit>() {
    private val manageFeaturesFeature: ManageFeaturesFeature by lazy {
        ManageFeaturesFeatureImpl()
    }
    internal val singletonComponent by lazy {
        ManageFeaturesComponentInitializer(object : ManageFeaturesDependency {
            override fun getCurrentAccount(): UserAccount? {
                return currentAccountProvider.get().getCurrentAccount()
            }

            override fun getCurrentPersonalAccount(): PersonalAccount {
                return currentPersonalAccountProvider.get().getCurrentPersonalAccount()
            }

        }).init(commonSingletonComponentProvider.get())
    }

    private lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    private lateinit var currentAccountProvider: FeatureProvider<CurrentAccount>
    private lateinit var currentPersonalAccountProvider: FeatureProvider<CurrentPersonalAccount>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(ManageFeaturesFeature::class.java) { manageFeaturesFeature }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
        .require(CurrentAccount::class.java) { currentAccountProvider = it }
        .require(CurrentPersonalAccount::class.java) { currentPersonalAccountProvider = it }
        .build()

    override val customizationOptions: Unit = Unit
}