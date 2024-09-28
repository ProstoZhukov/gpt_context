package ru.tensor.sbis.toolbox_decl.toolbar

import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Обобщённая модель вкладки верхней навигационной панели.
 *
 * @author us.bessonov
 */
data class NavigationTab(
    val navxId: NavxIdDecl?,
    val parentId: String,
    val title: String
)