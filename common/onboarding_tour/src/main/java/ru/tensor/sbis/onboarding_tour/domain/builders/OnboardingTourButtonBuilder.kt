package ru.tensor.sbis.onboarding_tour.domain.builders

import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.base.models.style.BrandButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.onboarding_tour.data.TourButton
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingButtonConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.PageCommand

/**
 * Билдер кнопки перехода [TourButton].
 *
 * @author as.chadov
 */
internal class OnboardingTourButtonBuilder :
    BaseOnboardingTourDslBuilder<TourButton>(),
    OnboardingButtonConfiguration {

    override var title: Int = ID_NULL
    override var icon: SbisMobileIcon.Icon? = null
    override var style: SbisButtonStyle = BrandButtonStyle
    override var titlePosition = HorizontalPosition.LEFT
    private var transitionCommand: PageCommand? = null

    override fun onClickForward(command: PageCommand) {
        transitionCommand = command
    }

    override fun build(): TourButton = TourButton(
        titleResId = title,
        icon = icon,
        style = style,
        titlePosition = titlePosition,
        command = transitionCommand
    )
}