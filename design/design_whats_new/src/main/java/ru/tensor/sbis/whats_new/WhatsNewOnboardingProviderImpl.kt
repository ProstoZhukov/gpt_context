package ru.tensor.sbis.whats_new

import android.content.SharedPreferences
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.onboarding.contract.providers.OnboardingProvider
import ru.tensor.sbis.onboarding.contract.providers.ShowPriority
import ru.tensor.sbis.onboarding.contract.providers.content.Onboarding
import ru.tensor.sbis.onboarding.ui.utils.OnboardingPreferenceManager

/**
 * Реализация [OnboardingProvider] для предоставления в зависимости onboarding.
 *
 * @author ps.smirnyh
 */
internal class WhatsNewOnboardingProviderImpl(
    private val preferences: SharedPreferences,
    private val versionNumber: String,
    private val whatsNewText: String,
    private val loginInterface: LoginInterface? = null
) : OnboardingProvider {

    override val showPriority: ShowPriority = ShowPriority.LOW

    override fun getCustomOnboardingPreferenceManger(): OnboardingPreferenceManager =
        WhatsNewOnboardingPreferenceManagerImpl(preferences, versionNumber, whatsNewText, loginInterface)

    override fun getOnboardingContent() = Onboarding.create {
        header {
            imageResId = WhatsNewPlugin.customizationOptions.logoRes
            gravityToBottom = false
        }

        customPage {
            creator = { WhatsNewFragment() }
        }

        swipeLeaving = false

    }
}