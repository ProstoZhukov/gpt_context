package ru.tensor.sbis.design.toolbar.appbar

import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import ru.tensor.sbis.design.toolbar.R

/**
 * Хелпер для динамической установки заголовка у [SbisAppBarLayout] при стандартной компановке
 * c шаблоном sbis_app_bar_view.xml
 * TODO: https://online.sbis.ru/opendoc.html?guid=31f3f061-7f04-428e-9582-887159411607
 *
 * @author ma.kolpakov
 */
data class SbisAppBarHelper internal constructor(
    private val toolbar: Toolbar
) {
    internal constructor(appBar: SbisAppBarLayout) : this(
        appBar.findViewById<Toolbar>(R.id.toolbar_sbisToolbar)
    )

    fun setTitle(@StringRes titleId: Int) {
        setTitle(toolbar.context.resources.getString(titleId))
    }

    fun setTitle(title: String) {
        toolbar.title = title
    }
}

/**
 * Использовать для динамической устаноки заголовка в компановке с sbis_app_bar_view.xml
 */
fun SbisAppBarLayout.setup(function: SbisAppBarHelper.() -> Unit) {
    SbisAppBarHelper(this).function()
}