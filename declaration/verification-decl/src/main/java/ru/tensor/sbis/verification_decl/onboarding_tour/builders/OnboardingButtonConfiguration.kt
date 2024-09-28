package ru.tensor.sbis.verification_decl.onboarding_tour.builders

import androidx.annotation.StringRes
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Интерфейс билдера для создания кнопки экрана в [OnboardingTour].
 *
 * @author as.chadov
 */
interface OnboardingButtonConfiguration {

    /** Ресурс подписи на кнопке перехода к следующему экрану. В случае отсутвия кнопка будет скрыта. */
    @get:StringRes
    var title: Int

    /** Шрифтовая иконка для кнопки перехода к следующему экрану. */
    var icon: SbisMobileIcon.Icon?

    /** Стили кнопок. По умолчанию BrandButtonStyle. */
    var style: SbisButtonStyle

    /** Положение [title] относительно [icon] по горизонтали. По умолчанию [HorizontalPosition.LEFT]. */
    var titlePosition: HorizontalPosition

    /** Опциональная команда выполняемая по клику на кнопку перехода к следующему экрану онбординга и настроек. */
    fun onClickForward(command: PageCommand)
}