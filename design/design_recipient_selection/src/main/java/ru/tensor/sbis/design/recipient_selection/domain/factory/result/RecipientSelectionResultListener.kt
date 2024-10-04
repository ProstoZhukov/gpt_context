package ru.tensor.sbis.design.recipient_selection.domain.factory.result

import androidx.fragment.app.FragmentActivity
import io.reactivex.internal.disposables.DisposableContainer
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultDelegate
import ru.tensor.sbis.design.recipient_selection.RecipientSelectionPlugin
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientItem
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener.SelectionComponentResult
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionLogger
import javax.inject.Inject

/**
 * Реализация слушателя результата компонента выбора получателей.
 *
 * @property config настройка компонента выбора получателей.
 * @property interactor интерактор для загрузки результата.
 *
 * @author vv.chekurda
 */
internal class RecipientSelectionResultListener @Inject constructor(
    private val config: RecipientSelectionConfig,
    private val interactor: RecipientSelectionInteractor
) : SelectionResultListener<RecipientItem, FragmentActivity> {

    private val resultDelegate: RecipientSelectionResultDelegate
        get() = RecipientSelectionPlugin.singletonComponent.recipientSelectionResultDelegate

    override fun onComplete(
        activity: FragmentActivity,
        result: SelectionComponentResult<RecipientItem>,
        requestKey: String,
        disposable: DisposableContainer
    ) {
        checkFacesCasesError(result.items, requestKey)
        disposable += interactor.getRecipientSelectionData(
            result = result,
            unfoldDepartments = config.unfoldDepartments
        )
            .doFinally { if (config.closeOnComplete) activity.onBackPressedDispatcher.onBackPressed() }
            .subscribe { selectionData ->
                SelectionLogger.onSendSuccessResult(selectionData.uuids, requestKey)
                resultDelegate.onSuccess(
                    data = selectionData,
                    requestKey = requestKey
                )
            }
    }

    override fun onCancel(activity: FragmentActivity, requestKey: String) {
        SelectionLogger.onSendCancelResult(requestKey)
        resultDelegate.onCancel(requestKey)
        if (config.closeOnCancel) activity.onBackPressed()
    }
}

/**
 * Ловля ошибки https://online.sbis.ru/opendoc.html?guid=49fdd1c2-ae1f-497c-a8d7-d31ea0c24d02&client=3
 */
private fun checkFacesCasesError(result: List<RecipientItem>, requestKey: String) {
    if (requestKey == "PASSAGE_EXECUTORS_SELECTION_REQUEST_KEY" ||
        requestKey == "DOCUMENT_EXECUTORS_SELECTION_REQUEST_KEY") {
        if (result.isEmpty()) {
            val history = SelectionLogger.sessionHistory
            illegalState { "Пустые получатели в кейсах эдо, история операций:\n$history" }
        }
    }
}