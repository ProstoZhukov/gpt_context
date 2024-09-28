package ${commonNamespace}.feature.${moduleName}.ui

import androidx.fragment.app.Fragment

/**
 * Предоставление внешней функциональности модуля для других модулей
 */
interface ${modelName}FragmentProvider {

    /**
     * Функция получения Fragment для отображения
     */
    fun get${modelName}Fragment(): Fragment
}