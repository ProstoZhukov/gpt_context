package ru.tensor.sbis.list.base.domain.fetcher

import io.reactivex.subjects.Subject
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.domain.stub.UnknownErrorEntity

/**
 * Создать бизнес модель списка с состоянием неопределенной ошибки.
 */
class CreateErrorEntity : (View<*>, Subject<Unit>) -> UnknownErrorEntity {

    override fun invoke(
        view: View<*>,
        subject: Subject<Unit>
    ) = UnknownErrorEntity {
        view.showLoading()
        subject.onNext(Unit)
    }
}