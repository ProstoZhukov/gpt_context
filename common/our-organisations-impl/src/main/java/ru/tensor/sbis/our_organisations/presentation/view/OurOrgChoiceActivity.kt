package ru.tensor.sbis.our_organisations.presentation.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.base_components.BaseActivity
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.our_organisations.R
import ru.tensor.sbis.our_organisations.data.OurOrgItemType
import ru.tensor.sbis.our_organisations.feature.OurOrgFragmentFactory
import ru.tensor.sbis.our_organisations.feature.OurOrgUnnecessaryChoiceHostContract
import ru.tensor.sbis.our_organisations.feature.OurOrgUnnecessaryFragmentResult
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.presentation.contract.OurOrgWithUnnecessaryChoiceFragmentContract
import ru.tensor.sbis.our_organisations.presentation.contract.register

internal class OurOrgChoiceActivity : BaseActivity() {

    private val params: OurOrgParams by lazy {
        intent.getParcelableUniversally(OUR_ORG_ACTIVITY_PARAMS)!!
    }

    private val toolbar by lazy { findViewById<Toolbar>(R.id.our_org_toolbar) }
    private val applyBtn by lazy { findViewById<SbisRoundButton>(R.id.our_org_btn_apply) }
    private val resetBtn by lazy { findViewById<SbisButton>(R.id.our_org_btn_reset) }

    private val ourOrgFragmentFactory: OurOrgFragmentFactory =
        OurOrgWithUnnecessaryChoiceFragmentContract(OurOrgItemType.SIMPLE).register(
            this,
            ::handleOurOrgFragmentResult
        )

    companion object {
        const val OUR_ORG_ACTIVITY_RESULT = "OUR_ORG_ACTIVITY_RESULT"
        private const val OUR_ORG_ACTIVITY_PARAMS = "OUR_ORG_ACTIVITY_PARAMS"

        /**
         * Создает интент для показа активности.
         */
        fun createIntent(
            context: Context,
            params: OurOrgParams
        ) = Intent(context, OurOrgChoiceActivity::class.java).apply {
            putExtra(OUR_ORG_ACTIVITY_PARAMS, params)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.our_org_choice_activity)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val fragment = getCurrentFragment()

                    if (fragment is FragmentBackPress && fragment.onBackPressed()) {
                        return
                    }
                }
            }
        )

        toolbar.leftIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        toolbar.leftIcon.setTextColor(StyleColor.PRIMARY.getIconColor(this))

        resetBtn.isVisible = params.selectedOrganisations.isNotEmpty()
        resetBtn.setOnClickListener {
            it.isVisible = false
            (getCurrentFragment() as? OurOrgUnnecessaryChoiceHostContract.ActionHandler)
                ?.onReset()
            applyBtn.isVisible = true
        }

        applyBtn.isVisible = false
        applyBtn.setOnClickListener {
            (getCurrentFragment() as? OurOrgUnnecessaryChoiceHostContract.ActionHandler)
                ?.onApply()
        }

        if (getCurrentFragment() == null) {
            supportFragmentManager.commit {
                add(R.id.our_org_body, ourOrgFragmentFactory.create(params))
            }
        }
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.our_org_body)
    }

    private fun handleOurOrgFragmentResult(result: OurOrgUnnecessaryFragmentResult) {
        when (result) {
            is OurOrgUnnecessaryFragmentResult.OnReturnOrganisation -> {
                setResult(
                    Activity.RESULT_OK,
                    Intent().apply {
                        putExtra(OUR_ORG_ACTIVITY_RESULT, result.organisations.asArrayList())
                    }
                )
                currentFocus?.run(KeyboardUtils::hideKeyboard)
                finish()
            }

            is OurOrgUnnecessaryFragmentResult.OrganizationChanged -> {
                if (!params.isMultipleChoice) {
                    (getCurrentFragment() as? OurOrgUnnecessaryChoiceHostContract.ActionHandler)
                        ?.onApply()
                } else {
                    resetBtn.isVisible = result.organisations.isNotEmpty()
                    applyBtn.isVisible = params.selectedOrganisations.size != result.organisations.size ||
                        !result.organisations.map { it.originalId }.containsAll(params.selectedOrganisations)
                }
            }

            else -> Unit
        }
    }
}
