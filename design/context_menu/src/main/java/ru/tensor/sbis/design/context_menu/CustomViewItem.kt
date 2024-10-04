package ru.tensor.sbis.design.context_menu

import android.content.Context
import android.view.View

/**
 * Элемент меню с прикладным представлением.
 * @param factory фабрика прикладных представлений.
 * @param handler обработчик нажатий на элемент.
 *
 * @author ma.kolpakov
 */
class CustomViewItem(val factory: (Context) -> View, override var handler: (() -> Unit)? = null) : Item, ClickableItem