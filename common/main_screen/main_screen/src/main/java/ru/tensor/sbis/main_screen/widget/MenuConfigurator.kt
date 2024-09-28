package ru.tensor.sbis.main_screen.widget

import ru.tensor.sbis.design.navigation.view.adapter.NavAdapter
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.content.ContentController

/**
 * Предназначен для доступа к разделам навигации приложения и модификации состава навигации.
 *
 * @author us.bessonov
 */
internal class MenuConfigurator(itemsMap: Map<String, MenuItemRecord> = emptyMap()) {
    private val menuItems = itemsMap.toMutableMap()

    private var navAdapter: NavAdapter<NavigationItem>? = null

    /** @SelfDocumented */
    val items: List<MenuItemRecord>
        get() = menuItems.values.toList()

    /**
     * Элементы меню с их уникальными идентификаторами.
     */
    val entries: List<MutableMap.MutableEntry<String, MenuItemRecord>>
        get() = menuItems.entries.toList()

    /** @SelfDocumented */
    operator fun get(key: String): MenuItemRecord? = menuItems[key]

    /** @SelfDocumented */
    fun addItem(
        item: NavigationItem,
        configuration: ConfigurableMainScreen.MenuItemConfiguration,
        contentController: ContentController
    ): MenuItemRecord {
        val itemId = item.persistentUniqueIdentifier
        when {
            configuration.embeddedWidget != null -> {
                requireAdapter().add(item, configuration.embeddedWidget!!)
            }

            configuration.content != null -> {
                requireAdapter().add(
                    item = item,
                    counter = configuration.counter,
                    iconButton = null,
                    content = configuration.content!!,
                    parent = configuration.parent,
                    alignmentItem = configuration.alignmentItem,
                )
            }

            configuration.iconButton != null -> {
                requireAdapter().add(
                    item = item,
                    counter = configuration.counter,
                    iconButton = configuration.iconButton,
                    content = null,
                    parent = configuration.parent,
                    alignmentItem = configuration.alignmentItem,
                )
            }

            else -> {
                requireAdapter().add(item, configuration.counter, configuration.parent, configuration.alignmentItem)
            }
        }
        require(!menuItems.contains(itemId)) {
            "Элемент меню с идентификатором $itemId уже зарегистрирован."
        }
        val itemRecord = MenuItemRecord(
            item,
            configuration,
            contentController
        )
        menuItems[itemId] = itemRecord

        return itemRecord
    }

    /** @SelfDocumented */
    fun removeItem(item: NavigationItem): MenuItemRecord? {
        requireAdapter().remove(item)

        return menuItems.remove(item.persistentUniqueIdentifier)
    }

    /** @SelfDocumented */
    fun hasItem(id: String) = menuItems.containsKey(id)

    /** @SelfDocumented */
    fun setAdapter(adapter: NavAdapter<NavigationItem>?) {
        navAdapter = adapter
    }

    /** @SelfDocumented */
    fun requireAdapter() = requireNotNull(navAdapter)
}