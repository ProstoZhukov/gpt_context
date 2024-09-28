package ru.tensor.sbis.onboarding_tour.data

import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.base.models.style.BrandButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingButtonConfiguration
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.PageCommand

/**
 * Кнопка перехода экрана онбординга и настроек.
 * Подробнее [OnboardingButtonConfiguration].
 *
 * @author as.chadov
 */
internal class TourButton(
    @StringRes
    val titleResId: Int,
    val icon: SbisMobileIcon.Icon?,
    val style: SbisButtonStyle,
    val titlePosition: HorizontalPosition,
    var command: PageCommand?
) {
    companion object {
        /** @SelfDocumented */
        val empty = TourButton(
            titleResId = ID_NULL,
            icon = null,
            style = BrandButtonStyle,
            titlePosition = HorizontalPosition.LEFT,
            command = null
        )
    }
}