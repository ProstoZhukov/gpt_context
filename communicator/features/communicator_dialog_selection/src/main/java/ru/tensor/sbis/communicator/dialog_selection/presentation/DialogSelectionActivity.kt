package ru.tensor.sbis.communicator.dialog_selection.presentation

import android.os.Bundle
import ru.tensor.sbis.base_components.AdjustResizeActivity
import ru.tensor.sbis.communicator.common.dialog_selection.CancelDialogSelectionResult
import ru.tensor.sbis.communicator.dialog_selection.R
import ru.tensor.sbis.communicator.dialog_selection.di.getDialogSelectionComponent
import ru.tensor.sbis.base_components.R as RBaseComponents
import ru.tensor.sbis.design.R as RDesign

/**
 * Активность выбора диалога/участников
 *
 * @author vv.chekurda
 */
internal class DialogSelectionActivity : AdjustResizeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(RBaseComponents.layout.base_components_activity_fragment_container)
        if (savedInstanceState == null) {
            placeSharingDialogSelectionFragment()
        }
    }

    private fun placeSharingDialogSelectionFragment() {
        val fragment = createDialogSelectionFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(contentViewId, fragment, fragment::class.java.simpleName)
            .commit()
    }

    override fun getContentViewId(): Int = RBaseComponents.id.base_components_content_container

    override fun swipeBackEnabled(): Boolean = true

    override fun onViewGoneBySwipe() {
        super.onViewGoneBySwipe()
        getDialogSelectionComponent().resultManager.putNewData(CancelDialogSelectionResult)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        getDialogSelectionComponent().resultManager.putNewData(CancelDialogSelectionResult)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(RDesign.anim.nothing, RDesign.anim.right_out)
    }
}