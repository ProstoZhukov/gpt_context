package ru.tensor.sbis.our_organisations.presentation.view

import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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
import ru.tensor.sbis.our_organisations.feature.OurOrgNecessaryFragmentResult
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.presentation.contract.register
import ru.tensor.sbis.our_organisations.presentation.list.OurOrgListContract
import ru.tensor.sbis.our_organisations.presentation.view.BaseOurOrgChoiceFragment.Companion.OUR_ORG_ITEM_TYPE_KEY
import ru.tensor.sbis.our_organisations.presentation.view.BaseOurOrgChoiceFragment.Companion.OUR_ORG_PARAMS_KEY
import ru.tensor.sbis.base_components.R as RBaseComponents

/**
 * Окно выбора с обязательным выбором из списка организаций.
 *
 * @author mv.ilin
 */
internal class OurOrgNecessaryChoiceSelectionWindow :
    SelectionWindowContent(),
    BaseOurOrgChoiceFragment {

    companion object {
        private const val RESULT_KEY = "RESULT_KEY"
        private const val ARG_REQUEST_KEY = "REQUEST_KEY"

        fun extractResult(data: Bundle) =
            data.getParcelableUniversally<OurOrgNecessaryFragmentResult>(RESULT_KEY)!!
    }

    class Creator(
        private val requestKey: String,
        private val ourOrgParams: OurOrgParams,
        private val ourOrgItemType: OurOrgItemType
    ) : ContentCreatorParcelable, DefaultParcelable {
        companion object {
            @JvmField
            val CREATOR = DefaultParcelable.generateCreator(OurOrgNecessaryChoiceSelectionWindow::Creator)
        }

        @Suppress("DEPRECATION")
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readParcelable<OurOrgParams>(),
            parcel.readSerializable() as OurOrgItemType
        )

        override fun writeToParcel(parcel: Parcel, parcelableFlags: Int) {
            parcel.writeString(requestKey)
            parcel.writeParcelable(ourOrgParams, parcelableFlags)
            parcel.writeSerializable(ourOrgItemType)
        }

        override fun createFragment(): Fragment = OurOrgNecessaryChoiceSelectionWindow().withArgs {
            putString(ARG_REQUEST_KEY, requestKey)
            putParcelable(OUR_ORG_PARAMS_KEY, ourOrgParams)
            putSerializable(OUR_ORG_ITEM_TYPE_KEY, ourOrgItemType)
        }
    }

    override val baseFragmentManager: FragmentManager by lazy { childFragmentManager }
    override val containerViewId: Int = RBaseComponents.id.base_components_content_container
    override val discardResultAfterClick: Boolean = true
    override val ourOrgListContract: OurOrgListContract.Factory =
        OurOrgListContract().register(this, ::handleOurOrgListResult)

    override fun inflateContentView(inflater: LayoutInflater, container: ViewGroup) {
        createOurOrgListFragment()
    }

    override fun getShadowVisibilityDispatcher(): ShadowVisibilityDispatcher? = null

    override fun getContentViewId(): Int = R.id.organisations

    override fun onShowContent() {
        if (view?.isVisible == false) {
            view?.isVisible = true
            anyContainerAs<Container.Showable>()?.showContent()
            parentFragment?.setFragmentResult(
                requireArguments().getString(ARG_REQUEST_KEY)!!,
                bundleOf(RESULT_KEY to OurOrgNecessaryFragmentResult.OnShowContent)
            )
        }
    }

    override fun onClickOrganisation(organisations: List<Organisation>) {
        parentFragment?.setFragmentResult(
            requireArguments().getString(ARG_REQUEST_KEY)!!,
            bundleOf(RESULT_KEY to OurOrgNecessaryFragmentResult.OrganizationChanged(organisations.first()))
        )
        requestCloseContainer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireView().findViewById<ViewGroup>(RBaseComponents.id.base_components_content_container)?.apply {
            /** Для контейнера включаем передачу фокуса детям **/
            descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        }

        updateHeaderViewModel { this.copy(titleRes = R.string.our_org_item_header_simple_title) }

        view.isVisible = false
    }
}
