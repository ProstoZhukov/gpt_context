package ru.tensor.sbis.appdesign.selection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.selection.data.DEMO_SELECTION_ERROR_INFO
import ru.tensor.sbis.appdesign.selection.datasource.*
import ru.tensor.sbis.appdesign.selection.factories.DemoRecipientMultiListDependenciesFactory
import ru.tensor.sbis.appdesign.selection.listeners.DemoCancelListener
import ru.tensor.sbis.appdesign.selection.listeners.DemoRecipientMultiCompleteListener
import ru.tensor.sbis.appdesign.selection.listeners.getDemoSelectorItemListeners
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectorOptions
import ru.tensor.sbis.design.selection.ui.contract.SelectorDoneButtonVisibilityMode
import ru.tensor.sbis.design.selection.ui.factories.createMultiRecipientSelector

const val IS_COMMON_RECIPIENT_API = "IS_COMMON_RECIPIENT_API"

/**
 * Демо экран для множественного выбора адресатов
 *
 * @author ma.kolpakov
 */
class RecipientMultiSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)

        supportFragmentManager.beginTransaction()
            .add(
                R.id.container,
                if (intent.getBooleanExtra(IS_COMMON_RECIPIENT_API, false))
                    createRecipientSelectorCommonApiFragment()
                else
                    createRecipientSelectorFragment()
            ).commit()
    }

    private fun createRecipientSelectorFragment(): Fragment =
        createMultiRecipientSelector(
            DemoRecipientMultiListDependenciesFactory(),
            DemoRecipientMultiCompleteListener(),
            DemoCancelListener(),
            null,
            DEMO_SELECTION_ERROR_INFO,
            DemoActivityStatusConductorProvider(),
            doneButtonVisibilityMode = SelectorDoneButtonVisibilityMode.AUTO_HIDDEN
        )

    private fun createRecipientSelectorCommonApiFragment(): Fragment {
        val options = MultiSelectorOptions(
            successListener = DemoRecipientMultiCompleteListener(),
            cancelListener = DemoCancelListener(),
            noResultsTitle = R.string.selection_all_recipients_selected_title,
            noResultsDescription = R.string.selection_all_recipients_selected_description,
            itemListeners = getDemoSelectorItemListeners(),
        )

        return createMultiRecipientSelector(
            DemoRecipientDataProvider(DemoRecipientController, DemoRecipientDataMapper()),
            DemoSerializableRecipientMultiSelectorLoader(DemoRecipientController, DemoRecipientDataMapper()),
            DemoSerializableRecipientResultHelper(),
            options,
            DemoActivityStatusConductorProvider()
        )
    }
}