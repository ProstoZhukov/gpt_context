package ru.tensor.sbis.widget_player.api

/**
 * Интерфейс фрагмента-держателя плеера виджетов.
 * Является временным решением для открытия экранов из виджетов.
 *
 * TODO Выпилить после появления общего механизма https://online.sbis.ru/opendoc.html?guid=0ceee9d1-d5b6-4509-9b95-d57e04417fa8&client=3
 *
 * @author am.boldinov
 */
interface WidgetPlayerHost {
    /** @SelfDocumented */
    val childContainerId: Int
}