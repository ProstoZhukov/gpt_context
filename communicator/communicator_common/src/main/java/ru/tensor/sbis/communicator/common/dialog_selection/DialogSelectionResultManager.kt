package ru.tensor.sbis.communicator.common.dialog_selection

import io.reactivex.Observable
import ru.tensor.sbis.mvp.multiselection.MultiSelectionResultManager

/**
 * Реализация менеджера результата при выборе диалога/участников.
 * Подписка на [mSelectionObservable] возвращает список участников или uuid диалога
 * @see [DialogSelectionResult]
 *
 * @author vv.chekurda
 */
internal class DialogSelectionResultManager : MultiSelectionResultManager<DialogSelectionResult>() {

    /** @SelfDocumented **/
    override fun getSelectionDoneObservable(): Observable<DialogSelectionResult> =
        super.getSelectionDoneObservable().also {
            clearSelectionResult()
        }

    /** @SelfDocumented **/
    override fun clearSelectionResult() {
        mSelectionSubject.onNext(ClearDialogSelectionResult)
    }
}