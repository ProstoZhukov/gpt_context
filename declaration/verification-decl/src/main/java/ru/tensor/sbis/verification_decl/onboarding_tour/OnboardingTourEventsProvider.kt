package ru.tensor.sbis.verification_decl.onboarding_tour

import io.reactivex.Maybe
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.OnboardingConfigurator

/**
 * Поставщик событий Приветственного экрана.
 * Доп. информация: рекомендуется использовать отслеживание событий тура через DSL [OnboardingConfigurator].
 *
 * @author as.chadov
 */
interface OnboardingTourEventsProvider : Feature {

    /**
     * Подписка на закрытие компонента "Приветственного экрана".
     * Подходит для выполнения кода, который должен выполниться после закрытия онбординга.
     */
    fun observeTourCloseEvent(): Maybe<Unit>
}