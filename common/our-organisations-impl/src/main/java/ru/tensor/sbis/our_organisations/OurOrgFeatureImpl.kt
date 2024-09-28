package ru.tensor.sbis.our_organisations

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import ru.tensor.sbis.android_ext_decl.getParcelableArrayListUniversally
import ru.tensor.sbis.our_organisations.feature.OurOrgFragmentFactory
import ru.tensor.sbis.our_organisations.feature.OurOrgNecessaryFragmentResult
import ru.tensor.sbis.our_organisations.feature.OurOrgSelectionWindowFactory
import ru.tensor.sbis.our_organisations.feature.OurOrgUnnecessaryFragmentResult
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.feature.data.OurOrgDataServiceWrapper
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.feature.di.OurOrgFeature
import ru.tensor.sbis.our_organisations.presentation.OurOrgListModule
import ru.tensor.sbis.our_organisations.presentation.view.OurOrgChoiceActivity

/**
 * Реализация [OurOrgFeature].
 *
 * @author mv.ilin
 */
internal class OurOrgFeatureImpl(
    private val ourOrgListModule: OurOrgListModule,
    private val ourOrgDataServiceWrapper: OurOrgDataServiceWrapper
) : OurOrgFeature {

    override fun getOurOrgChoiceActivityContract() =
        object : ActivityResultContract<OurOrgParams, List<Organisation>>() {
            override fun createIntent(
                context: Context,
                input: OurOrgParams
            ): Intent {
                return OurOrgChoiceActivity.createIntent(context, input)
            }

            override fun parseResult(resultCode: Int, intent: Intent?): List<Organisation> {
                if (resultCode == Activity.RESULT_OK && intent?.extras != null) {
                    return intent.getParcelableArrayListUniversally(
                        OurOrgChoiceActivity.OUR_ORG_ACTIVITY_RESULT,
                        Organisation::class.java
                    ) ?: emptyList()
                }
                return emptyList()
            }
        }

    override fun getOurOrgDataServiceWrapper(): OurOrgDataServiceWrapper {
        return ourOrgDataServiceWrapper
    }

    override fun ourOrgWithNecessaryChoiceFragmentFactory(
        fragment: Fragment,
        onResult: (OurOrgNecessaryFragmentResult) -> Unit
    ): OurOrgFragmentFactory {
        return ourOrgListModule.ourOrgWithNecessaryChoiceFragmentFactory(fragment, onResult)
    }

    override fun ourOrgWithNecessaryChoiceSelectionWindowFactory(
        fragment: Fragment,
        onResult: (OurOrgNecessaryFragmentResult) -> Unit
    ): OurOrgSelectionWindowFactory {
        return ourOrgListModule.ourOrgWithNecessaryChoiceSelectionWindowFactory(fragment, onResult)
    }

    override fun ourOrgWithUnnecessaryChoiceFragmentFactory(
        fragment: Fragment,
        onResult: (OurOrgUnnecessaryFragmentResult) -> Unit
    ): OurOrgFragmentFactory {
        return ourOrgListModule.ourOrgWithUnnecessaryChoiceFragmentFactory(fragment, onResult)
    }

    override fun ourOrgWithUnnecessaryChoiceSelectionWindowFactory(
        fragment: Fragment,
        onResult: (OurOrgUnnecessaryFragmentResult) -> Unit
    ): OurOrgSelectionWindowFactory {
        return ourOrgListModule.ourOrgWithUnnecessaryChoiceSelectionWindowFactory(fragment, onResult)
    }
}
