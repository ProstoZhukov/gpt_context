package ru.tensor.sbis.appdesign.selection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.selection.data.DEMO_SELECTION_ERROR_INFO
import ru.tensor.sbis.appdesign.selection.datasource.DemoActivityStatusConductorProvider
import ru.tensor.sbis.appdesign.selection.factories.DemoRecipientSingleListDependenciesFactory
import ru.tensor.sbis.appdesign.selection.listeners.DemoCancelListener
import ru.tensor.sbis.appdesign.selection.listeners.DemoNewGroupClickListener
import ru.tensor.sbis.appdesign.selection.listeners.DemoRecipientSingleCompleteListener
import ru.tensor.sbis.design.selection.ui.factories.createSingleRecipientSelector

/**
 * Демо экран для одиночного выбора адресатов
 *
 * @author ma.kolpakov
 */
class RecipientSingleSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.container, createSingleRecipientSelector(
                        DemoRecipientSingleListDependenciesFactory(),
                        DemoRecipientSingleCompleteListener(),
                        DemoCancelListener(),
                        DEMO_SELECTION_ERROR_INFO,
                        DemoNewGroupClickListener(),
                        DemoActivityStatusConductorProvider()
                    )
                ).commit()
        }
    }
}