package ru.tensor.sbis.person_decl.status.bl

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс, который предоставляет доступ к основному объекту БЛ модуля "Статус"
 * @author us.bessonov
 */
interface StatusDataSourceProvider : Feature {
    /**
     * Предоставляет интерфейс БЛ [StatusDataSource]
     */
    fun getStatusDataSource(): StatusDataSource
}
