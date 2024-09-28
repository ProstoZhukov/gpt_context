package ru.tensor.sbis.business.common.data

/**
 * Интерфейс поставщика VM элемента иерархической структуры - папки, либо листового элемента
 *
 * @property id строковый идентификатор
 * @property name название
 * @property highlightedNameRanges диапазоны индексов подсвеченных символов [name]
 * @property parentId идентификатор родительского элемента
 * @property isFolder является ли элемент папкой
 * @property isEmptyFolder true, если при обработке элементов папка помечена пустой и не должна учавствовать в формировании хлебных крошек
 */
interface FolderStructureItemVmProvider : ViewModelProvider {
    var highlightedNameRanges: List<IntRange>
    val isFolder: Boolean
    val name: String
    val id: String
    val parentId: String?
    var isEmptyFolder: Boolean

    val isNotFolder: Boolean
        get() = !isFolder

    val hasParent: Boolean
        get() = parentId != null
}