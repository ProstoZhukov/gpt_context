package ru.tensor.sbis.list.base.utils.stub

import android.content.Context
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory

class DefaultStubContent : StubViewContentFactory {

    override fun invoke(context: Context) =
        StubViewCase.NO_SEARCH_RESULTS.getContent()
}