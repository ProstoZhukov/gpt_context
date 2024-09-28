package ru.tensor.sbis.version_checker_decl

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик состояния необходимости принудительного обновления
 *
 * @author as.chadov
 */
interface CriticalIncompatibilityProvider: Feature {

    /**
     * Проверить приложение на совместимость
     * @return true если критическая несовместимость версии МП, иначе false
     */
    fun isApplicationCriticalIncompatibility(): Boolean
}