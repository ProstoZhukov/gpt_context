package ru.tensor.sbis.viewer.decl.slider

import android.content.Intent
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs

/**
 * Интерфейс слайдера просмотрщиков
 *
 * @author sa.nikitin
 */
interface ViewerSlider {

    /**
     * Обновить аргументы просмотрщика
     * Сравнение по [ViewerArgs.id]
     * Следует применять, если нужно обновить [ViewerArgs.title] или миниатюру в барабане
     */
    fun updateViewerArgs(viewerArgs: ViewerArgs)

    /**
     * Удалить просмотрщик по его аргументам [viewerArgs]
     * Сравнение по [ViewerArgs.id]
     */
    fun removeViewer(viewerArgs: ViewerArgs)

    /**
     * Отправить статус "включённость" swipe back в слайдер
     * Новый статус применится, если не противоречит глобальному, см. [ViewerSliderArgs.swipeBackEnabled]
     */
    fun dispatchSwipeBackEnabled(swipeBackEnabled: Boolean)

    /**
     * Один ли просмотрщик в слайдере
     * Требуется, например, для просмотрщика видео. Если видео одно, то сразу воспроизводим
     */
    fun isOneViewer(): Boolean

    /**
     * Установить результат, который вернет activity после завершения
     */
    fun setResult(data: Intent) {}
}