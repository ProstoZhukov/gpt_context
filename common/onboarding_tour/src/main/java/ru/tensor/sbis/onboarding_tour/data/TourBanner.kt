package ru.tensor.sbis.onboarding_tour.data

import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.BannerCommand
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingBannerConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BannerButtonType

/**
 * Баннер экранов онбординга и настроек.
 * Подробнее [OnboardingBannerConfiguration].
 *
 * @author as.chadov
 */
internal class TourBanner(
    val bannerLogoType: SbisLogoType,
    val bannerButtonType: BannerButtonType,
    val bannerCommand: BannerCommand?
) {

    companion object {
        val empty = TourBanner(
            bannerLogoType = SbisLogoType.Empty,
            bannerButtonType = BannerButtonType.NONE,
            bannerCommand = null
        )
    }
}