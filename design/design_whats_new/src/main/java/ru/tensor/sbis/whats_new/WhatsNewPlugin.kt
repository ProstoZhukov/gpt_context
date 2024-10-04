package ru.tensor.sbis.whats_new

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import io.reactivex.Observable
import ru.tensor.sbis.common.util.AppConfig
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.onboarding.contract.providers.OnboardingProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.whats_new.WhatsNewOnboardingPreferenceManagerImpl.Companion.DEFAULT_USER
import timber.log.Timber

/**
 * Плагин для компонента "Что нового".
 *
 * @author ps.smirnyh
 */
object WhatsNewPlugin : BasePlugin<WhatsNewPlugin.CustomizationOptions>() {

    private var loginInterface: FeatureProvider<LoginInterface>? = null

    private val whatsNewOnboardingProviderImpl by lazy {
        WhatsNewOnboardingProviderImpl(
            application.getSharedPreferences(WHATSNEW_SHARED_PREFERENCES, Context.MODE_PRIVATE),
            AppConfig.getApplicationCurrentVersion(),
            application.getString(customizationOptions.whatsNewRes),
            loginInterface?.get()
        )
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(OnboardingProvider::class.java) { whatsNewOnboardingProviderImpl }
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .apply {
                if (customizationOptions.userAware) {
                    require(LoginInterface::class.java) { loginInterface = it }
                }
            }
            .build()
    }

    override val customizationOptions: CustomizationOptions = CustomizationOptions()

    @SuppressLint("CheckResult")
    override fun doAfterInitialize() {
        val accountObservable =
            loginInterface?.run { get().userAccountObservable }
            ?: Observable.just(DEFAULT_USER)

        accountObservable.subscribe(
            {
                whatsNewOnboardingProviderImpl.getCustomOnboardingPreferenceManger()
                    .restoreEntrance()
            },
            { error ->
                val version = AppConfig.getApplicationCurrentVersion()
                Timber.e(error, "Unable to save app version. Version is $version")
            }
        )
    }

    /**
     * Опции компонента "Что нового".
     */
    class CustomizationOptions internal constructor() {

        /**
         * Заголовок фрагмента "Что нового" в шапке.
         */
        //TODO убрать переменную https://dev.sbis.ru/opendoc.html?guid=45d7bcc5-7b10-445b-bb9e-335c6a1f3e90
        @Deprecated("Теперь заголовок передается вместе с лого в изображении")
        @StringRes
        var titleRes: Int = ID_NULL

        /**
         * Логотип приложения в шапке фрагмента "Что нового".
         */
        @DrawableRes
        var logoRes: Int = ID_NULL

        /**
         * Описание "Что нового" с разделителем пунктов в виде "|".
         * Стандартный id ресурса, который заполняется автоматически роботом на CI.
         * Нужно создать файл строковых ресурсов whats_new_strings.xml с элементом whats_new_items.
         * В него будет записываться нужный текст при сборке приложения.
         */
        @StringRes
        var whatsNewRes: Int = ID_NULL

        /**
         * Поддержки персонализации события отображения "Что нового"
         * Если включено, каждому пользователю отдельно будет показан "Что нового"
         * Если выключено, то независимо от пользователя
         */
        var userAware: Boolean = true
    }
}