package ru.tensor.sbis.list.base.data

import org.mockito.kotlin.argThat
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.same
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.reactivex.subjects.PublishSubject
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.data.filter.FilterProvider
import ru.tensor.sbis.list.base.domain.entity.EntityFactory
import ru.tensor.sbis.list.base.domain.entity.PagingListScreenEntity

@RunWith(MockitoJUnitRunner.StrictStubs::class)
internal class CrudRepositoryTest {

    private val subject = PublishSubject.create<Map<String, String>>()

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    private val filter0 = "filter0"
    private val filter1 = "filter"
    private val list0 = "list0"
    private val list1 = "list1"
    private val refresh0 = "refresh0"
    private val refresh1 = "refresh1"
    private val mockEntityFactory = mock<EntityFactory<TestEntity, String>>()
    private val params = mutableMapOf<String, String>().also { it["key"] = "value" }
    private val mockServiceWrapper = mock<ServiceWrapper<String, String>> {
        on { list(filter0) } doReturn list0
        on { list(filter1) } doReturn list1
        on { refresh(filter0, params) } doReturn refresh0
        on { refresh(filter1, params) } doReturn refresh1
    }

    private val filterAndPageProvider0 = mock<FilterAndPageProvider<String>> {
        on { getServiceFilter() } doReturn filter0
        on { getPageNumber() } doReturn 0
    }
    private val filterAndPageProvider1 = mock<FilterAndPageProvider<String>> {
        on { getServiceFilter() } doReturn filter1
        on { getPageNumber() } doReturn 1
    }
    private val entity = mock<TestEntity>()
    private val crudRepository =
        CrudRepository(
            mockEntityFactory,
            mockServiceWrapper,
            subject
        )

    @Test
    fun `When update, then call refresh to wrapper and update  entity with factory`() {
        crudRepository.update(entity, filterAndPageProvider0).test()

        verify(mockEntityFactory).updateEntityWithData(eq(entity), argThat { list ->
            val pair0 = list[0]
            pair0.first == 0
                    && pair0.second == list0
        })
    }

    @Test
    fun `When update, then call refresh to wrapper and update  entity with factory0`() {
        crudRepository.update(entity, filterAndPageProvider0).subscribe().dispose()
        crudRepository.update(entity, filterAndPageProvider1).subscribe().dispose()

        verify(mockEntityFactory).updateEntityWithData(same(entity), argThat { list ->
            val pair0 = list[0]
            pair0.first == 0
                    && pair0.second == list0
        })
        verify(mockEntityFactory).updateEntityWithData(same(entity), argThat { list ->
            val pair1 = list[0]
            pair1.first == 1
                    && pair1.second == list1
        })
    }

    @Test
    fun `When update, then call refresh to wrapper and update entity with factory1`() {
        whenever(entity.getPageFilters()).doReturn(listOf(filterAndPageProvider0, filterAndPageProvider1))
        crudRepository.update(entity, filterAndPageProvider0).test()
        clearInvocations(mockEntityFactory)

        subject.onNext(params)

        verify(mockEntityFactory).updateEntityWithData(same(entity), argThat { list ->
            val pair0 = list[0]
            val pair1 = list[1]

            pair0.first == 0
                    && pair0.second == refresh0
                    &&
                    pair1.first == 1
                    && pair1.second == refresh1
        })
    }

    //todo Проверить, что подписа на колбек создается до обращения к методу list
}

interface TestEntity : PagingListScreenEntity<String>, FilterProvider<String>