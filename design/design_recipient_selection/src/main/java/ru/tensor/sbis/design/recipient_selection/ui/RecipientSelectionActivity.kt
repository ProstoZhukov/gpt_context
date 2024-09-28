package ru.tensor.sbis.design.recipient_selection.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import ru.tensor.sbis.base_components.AdjustResizeActivity
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultDelegate
import ru.tensor.sbis.design.recipient_selection.R
import ru.tensor.sbis.design.recipient_selection.ui.di.singleton.RecipientSelectionComponentProvider
import ru.tensor.sbis.design.utils.obtainThemeAttrsAndMerge
import ru.tensor.sbis.design.R as RDesign

/**
 * Активити компонента выбора получателей.
 *
 * @author vv.chekurda
 */
internal class RecipientSelectionActivity : AdjustResizeActivity() {

    companion object {

        /**
         * Создать новый intent для открытия активити выбора получателей.
         *
         * @param config конфигурация выбора получателей.
         */
        fun newIntent(context: Context, config: RecipientSelectionConfig): Intent =
            Intent(context, RecipientSelectionActivity::class.java).apply {
                putExtra(EXTRA_SELECTION_CONFIG_KEY, config)
            }
    }

    private var resultDelegate: RecipientSelectionResultDelegate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        obtainThemeAttrsAndMerge(R.attr.recipientSelectionActivityTheme, R.style.RecipientSelectionActivityTheme)
        super.onCreate(savedInstanceState)
        // TODO https://online.sbis.ru/opendoc.html?guid=5d6aa3ce-3498-4362-9eff-b28624eeb6b4&client=3
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        overridePendingTransition(RDesign.anim.right_in, RDesign.anim.nothing)
        setContentView(R.layout.design_recipient_selection_activity)

        initSelectionManager()
        if (savedInstanceState == null) {
            addSelectionFragment()
        }
    }

    private fun initSelectionManager() {
        resultDelegate = RecipientSelectionComponentProvider
                .getRecipientSelectionSingletonComponent(this)
                .recipientSelectionResultDelegate
    }

    private fun addSelectionFragment() {
        val config = intent.extras!!.getSerializable(EXTRA_SELECTION_CONFIG_KEY) as RecipientSelectionConfig
        val fragment = RecipientSelectionFragmentFactory.createRecipientSelectionFragment(config)
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
        resultDelegate?.onCancel()
        super.onViewGoneBySwipe()
    }

    override fun onDestroy() {
        super.onDestroy()
        resultDelegate = null
    }

    override fun getContentViewId(): Int = R.id.root_container

    override fun swipeBackEnabled(): Boolean = true
}

private const val EXTRA_SELECTION_CONFIG_KEY = "EXTRA_SELECTION_CONFIG"