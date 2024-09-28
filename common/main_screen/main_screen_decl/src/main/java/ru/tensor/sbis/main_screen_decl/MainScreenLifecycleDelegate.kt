package ru.tensor.sbis.main_screen_decl

/**
 * Характеризует компонент главного экрана как носителя жизненного цикла.
 *
 * @author us.bessonov
 */
interface MainScreenLifecycleDelegate {

    /** @SelfDocumented */
    fun setup()

    /** @SelfDocumented */
    fun activate()

    /** @SelfDocumented */
    fun resume()

    /** @SelfDocumented */
    fun pause()

    /** @SelfDocumented */
    fun deactivate()

    /** @SelfDocumented */
    fun reset()
}