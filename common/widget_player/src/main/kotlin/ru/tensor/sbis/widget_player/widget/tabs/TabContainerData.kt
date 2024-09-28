package ru.tensor.sbis.widget_player.widget.tabs

import ru.tensor.sbis.design.tabs.api.SbisTabsViewItem
import java.util.LinkedList

/**
 * @author am.boldinov
 */
internal data class TabContainerData(
    val selectedIndex: Int,
    val tabs: LinkedList<SbisTabsViewItem>
)