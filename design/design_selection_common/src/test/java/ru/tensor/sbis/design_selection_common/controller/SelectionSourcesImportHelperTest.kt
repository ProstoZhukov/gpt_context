package ru.tensor.sbis.design_selection_common.controller

import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.communication_decl.selection.SelectionUseCase
import ru.tensor.sbis.communication_decl.selection.sources.SelectionExternalSource
import ru.tensor.sbis.recipients.generated.RecipientViewModel

/**
 * Тесты вспомогательной реализации для импорта результата компонента выбора [SelectionSourcesImportHelper].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionSourcesImportHelperTest {

    @Mock
    private lateinit var useCase: SelectionUseCase

    @Test
    fun `When call import selected recipients, then call import selection result on all sources`() {
        val selectedItems = listOf<RecipientViewModel>(mock(), mock())
        val source1 = mock<SelectionExternalSource>()
        val source2 = mock<SelectionExternalSource>()
        val externalSources = listOf(source1, source2)
        val importHelper = SelectionSourcesImportHelper(useCase, externalSources)

        importHelper.importSelectedRecipients(selectedItems)

        verify(source1).importSelectionResult(useCase, selectedItems)
        verify(source2).importSelectionResult(useCase, selectedItems)
    }
}