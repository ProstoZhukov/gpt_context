package ru.tensor.sbis.list.base.domain.fetcher

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import junitparams.JUnitParamsRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity
import ru.tensor.sbis.list.base.domain.stub.UnknownErrorEntity

@RunWith(JUnitParamsRunner::class)
class EntityCreationSubscriberTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()
    private val updateSubject = BehaviorSubject.create<Unit>()
    private val createErrorEntity = mock<CreateErrorEntity>()
    private val errorEntity = mock<UnknownErrorEntity>()
    private val mockView = mock<View<ListScreenEntity>>()

    @Test
    fun `No invocation`() {
        val entityCreation = PublishSubject.create<ListScreenEntity>()
        subscribeOnFetchingResult(entityCreation)

        verifyNoMoreInteractions(mockView)
    }

    @Test
    fun `Show error`() {
        val exception = Exception("Some error")
        val entityCreationObservable = Observable.error<ListScreenEntity>(exception)
        whenever(createErrorEntity(mockView, updateSubject)).thenReturn(errorEntity)
        //act
        subscribeOnFetchingResult(entityCreationObservable)
        //verify
        verify(mockView).showStub(errorEntity)
    }

    @Test
    fun showData() {
        val entity = mock<ListScreenEntity> {
            on { isStub() } doReturn false
            on { isData() } doReturn true
        }
        val entityCreationObservable = Observable.just(entity)
        whenever(createErrorEntity.invoke(mockView, updateSubject)).thenReturn(errorEntity)
        //act
        subscribeOnFetchingResult(entityCreationObservable)
        //verify
        verify(mockView).showData(entity)
    }

    @Test
    fun showStub() {
        val entity = mock<ListScreenEntity> {
            on { isStub() } doReturn true
        }
        val entityCreationObservable = Observable.just(entity)
        whenever(createErrorEntity.invoke(mockView, updateSubject)).thenReturn(errorEntity)
        //act
        subscribeOnFetchingResult(entityCreationObservable)
        //verify
        verify(mockView).showStub(entity)
    }

    private fun subscribeOnFetchingResult(entityCreationObservable: Observable<ListScreenEntity>) {
        EntityCreationSubscriber<ListScreenEntity>(
            updateSubject,
            createErrorEntity
        )
            .subscribeOnFetchingResult(entityCreationObservable, mockView)
    }
}