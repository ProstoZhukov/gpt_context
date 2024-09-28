package ru.tensor.sbis.common_views.document

/**
 * Интерфейс слушателя событий загрузки изображения
 *
 * @author sa.nikitin
 */
interface LoadImageListener<I> {

    /**
     * Изображение успешно загружено
     *
     * @param imageInfo Информации о загруженном изображении
     */
    fun onImageSuccessLoad(imageInfo: I?) {}

    /**
     * Ошибка при загрузке изображения
     *
     * @param throwable Исключение, характеризующее ошибку
     *
     * @return true, если ошибка полностью потреблена и обработана, false - иначе
     */
    fun onLoadImageFailure(throwable: Throwable): Boolean {
        return false
    }
}