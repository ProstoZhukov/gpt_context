package ru.tensor.sbis.design_selection_common.controller

import org.json.JSONObject
import ru.tensor.sbis.communication_decl.selection.SelectionUseCase
import ru.tensor.sbis.design_selection_common.SelectionIntentJsonFactory

/**
 * Реализация фабрики для создания intent_json для конфигурации источников на контроллере компонента выбора.
 *
 * @property useCase сценарий компонента выбора.
 *
 * @author vv.chekurda
 */
internal class DefaultSelectionIntentJsonFactory(
    private val useCase: SelectionUseCase
) : SelectionIntentJsonFactory {

    override fun createIntentJson(): String =
        JSONObject().apply {
            put(SELECTION_USE_CASE_KEY, useCase.name)
            useCase.args.forEach { put(it.key, it.value) }
        }.toString()
}

private const val SELECTION_USE_CASE_KEY = "use_case"