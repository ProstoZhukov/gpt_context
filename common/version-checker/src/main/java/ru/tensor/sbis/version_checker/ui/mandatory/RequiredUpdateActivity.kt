package ru.tensor.sbis.version_checker.ui.mandatory

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import ru.tensor.sbis.android_ext_decl.BuildConfig
import ru.tensor.sbis.base_components.AdjustResizeActivity
import ru.tensor.sbis.common.util.ContextReplacer
import ru.tensor.sbis.design.change_theme.contract.SelfThemedActivity
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard.LegacyEntryPoint
import ru.tensor.sbis.verification_decl.auth.AuthAware.CheckAuthStrategy
import ru.tensor.sbis.version_checker.R
import ru.tensor.sbis.version_checker.VersionCheckerPlugin
import ru.tensor.sbis.version_checker_decl.VersionedComponent

/**
 * Хост активити для фрагмента принудительного обновления [RequiredUpdateFragment].
 *
 * @author as.chadov
 */
internal class RequiredUpdateActivity :
    AdjustResizeActivity(),
    VersionedComponent,
    SelfThemedActivity,
    LegacyEntryPoint {

    override val checkAuthStrategy: CheckAuthStrategy
        get() = CheckAuthStrategy.Skip

    override fun attachBaseContext(base: Context?) {
        EntryPointGuard.activityAssistant.interceptAttachBaseContextLegacy(this, base) {
            super.attachBaseContext(ContextReplacer.replace(it))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
        setFullscreen()
        setContentView(R.layout.versioning_activity_required_update)

        if (supportFragmentManager.findFragmentByTag(RequiredUpdateFragment.TAG) == null) {
            supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.versioning_fade_in_animation, 0)
                replace(contentViewId, RequiredUpdateFragment.newInstance(), RequiredUpdateFragment.TAG)
            }.commitAllowingStateLoss()
        }
    }

    override fun getThemeRes() = ThemeContextBuilder(
        context = this,
        defStyleAttr = R.attr.versioningTheme,
        defaultStyle = R.style.VersioningUpdateTheme
    ).buildThemeRes()

    private fun setTheme() {
        val overrideThemeApplication = VersionCheckerPlugin.customizationOptions.overrideThemeApplication
        val themeSource: Context = if (overrideThemeApplication) application else this

        var themeId = themeSource.getDataFromAttrOrNull(R.attr.versioningTheme, false)
        if (themeId == null) {
            themeId = R.style.VersioningUpdateTheme
        }
        setTheme(themeId)
    }

    @Suppress("DEPRECATION")
    private fun setFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    override fun getContentViewId(): Int = R.id.versioning_activity_content

    companion object {
        private const val ACTION_FORCED_UPDATE_ACTIVITY = BuildConfig.MAIN_APP_ID + ".FORCED_UPDATE_ACTIVITY"

        /**
         * Создание интента [RequiredUpdateActivity] с нужными флагами.
         */
        fun createIntent(): Intent = Intent(ACTION_FORCED_UPDATE_ACTIVITY).apply {
            setPackage(BuildConfig.MAIN_APP_ID)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
    }
}