package ru.tensor.sbis.appdesign.selection.datasource

/**
 * @author ma.kolpakov
 */
interface DemoController<SERVICE_RESULT, FILTER> {

    fun list(filter: FILTER): SERVICE_RESULT

    fun refresh(filter: FILTER): SERVICE_RESULT

    fun loadSelectedItems(): SERVICE_RESULT
}