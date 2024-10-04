package ru.tensor.sbis.design.universal_selection.contract

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.selection.universal.UniversalSelectionConfig
import ru.tensor.sbis.communication_decl.selection.universal.manager.UniversalSelectionResultDelegate
import ru.tensor.sbis.communication_decl.selection.universal.manager.UniversalSelectionResultManager
import ru.tensor.sbis.design.universal_selection.domain.result_manager.UniversalSelectionResultManagerImpl
import ru.tensor.sbis.design.universal_selection.ui.UniversalSelectionActivity
import ru.tensor.sbis.design.universal_selection.ui.UniversalSelectionFragmentFactory

/**
 * Реализация фичи модуля компонента универсального выбора [UniversalSelectionFeature].
 *
 * @author vv.chekurda
 */
internal object UniversalSelectionFeatureFacade : UniversalSelectionFeature {

    private val resultManager by lazy { UniversalSelectionResultManagerImpl() }

    override fun getUniversalSelectionFragment(config: UniversalSelectionConfig): Fragment =
        UniversalSelectionFragmentFactory.createFragment(config)

    override fun getUniversalSelectionIntent(context: Context, config: UniversalSelectionConfig): Intent =
        UniversalSelectionActivity.newIntent(context, config)

    override fun getUniversalSelectionResultDelegate(): UniversalSelectionResultDelegate =
        resultManager

    override fun getUniversalSelectionResultManager(): UniversalSelectionResultManager =
        resultManager
}