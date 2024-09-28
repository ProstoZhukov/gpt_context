package ru.tensor.sbis.onboarding.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.onboarding.R
import ru.tensor.sbis.onboarding.ui.base.OnboardingBackPress
import ru.tensor.sbis.onboarding.ui.host.OnboardingHostFragmentImpl
import ru.tensor.sbis.toolbox_decl.language.LanguageProvider
import ru.tensor.sbis.user_activity_track.activity.UserActivityTrackable

/**
 * Хост Activity
 *
 * @author as.chadov
 */
internal class OnboardingActivity :
    AppCompatActivity(),
    UserActivityTrackable,
    EntryPointGuard.LegacyEntryPoint {

    // region UserActivityTrackable
    override val isTrackActivityEnabled: Boolean = true

    override val screenName: String by lazy { javaClass.name }
    //endregion

    override fun attachBaseContext(newBase: Context?) {
        EntryPointGuard.activityAssistant.interceptAttachBaseContextLegacy(this, replace(newBase)) {
            super.attachBaseContext(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val fragment = OnboardingHostFragmentImpl.newInstance()
            supportFragmentManager.beginTransaction()
                .run {
                    val tag = OnboardingHostFragmentImpl::class.java.canonicalName
                    replace(containerId, fragment, tag)
                    addToBackStack(tag)
                    commitAllowingStateLoss()
                }
        }
    }

    override fun onBackPressed() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is OnboardingBackPress && fragment.isVisible) {
                if ((fragment as OnboardingBackPress).onBackPressed()) {
                    return
                }
            }
        }
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.onboarding_slide_out_from_top_animation)
    }

    private fun setTheme() {
        val themeId = getDataFromAttrOrNull(R.attr.onboardingTheme, false)
            ?: R.style.OnboardingTheme
        setTheme(themeId)
    }

    private fun replace(context: Context?): Context? {
        return context
            ?.let(LanguageProvider::get)
            ?.updateContext(context)
            ?: context
    }

    companion object {
        const val containerId = android.R.id.content
    }
}