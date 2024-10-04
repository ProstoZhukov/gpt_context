package ru.tensor.sbis.design.view.input.searchinput.filter

import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import java.util.LinkedList

/**
 * @author ma.kolpakov
 */
internal class FilterController : FilterAPI {

    private var isCurrentFilterVisible = true

    private var filtersList = LinkedList<String>()

    lateinit var sbisFilterView: SbisFilterView

    override var clickListener: ((SbisFilterView) -> Unit)? = null

    override var isFilterDefault: Boolean = false

    fun attachView(sbisFilterView: SbisFilterView) {
        this.sbisFilterView = sbisFilterView
        sbisFilterView.selectedFilters.setOnClickListener { _, _ ->
            this.sbisFilterView.requestFocus()
            clickListener?.invoke(sbisFilterView)
        }
        sbisFilterView.filterIcon.setOnClickListener { _, _ ->
            this.sbisFilterView.requestFocus()
            clickListener?.invoke(sbisFilterView)
        }
        setFilterSize(FilterSize.MEDIUM)
        setFilterColorType(FilterColorType.BASE)
    }

    override fun setIcon(icon: String) {
        sbisFilterView.filterIcon.configure {
            text = icon
        }
    }

    override fun hasFilters() = !filtersList.isEmpty()

    override fun setFilterColorType(filterColor: FilterColorType) {
        sbisFilterView.setColor(filterColor)
    }

    override fun setFilterSize(filterSize: FilterSize) {
        sbisFilterView.setSize(filterSize)
    }

    override fun setDividerVisible(isVisible: Boolean) {
        sbisFilterView.dividerIsVisible = isVisible
    }

    override fun setSelectedFilters(filters: List<String>, asDefault: Boolean) {
        filtersList.clear()
        filtersList.addAll(filters.filterNot { it.isBlank() })
        showCurrentFilters(isCurrentFilterVisible)
        isFilterDefault = asDefault
    }

    override fun showCurrentFilters(visible: Boolean) {
        isCurrentFilterVisible = visible
        setSelectedFilter(if (isCurrentFilterVisible) filterString() else null)
        sbisFilterView.setFilled(!visible && filtersList.isNotEmpty())
    }

    override fun filterString() = filtersList.joinToString()

    private fun setSelectedFilter(filter: String?) {
        sbisFilterView.selectedFilters.buildLayout {
            text = filter ?: StringUtils.EMPTY
        }
        sbisFilterView.contentDescription = filter
        sbisFilterView.safeRequestLayout()
    }

}

/**
 * Цвет фильтра
 */
enum class FilterColorType {
    BASE,
    ADDITIONAL
}

/**
 * Размер фильтра
 */
enum class FilterSize {
    MEDIUM,
    SMALL
}
