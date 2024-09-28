package ru.tensor.sbis.design.universal_selection.domain.factory

import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionMode
import ru.tensor.sbis.communication_decl.selection.universal.data.BaseUniversalItemId
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalPreselectedData
import ru.tensor.sbis.communication_decl.selection.universal.manager.UniversalSelectionResult
import ru.tensor.sbis.design.universal_selection.contract.UniversalSelectionFeatureFacade
import ru.tensor.sbis.design_selection_common.PreselectedDataProvider
import ru.tensor.sbis.design_selection_common.controller.PreselectedData
import javax.inject.Inject

/**
 * Реализация поставщика предвыбранных данных для компонента универсального выбора.
 *
 * @property universalSelectionManager менеджер для работы с результатами компонента универсального выбора.
 *
 * @author vv.chekurda
 */
internal class UniversalPreselectionProvider @Inject constructor() : PreselectedDataProvider {

    private val universalSelectionManager = UniversalSelectionFeatureFacade.getUniversalSelectionResultManager()

    private val lastResult: UniversalSelectionResult
        get() = universalSelectionManager.selectionResult

    private val preselectedData: UniversalPreselectedData?
        get() = universalSelectionManager.preselectedData

    override fun getPreselectedData(config: SelectionConfig): PreselectedData =
        when {
            // Для одиночного выбора нет предвыбранных элементов.
            config.selectionMode == SelectionMode.SINGLE -> {
                PreselectedData()
            }
            // Есть последний рабочий результат - используем его для повторного открытия в том же состоянии.
            !lastResult.isCleared && lastResult.requestKey == config.requestKey -> {
                PreselectedData(ids = lastResult.data.items.map { BaseUniversalItemId(it.id) })
            }
            // Компонент находится в чистом состоянии, проверяем данные для предустановки.
            else -> {
                PreselectedData(
                    ids = preselectedData?.ids?.also {
                        universalSelectionManager.preselect(data = null)
                    }.orEmpty()
                )
            }
        }
}