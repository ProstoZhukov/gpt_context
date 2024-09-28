package ru.tensor.sbis.business.common.ui.utils

import androidx.databinding.BaseObservable
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.business.common.data.FolderStructureItemVmProvider
import ru.tensor.sbis.business.common.ui.viewmodel.BreadCrumbsVm

internal class ListDataUtilsKtTest {

    private val flatResponse = arrayListOf(
        TestItem(id = "7", isFolder = true),
        TestItem(id = "19", isFolder = false),
        TestItem(id = "30", isFolder = true),
        TestItem(id = "35", isFolder = false),
        TestItem(id = "45", isFolder = false),
        TestItem(id = "53", isFolder = true),
    )

    private val hierarchyResponse = arrayListOf(
        TestItem(id = "17", parentId = null, isFolder = true, name = "Содержание компании"),
        TestItem(id = "18", parentId = "17", isFolder = false, name = "Аренда помещений"),
        TestItem(id = "33", parentId = null, isFolder = true, name = "Производственные расходы"),
        TestItem(
            id = "37",
            parentId = "33",
            isFolder = false,
            name = "Аренда оборудования, основных фондов"
        ),
        TestItem(id = "53", parentId = null, isFolder = true, name = "Транспорт"),
        TestItem(
            id = "56",
            parentId = "53",
            isFolder = false,
            name = "Аренда транспорта у сотрудников"
        ),
        TestItem(
            id = "57",
            parentId = "53",
            isFolder = false,
            name = "Аренда транспорта у организаций"
        ),
        TestItem(id = "41637", parentId = "53", isFolder = true, name = "Тест Аренды"),
        TestItem(
            id = "41638",
            parentId = "41637",
            isFolder = false,
            name = "Тестирование у сотрудников"
        ),
        TestItem(
            id = "41639",
            parentId = "41637",
            isFolder = false,
            name = "Тестирование аренда у сотрудников"
        ),
        TestItem(id = "41527", parentId = null, isFolder = true, name = "Аренда"),
        TestItem(id = "41704", parentId = null, isFolder = true, name = "Аренда 1"),
        TestItem(id = "41705", parentId = null, isFolder = true, name = "Аренда 2")
    )

    @Test
    fun `Form correct list for flat result`() {
        val vmList = flatResponse.toVmListWithBreadCrumbs()

        val itemVm = vmList.filter { it !is BreadCrumbsVm }
        val breadCrumbsVm = vmList.filterIsInstance<BreadCrumbsVm>()

        assertEquals(3, itemVm.size)
        assertEquals(3, breadCrumbsVm.size)
    }

    @Test
    fun `Form correct list for hierarchy result`() {
        val vmList = hierarchyResponse.toVmListWithBreadCrumbs()
        val breadCrumbsVm = vmList.filterIsInstance<BreadCrumbsVm>()

        assertEquals(13, vmList.size)
        assertEquals(7, breadCrumbsVm.size)

        breadCrumbsVm[0].assertBreadcrumb("17", 1)
        breadCrumbsVm[1].assertBreadcrumb("33", 1)
        breadCrumbsVm[2].assertBreadcrumb("53", 1)
        breadCrumbsVm[3].assertBreadcrumb("41637", 2)
        breadCrumbsVm[4].assertBreadcrumb("41527", 1)
        breadCrumbsVm[5].assertBreadcrumb("41704", 1)
        breadCrumbsVm[6].assertBreadcrumb("41705", 1)
    }

    private fun BreadCrumbsVm.assertBreadcrumb(
        expectedId: String,
        expectedDeep: Int
    ) {
        assertEquals(expectedId, parentId)
        assertEquals(expectedDeep, items.size)
    }

    private data class TestItem(
        override var id: String,
        override val parentId: String? = null,
        override var isFolder: Boolean,
        override var isEmptyFolder: Boolean = false,
        override var name: String = "",
        override var highlightedNameRanges: List<IntRange> = emptyList(),
    ) : FolderStructureItemVmProvider {
        override fun toBaseObservableVM(): BaseObservable = BaseObservable()
    }
}