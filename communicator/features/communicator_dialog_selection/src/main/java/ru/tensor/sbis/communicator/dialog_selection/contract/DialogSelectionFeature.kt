package ru.tensor.sbis.communicator.dialog_selection.contract

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.common.dialog_selection.DialogSelectionResult
import ru.tensor.sbis.mvp.multiselection.MultiSelectionResultManager

/**
 * Интерфейс фичи экрана выбора диалога или участников
 *
 * @author vv.chekurda
 */
interface DialogSelectionFeature {

    /**
     * Получить интент активности экрана выбора диалога или участников
     */
    fun getDialogSelectionActivityIntent(context: Context): Intent

    /**
     * Получить фрагмент экрана выбора диалога или участников
     */
    fun getDialogSelectionFragment(): Fragment

    /**
     * Получить менеджер результата выбора [DialogSelectionResult]
     * @see [ru.tensor.sbis.communicator.common.dialog_selection.DialogSelectionResultManager]
     */
    fun getDialogSelectionResultManager(context: Context): MultiSelectionResultManager<DialogSelectionResult>
}