package ru.tensor.sbis.onboarding_tour.domain.builders

import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.onboarding_tour.R
import ru.tensor.sbis.onboarding_tour.data.TourBanner
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.BannerCommand
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingBannerConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BannerButtonType.*

/**
 * Билдер баннера [TourBanner].
 *
 * @author as.chadov
 */
internal class OnboardingTourBannerBuilder :
    BaseOnboardingTourDslBuilder<TourBanner>(),
    OnboardingBannerConfiguration {

    private var bannerButtonCommand: BannerCommand? = null

    override var logoType: SbisLogoType = SbisLogoType.Empty
    override var buttonType = NONE

    override fun onButtonClick(command: BannerCommand) {
        bannerButtonCommand = command
    }

    override fun build(): TourBanner {
        val captionRes = when {
            buttonType == SKIP && buttonType.caption != ID_NULL -> buttonType.caption
            buttonType == SKIP -> R.string.onboarding_tour_skip_title
            buttonType == CLOSE -> R.string.onboarding_tour_cross
            else -> ID_NULL
        }
        buttonType.caption = captionRes
        return TourBanner(
            bannerLogoType = logoType,
            bannerButtonType = buttonType,
            bannerCommand = bannerButtonCommand
        )
    }
}