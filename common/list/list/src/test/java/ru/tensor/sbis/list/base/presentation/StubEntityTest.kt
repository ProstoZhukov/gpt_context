package ru.tensor.sbis.list.base.presentation

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import ru.tensor.sbis.list.base.utils.stub.DefaultStubContent

class StubEntityTest {

    @Test
    fun default() {
        MatcherAssert.assertThat(
            object : StubEntity {}.provideStubViewContentFactory(),
            CoreMatchers.instanceOf(DefaultStubContent::class.java)
        )
    }
}