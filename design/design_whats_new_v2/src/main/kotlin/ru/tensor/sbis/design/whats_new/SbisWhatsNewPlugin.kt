package ru.tensor.sbis.design.whats_new

import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.common.util.AppConfig
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.whats_new.feature.SbisWhatsNewFeature
import ru.tensor.sbis.design.whats_new.feature.SbisWhatsNewFeatureImpl
import ru.tensor.sbis.design.whats_new.model.SbisWhatsNewButtonStyle
import ru.tensor.sbis.design.whats_new.model.SbisWhatsNewDisplayBehavior
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.plugin_struct.requireIf
import ru.tensor.sbis.verification_decl.login.LoginInterface
import timber.log.Timber

/**
 * Плагин для компонента "Что нового".
 *
 * @author ps.smirnyh
 */
object SbisWhatsNewPlugin : BasePlugin<SbisWhatsNewPlugin.CustomizationOptions>() {

    private var loginInterface: FeatureProvider<LoginInterface>? = null

    internal val whatsNewFeature by lazy {
        SbisWhatsNewFeatureImpl(application.applicationContext, loginInterface?.get())
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(SbisWhatsNewFeature::class.java) { whatsNewFeature }
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .requireIf(
                customizationOptions.displayBehavior == SbisWhatsNewDisplayBehavior.PER_USER,
                LoginInterface::class.java
            ) { loginInterface = it }
            .build()
    }

    override val customizationOptions: CustomizationOptions = CustomizationOptions()

    override fun doAfterInitialize() {
        loginInterface?.get()?.userAccountObservable?.subscribe(
            {
                whatsNewFeature.showConditionManager.checkShowing()
            },
            { error ->
                val version = AppConfig.getApplicationCurrentVersion()
                Timber.e(error, "Unable to save app version. Version is $version")
            }
        ) ?: whatsNewFeature.showConditionManager.checkShowing()

        require(customizationOptions.whatsNewRes != ID_NULL) {
            "The value of whatsNewRes is not set in SbisWhatsNewPlugin."
        }
    }

    /**
     * Опции компонента "Что нового".
     */
    class CustomizationOptions internal constructor() {

        /**
         * Тип логотипа в шапке фрагмента "Что нового".
         */
        var bannerLogo: SbisLogoType = SbisLogoType.TextIcon

        /**
         * Описание "Что нового" с разделителем пунктов в виде "|".
         * Стандартный id ресурса, который заполняется автоматически роботом на CI.
         * Нужно создать файл строковых ресурсов whats_new_strings.xml с элементом whats_new_items.
         * В него будет записываться нужный текст при сборке приложения.
         */
        @StringRes
        var whatsNewRes: Int = ID_NULL

        /**
         * Поддержка персонализации события отображения "Что нового".
         * Если [SbisWhatsNewDisplayBehavior.PER_USER], каждому пользователю отдельно будет показан "Что нового".
         * Если [SbisWhatsNewDisplayBehavior.ONLY_ONCE], то независимо от пользователя один раз после обновления.
         */
        var displayBehavior: SbisWhatsNewDisplayBehavior = SbisWhatsNewDisplayBehavior.ONLY_ONCE

        /** Тип кнопки начать. */
        var buttonStyle: SbisWhatsNewButtonStyle = SbisWhatsNewButtonStyle.BRAND
    }
}