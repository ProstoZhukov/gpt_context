package ru.tensor.sbis.design.tabs

import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Тестовая реализация [NavxIdDecl].
 *
 * @author da.zolotarev
 */
internal enum class NavxTestId(vararg identifiers: String) : NavxIdDecl {
    FIRST("a"),
    SECOND("b", "c"),
    THIRD("d", "e"),
    FOURTH("f");

    override var ids = identifiers.toSet()

    override var forceEnabled = false
    override fun matches(id: String) = ids.contains(id)

    override var id: String
        get() = ids.first()
        set(value) {
            ids = setOf(value)
        }
}