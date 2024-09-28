package ru.tensor.sbis.onboarding.ui.utils

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StyleRes
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.onboarding.R
import ru.tensor.sbis.onboarding.contract.providers.content.BasePage
import ru.tensor.sbis.onboarding.domain.OnboardingRepository
import ru.tensor.sbis.onboarding.ui.utils.ThemeProvider.ClientMode.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Провайдер тем экранов фич
 *
 * @author as.chadov
 */
class ThemeProvider @Inject constructor(
    private val appContext: Context,
    @Named("isDialogContent")
    private val isDialogContent: Boolean,
    private val repository: OnboardingRepository
) {
    /**
     * Возвращает идентификатор темы хоста
     *
     * @param context контекст для получения темы [onboardingHostTheme] приложения
     */
    @StyleRes
    fun getHostTheme(context: Context) = when (getClientMode(context)) {
        PHONE  ->
            context.getDataFromAttrOrNull(R.attr.onboardingHostTheme, false)
                ?: R.style.HostTheme
        TABLET ->
            context.getDataFromAttrOrNull(R.attr.onboardingHostDialogTheme, false)
                ?: R.style.HostTheme_Dialog
        TV     ->
            context.getDataFromAttrOrNull(R.attr.onboardingHostTvTheme, false)
                ?: R.style.HostTheme_Tv
    }

    /**
     * Возвращает идентификатор темы экрана фичи
     *
     * @param context контекст для получения темы [onboardingFeatureTheme] приложения
     * @param uuid идентификатор экрана фичи [FeatureFragment]
     */
    @StyleRes
    fun getFeatureTheme(
        context: Context,
        uuid: String
    ): Int {
        val customTheme = getCustomFeatureTheme(context)
        val specificTheme = repository.findPageSafely(uuid)
            .takeIf { it is BasePage }
            ?.let {
                it as BasePage
                when (getClientMode(context)) {
                    PHONE  -> it.style.themeResId
                    TABLET -> it.style.tabletThemeResId
                    TV     -> 0
                }
            }
        return when {
            specificTheme.let { it != null && it != 0 } -> specificTheme!!
            customTheme.let { it != null && it != 0 }   -> customTheme!!
            else                                        -> getDefaultFeatureTheme(context)
        }
    }

    val isTV: Boolean
        get() = isTV(appContext)

    val isPreventRotation: Boolean
        get() = getClientMode(appContext) == PHONE

    @StyleRes
    private fun getCustomFeatureTheme(context: Context): Int? =
        when (getClientMode(context)) {
            PHONE  -> context.getDataFromAttrOrNull(R.attr.onboardingFeatureTheme, false)
            TABLET -> context.getDataFromAttrOrNull(R.attr.onboardingFeatureDialogTheme, false)
            TV     -> context.getDataFromAttrOrNull(R.attr.onboardingFeatureTvTheme, false)
        }

    @StyleRes
    private fun getDefaultFeatureTheme(context: Context): Int =
        when (getClientMode(context)) {
            PHONE  -> R.style.FeatureTheme
            TABLET -> R.style.FeatureTheme_Dialog
            TV     -> R.style.FeatureTheme_Tv
        }

    private fun getClientMode(context: Context): ClientMode =
        when {
            isDialogOnTablet(context) -> TABLET
            isTV                      -> TV
            else                      -> PHONE
        }

    private fun isDialogOnTablet(context: Context): Boolean {
        return isDialogContent && isTablet(context)
    }

    private enum class ClientMode {
        PHONE,
        /* режим планшета с приветственным экраном на диалогах */
        TABLET,
        TV
    }

    companion object {

        fun isTablet(context: Context) = DeviceConfigurationUtils.isTablet(context)

        fun isTV(context: Context): Boolean {
            val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
        }
    }
}