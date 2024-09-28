package ru.tensor.sbis.design.view.input.searchinput.filter

/**
 * @author ma.kolpakov
 */
interface FilterAPI {
    /**@SelfDocumented */
    var clickListener: ((SbisFilterView) -> Unit)?

    /**
     * Признак того что установленный фильтр является фильтром по умолчанию
     */
    var isFilterDefault: Boolean

    /**
     * Установить иконку фильра
     */
    fun setIcon(icon: String)

    /**
     * Есть ли установленные фильтры
     */
    fun hasFilters(): Boolean

    /**
     * Установить цвет фильтра
     */
    fun setFilterColorType(filterColor: FilterColorType)

    /**
     * Установить размер фильтра
     */
    fun setFilterSize(filterSize: FilterSize)

    /**
     * Установить видимость нижнего разделителя фильтра
     */
    fun setDividerVisible(isVisible: Boolean)

    /**
     * Установка выбранных фильтров и обновление разметки
     *
     * @param asDefault являются ли данные фильтры фильтрами по умолчанию
     */
    fun setSelectedFilters(filters: List<String>, asDefault: Boolean = false)

    /**
     * Отображать ли текущие выбранные фильтры или только иконку
     */
    fun showCurrentFilters(visible: Boolean)

    /**
     * Возвращает строку с текущими фильтрами, перечисленными через запятую
     */
    fun filterString(): String
}
