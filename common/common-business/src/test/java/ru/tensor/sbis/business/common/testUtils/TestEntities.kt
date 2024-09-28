package ru.tensor.sbis.business.common.testUtils

import androidx.databinding.BaseObservable
import ru.tensor.sbis.business.common.data.ViewModelProvider
import ru.tensor.sbis.business.common.domain.result.PayloadPagedListResult
import ru.tensor.sbis.business.common.ui.base.state_vm.DisplayedErrors

internal class TestCppFilter() {
    fun getHash(): Int = hashCode()
    fun getHashString(): String = "${getHash()}"
}

internal class TestData : ViewModelProvider {
    override fun toBaseObservableVM() = BaseObservable()
}

internal object TestDisplayedErrors : DisplayedErrors

internal class TestListResult(
    list: List<TestData>,
    extra: TestData,
    hasMore: Boolean
) : PayloadPagedListResult<TestData, TestData>(list, extra, hasMore) {

    companion object {
        fun create(
            size: Int = 0,
            extra: TestData = TestData(),
            hasMore: Boolean = true
        ): TestListResult {
            val list = mutableListOf<TestData>()
            repeat(size) {
                list.add(TestData())
            }
            return TestListResult(list, extra, hasMore)
        }
    }
}

internal class TestCppListResult(
    val list: List<TestData> = emptyList(),
    val extra: TestData = TestData(),
    val hasMore: Boolean = true
)


