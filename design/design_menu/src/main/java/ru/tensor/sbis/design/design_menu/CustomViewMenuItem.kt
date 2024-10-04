package ru.tensor.sbis.design.design_menu

import android.content.Context
import android.view.View
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.design_menu.api.ClickableItem
import ru.tensor.sbis.design.design_menu.api.MenuItem

/**
 * Элемент меню с прикладным представлением.
 *
 * @param factory фабрика прикладных представлений.
 * @param handler обработчик нажатий на элемент.
 *
 * @author ra.geraskin
 */
@Parcelize
class CustomViewMenuItem(
    val factory: (Context) -> View,
    override var handler: (() -> Unit)? = null
) :
    MenuItem, ClickableItem