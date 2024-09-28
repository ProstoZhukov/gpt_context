package ru.tensor.sbis.business.common.domain.filter.base

import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.business.common.data.HashFilterProvider
import ru.tensor.sbis.business.common.domain.filter.HashFilter
import ru.tensor.sbis.business.common.testUtils.TestCppFilter

class HashFilterImplTest {

    private val mockHashFilterProvider = mock<HashFilterProvider> {
        on { getHash<TestCppFilter>(any()) } doAnswer { invocationOnMock ->
            (invocationOnMock.arguments.first() as TestCppFilter).getHash()
        }
        on { getName(any()) } doAnswer { invocationOnMock ->
            (invocationOnMock.arguments.first() as TestCppFilter)::class.java.simpleName
        }
    }
    private var testLastCpp: TestCppFilter = TestCppFilter()

    private lateinit var hashFilter: HashFilter

    @Before
    fun setUp() {
        hashFilter = TestHashFilter(mockHashFilterProvider)
    }

    @Test
    fun `When given hash in refresh callback EQUALS to last one then return true`() {
        val targetHash = testLastCpp.getHashString()
        val callback = RefreshCallback(mapOf(RefreshCallback.HASH_KEY to targetHash))

        assertTrue(hashFilter.equalCallback(callback))
    }

    @Test
    fun `When given hash in refresh callback NOT equals to last one then return false`() {
        val foreignHash = TestCppFilter().getHashString()
        val callback = RefreshCallback(mapOf(RefreshCallback.HASH_KEY to foreignHash))

        assertFalse(hashFilter.equalCallback(callback))
    }

    private inner class TestHashFilter(hashProvider: HashFilterProvider) :
        HashFilterImpl<TestCppFilter>(hashProvider) {
        override val lastCppFilter: TestCppFilter?
            get() = testLastCpp
    }
}