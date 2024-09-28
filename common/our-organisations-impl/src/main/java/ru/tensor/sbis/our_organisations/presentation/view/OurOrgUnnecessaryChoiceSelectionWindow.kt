package ru.tensor.sbis.our_organisations.presentation.view

import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import ru.tensor.sbis.android_ext_decl.DefaultParcelable
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.android_ext_decl.readParcelable
import ru.tensor.sbis.base_components.fragment.selection.SelectionWindowContent
import ru.tensor.sbis.base_components.fragment.selection.shadow.ShadowVisibilityDispatcher
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.dialogs.content.utils.anyContainerAs
import ru.tensor.sbis.our_organisations.R
import ru.tensor.sbis.our_organisations.data.OurOrgItemType
import ru.tensor.sbis.our_organisations.feature.OurOrgUnnecessaryChoiceHostContract
import ru.tensor.sbis.our_organisations.feature.OurOrgUnnecessaryFragmentResult
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.presentation.contract.register
import ru.tensor.sbis.our_organisations.presentation.list.OurOrgListContract
import ru.tensor.sbis.our_organisations.presentation.view.BaseOurOrgChoiceFragment.Companion.OUR_ORG_ITEM_TYPE_KEY
import ru.tensor.sbis.our_organisations.presentation.view.BaseOurOrgChoiceFragment.Companion.OUR_ORG_PARAMS_KEY
import ru.tensor.sbis.base_components.R as RBaseComponents

/**
 * Окно выбора с необязательным выбором из списка организаций.
 *
 * @author mv.ilin
 */
internal class OurOrgUnnecessaryChoiceSelectionWindow :
    SelectionWindowContent(),
    BaseOurOrgChoiceFragment {

    companion object {
        private const val FRAGMENT_RESULT_REQUEST_KEY = "FRAGMENT_RESULT_REQUEST_KEY"
        private const val RETURN_ORGANISATION_KEY = "RETURN_ORGANISATION_KEY"
        private const val RESULT_KEY = "RESULT_KEY"

        fun extractResult(bundle: Bundle): Organisation? {
            return bundle.getParcelableUniversally(RETURN_ORGANISATION_KEY)
        }

        fun extractFragmentResult(data: Bundle) =
            data.getParcelableUniversally<OurOrgUnnecessaryFragmentResult>(RESULT_KEY)!!
    }

    class Creator(
        private val requestKey: String,
        private val ourOrgItemType: OurOrgItemType,
        private val ourOrgParams: OurOrgParams
    ) : ContentCreatorParcelable, DefaultParcelable {
        companion object {
            @JvmField
            val CREATOR = DefaultParcelable.generateCreator(OurOrgUnnecessaryChoiceSelectionWindow::Creator)
        }

        @Suppress("DEPRECATION")
        constructor(parcel: Parcel) : this(
            requestKey = parcel.readString()!!,
            ourOrgItemType = parcel.readSerializable() as OurOrgItemType,
            ourOrgParams = parcel.readParcelable<OurOrgParams>()
        )

        override fun writeToParcel(parcel: Parcel, parcelableFlags: Int) {
            parcel.writeString(requestKey)
            parcel.writeSerializable(ourOrgItemType)
            parcel.writeParcelable(ourOrgParams, parcelableFlags)
        }

        override fun createFragment(): Fragment = OurOrgUnnecessaryChoiceSelectionWindow().withArgs {
            putString(FRAGMENT_RESULT_REQUEST_KEY, requestKey)
            putSerializable(OUR_ORG_ITEM_TYPE_KEY, ourOrgItemType)
            putParcelable(OUR_ORG_PARAMS_KEY, ourOrgParams)
        }
    }

    private val requestKey: String by lazy(LazyThreadSafetyMode.NONE) {
        requireArguments().getString(FRAGMENT_RESULT_REQUEST_KEY)!!
    }

    private val params: OurOrgParams by lazy {
        requireArguments().getParcelableUniversally(OUR_ORG_PARAMS_KEY)!!
    }

    override val baseFragmentManager: FragmentManager by lazy { childFragmentManager }
    override val containerViewId: Int = RBaseComponents.id.base_components_content_container
    override val discardResultAfterClick: Boolean = false
    override val ourOrgListContract: OurOrgListContract.Factory =
        OurOrgListContract().register(this, ::handleOurOrgListResult)

    override fun inflateContentView(inflater: LayoutInflater, container: ViewGroup) {
        createOurOrgListFragment()
    }

    override fun getShadowVisibilityDispatcher(): ShadowVisibilityDispatcher? = null

    override fun getContentViewId(): Int = R.id.organisations

    override fun onShowContent() {
        anyContainerAs<Container.Showable>()?.showContent()
        parentFragment?.setFragmentResult(
            requestKey,
            bundleOf(RESULT_KEY to OurOrgUnnecessaryFragmentResult.OnShowContent)
        )
    }

    override fun onApplyClick() {
        getActionHandler()?.onApply()
        super.onApplyClick()
    }

    override fun onReturnOrganisation(organisations: List<Organisation>) {
        parentFragmentManager.setFragmentResult(requestKey, bundleOf(RETURN_ORGANISATION_KEY to organisations))
        parentFragment?.setFragmentResult(
            requestKey,
            bundleOf(RESULT_KEY to OurOrgUnnecessaryFragmentResult.OnReturnOrganisation(organisations))
        )
        requestCloseContainer()
    }

    override fun onClickOrganisation(organisations: List<Organisation>) {
        parentFragment?.setFragmentResult(
            requestKey,
            bundleOf(RESULT_KEY to OurOrgUnnecessaryFragmentResult.OrganizationChanged(organisations))
        )
        if (params.autoApplyAtFirstSelect) {
            getActionHandler()?.onApply()
        } else {
            setResetButtonVisibility(organisations.isNotEmpty())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAcceptButtonVisible(true)

        updateHeaderViewModel {
            this.copy(
                titleRes = R.string.our_org_item_header_simple_title,
                hasButton = params.selectedOrganisations.isNotEmpty(),
                onButtonClick = {
                    getActionHandler()?.onReset()
                    setResetButtonVisibility(false)
                }
            )
        }

        view.findViewById<ViewGroup>(RBaseComponents.id.base_components_content_container)?.apply {
            /** Для контейнера включаем передачу фокуса детям **/
            descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        }
    }

    private fun setResetButtonVisibility(isVisible: Boolean) =
        updateHeaderViewModel { copy(hasButton = isVisible) }

    private fun getActionHandler(): OurOrgUnnecessaryChoiceHostContract.ActionHandler? {
        return childFragmentManager
            .findFragmentById(RBaseComponents.id.base_components_content_container)
            .takeIf { it is OurOrgUnnecessaryChoiceHostContract.ActionHandler }
            ?.let { it as OurOrgUnnecessaryChoiceHostContract.ActionHandler }
    }
}
