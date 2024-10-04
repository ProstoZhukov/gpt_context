package ru.tensor.sbis.design.folders.support.presentation

/**
 * Режим отображения списка папок в шторке
 *
 * @author ma.kolpakov
 */
internal enum class FolderListViewMode {

    /**
     * Список папок скрыт. Сигнал о закрытии шторки
     */
    HIDDEN,

    /**
     * Отображение по умолчанию. Доступно меню на элементах списка
     */
    DEFAULT,

    /**
     * Отображение списка для выбора. Меню на элементах списка недоступно. Обработка только нажатий
     */
    SELECTION
}