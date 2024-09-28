package ru.tensor.sbis.toolbox_decl.linkopener.data

/**
 * Данные о поддерживаемых ссылках.
 *
 * @property hosts Список поддерживаемых хостов.
 * @property paths Список шаблонов/префиксов целевых ссылок.
 *
 * @author us.bessonov
 */
interface LinksData {
    val hosts: List<String>
    val paths: List<String>
}