package ru.tensor.sbis.onboarding_tour.domain.builders

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.onboarding_tour.OnboardingTourPlugin
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour
import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourDependency
import ru.tensor.sbis.onboarding_tour.data.TourContent
import ru.tensor.sbis.onboarding_tour.data.TourBanner
import ru.tensor.sbis.onboarding_tour.data.TourPage
import ru.tensor.sbis.onboarding_tour.data.TourPage.Companion.FIRST_POSITION
import ru.tensor.sbis.onboarding_tour.data.TourRules
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.DismissCommand
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingBannerConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingConfigurator
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingPageConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingRulesConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.data.DisplayBehavior.PER_USER

/**
 * Билдер тура по экранам приветсвия и настроек [OnboardingTour].
 * @param context контекст приложения
 *
 * @author as.chadov
 */
internal class OnboardingTourBuilder(
    private val context: Context,
    private val dependency: OnboardingTourDependency
) : BaseOnboardingTourDslBuilder<OnboardingTour>(),
    OnboardingConfigurator {

    constructor() : this(
        context = OnboardingTourPlugin.application,
        dependency = OnboardingTourPlugin.dependencies
    )

    private var rules: TourRules = TourRules.default
    private var banner: TourBanner = TourBanner.empty
    private var dismissCommand: DismissCommand? = null
    private var pages = mutableListOf<TourPage>()

    override fun rules(init: OnboardingRulesConfiguration.() -> Unit) {
        rules = OnboardingTourRulesBuilder().apply(init).build()
    }

    override fun defaultBanner(init: OnboardingBannerConfiguration.() -> Unit) {
        banner = OnboardingTourBannerBuilder().apply(init).build()
    }

    override fun page(init: OnboardingPageConfiguration.() -> Unit) {
        val newPage = OnboardingTourPageBuilder().apply(init).build()
        pages.add(newPage)
    }

    override fun onDismiss(command: DismissCommand) {
        dismissCommand = command
    }

    override fun build(): OnboardingTour {
        require(pages.isNotEmpty()) { "There must be at least one page." }
        val tourPages = if (pages.all { it.position == FIRST_POSITION }) {
            addPositions(pages)
        } else {
            require(hasUniquePositions(pages)) { "There are non-unique page positions." }
            pages.sortedBy(TourPage::position)
        }
        for (page in tourPages) {
            if (page.banner == TourBanner.empty) page.banner = banner
        }
        val isLoginRequired = rules.displayBehavior == PER_USER && dependency.loginInterface == null
        require(!isLoginRequired) {
            "Required to provide ${LoginInterface::class.java.simpleName} when " +
                "${OnboardingRulesConfiguration::class.java.simpleName} has showOnlyOnce = true"
        }
        if (!rules.swipeTransition) {
            require(tourPages.all { it.button.titleResId != ResourcesCompat.ID_NULL }) {
                "There should be buttons on every page of the tour. Specify the buttonTextResId property."
            }
        }
        return TourContent(
            pages = tourPages,
            rules = rules,
            command = dismissCommand
        )
    }

    private fun addPositions(pages: List<TourPage>): List<TourPage> =
        pages.mapIndexed { index, tourPage -> tourPage.copy(id = index, position = index) }

    private fun hasUniquePositions(pages: List<TourPage>): Boolean =
        pages.map(TourPage::position).toSet().size == pages.size
}