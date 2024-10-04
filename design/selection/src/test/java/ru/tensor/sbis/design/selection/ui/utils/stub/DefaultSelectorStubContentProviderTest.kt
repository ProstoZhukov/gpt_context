package ru.tensor.sbis.design.selection.ui.utils.stub

import android.content.Context
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.testing.doReturn
import ru.tensor.sbis.common.testing.mockStatic
import ru.tensor.sbis.common.testing.on
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.design.selection.ui.contract.Data
import ru.tensor.sbis.design.selection.ui.contract.SelectorStrings

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class DefaultSelectorStubContentProviderTest {

    @Mock
    private lateinit var selectorStrings: SelectorStrings

    private val mockContext = mock<Context>()

    @InjectMocks
    private lateinit var provider: DefaultSelectorStubContentProvider

    @Test
    fun `When stub content requested, then strings should be called from SelectorStrings object`() {
        val mock = mockStatic<NetworkUtils>()
        mock.on<NetworkUtils, Boolean> { NetworkUtils.isConnected(mockContext) } doReturn true

        provider.provideStubViewContentFactory(Data(mock()))(mockContext)

        verify(selectorStrings).notFoundIcon
        verify(selectorStrings).notFoundTitle
        verify(selectorStrings).notFoundDescription
    }
}