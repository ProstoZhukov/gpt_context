package ru.tensor.sbis.crud3.view.datachange

/**
 * Класс представляющий событие вставки элемента в список элементов.
 * Он валидирует входные списки с помощью заданного валидатора и предоставляет метод для группировки последовательных элементов.
 *
 * @param indexItemList Список пар индекса и элемента, где каждая пара представляет элемент и его индекс в списке.
 * @param allItems Список всех элементов.
 * @param validator Валидатор для проверки входных списков. По умолчанию используется ItemInsertedValidator.
 * @property indexItemList Список пар индекса и элемента, где каждая пара представляет элемент и его индекс в списке.
 *
 * @constructor Создает экземпляр ItemInserted и валидирует входные списки.
 *
 * @throws IllegalArgumentException Если indexItemList или allItems пустые.
 */
class ItemInserted<ITEM>(
    indexItemList: List<Pair<Long, ITEM>>,
    allItems: List<ITEM>,
    private val validator: ItemInsertedValidator<ITEM> = ItemInsertedValidator<ITEM>()
) : DataChange<ITEM>(allItems) {
    val indexItemList: List<Pair<Long, ITEM>> = indexItemList.toList()

    init {
        validator.validate(indexItemList, allItems)
    }

    /**
     * Группирует последовательные элементы в indexItemList в пары начальной позиции и количества.
     *
     * @param firstItemOffset Смещение, которое будет добавлено к начальной позиции каждой группы.
     * @return Список пар, где каждая пара представляет группу последовательных элементов в форме (начальная позиция, количество).
     */
    fun toGroupsOfConsecutiveElements(firstItemOffset: Int): List<Pair<Int, Int>> {
        val notifyGroups = mutableListOf<Pair<Int, Int>>()
        var positionStart = indexItemList.first().first.toInt() + firstItemOffset
        var itemCount = 1
        indexItemList.reduce { prevIndexItem, indexItem ->
            if (prevIndexItem.first + 1 != indexItem.first) {
                notifyGroups.add(positionStart to itemCount)
                positionStart = indexItem.first.toInt() + firstItemOffset
                itemCount = 1
            } else {
                itemCount++
            }
            indexItem
        }
        notifyGroups.add(positionStart to itemCount)

        return notifyGroups
    }
}