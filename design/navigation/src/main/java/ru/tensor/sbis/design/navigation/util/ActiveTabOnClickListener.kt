package ru.tensor.sbis.design.navigation.util

import ru.tensor.sbis.design.navigation.view.model.NavigationItem

/**
 * Слушатель нажатий на активный таб ННП.
 *
 * @author ma.kolpakov
 */
@Suppress("unused")
fun interface ActiveTabOnClickListener {
    /** @SelfDocumented */
    fun onActiveTabClicked(item: NavigationItem)
}