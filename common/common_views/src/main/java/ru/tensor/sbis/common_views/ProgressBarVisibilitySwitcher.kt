package ru.tensor.sbis.common_views

/**
 * Интерфейс переключателя видимость прогресс бара
 *
 * @author sa.nikitin
 */
interface ProgressBarVisibilitySwitcher {

    /**
     * Переключить видимость прогресс бара
     *
     * @param visible Виден ли прогресс бар
     */
    fun switchProgressBarVisibility(visible: Boolean)

    /**
     * Изменить прозрачность прогресс бара
     *
     * @param alpha Прозрачность от 0.0 до 1.0
     */
    fun changeProgressBarAlpha(alpha: Float)
}