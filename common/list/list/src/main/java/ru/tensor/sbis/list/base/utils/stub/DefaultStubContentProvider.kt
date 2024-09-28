package ru.tensor.sbis.list.base.utils.stub

import ru.tensor.sbis.list.base.presentation.StubViewContentFactory

/**
 * Реализация по умолчанию для [StubContentProvider].
 *
 * @author du.bykov
 */
class DefaultStubContentProvider : StubContentProvider<Any> {

    override fun provideStubViewContentFactory(result: Any?): StubViewContentFactory =
        DefaultStubContent()
}