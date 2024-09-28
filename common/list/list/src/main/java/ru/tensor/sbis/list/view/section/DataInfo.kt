package ru.tensor.sbis.list.view.section

import android.content.Context
import android.graphics.Rect
import androidx.annotation.ColorInt
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.utils.ListData

/**
 * Производная служебная информация о данных.
 */
interface DataInfo {

    /**
     * Содержит ли более 1 блока.
     */
    fun hasMoreThanOneSection(): Boolean

    /**
     * Общее количество элементов во всех блоках.
     */
    fun getItemsTotal(): Int

    /**
     * Не содержит данных.
     */
    fun isEmpty(): Boolean

    /**
     * Имеет ли разделитель блок, в который входит элемент по переданной позиции [absoluteItemPosition].
     */
    fun hasDividers(absoluteItemPosition: Int): Boolean

    /**
     * Является ли элемент по переданной позиции [absoluteItemPosition] первым в блоке.
     */
    fun isFirstInSection(absoluteItemPosition: Int): Boolean

    /**
     * Является ли элемент по переданной позиции [absoluteItemPosition] последним в блоке.
     */
    fun isLastItemInSection(absoluteItemPosition: Int): Boolean

    fun getIndexOfSection(absoluteItemPosition: Int): Int

    /**
     * Выполнить переданный метод, если элемент по переданной позиции [absoluteItemPosition] входит в блок,
     * имеющий цветной индикатор.
     * Метод [function] будет вызван с значением цвета цветного индикатора.
     */
    fun runIfIsFirstItemInSectionAndHasLine(
        absoluteItemPosition: Int,
        function: (color: Int) -> Unit
    )

    /**
     * Доступна ли операция перетаскивания для элемента по переданной позиции [absoluteItemPosition].
     */
    fun isMovable(absoluteItemPosition: Int): Boolean

    /**
     * Получить размер ячейки по переданной позиции [absoluteItemPosition]
     * для [androidx.recyclerview.widget.GridLayoutManager], если элемент не является карточкой,
     * то вернуть [returnIfIsNotCard].
     */
    fun getSpanSize(absoluteItemPosition: Int, returnIfIsNotCard: Int): Int

    /**
     * Нужно ли отображать разделитель под первым элементом в блоке, в который входит элемент по переданной
     * позиции [absoluteItemPosition].
     */
    fun needDrawDividerUnderFirst(absoluteItemPosition: Int): Boolean

    /**
     * Нужно ли отображать разделитель над последним элементом в блоке, в который входит элемент по переданной
     * позиции [absoluteItemPosition].
     */
    fun needDrawDividerUpperLast(absoluteItemPosition: Int): Boolean

    /**
     * Получить все элементы списком.
     */
    fun getItems(): List<AnyItem>

    /**
     * Получить все секции.
     */
    //todo удалить https://online.sbis.ru/opendoc.html?guid=55f4af80-97f9-4347-b377-4d7e17819067
    fun getSections(): List<Section>

    /**
     * Получить цвет фона для всего списка.
     */
    @ColorInt
    fun getBackgroundResId(context: Context): Int

    /**
     * Признак того надо ли заполнить пустое пространство под последней секцией цветом ячеек - "растянуть секцию"
     */
    fun isNeedExpandLastSection(): Boolean

    /**
     * Отступы с боков и сверху для карточек.
     */
    fun getMarginDp(
        context: Context,
        position: Int,
        hasNoItemAtLeft: Boolean,
        hasNoSpanSpaceAtRight: Boolean,
        isFirstGroupInSection: Boolean,
        isInLastGroup: Boolean
    ): Rect

    /**
     * Является ли элемент [position] карточкой.
     */
    fun isCard(position: Int): Boolean

    /**
     * Получить опции карточки для элементо в [position]
     */
    fun getCardOption(position: Int): CardOption

    /**
     * Содержит ли сворачиваемые/разворачиваемые элементы.
     */
    fun hasCollapsibleItems(): Boolean

    /**
     * Вернуть копию переданной коллекции данных для сбислиста [listData], в которой поменяли местами элементы
     * [AnyItem] по индексам в адаптере [p1] и [p2]. Реализация не должна содержать сайд-эффектов.
     * Если элементы находятся в разных секциях, вернется null.
     */
    fun reorder(listData: ListData, p1: Int, p2: Int): ListData?
}