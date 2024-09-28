package ru.tensor.sbis.verification_decl.onboarding_tour.builders

import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Интерфейс билдера для создания запросов на получение прав доступа и разрешений на [OnboardingTour].
 *
 * @author as.chadov
 */
interface OnboardingPermissionConfiguration {

    /** Список системных разрешений запрашиваемых компонентом. */
    var permissions: List<String>

    /** Являются ли предоставление [permissions] блокирующим для перехода к следующему экрану тура. По умолчанию false. */
    var isRequired: Boolean

    /** Опциональная команда выполняемая при необходимости обоснования предоставления [permissions] пользователем. */
    fun onRequestRationale(callback: RationaleCallback)
}