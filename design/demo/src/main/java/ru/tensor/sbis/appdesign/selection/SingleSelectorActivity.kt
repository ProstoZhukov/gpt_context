package ru.tensor.sbis.appdesign.selection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.selection.data.DEMO_SELECTION_ERROR_INFO
import ru.tensor.sbis.appdesign.selection.factories.DemoSingleListDependenciesFactory
import ru.tensor.sbis.appdesign.selection.listeners.DemoCancelListener
import ru.tensor.sbis.appdesign.selection.listeners.DemoSingleCompleteListener
import ru.tensor.sbis.design.selection.ui.factories.createSingleRegionSelector

/**
 * @author us.bessonov
 */
class SingleSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.container, createSingleRegionSelector(
                        DemoSingleListDependenciesFactory(),
                        DemoSingleCompleteListener(),
                        DemoCancelListener(),
                        DEMO_SELECTION_ERROR_INFO
                    )
                ).commit()
        }
    }
}

