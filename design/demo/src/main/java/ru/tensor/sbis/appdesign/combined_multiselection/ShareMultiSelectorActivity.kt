package ru.tensor.sbis.appdesign.combined_multiselection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.combined_multiselection.factories.DemoCombinedRecipientFactory
import ru.tensor.sbis.appdesign.combined_multiselection.listeners.DemoMultiSelectionCompleteListener
import ru.tensor.sbis.appdesign.selection.data.DEMO_SELECTION_ERROR_INFO
import ru.tensor.sbis.appdesign.selection.listeners.DemoCancelListener
import ru.tensor.sbis.design.selection.ui.factories.createShareMultiSelector

/**
 * @author ma.kolpakov
 */
class ShareMultiSelectorActivity : AppCompatActivity(R.layout.activity_combined_multiselector) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayFragment()
    }

    private fun displayFragment() {

        val fragment = createShareMultiSelector(
            listDependenciesFactory = DemoCombinedRecipientFactory(),
            completeListener = DemoMultiSelectionCompleteListener(),
            cancelListener = DemoCancelListener(),
            selectorStrings = DEMO_SELECTION_ERROR_INFO,
        )
        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragment)
            .commit()
    }
}
