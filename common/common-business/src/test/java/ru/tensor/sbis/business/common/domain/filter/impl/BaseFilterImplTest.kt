package ru.tensor.sbis.business.common.domain.filter.impl

import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.business.common.data.HashFilterProvider
import ru.tensor.sbis.business.common.testUtils.TestCppFilter
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule

class BaseFilterImplTest {

    @get:Rule
    @Suppress("RedundantVisibilityModifier")
    public var rule = TrampolineSchedulerRule()

    private val mockHashFilterProvider = mock<HashFilterProvider> {
        on { getHash<TestCppFilter>(any()) } doAnswer { invocationOnMock ->
            val cppFilter = invocationOnMock.arguments.first() as TestCppFilter
            cppFilter.getHash()
        }
    }

    private lateinit var filter: TestBaseFilter

    @Before
    fun setUp() {
        filter = TestBaseFilter(mockHashFilterProvider)
    }

    @Test
    fun `Return correct last cpp filter`() {
        val firstCppFilter = filter.build()
        val lastCppFilter = filter.build()

        assertNotEquals(filter.lastHash, firstCppFilter.getHashString())
        assertEquals(filter.lastHash, lastCppFilter.getHashString())
    }

    @Test
    fun `Return correct last cpp filter after mixed requests`() {
        filter.build()
        val repeatedCppFilter1 = filter.build()
        val lastUniqueCppFilter = filter.build()
        val repeatedFilterHash = repeatedCppFilter1.getHashString()
        val repeatedCppFilter2 = filter.build(repeatedFilterHash)

        assertNotEquals(filter.lastHash, lastUniqueCppFilter.getHash())
        assertEquals(filter.lastHash, repeatedFilterHash)

        assertNotNull(filter.lastCppFilter)
        assertEquals(filter.lastCppFilter, repeatedCppFilter1)
        assertEquals(filter.lastCppFilter, repeatedCppFilter2)
    }

    private class TestBaseFilter(hashProvider: HashFilterProvider) :
        BaseFilterImpl<TestCppFilter>(hashProvider) {

        override fun innerBuild() = TestCppFilter()
    }
}