package ru.tensor.sbis.design.design_menu.utils

import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.design_menu.SbisMenu
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable

/**
 * Класс реализующий функционал создания фрагмента, отображающего в себе компонент меню.
 *
 * @property menu меню для отображения в шторке.
 *
 * @author ra.geraskin
 */
@Parcelize
internal class MovablePanelContentCreator(
    private val menu: SbisMenu = SbisMenu(children = emptyList())
) : ContentCreatorParcelable {

    /** @SelfDocumented */
    override fun createFragment(): Fragment = MenuMovablePanelFragment(menu)

}