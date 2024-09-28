package ru.tensor.sbis.navigation_service.model

import ru.tensor.sbis.desktop.navigation.generated.ImenuItem
import ru.tensor.sbis.main_screen_decl.navigation.service.ItemType
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationPageData
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationServiceItem

/**
 * Модель пункта меню навигации.
 *
 * @author us.bessonov
 */
internal class MenuItemData(
    override val itemId: String,
    override val title: String?,
    override val shortTitle: String?,
    override val activeTitle: String?,
    override val icon: String?,
    override val itemType: ItemType,
    override val isVisible: Boolean,
    override val parentId: String?,
    override val pageData: NavigationPageData?
) : NavigationServiceItem

/** @SelfDocumented */
internal fun ImenuItem.getData(pageData: NavigationPageData?) = with(data()) {
    @Suppress("UNNECESSARY_SAFE_CALL")
    MenuItemData(
        itemId.ifEmpty { id },
        title,
        shortTitle,
        activeTitle,
        icon,
        ItemType.values()[type.ordinal],
        isVisible,
        parent()?.data()?.id?.takeUnless { it.isEmpty() },
        pageData
    )
}