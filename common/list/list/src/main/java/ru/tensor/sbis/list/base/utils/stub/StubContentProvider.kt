package ru.tensor.sbis.list.base.utils.stub

import ru.tensor.sbis.list.base.domain.entity.paging.PagingData
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory

/**
 * Поставщик содержимого заглушки для необработанных ошибок с абстрактным текстом.
 *
 * @param SERVICE_RESULT тип результата, который возвращает микросервис
 *
 * @author du.bykov
 */
interface StubContentProvider<in SERVICE_RESULT> {

    /**
     * Предоставить контент для заглушки.
     *
     * @param result данные микросервиса для отображения заглушки.
     *
     * @see PagingData.isStub
     */
    fun provideStubViewContentFactory(result: SERVICE_RESULT?): StubViewContentFactory =
        DefaultStubContent()
}