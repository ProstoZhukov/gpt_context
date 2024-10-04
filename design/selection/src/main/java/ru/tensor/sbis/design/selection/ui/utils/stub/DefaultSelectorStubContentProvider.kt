package ru.tensor.sbis.design.selection.ui.utils.stub

import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.design.selection.ui.contract.*
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory
import ru.tensor.sbis.list.base.utils.stub.StubContentProvider

/**
 * Реализация [StubContentProvider] для создания содержимого заглушки с учётом специфичных сообщений, указанных в
 * [SelectorStrings]
 *
 * @author us.bessonov
 */
internal class DefaultSelectorStubContentProvider(
    private val selectorStrings: SelectorStrings
) : SelectorStubContentProvider<Any> {
    override fun provideStubViewContentFactory(result: SelectorStubInfo<Any>?): StubViewContentFactory = { context ->
        requireNotNull(result)
        if (!NetworkUtils.isConnected(context)) {
            StubViewCase.NO_CONNECTION.getContent()
        } else when (result) {
            is Data -> ImageStubContent(
                imageType = selectorStrings.notFoundIcon,
                messageRes = selectorStrings.notFoundTitle,
                details = selectorStrings.notFoundDescription.run(context::getString)
            )
            AllItemsSelected -> ImageStubContent(
                imageType = selectorStrings.allSelectedIcon,
                messageRes = selectorStrings.allSelectedTitle,
                details = selectorStrings.allSelectedDescription?.run(context::getString)
            )
            NoData -> StubViewCase.SBIS_ERROR.getContent()
        }
    }
}
