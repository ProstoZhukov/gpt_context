package ru.tensor.sbis.crud3.view.datachange

/**
 * Валидатор элементов, вставленных в список.
 *
 * @param ITEM Тип элементов в списке.
 */
class ItemInsertedValidator<ITEM>  {
    fun validate(indexItemList: List<Pair<Long, ITEM>>, allItems: List<ITEM>) {
        require(indexItemList.isNotEmpty()) { "indexItemList не должен быть пустым" }
        require(allItems.isNotEmpty()) { "allItems не должен быть пустым" }
    }
}