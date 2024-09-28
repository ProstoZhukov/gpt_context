package ru.tensor.sbis.storage

import android.app.Application
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.storage.contract.ExternalStorageProvider
import ru.tensor.sbis.storage.contract.InternalStorageProvider
import ru.tensor.sbis.storage.contract.StorageFeature

/**
 * Плагин для хранилищ
 *
 * @author kv.martyshenko
 */
object StoragePlugin : BasePlugin<StoragePlugin.CustomizationOptions>() {
    private lateinit var storageFeature: StorageFeature

    private var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(ExternalStorageProvider::class.java) { storageFeature },
        FeatureWrapper(InternalStorageProvider::class.java) { storageFeature }
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .apply {
                if(customizationOptions.userDirEnabled) {
                    require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
                }
            }
            .build()
    }

    override val customizationOptions: CustomizationOptions  = CustomizationOptions()

    override fun initialize() {
        storageFeature = StorageFeature(application, loginInterfaceProvider?.get())
    }

    /**
     * Конфигурация
     */
    class CustomizationOptions internal constructor() {
        /**
         * Создаем ли отдельные директории под каждого пользователя.
         * Доступно только при наличии авторизации.
         */
        var userDirEnabled: Boolean = true
    }

}