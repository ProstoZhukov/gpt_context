package ru.tensor.sbis.toolbox_decl.language

/**
 * Холдер [LanguageFeature]
 *
 * @author av.krymov
 */
interface HasLanguageFeature {
    /**
     * Провайдит фичу смены языка.
     */
    fun getLanguageFeature(): LanguageFeature
}