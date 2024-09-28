package ru.tensor.sbis.list.base.domain.fetcher

import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.never
import org.mockito.kotlin.whenever
import io.reactivex.Emitter
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.list.base.data.CrudRepository
import ru.tensor.sbis.list.base.data.ServiceWrapper
import ru.tensor.sbis.list.base.data.TestEntity
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.domain.entity.EntityFactory
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity

/**
 * @author du.bykov
 *
 * Реализовать тест, сейчас в нём содержится ряд недостатков, которые не позволяют активировать его
 * 1. сон для имитации вызова refresh контроллера
 * 2. нет возможности дождаться завершения rx-потока, нужен рефакторинг
 * 3. вытекает из п.2, сон для завершения внутренних rx-обновлений
 * 4. проверка порядка с [never] проверяет только отсутствие вызова, а не "метод не вызывался в указанном порядке"
 */
@Ignore("TODO: 10/13/2020 https://online.sbis.ru/opendoc.html?guid=3673ddbf-8c11-4878-8ec3-de7755ebd4f8")
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class RepositoryFetcherIntegrationTest {

    private val page = 4

    private val filter = "Test filter"

    @Mock
    private lateinit var listResult: Any

    @Mock
    private lateinit var refreshResult: Any

    @Mock
    private lateinit var filterAndPageProvider: FilterAndPageProvider<String>

    @Mock
    private lateinit var view: View<TestEntity>

    @Mock
    private lateinit var entity: TestEntity

    @Mock
    private lateinit var factory: EntityFactory<TestEntity, Any>

    @Mock
    private lateinit var service: ServiceWrapper<Any, String>

    private lateinit var repository: CrudRepository<Any, TestEntity, String>

    private lateinit var subscriber: EntityCreationSubscriber<TestEntity>

    private lateinit var fetcher: RepositoryFetcher<TestEntity, String>

    @Before
    fun setUp() {
        whenever(filterAndPageProvider.getPageNumber()).thenReturn(page)
        whenever(filterAndPageProvider.getServiceFilter()).thenReturn(filter)

        whenever(service.list(filter)).thenReturn(listResult)
        whenever(service.setCallbackAndReturnSubscription(any())).doAnswer { invocationOnMock ->
            // п.1
            Thread.sleep(10L)
            val emitter: Emitter<Any> = invocationOnMock.getArgument(0)
            emitter.onNext(refreshResult)
            null
        }

//        repository = CrudRepository(factory, service, )
//        subscriber = EntityCreationSubscriber(mainThreadScheduler = Schedulers.single())
//        fetcher = RepositoryFetcher(repository, subscriber)
    }

    /**
     * Проверяет атомарность отображения данных: между проверкой [ListScreenEntity.isStub] и отображением
     * [View.showStub]/[View.showData] объект [entity] не должен обновляться фоновыми потоками
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=1b348b8d-5f36-4241-91e6-eb6070e7a2ba
     */
    @Test
    fun `When view shows entity, then entity shouldn't be updated on background thread`() {
        whenever(entity.isStub()).thenReturn(true)
        val verificationOrder = inOrder(entity, factory, view)

        // п.2
        fetcher.updateListEntity(entity, filterAndPageProvider, view)

        // п.3
        Thread.sleep(100L)

        verificationOrder.verify(factory).updateEntityWithData(page, entity, listResult)
        verificationOrder.verify(entity).isStub()
        // п.4
        verificationOrder.verify(factory, never()).updateEntityWithData(page, entity, refreshResult)
        verificationOrder.verify(view).showData(entity)
    }
}