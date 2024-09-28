package ru.tensor.sbis.verification_decl.onboarding_tour.builders

import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BannerButtonType
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Интерфейс билдера для создания баннера экрана(ов) в [OnboardingTour].
 *
 * @author as.chadov
 */
interface OnboardingBannerConfiguration {

    /** Логотип на баннере экрана тура. По умолчанию [SbisLogoType.Empty] */
    var logoType: SbisLogoType

    /** Тип кнопки на баннере экрана тура. По умолчанию не отображается. */
    var buttonType: BannerButtonType

    /** Опциональный коллбэк выполняемый при клике на кнопку пропустить или закрыть в баннере. */
    fun onButtonClick(command: BannerCommand)
}