package ru.tensor.sbis.design.retail_views.menu

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.context_menu.CustomViewItem
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.context_menu.Item as SbisMenuItem

/**
 * Элемент меню для компонента PlatformPopupMenu
 */
class PlatformMenuItem(
    title: String,
    handler: (() -> Unit)? = null,
    image: SbisMobileIcon.Icon? = null,
    @ColorRes imageColor: Int = ResourcesCompat.ID_NULL,
    discoverabilityTitle: String? = null,
    needExtraPadding: Boolean = false,
    imageAlignment: HorizontalPosition = HorizontalPosition.LEFT
) : Item {
    override val item = MenuItem(
        title = title,
        handler = handler,
        image = image,
        imageColor = SbisColor.Res(imageColor),
        discoverabilityTitle = discoverabilityTitle,
        imageAlignment = imageAlignment,
        emptyImageAlignment = if (needExtraPadding) HorizontalPosition.LEFT else null
    )
}

/**
 * Элемент меню с кастомным вью для компонента PlatformPopupMenu
 */
class CustomPlatformMenuItem(
    factory: (Context) -> View,
    handler: (() -> Unit)? = null
) : Item {
    override val item = CustomViewItem(factory = factory, handler = handler)
}

/**
 * Базовый элемент меню для компонента PlatformPopupMenu
 */
interface Item {
    val item: SbisMenuItem
}