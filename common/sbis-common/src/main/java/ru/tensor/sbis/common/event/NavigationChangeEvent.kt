package ru.tensor.sbis.common.event

import ru.tensor.sbis.common.navigation.MenuNavigationItemType

/**
 * Created by r.a.stepanov on 18.09.2019.
*/
data class NavigationChangeEvent(val menuItem: MenuNavigationItemType)