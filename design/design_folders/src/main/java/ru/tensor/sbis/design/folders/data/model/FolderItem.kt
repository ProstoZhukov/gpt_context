/**
 * Модели данных компонента папок
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.folders.data.model

/**
 * Идентификатор корневой папки
 */
const val ROOT_FOLDER_ID = ""

/**
 * Модель элемента компонента папок
 */
sealed class FolderItem

/**
 *  Модель папки
 *
 * @param id уникальный id папки. Для корневой папки нужно указать [ROOT_FOLDER_ID]
 * @param title имя папки для отображения
 * @param type режим отображения папки. Влияет на отображаемую иконку и на экшены по свайпу
 * @param depthLevel глубина вложенности. Начинается с нуля
 * @param totalContentCount общее количество контента папки (серое число)
 * @param unreadContentCount непрочитанное количество контента папки (оранжевое число)
 * @param canMove можно ли перемещать папку и перемещать в нее
 */
data class Folder(
    val id: String,
    val title: String,
    val type: FolderType,
    val depthLevel: Int,
    val totalContentCount: Int,
    val unreadContentCount: Int,
    val canMove: Boolean = true
) : FolderItem() {

    /** Находится ли папка на верхнем уровне */
    internal val isTopLevel: Boolean
        get() = depthLevel == 0

    /** Является ли папка первой в списке */
    internal var isFirst = false
}

/**
 * Модель дополнительного действия
 *
 * @param title имя действия для отображения
 * @param type тип действия. Влияет на отображаемую иконку
 */
data class AdditionalCommand(
    val title: String,
    val type: AdditionalCommandType,
) : FolderItem() {

    companion object {

        /**
         * Служебный объект для работы с Rx потоками данных. Обозначает отсутствия дополнительного действия
         */
        val EMPTY: AdditionalCommand
            get() = AdditionalCommand("", AdditionalCommandType.EMPTY)
    }
}

/**
 * Модель кнопки в виде иконки папки
 */
internal object FolderButton : FolderItem()

/**
 * Модель кнопки "ещё"
 */
internal object MoreButton : FolderItem()
