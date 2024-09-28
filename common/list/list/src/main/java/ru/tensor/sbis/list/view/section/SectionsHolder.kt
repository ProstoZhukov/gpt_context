package ru.tensor.sbis.list.view.section

import android.content.Context
import android.graphics.Rect
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.GridLayoutManager
import ru.tensor.sbis.list.R
import ru.tensor.sbis.list.view.background.ColorProvider
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.utils.ListData
import ru.tensor.sbis.list.view.utils.Plain
import ru.tensor.sbis.list.view.utils.Sections
import timber.log.Timber

/**
 * Набор блоков и операции над ними.
 */
class SectionsHolder(
    sections: List<Section> = emptyList(),
    private val forcedBackgroundColor: ForceBackgroundColor = ForceBackgroundColor.NONE,
    private val expandLastSection: Boolean = false,
    private val priorityBackgroundColor: Int? = null
) : DataInfo {

    private val sections: ArrayList<Section> = ArrayList()
    private val items: ArrayList<AnyItem> = ArrayList()

    init {
        setSections(sections)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SectionsHolder

        if (priorityBackgroundColor != other.priorityBackgroundColor) return false
        if (forcedBackgroundColor != other.forcedBackgroundColor) return false
        if (sections != other.sections) return false
        if (items != other.items) return false
        if (spaceBetweenSectionsPx != other.spaceBetweenSectionsPx) return false
        if (expandLastSection != other.expandLastSection) return false

        return true
    }

    override fun hashCode(): Int {
        var result = forcedBackgroundColor.hashCode()
        result = 31 * result + sections.hashCode()
        result = 31 * result + items.hashCode()
        result = 31 * result + spaceBetweenSectionsPx
        result = 31 * result + expandLastSection.hashCode()
        result = 31 * result + priorityBackgroundColor.hashCode()
        return result
    }

    /**
     * Содержит ли более 1 блока.
     */
    override fun hasMoreThanOneSection() = sections.size > 1

    /**
     * Общее количество элементов во всех блоках.
     */
    @Synchronized
    override fun getItemsTotal(): Int {
        var count = 0

        sections.forEach {
            count += it.items.size
        }
        return count
    }

    /**
     * true - если не содержит ни одного блока.
     */
    override fun isEmpty() = sections.isEmpty()

    /**
     * Имеет ли разделитель блок в который входит элемент по переданной позиции.
     * @param absoluteItemPosition Int позиция элемента.
     */
    @Synchronized
    override fun hasDividers(absoluteItemPosition: Int): Boolean {
        var ind = 0
        sections.forEach { section ->
            section.items.forEachIndexed { index, _ ->
                if (ind + index == absoluteItemPosition) {
                    return@hasDividers section.hasDividers && index < section.items.size - 1
                }
            }
            ind += section.items.size
        }
        return false
    }

    /**
     * Является ли элемент по переданной позиции первым в блоке.
     * @param absoluteItemPosition Int позиция элемента.
     */
    @Synchronized
    override fun isFirstInSection(absoluteItemPosition: Int): Boolean {
        var summaryIndex = 0
        sections.forEach {
            it.items.forEachIndexed { indexInSectionItems, _ ->

                if (summaryIndex > absoluteItemPosition) return false

                if (summaryIndex == absoluteItemPosition) {
                    return indexInSectionItems == 0
                }
                summaryIndex++
            }
        }
        return false
    }

    /**
     * Является ли элемент по переданной позиции последним в блоке.
     * @param absoluteItemPosition Int позиция элемента.
     */
    @Synchronized
    override fun isLastItemInSection(absoluteItemPosition: Int): Boolean {
        var summaryIndex = 0
        sections.forEach {
            it.items.forEachIndexed { index, _ ->
                if (summaryIndex + index == absoluteItemPosition) {
                    return index == it.items.size - 1
                }

            }
            summaryIndex += it.items.size
        }
        return false
    }

    /**
     * Выполнить переданный метод, если элемент по переданной позиции входит в блок, имеющий цветной индикатор.
     * Метод будет вызван с значением цвета цветного индикатора.
     * @param absoluteItemPosition Int позиция элемента.
     * @param function Function1<[@kotlin.ParameterName] Int, Unit> @SelDocumented.
     */
    @Synchronized
    override fun runIfIsFirstItemInSectionAndHasLine(
        absoluteItemPosition: Int,
        function: (color: Int) -> Unit
    ) {
        var summaryIndex = 0
        sections.forEach {
            it.items.forEachIndexed { indexInSectionItems, _ ->

                if (summaryIndex > absoluteItemPosition) return

                if (summaryIndex == absoluteItemPosition && indexInSectionItems == 0) {
                    if (it.hasIndicatorColor) function(it.indicatorColor)
                    return
                }
                summaryIndex++
            }
        }
    }

    /**
     * Доступна ли операция перетаскивания для элемента по переданной позиции.
     * @param absoluteItemPosition Int позиция элемента.
     */
    @Synchronized
    override fun isMovable(absoluteItemPosition: Int): Boolean {
        if (absoluteItemPosition > items.size - 1 || absoluteItemPosition < 0) {
            Timber.e(ArrayIndexOutOfBoundsException("Index: $absoluteItemPosition, Size: $items.size"))
            return false
        }
        return items[absoluteItemPosition].isMovable
    }

    /**
     * Получить размер ячейки для [GridLayoutManager].
     */
    override fun getSpanSize(absoluteItemPosition: Int, returnIfIsNotCard: Int): Int {
        findSection(absoluteItemPosition).cardOption.let {
            if (it is Spanned) return it.spanSize

            return returnIfIsNotCard
        }
    }

    /**
     * Нужно ли отображать разделитель под первым элементом в блоке, в который входит элемент по переданной позиции.
     * @param absoluteItemPosition Int позиция элемента.
     */
    override fun needDrawDividerUnderFirst(absoluteItemPosition: Int) =
        findSection(absoluteItemPosition).needDrawDividerUnderFirst

    override fun needDrawDividerUpperLast(absoluteItemPosition: Int) =
        findSection(absoluteItemPosition).needDrawDividerUpperLast

    override fun getItems() = items

    //todo удалить https://online.sbis.ru/opendoc.html?guid=ca906e1c-2a7c-4fdf-aeb5-0dfada2a8790
    override fun getSections() = sections

    @ColorInt
    override fun getBackgroundResId(context: Context): Int {
        val colorProvider = ColorProvider(context)

        return when {
            priorityBackgroundColor != null -> priorityBackgroundColor
            forcedBackgroundColor == ForceBackgroundColor.NONE && hasMoreThanOneSection() -> colorProvider.contentDarkBackground
            forcedBackgroundColor == ForceBackgroundColor.DARK -> colorProvider.contentDarkBackground
            forcedBackgroundColor == ForceBackgroundColor.WHITE -> colorProvider.contentBackground
            else -> colorProvider.contentBackground
        }
    }

    override fun isNeedExpandLastSection() = expandLastSection

    /**
     * Инициализируется в getItemOffsets, 0 считается как не инициализировано.
     */
    private var spaceBetweenSectionsPx = 0

    override fun getMarginDp(
        context: Context,
        position: Int,
        hasNoItemAtLeft: Boolean,
        hasNoSpanSpaceAtRight: Boolean,
        isFirstGroupInSection: Boolean,
        isInLastGroup: Boolean
    ): Rect {
        val rect = Rect()
        val cardOption = getCardOption(position)
        when {
            cardOption !is NoCards && cardOption !is SingleCard -> {
                addSpaceBetweenItems(
                    position,
                    context,
                    rect,
                    hasNoItemAtLeft,
                    hasNoSpanSpaceAtRight,
                    isFirstGroupInSection,
                    isInLastGroup
                )
            }

            cardOption is SingleCard -> {
                val margin = cardOption.cardMarginDp
                with(rect) {
                    val topMargin = if (isFirstInSection(position)) margin.top else 0
                    val bottomMargin = if (isLastItemInSection(position)) margin.bottom else 0

                    top = (topMargin * context.resources.displayMetrics.density).toInt()
                    left = (margin.left * context.resources.displayMetrics.density).toInt()
                    right = (margin.right * context.resources.displayMetrics.density).toInt()
                    bottom = (bottomMargin * context.resources.displayMetrics.density).toInt()
                }
            }

            needSectionTopPadding(position) -> {
                if (spaceBetweenSectionsPx == 0) spaceBetweenSectionsPx =
                    (context.resources.getDimensionPixelSize(R.dimen.list_divider_space_size))

                rect.top = spaceBetweenSectionsPx
            }
        }
        return rect
    }

    override fun getCardOption(position: Int) = findSection(position).cardOption

    /**
     * Является ли элемент карточкой.
     * @param position Int позиция элемента.
     */
    override fun isCard(position: Int) = getCardOption(position) != NoCards

    /**
     * Содержит ли сворачиваемые/разворачиваемые элементы.
     */
    override fun hasCollapsibleItems(): Boolean = sections.any { section ->
        section.items.any { it.isCollapsible }
    }

    /**
     * В копии [sections] меняем местами элементы [AnyItem] в по индексам [p1] и [p2], и используем полученный
     * список секций для возврата из метода копии переданной [listData]. Если индексы элементов будут находиться
     * в разных секциях, вернется null.
     * Метод не содержит сайд-эффектов.
     */
    override fun reorder(listData: ListData, p1: Int, p2: Int): ListData? {
        val sectionIndexOfItem1 = listData.getIndexOfSection(p1)
        val sectionIndexOfItem2 = listData.getIndexOfSection(p2)
        // проверяем, что индексы секций у обоих элементов совпадают
        if (sectionIndexOfItem1 != sectionIndexOfItem2) return null
        val necessarySection = listData.getSections()[sectionIndexOfItem1]
        val allItems = listData.getItems()
        // ищем позицию элемента в конкретной секции, зная позицию элемента среди всех элементов.
        val indexOfItem1InSection = necessarySection.items.indexOf(allItems[p1])
        val indexOfItem2InSection = necessarySection.items.indexOf(allItems[p2])
        val sectionsWithReorderedItems: List<Section> = listData.getSections().mapIndexed { index, section ->
            if (index != sectionIndexOfItem1) section
            else {
                val mutableReorderedItems = section.items.toMutableList()
                mutableReorderedItems[indexOfItem1InSection] = section.items[indexOfItem2InSection]
                mutableReorderedItems[indexOfItem2InSection] = section.items[indexOfItem1InSection]
                section.copy(items = mutableReorderedItems)
            }
        }
        return when (listData) {
            is Plain -> listData.copy(data = sectionsWithReorderedItems[0].items)
            is Sections -> listData.copy(
                info = SectionsHolder(
                    sectionsWithReorderedItems,
                    forcedBackgroundColor,
                    expandLastSection,
                    priorityBackgroundColor
                )
            )
        }
    }

    /**
     * 1) Секция с карточками отступа от предыдущей секции не имеют.
     * 2) Любая секция не имеет отступа от секции с карточками.
     * 3) Секция не имеет отступа, если для нее задан такой флаг [Section.hasTopMargin].
     */
    private fun needSectionTopPadding(position: Int): Boolean {
        if (position > 0 && isCard(position - 1)) return false

        if (!findSection(position).hasTopMargin) return false

        return isFirstInSection(position)
    }

    private fun addSpaceBetweenItems(
        position: Int,
        context: Context,
        outRect: Rect,
        hasNoItemAtLeft: Boolean,
        hasNoSpanSpaceAtRight: Boolean,
        isFirstGroupInSection: Boolean,
        isInLastGroup: Boolean
    ) {
        val cardMarginPx =
            (context.resources.displayMetrics.density * findSection(position).cardMarginDp).toInt()

        with(outRect) {
            top = 0
//            left = if (hasNoItemAtLeft) cardMarginPx else cardMarginPx / 2
//            right = if (hasNoSpanSpaceAtRight) cardMarginPx else cardMarginPx / 2
            // TODO: https://online.sbis.ru/opendoc.html?guid=7c1b2116-6928-420e-bad3-233d23c3a37a
            left = cardMarginPx / 2
            right = cardMarginPx / 2
            bottom = cardMarginPx
        }
    }

    private fun findSection(absoluteItemPosition: Int): Section {
        if (sections.isEmpty()) return Section(emptyList())
        return sections[getIndexOfSection(absoluteItemPosition)]
    }

    /**
     * Найти блок в который входит элемент по переданной позиции.
     * @param absoluteItemPosition Int позиция элемента.
     */
    override fun getIndexOfSection(absoluteItemPosition: Int): Int {
        var offset = 0
        sections.forEachIndexed { index, section ->
            if (absoluteItemPosition < offset + section.items.size) {
                return index
            } else offset += section.items.size
        }
        return 0
    }

    /**
     * Заменить хранящийся список блоков на переданный.
     * @param sections List<Section>
     */
    private fun setSections(sections: List<Section>) {
        this.sections.clear()
        this.sections.addAll(sections)

        items.clear()
        sections.forEach {
            items.addAll(it.items)
        }
    }
}