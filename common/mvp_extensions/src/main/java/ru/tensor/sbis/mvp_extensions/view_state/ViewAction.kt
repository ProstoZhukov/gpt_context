@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection")

package ru.tensor.sbis.mvp_extensions.view_state

/**
 * Действие над вью
 * @author sa.nikitin
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface ViewAction<in VIEW> {

    /**
     * Выполнить действие
     */
    fun perform(view: VIEW)
}
