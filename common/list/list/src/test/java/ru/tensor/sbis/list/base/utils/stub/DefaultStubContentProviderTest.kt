package ru.tensor.sbis.list.base.utils.stub

import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class DefaultStubContentProviderTest {

    @Test
    fun provideStubContent() {
        assertThat(
            DefaultStubContentProvider().provideStubViewContentFactory(Unit),
            instanceOf(DefaultStubContent::class.java)
        )
    }
}