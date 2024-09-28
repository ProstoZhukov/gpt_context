package ru.tensor.sbis.list.base.domain.fetcher

import org.mockito.kotlin.mock
import io.reactivex.subjects.BehaviorSubject
import org.junit.Test
import ru.tensor.sbis.list.base.domain.boundary.View

class CreateErrorEntityTest {

    @Test
    operator fun invoke() {

        val mock = mock<View<*>>()
        CreateErrorEntity()(
            mock,
            BehaviorSubject.create()
        )
    }
}