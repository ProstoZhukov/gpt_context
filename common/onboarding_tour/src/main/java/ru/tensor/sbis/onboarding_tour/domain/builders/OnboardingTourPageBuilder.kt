package ru.tensor.sbis.onboarding_tour.domain.builders

import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.design.buttons.base.models.style.BrandButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.onboarding_tour.data.TourBanner
import ru.tensor.sbis.onboarding_tour.data.TourButton
import ru.tensor.sbis.onboarding_tour.data.TourPage
import ru.tensor.sbis.onboarding_tour.data.TourPage.Companion.FIRST_POSITION
import ru.tensor.sbis.onboarding_tour.data.TourPermissions
import ru.tensor.sbis.onboarding_tour.data.TourTerms
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingBannerConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingButtonConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingPageConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingPermissionConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingTermsConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.PageRequiredCallback

/**
 * Билдер страницы [TourPage].
 *
 * @author as.chadov
 */
internal class OnboardingTourPageBuilder :
    BaseOnboardingTourDslBuilder<TourPage>(),
    OnboardingPageConfiguration {

    override var position: Int = FIRST_POSITION
    override var title: Int = ID_NULL
    override var description: Int = ID_NULL
    override var image: Int = ID_NULL
    override var nextButtonTitle: Int = ID_NULL
    override var nextButtonStyle: SbisButtonStyle = BrandButtonStyle

    private var banner: TourBanner = TourBanner.empty
    private var button: TourButton = TourButton.empty
    private var terms: TourTerms? = null
    private var permissions: TourPermissions? = null

    private var requiredCallback = PageRequiredCallback { true }

    override fun checkIsRequired(callback: PageRequiredCallback) {
        requiredCallback = callback
    }

    override fun customBanner(init: OnboardingBannerConfiguration.() -> Unit) {
        banner = OnboardingTourBannerBuilder().apply(init).build()
    }

    override fun button(init: OnboardingButtonConfiguration.() -> Unit) {
        button = OnboardingTourButtonBuilder().apply(init).build()
    }

    override fun terms(init: OnboardingTermsConfiguration.() -> Unit) {
        terms = OnboardingTourTermsBuilder().apply(init).build()
    }

    override fun permissions(init: OnboardingPermissionConfiguration.() -> Unit) {
        permissions = OnboardingTourPermissionsBuilder().apply(init).build()
    }

    override fun build(): TourPage {
        val pageButton = if (button == TourButton.empty) {
            TourButton(
                titleResId = nextButtonTitle,
                icon = null,
                style = nextButtonStyle,
                titlePosition = HorizontalPosition.RIGHT,
                command = null
            )
        } else {
            button
        }
        return TourPage(
            id = position,
            position = position,
            titleResId = title,
            descriptionResId = description,
            imageResId = image,
            requiredCommand = requiredCallback,
            banner = banner,
            button = pageButton,
            terms = terms,
            permissions = permissions
        )
    }
}