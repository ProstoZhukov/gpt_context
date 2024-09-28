package ru.tensor.sbis.viewer.decl.slider.source

import ru.tensor.sbis.crud3.view.datachange.DataChange
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs
import timber.log.Timber

/**
 * Коллекция слайдера просмотрщиков
 *
 * @author sa.nikitin
 */
interface ViewerSliderCollection {

    /**
     * Установить наблюдатель коллекции
     */
    fun setObserver(observer: ViewerSliderCollectionObserver)

    /**
     * Обновить коллекцию
     */
    fun refresh()

    /**
     * Загрузить следующую страницу в направлении [ViewerSliderDirection.FORWARD]
     *
     * @param anchorIndex Индекс элемента, на который необходимо переместить текущий viewport
     */
    fun loadNext(anchorIndex: Long)

    /**
     * Загрузить следующую страницу в направлении [ViewerSliderDirection.BACKWARD]
     *
     * @param anchorIndex Индекс элемента, на который необходимо переместить текущий viewport
     */
    fun loadPrevious(anchorIndex: Long)

    /**
     * Освободить ресурсы и остановить потоки обработки событий.
     * Вызывается на android, для явного освобождения ресурсов, т.к.
     * при RAII семантике сборщик мусора может разрушать объекты далеко не сразу, что
     * приводит к росту числа активных процессов на стороне С++
     */
    fun dispose()
}

/**
 * Направление прокрутки слайдера просмотрщиков
 *
 * @author sa.nikitin
 */
enum class ViewerSliderDirection {
    /**
     * Прямое направление получения данных (что такое прямое - зависит от задачи)
     * Все строго больше якоря >
     */
    FORWARD,

    /**
     * Обратное направление получения данных (что такое обратное - зависит от задачи)
     * Все строго меньше якоря <
     */
    BACKWARD,

    /**
     * Получение данных в обоих направлениях
     * Все больше либо равно и меньше либо равно якоря ≤≥
     */
    BOTH_WAY,
}

/**
 * Положение заглушки или крутилки на экране просмотрщика
 *
 * @author sa.nikitin
 */
enum class ViewerSliderViewPosition {
    /** По месту в зависимости от направления прокрутки */
    IN_PLACE,

    /** В заголовке, т.е. выполняется общее обновление, а не подгрузка новой страницы */
    HEADER,
}

/**
 * Тип заглушки на экране просмотрщика
 *
 * @author sa.nikitin
 */
enum class ViewerSliderStubType {
    /** Нет данных по дефолтному фильтру */
    NO_DATA_STUB,

    /** Нет данных по недефолтному фильтру */
    BAD_FILTER_STUB,

    /** Нет сети */
    NO_NETWORK_STUB,

    /** Проблемы с сервером */
    SERVER_TROUBLE,
}

/**
 * Логировать процесс работы коллекции в слайдере просмотрщиков
 */
fun logViewerCollection(message: String) {
    Timber.tag("ViewerCollectionTag").d(message)
}

fun DataChange<ViewerArgs>.logDesc(): String =
    "${javaClass.simpleName}-${hashCode()}-${allItems.map { "${it.id}-${it.title}" }}"