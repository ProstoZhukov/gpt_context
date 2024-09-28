package ru.tensor.sbis.list.view.decorator

import androidx.annotation.AnyThread
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.list_utils.decoration.SelectionMarkItemDecoration
import ru.tensor.sbis.list.view.ListDataHolder
import ru.tensor.sbis.list.view.background.ColorProvider
import ru.tensor.sbis.list.view.decorator.stiky_header.StickyHeaderInterface
import ru.tensor.sbis.list.view.utils.layout_manager.SbisGridLayoutManager

/**
 * Держатель декораторов для инициализации и выполнения операция над ними.
 */
internal class DecoratorHolder {

    private lateinit var selectionMarkItemDecoration: SelectionMarkItemDecoration
    private lateinit var lastItemBottomPaddingDecoration: LastItemBottomPaddingDecoration

    /**
     * Добавить весь набор декораторов.
     * @param recyclerView RecyclerView @SelDocumented
     * @param sections SectionsHolder @SelDocumented
     * @param stickyHeaderInterface StickyHeaderInterface
     * @param spaceBetweenSectionsPx Int расстояние между блоками.
     * @param factory DecoratorFactory фабрика декораторов.
     */
    @AnyThread
    fun addDecorators(
        recyclerView: RecyclerView,
        gridLayoutManager: SbisGridLayoutManager,
        sections: ListDataHolder,
        stickyHeaderInterface: StickyHeaderInterface,
        spaceBetweenSectionsPx: Int,
        colorProvider: ColorProvider,
        @VisibleForTesting
        factory: DecoratorFactory = DecoratorFactory(
            sections,
            recyclerView.context,
            gridLayoutManager,
            stickyHeaderInterface,
            spaceBetweenSectionsPx,
            colorProvider
        )
    ) {
        lastItemBottomPaddingDecoration = factory.lastItemBottomPaddingDecoration()
        selectionMarkItemDecoration = factory.selectionMarkItemDecoration()
        with(recyclerView) {
            addItemDecoration(factory.offsetsDecoration())
            addItemDecoration(factory.backgroundDecoration())
            addItemDecoration(factory.dividerItemDecoration())
            addItemDecoration(factory.sectionDecoration())
            /**
             * Порядок установки selectionMarkItemDecoration и stickHeaderItemDecoration важен,
             * так как иначе фон выделенного элемента списка отрисовывается поверх фона липкого заголовка.
             */
            addItemDecoration(selectionMarkItemDecoration)
            addItemDecoration(factory.stickHeaderItemDecoration())
            addItemDecoration(factory.customDecorator())
        }
    }

    /**
     * Удалить декоратор для отступа снизу для FAB.
     * @param recyclerView RecyclerView
     */
    @AnyThread
    fun removeLastItemBottomPadding(recyclerView: RecyclerView) {
        recyclerView.removeItemDecoration(lastItemBottomPaddingDecoration)
    }

    /**
     * Добавить декоратор для отступа снизу для FAB, если не добавлен.
     * @param recyclerView RecyclerView
     */
    @AnyThread
    fun makeSureLastItemPaddingDecoratorIsAdded(recyclerView: RecyclerView, hasNav: Boolean, hasFab: Boolean) {
        /**
         * Нужно именно добавить декоратор, чтобы он сработал сразу, а не после следующего обновления списка.
         */
        lastItemBottomPaddingDecoration.hasNav = hasNav
        lastItemBottomPaddingDecoration.hasFab = hasFab
        recyclerView.removeItemDecoration(lastItemBottomPaddingDecoration)
        recyclerView.addItemDecoration(lastItemBottomPaddingDecoration)
    }

    /**
     * Выделить элемент по указанной позиции.
     * @param position Int
     */
    @AnyThread
    fun highlightItem(position: Int) {
        selectionMarkItemDecoration.setPosition(position)
    }

    /**
     * Очистить данные о блоках.
     */
    @AnyThread
    fun cleanSelection() {
        selectionMarkItemDecoration.cleanSelection()
    }
}