package ru.tensor.sbis.main_screen_decl.navigation.service

/**
 * Элемент иерархической структуры навигации приложения.
 *
 * @author us.bessonov
 */
class NavigationServiceNode(
    val data: NavigationServiceItem,
    val children: List<NavigationServiceNode>,
    var parent: NavigationServiceNode? = null
) {
    /**
     * Является ли элемент пустым (вероятнее всего, корневой).
     */
    val isEmpty: Boolean
        get() = data.itemId.isEmpty()
}

/** @SelfDocumented */
fun NavigationServiceNode?.asFlatList(
    filter: (NavigationServiceNode) -> Boolean = { true }
): List<NavigationServiceItem> {
    val root = this
        ?: return emptyList()
    return mutableListOf<NavigationServiceItem>().apply {
        addItems(root, this, filter)
    }
}

private fun addItems(
    parent: NavigationServiceNode,
    container: MutableList<NavigationServiceItem>,
    filter: (NavigationServiceNode) -> Boolean
) {
    if (filter(parent)) {
        container.add(parent.data)
    }
    parent.children.forEach {
        addItems(it, container, filter)
    }
}