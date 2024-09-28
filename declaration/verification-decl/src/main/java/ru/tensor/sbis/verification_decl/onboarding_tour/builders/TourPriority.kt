package ru.tensor.sbis.verification_decl.onboarding_tour.builders

import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * Перечисление приоритетов показа конкретного [OnboardingTour].
 * При совпадении приоритетов у нескольких [OnboardingTour] они будут показаны в порядке объявления или в порядке инициализации плагинов.
 *
 * @author as.chadov
 */
enum class TourPriority {

    /** Низкий приоритет. Такой [OnboardingTour] будет показан самым последним. */
    LOW,

    /** Средний приоритет, по умолчанию. Следует использовать, если не важен порядок отображения. */
    NORMAL,

    /** Высокий приоритет. Использовать, если необходимо показать раньше остальных [OnboardingTour]. */
    HIGH,

    /** Исключительный приоритет. Требуется показать только текущий [OnboardingTour], а остальные должны быть проигнорированы. */
    SOLE
}