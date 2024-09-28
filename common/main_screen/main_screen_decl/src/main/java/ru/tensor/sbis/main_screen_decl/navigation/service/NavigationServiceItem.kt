package ru.tensor.sbis.main_screen_decl.navigation.service

import ru.tensor.sbis.common.navigation.NavxId

/**
 * Модель пункта навигации, получаемая от микросервиса.
 *
 * @property title заголовок элемента навигации.
 * @property shortTitle короткий заголовок элемента навигации (если обычный не помещается).
 * @property activeTitle заголовок элемента навигации, когда он активен (выбран).
 *
 * @author us.bessonov
 */
interface NavigationServiceItem {
    val itemId: String
    val title: String?
    val shortTitle: String?
    val activeTitle: String?
    val icon: String?
    val itemType: ItemType
    val isVisible: Boolean
    val parentId: String?
    val navxId: NavxId?
        get() = NavxId.of(itemId, parentId)
    val pageData: NavigationPageData?
}