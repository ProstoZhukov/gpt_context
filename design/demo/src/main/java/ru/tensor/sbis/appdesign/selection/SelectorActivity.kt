package ru.tensor.sbis.appdesign.selection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.selection.data.DEMO_SELECTION_ERROR_INFO
import ru.tensor.sbis.appdesign.selection.factories.DemoItemHandleStrategy
import ru.tensor.sbis.appdesign.selection.factories.DemoListDependenciesFactory
import ru.tensor.sbis.appdesign.selection.listeners.DemoCancelListener
import ru.tensor.sbis.appdesign.selection.listeners.DemoCompleteListener
import ru.tensor.sbis.design.selection.ui.factories.createMultiHierarchicalRegionSelector
import ru.tensor.sbis.design.selection.ui.utils.CounterFormat

/**
 * Демо экран компонента выбора.
 *
 * @author ma.kolpakov
 */
class SelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.container, createMultiHierarchicalRegionSelector(
                        DemoListDependenciesFactory(),
                        DemoCompleteListener(),
                        DemoCancelListener(),
                        DEMO_SELECTION_ERROR_INFO,
                        DemoItemHandleStrategy(),
                        CounterFormat.THOUSANDS_DECIMAL_FORMAT
                    )
                ).commit()
        }
    }
}