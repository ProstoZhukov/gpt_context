/**
 * Набор расширений для работы с аргументами фрагментов без использования ключей.
 * Аргументы в этом файле определены на уровне компонента.
 *
 * @author vv.chekurda
 */
@file:Suppress("DEPRECATION")

package ru.tensor.sbis.design_selection.ui.main.utils

import android.os.Bundle
import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.annotation.StyleRes
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.design_selection.contract.SelectionDependenciesFactory
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem

internal var Bundle.config: SelectionConfig
    get() = getSerializable(CONFIG_KEY) as SelectionConfig
    set(value) = putSerializable(CONFIG_KEY, value)

internal var Bundle.folderItem: SelectionFolderItem?
    get() = getParcelable(FOLDER_ITEM_KEY) as? SelectionFolderItem
    set(value) = putParcelable(FOLDER_ITEM_KEY, value)

@get:AttrRes
internal var Bundle.themeAttr: Int
    get() = getInt(THEME_ATTR_KEY)
    set(value) = putInt(THEME_ATTR_KEY, value)

@get:StyleRes
internal var Bundle.defTheme: Int
    get() = getInt(THEME_RES_KEY)
    set(value) = putInt(THEME_RES_KEY, value)

@get:Px
internal var Bundle.listTopPadding: Int
    get() = getInt(LIST_TOP_PADDING_KEY)
    set(value) = putInt(LIST_TOP_PADDING_KEY, value)

internal var Bundle.itemsAnimationDurationMs: Long
    get() = getLong(ITEMS_ANIMATION_DURATION_MS_KEY, -1)
    set(value) = putLong(ITEMS_ANIMATION_DURATION_MS_KEY, value)

internal var Bundle.useRouterReplaceStrategy: Boolean
    get() = getBoolean(USE_ROUTER_REPLACE_STRATEGY_KEY, false)
    set(value) = putBoolean(USE_ROUTER_REPLACE_STRATEGY_KEY, value)

internal var Bundle.autoHideKeyboard: Boolean
    get() = getBoolean(AUTO_HIDE_KEYBOARD_KEY, true)
    set(value) = putBoolean(AUTO_HIDE_KEYBOARD_KEY, value)

internal var Bundle.showStubs: Boolean
    get() = getBoolean(SHOW_STUBS_KEY, true)
    set(value) = putBoolean(SHOW_STUBS_KEY, value)

internal var Bundle.showLoaders: Boolean
    get() = getBoolean(SHOW_LOADERS_KEY)
    set(value) = putBoolean(SHOW_LOADERS_KEY, value)

@Suppress("UNCHECKED_CAST")
internal var Bundle.dependenciesProvider: SelectionDependenciesFactory.Provider<*, *>
    get() = getSerializable(SELECTION_DEPENDENCIES_PROVIDER)
        as SelectionDependenciesFactory.Provider<*, *>
    set(value) = putSerializable(SELECTION_DEPENDENCIES_PROVIDER, value)

private const val CONFIG_KEY = "SELECTION_CONFIG"
private const val SELECTION_DEPENDENCIES_PROVIDER = "SELECTION_DEPENDENCIES_PROVIDER"
private const val FOLDER_ITEM_KEY = "PARENT_ITEM"
private const val THEME_ATTR_KEY = "THEME_ATTR_KEY"
private const val THEME_RES_KEY = "THEME_RES_KEY"
private const val LIST_TOP_PADDING_KEY = "LIST_TOP_PADDING_KEY"
private const val ITEMS_ANIMATION_DURATION_MS_KEY = "ITEMS_ANIMATION_DURATION_MS_KEY"
private const val USE_ROUTER_REPLACE_STRATEGY_KEY = "USE_ROUTER_REPLACE_STRATEGY_KEY"
private const val AUTO_HIDE_KEYBOARD_KEY = "AUTO_HIDE_KEYBOARD_KEY"
private const val SHOW_STUBS_KEY = "SHOW_STUBS_KEY"
private const val SHOW_LOADERS_KEY = "SHOW_LOADERS_KEY"