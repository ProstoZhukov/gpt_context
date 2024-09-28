package ru.tensor.sbis.mediaplayer.zoom

/**
 * Интерфейс слушателя событий при зумировании изображения видео на плеере
 *
 *  @author as.chadov
 */
interface ZoomPlayerStateListener {

    /**
     * Обработать изменение состояния зума
     * @param isZoomActive используется ли зум
     * @param factor фактор масштабирования
     */
    fun onScale(isZoomActive: Boolean, factor: Float)
}