package ru.tensor.sbis.list.base.data

import ru.tensor.sbis.list.base.utils.stub.StubContentProvider

/**
 * Позволяет получать данные о результате выборки - то, что возвращают методы list и refresh.
 * Используется для определения состояния бизнес модель экрана - нужно ли показать заглушку, можно ли подгружать
 * следующую страницу данных и прочее.
 * [ANCHOR] "якорный" объект, который используется для получения начальной границы страницы выборки, как правило,
 * это uuid последнего(если нужно получить следующую страницу, для предыдущей - первый) элемента в предыдущей странице,
 * null(для самой первой страницы) или сам последний(первый) элемент предыдущей страницы.
 *
 * @param[SERVICE_RESULT] объект, возвращаемый методами list и refresh.
 */
interface ResultHelper<ANCHOR, SERVICE_RESULT> {

    /**
     * Вернуть true, если [result] содержит признак, что можно подгрузить следующую страницу, иначе false.
     *
     * Пример:
     * ```
     * override fun hasNext(result: ListResultOfPerson) = result.haveMore
     *```
     */
    fun hasNext(result: SERVICE_RESULT): Boolean

    /**
     * Вернуть true, если [result] содержит признак, что можно подгрузить предыдущую страницу, иначе false.
     * Как правило, микросервисы не поддерживают показ списка не с первой страницы и методов в нем может не быть.
     *
     * Пример:
     * ```
     * override fun hasPrevious(result: ListResultOfPerson) = result.havePrevious
     *```
     */
    fun hasPrevious(result: SERVICE_RESULT): Boolean = false

    /**
     * Вернуть true, если [result] содержит результат выборки элементы списка для отображения, иначе false.
     *
     * Пример:
     * ```
     * override fun isEmpty(result: ListResultOfPerson) = result.result.isEmpty()
     *```
     */
    fun isEmpty(result: SERVICE_RESULT): Boolean

    /**
     * Вернуть true, если [result] является данными для отображения заглушки, иначе false. Метод актуален для реестров,
     * где микросервис может вернуть заглушку или информация о ней приходит в метаданных DataRefreshCallback
     *
     * @see StubContentProvider.provideStubViewContentFactory
     */
    fun isStub(result: SERVICE_RESULT): Boolean = false

    /**
     * Получить объект из [result], который должен быть использован для получения следующей страницы данных.
     * Если же микросервис использует не опорный объект, а номер страницы, то достаточно вернуть null.
     *
     * Пример:
     * ```
     * override fun getAnchorForNextPage(page: ListResultOfPerson): Anchor? {
     *     return page.result.lastOrNull()?.anchor
     * }
     * ```
     */
    fun getAnchorForNextPage(result: SERVICE_RESULT): ANCHOR?

    /**
     * Получить объект из [result], который должен быть использован для получения предыдущей страницы данных.
     * Если же микросервис использует не опорный объект, а номер страницы, то достаточно вернуть null.
     *
     * Пример:
     * ```
     * override fun getAnchorForPreviousPage(page: ListResultOfPerson): Anchor? {
     *     return page.result.firstOrNull()?.anchor
     * }
     * ````
     */
    fun getAnchorForPreviousPage(result: SERVICE_RESULT): ANCHOR?
}