package ru.tensor.sbis.list.view.decorator

import android.content.Context
import androidx.annotation.AnyThread
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import ru.tensor.sbis.design.list_utils.decoration.SelectionMarkItemDecoration
import ru.tensor.sbis.list.view.ListDataHolder
import ru.tensor.sbis.list.view.background.ColorProvider
import ru.tensor.sbis.list.view.decorator.stiky_header.StickHeaderItemDecoration
import ru.tensor.sbis.list.view.decorator.stiky_header.StickyHeaderInterface
import ru.tensor.sbis.list.view.utils.layout_manager.SbisGridLayoutManager

/**
 * Фабрика декораторов для [ru.tensor.sbis.list.view.SbisList]
 * @property sectionsHolder SectionsHolder набор блоков.
 * @property context Context @SelDocumented.
 * @property stickyHeaderInterface StickyHeaderInterface @SelDocumented.
 * @property spaceBetweenSectionsPx Int расстояние между блоками.
 * @constructor
 */
internal class DecoratorFactory(
    private val sectionsHolder: ListDataHolder,
    private val context: Context,
    private val gridLayoutManager: SbisGridLayoutManager,
    private val stickyHeaderInterface: StickyHeaderInterface,
    private val spaceBetweenSectionsPx: Int,
    private val colorProvider: ColorProvider
) {
    /**
     * Декоратор для реализации разделителя между ячейками.
     */
    @AnyThread
    fun dividerItemDecoration(): ItemDecoration =
        DividerItemDecoration(sectionsHolder, context, colorProvider)

    /**
     * Декоратор для реализации отступа снизу для последнего элемента.
     */
    @AnyThread
    fun lastItemBottomPaddingDecoration(): LastItemBottomPaddingDecoration =
        LastItemBottomPaddingDecoration(
            gridLayoutManager,
            context.resources
        )

    /**
     * Декоратор для реализации стики-заголовков.
     */
    @AnyThread
    fun stickHeaderItemDecoration(): ItemDecoration =
        StickHeaderItemDecoration(stickyHeaderInterface, colorProvider, sectionsHolder)

    /**
     * Декоратор для отрисовки пользователских декораций.
     */
    @AnyThread
    fun customDecorator(): ItemDecoration = CustomDecorator()

    /**
     * Декоратор для реализации отступов блоков.
     */
    @AnyThread
    fun sectionDecoration(): ItemDecoration =
        SectionDecoration(gridLayoutManager, sectionsHolder, spaceBetweenSectionsPx, colorProvider)

    /**
     * Декоратор для реализации выделения нажатого элемента.
     */
    @AnyThread
    fun selectionMarkItemDecoration(): SelectionMarkItemDecoration =
        SelectionMarkItemDecoration(context)

    /**
     * Декоратор для отрисовки стандартного фона содержимого.
     */
    @AnyThread
    fun backgroundDecoration() : ItemDecoration =
        ItemBackgroundDecoration(sectionsHolder, colorProvider)

    /**
     * Декоратор для применения стандартных отступов внутри содержимого.
     */
    @AnyThread
    fun offsetsDecoration() : ItemDecoration =
        ItemOffsetsDecoration(sectionsHolder)
}