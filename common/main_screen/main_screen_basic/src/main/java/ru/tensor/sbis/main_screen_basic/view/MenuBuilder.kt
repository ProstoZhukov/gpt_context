package ru.tensor.sbis.main_screen_basic.view

import android.content.Context
import ru.tensor.sbis.design.design_menu.SbisMenu
import ru.tensor.sbis.design.design_menu.SbisMenuItem
import ru.tensor.sbis.design.theme.res.SbisString
import ru.tensor.sbis.main_screen_decl.basic.data.ScreenId

/**
 * Предназначен для формирования [SbisMenu], используемого в шапке.
 *
 * @author us.bessonov
 */
internal class MenuBuilder(
    private val context: Context,
    private val hiddenItems: Set<ScreenId>
) {

    private val items = mutableListOf<Pair<SbisMenuItem, ScreenId>>()

    /** @SelfDocumented */
    fun addItem(id: ScreenId, title: SbisString, onClick: (id: ScreenId) -> Unit) {
        items.add(
            SbisMenuItem(
                title.getString(context),
                handler = { onClick(id) }
            ) to id
        )
    }

    /** @SelfDocumented */
    fun buildMenu() = SbisMenu(
        children = items
            .filterNot { hiddenItems.contains(it.second) }
            .map { it.first }
    )

    /** @SelfDocumented */
    fun isEmpty() = items.none { !hiddenItems.contains(it.second) }
}
