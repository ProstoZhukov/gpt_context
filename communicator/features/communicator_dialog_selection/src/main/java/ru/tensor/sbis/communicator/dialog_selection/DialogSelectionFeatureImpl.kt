package ru.tensor.sbis.communicator.dialog_selection

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.common.dialog_selection.DialogSelectionResult
import ru.tensor.sbis.communicator.dialog_selection.contract.DialogSelectionFeature
import ru.tensor.sbis.communicator.dialog_selection.di.getDialogSelectionComponent
import ru.tensor.sbis.communicator.dialog_selection.presentation.DialogSelectionActivity
import ru.tensor.sbis.communicator.dialog_selection.presentation.createDialogSelectionFragment
import ru.tensor.sbis.mvp.multiselection.MultiSelectionResultManager

/**
 * Реализация фичи экрана выбора диалога/участников
 *
 * @author vv.chekurda
 */
object DialogSelectionFeatureImpl : DialogSelectionFeature {

    override fun getDialogSelectionActivityIntent(context: Context): Intent =
        Intent(context, DialogSelectionActivity::class.java)

    override fun getDialogSelectionFragment(): Fragment =
        createDialogSelectionFragment()

    override fun getDialogSelectionResultManager(context: Context): MultiSelectionResultManager<DialogSelectionResult> =
        context.getDialogSelectionComponent().resultManager
}