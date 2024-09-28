package ru.tensor.sbis.list.base.presentation

import ru.tensor.sbis.list.base.utils.stub.DefaultStubContent

/**
 * Поставщик данных для заглушки.
 */
interface StubEntity {

    /**
     * @SelfDocumented
     *
     * @return @SelfDocumented
     */
    fun provideStubViewContentFactory(): StubViewContentFactory =
        DefaultStubContent()
}