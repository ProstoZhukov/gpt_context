package ru.tensor.sbis.toolbox_decl.navigation

import java.io.Serializable

/**
 * Контракт идентификатора элемента структуры навигации в приложении.
 * Не следует объявлять собственные реализации для элементов - используйте одно из значений
 * `ru.tensor.sbis.common.navigation.NavxId`.
 *
 * @author us.bessonov
 */
interface NavxIdDecl : Serializable {
    /**
     * Строковые идентификаторы, соответствующие одной и той же структурной единице навигации.
     */
    val ids: Set<String>

    /**
     * Должен ли пункт быть доступен принудительно (например, если сервис пока не знает о нём, или не должен влиять).
     */
    val forceEnabled: Boolean

    /**
     * Проверить соответствие строкового идентификатора данному значению.
     */
    fun matches(id: String): Boolean

    @Deprecated(
        "Строковых идентификаторов может быть несколько. Будет удалено по " +
            "https://online.sbis.ru/opendoc.html?guid=307c8603-08ac-44b8-a0f0-0673ef3c6293&client=3",
        replaceWith = ReplaceWith("ids")
    )
    val id: String
}