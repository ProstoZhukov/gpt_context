package ru.tensor.sbis.design.universal_selection.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import ru.tensor.sbis.base_components.AdjustResizeActivity
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.communication_decl.selection.universal.UniversalSelectionConfig
import ru.tensor.sbis.design.universal_selection.R
import ru.tensor.sbis.design.universal_selection.contract.UniversalSelectionFeatureFacade
import ru.tensor.sbis.design.utils.obtainThemeAttrsAndMerge
import ru.tensor.sbis.design.R as RDesign

/**
 * Активити компонента универсального выбора.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectionActivity : AdjustResizeActivity() {

    companion object {

        /**
         * Создать новый intent для открытия активити универсального выбора.
         *
         * @param config конфигурация универсального выбора.
         */
        fun newIntent(context: Context, config: UniversalSelectionConfig): Intent =
            Intent(context, UniversalSelectionActivity::class.java).apply {
                putExtra(EXTRA_SELECTION_CONFIG_KEY, config)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        obtainThemeAttrsAndMerge(R.attr.universalSelectionActivityTheme, R.style.UniversalSelectionActivityTheme)
        super.onCreate(savedInstanceState)
        overridePendingTransition(RDesign.anim.right_in, RDesign.anim.nothing)
        setContentView(R.layout.design_universal_selection_activity)

        if (savedInstanceState == null) {
            addSelectionFragment()
        }
    }

    private fun addSelectionFragment() {
        val config = intent.extras!!.getSerializable(EXTRA_SELECTION_CONFIG_KEY) as UniversalSelectionConfig
        val fragment = UniversalSelectionFragmentFactory.createFragment(config)
        supportFragmentManager.beginTransaction()
            .replace(contentViewId, fragment, fragment::class.java.simpleName)
            .commit()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(contentViewId)
        if (!(fragment is FragmentBackPress && fragment.onBackPressed())) {
            super.onBackPressed()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(RDesign.anim.nothing, RDesign.anim.right_out)
    }

    override fun onViewGoneBySwipe() {
        UniversalSelectionFeatureFacade.getUniversalSelectionResultDelegate().onCancel()
        super.onViewGoneBySwipe()
    }

    override fun getContentViewId(): Int = R.id.root_container

    override fun swipeBackEnabled(): Boolean = true
}

private const val EXTRA_SELECTION_CONFIG_KEY = "EXTRA_SELECTION_CONFIG"