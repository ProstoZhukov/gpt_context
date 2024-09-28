package ru.tensor.sbis.onboarding.contract.providers

import ru.tensor.sbis.onboarding.contract.providers.content.Onboarding
import ru.tensor.sbis.onboarding.ui.utils.OnboardingPreferenceManager
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс предоставляющий данные для представления приветственных экранов
 *
 * @author as.chadov
 */
interface OnboardingProvider : Feature {

    /**
     * Поле для определения приоритета показа провайдера.
     * Стандартно у Onboarding [ShowPriority.NORMAL].
     * Влияет на позицию среди других провайдеров при проверке условий показа.
     */
    val showPriority: ShowPriority
        get() = ShowPriority.NORMAL

    /**
     * Метод, предоставляющий кастомные правила для показа onboarding.
     */
    fun getCustomOnboardingPreferenceManger(): OnboardingPreferenceManager? = null

    fun getOnboardingContent(): Onboarding
}

/**
 * Перечисление приоритетов показа провайдеров onboarding.
 * При совпадении приоритетов у нескольких провайдеров они будут показаны в том порядке,
 * в котором были объявлены.
 * Если они в разных плагинах, то в порядке инициализации плагинов.
 *
 * @author ps.smirnyh
 */
enum class ShowPriority {

    /** Самый низкий приоритет. Провайдер будет показан самым последним, среди остальных. */
    MIN,

    /** Приоритет ниже онбординга, но выше минимального. Используется в "Что нового". */
    LOW,

    /** 
     * Средний приоритет, наравне с onboarding.
     * Следует использовать, если не важен порядок показа с онбордингом.
     */
    NORMAL,

    /** Высокий приоритет. Использовать, если нужно показать раньше онбординга. */
    HIGH,

    /** Самый высокий приоритет. Использовать, если нужно показать всегда самым первым. */
    MAX
}
