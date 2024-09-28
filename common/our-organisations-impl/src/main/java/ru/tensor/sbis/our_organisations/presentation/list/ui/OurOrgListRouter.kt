package ru.tensor.sbis.our_organisations.presentation.list.ui

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.context_menu.Item
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithScreenAlignment
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.mvi_extension.router.Router
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter
import ru.tensor.sbis.our_organisations.feature.data.Organisation
import ru.tensor.sbis.our_organisations.presentation.list.OurOrgListContract.OurOrgListResult
import ru.tensor.sbis.our_organisations.presentation.list.OurOrgListFragment

/**
 * Интерфейс роутера нашей организации.
 *
 * @author mv.ilin
 */
internal interface OurOrgListRouter : Router<Fragment> {

    fun showErrorMessage(message: PlatformSbisString)

    fun openFilter(anchor: View, sbisMenuItems: Iterable<Item>)

    fun onShowContent()

    fun clickApply(organisations: List<Organisation>)

    fun clickOrganisation(organisations: List<Organisation>)
}

/**
 * Реализация роутера нашей организации.
 *
 * @author mv.ilin
 */
internal class OurOrgListRouterImpl : FragmentRouter(), OurOrgListRouter {

    override fun showErrorMessage(message: PlatformSbisString) = execute {
        SbisPopupNotification.push(
            SbisPopupNotificationStyle.ERROR,
            message.getString(requireContext())
        )
    }

    override fun openFilter(anchor: View, sbisMenuItems: Iterable<Item>) {
        execute {
            SbisMenu(children = sbisMenuItems)
                .showMenuWithScreenAlignment(
                    fragmentManager = childFragmentManager,
                    anchor = anchor,
                    screenHorizontalAlignment = HorizontalAlignment.RIGHT,
                    dimType = DimType.NONE
                )
        }
    }

    override fun onShowContent() = execute {
        setFragmentResult(
            requireArguments().getString(OurOrgListFragment.ARG_REQUEST_KEY)!!,
            bundleOf(OurOrgListFragment.RESULT_KEY to OurOrgListResult.OnShowContent)
        )
    }

    override fun clickApply(organisations: List<Organisation>) = execute {
        setFragmentResult(
            requireArguments().getString(OurOrgListFragment.ARG_REQUEST_KEY)!!,
            bundleOf(OurOrgListFragment.RESULT_KEY to OurOrgListResult.OnReturnOrganisation(organisations))
        )
    }

    override fun clickOrganisation(organisations: List<Organisation>) = execute {
        setFragmentResult(
            requireArguments().getString(OurOrgListFragment.ARG_REQUEST_KEY)!!,
            bundleOf(OurOrgListFragment.RESULT_KEY to OurOrgListResult.OrganizationChanged(organisations))
        )
    }
}
