package ru.tensor.sbis.list.view.utils.layout_manager

import ru.tensor.sbis.list.view.section.DataInfo

/**
 * Определить, расположен ли элемент в первой группе(строке) секции.
 */
internal class IsFirstGroupInSection(
    private val info: DataInfo,
    private val sizeLookup: ItemSpanSizeLookup
) : (Int, Int) -> Boolean {

    override fun invoke(position: Int, spanCount: Int): Boolean {
        val spanGroupIndex = sizeLookup.getSpanGroupIndex(
            position,
            spanCount
        )

        /**
         * Ищем элемент в предыдущей строке, смотрим, не в той же ли он секции.
         */
        val sectionIndex = info.getIndexOfSection(position)
        var i = position - 1
        while (i >= 0 && info.getIndexOfSection(i) == sectionIndex) {
            if (sizeLookup.getSpanGroupIndex(
                    i,
                    spanCount
                ) < spanGroupIndex
            ) return false
            i--
        }

        return true
    }
}