package ru.tensor.sbis.list.base.domain.entity.paging

import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.domain.entity.Mapper
import ru.tensor.sbis.list.base.domain.entity.PagingListScreenEntity
import ru.tensor.sbis.list.base.domain.entity.paging.filter.FilterFactory
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory
import ru.tensor.sbis.list.base.utils.stub.DefaultStubContentProvider
import ru.tensor.sbis.list.base.utils.stub.StubContentProvider

/**
 * Бизнес модель экрана списка с пагинацией. Содержит логику работы со страницами данных.
 * @param ANCHOR тип объекта для построение границы запроса.
 * @param SERVICE_RESULT тип результата, возвращаемого микросервисом.
 * @param FILTER тип фильтра, принимаемого микросервисом.
 * @property mapper Mapper<SERVICE_RESULT>
 * @property helper ResultHelper<ANCHOR, SERVICE_RESULT>
 * Если будет выставлено меньше [ITEMS_ON_PAGE], то будет использовано [ITEMS_ON_PAGE].
 * @property pagingData TreeMap<Int, SERVICE_RESULT>
 * @constructor
 */
class PagingEntity<ANCHOR, SERVICE_RESULT : Any, FILTER> internal constructor(
    private val mapper: Mapper<SERVICE_RESULT>,
    private val helper: ResultHelper<ANCHOR, SERVICE_RESULT>,
    private val stubContentProvider: StubContentProvider<SERVICE_RESULT> = DefaultStubContentProvider(),
    itemsOnPage: Long = ITEMS_ON_PAGE,
    private val pagingData: PagingData<SERVICE_RESULT>,
    private val filterFactory: FilterFactory<ANCHOR, SERVICE_RESULT, FILTER> = FilterFactory(
        pagingData,
        helper,
        itemsOnPage
    )
) : PagingListScreenEntity<FILTER> {

    constructor(
        mapper: Mapper<SERVICE_RESULT>,
        helper: ResultHelper<ANCHOR, SERVICE_RESULT>,
        stubContentProvider: StubContentProvider<SERVICE_RESULT> = DefaultStubContentProvider(),
        maxPages: Int = DEFAULT_PAGES,
        itemsOnPage: Long = ITEMS_ON_PAGE,
        pagingData: PagingData<SERVICE_RESULT> = PagingData(helper, maxPages)
    ) : this(
        mapper,
        helper,
        stubContentProvider,
        itemsOnPage,
        pagingData
    )

    override fun hasNext() = pagingData.hasNext()

    override fun hasPrevious() = pagingData.hasPrevious()

    override fun increasePage() {
        pagingData.increasePage()
    }

    override fun decreasePage() {
        pagingData.decreasePage()
    }

    override fun cleanPagesData() {
        pagingData.clear()
        filters.clear()
        filtersCache.clear()
    }

    override fun isStub() = pagingData.isStub() || (pagingData.isEmptyAndNoNextOrPreviousData())

    override fun isData() =
        !pagingData.isStub() && !(pagingData.isEmpty() && pagingData.hasNextOrPreviousData())

    override fun isUpToDate(): Boolean =
        // актуально, если нет страниц, которые загружены из кэша
        pagingData.pagingState.none { (_, event) -> event == PageState.LOADED }

    override fun provideStubViewContentFactory(): StubViewContentFactory =
        stubContentProvider.provideStubViewContentFactory(pagingData.getStubData())

    override fun toListData() = mapper.map(pagingData.listOfValues())

    private val filtersCache = HashMap<Int, FilterAndPageProvider<FILTER>>()
    private val filters = HashMap<Int, FilterAndPageProvider<FILTER>>()

    /**
     * Фильтр для следующей страницы данных.
     * @param createFilter Function3<[@kotlin.ParameterName] ANCHOR?, [@kotlin.ParameterName] Boolean, [@kotlin.ParameterName] Int, FILTER>
     * @return ListFilter<FILTER>
     */
    fun filterForNext(createFilter: CreateFilter<ANCHOR, FILTER>): FilterAndPageProvider<FILTER> {
        val provider = filterFactory.filterForNext(createFilter)
        filtersCache[provider.getPageNumber()] = provider
        return provider
    }


    /**
     * Фильтр для предыдущей страницы данных.
     * @param createFilter Function3<[@kotlin.ParameterName] ANCHOR?, [@kotlin.ParameterName] Boolean, [@kotlin.ParameterName] Int, FILTER>
     * @return ListFilter<FILTER>
     */
    fun filterForPrevious(createFilter: CreateFilter<ANCHOR, FILTER>): FilterAndPageProvider<FILTER> {
        val provider = filterFactory.filterForPrevious(createFilter)
        filtersCache[provider.getPageNumber()] = provider
        return provider
    }

    /**
     * Обновить БМ данными для указанной страницы. Либо добавить страницу, если такой еще не было.
     * @param page Int номер страницы, для которых производилась выборка.
     * @param result SERVICE_RESULT данные микросервиса контроллера.
     */
    fun update(page: Int, result: SERVICE_RESULT) {
        if (!pagingData.isEmpty() && helper.isEmpty(result)
            && (page == pagingData.lastKeyOrZeroIfEmpty() + 1
                    || page == pagingData.firstKeyOrZeroIfEmpty() - 1)
        ) return

        val pages = synchronized(pagingData) {
            pagingData.update(page, result)
        }
        filtersCache[page]?.let {
            filters[page] = it
            filtersCache.remove(page)
        }

        filters.keys.filter { key ->
            !pages.contains(key)
        }.forEach {
            filters.remove(it)
        }
    }

    override fun getPageFilters(): List<FilterAndPageProvider<FILTER>> = filters.values.toList()
}

/**
 * Создать фильтр для передачи в микросервис для загрузки страницы с данными.
 */
typealias CreateFilter<ANCHOR, FILTER> = (
    anchor: ANCHOR?,
    includeAnchor: Boolean,
    itemsOnPage: Long,
    pageNumber: Int
) -> FILTER

const val ITEMS_ON_PAGE = 30L
const val MIN_PAGES = 3
const val DEFAULT_PAGES = 5