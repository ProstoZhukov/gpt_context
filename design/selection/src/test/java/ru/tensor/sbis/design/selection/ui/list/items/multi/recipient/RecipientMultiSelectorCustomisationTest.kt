package ru.tensor.sbis.design.selection.ui.list.items.multi.recipient

import org.mockito.kotlin.mock
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.*
import ru.tensor.sbis.design.selection.ui.model.share.dialog.DialogSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.share.TitleItemModel

@RunWith(JUnitParamsRunner::class)
class RecipientMultiSelectorCustomisationTest {

    private class TestSelectorItemModel : SelectorItemModel {
        override val title: String = ""
        override val subtitle: String? = null
        override val id: String = ""
        override var meta: SelectorItemMeta = SelectorItemMeta()
    }

    private val customization = RecipientMultiSelectorCustomisation()

    @Test(expected = IllegalStateException::class)
    fun `Given unknown type, when getViewHolderType() called, then throw exception`() {
        customization.getViewHolderType(TestSelectorItemModel())
    }

    @Test
    @Parameters(method = "modelsParams")
    fun `getViewHolderType test`(model: SelectorItemModel, clasz: Class<*>) =
        assertEquals(clasz, customization.getViewHolderType(model))

    @Suppress("unused")
    private fun modelsParams() = params {
        add(mock<PersonSelectorItemModel>(), PersonSelectorItemModel::class.java)
        add(mock<GroupSelectorItemModel>(), GroupSelectorItemModel::class.java)
        add(mock<DepartmentSelectorItemModel>(), DepartmentSelectorItemModel::class.java)
        add(mock<TitleItemModel>(), TitleItemModel::class.java)
        add(mock<DialogSelectorItemModel>(), DialogSelectorItemModel::class.java)
    }

}