package ru.tensor.sbis.design.folders.view.full

/**
 * Мод отображения компонента [FolderListView].
 * Влияет на доступность прокрутки списка.
 *
 * @author ma.kolpakov
 */
internal enum class FolderListViewMode {

    /** Самостоятельное отображение */
    STAND_ALONE,

    /** Вложенное отображение */
    NESTED
}