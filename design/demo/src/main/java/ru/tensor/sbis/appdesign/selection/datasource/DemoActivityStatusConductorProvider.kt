package ru.tensor.sbis.appdesign.selection.datasource

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.person_decl.profile.ActivityStatusConductor
import ru.tensor.sbis.design.selection.ui.contract.recipient.ActivityStatusConductorProvider

/**
 * @author ma.kolpakov
 */
internal class DemoActivityStatusConductorProvider : ActivityStatusConductorProvider<FragmentActivity> {

    override fun getActivityStatusConductor(activity: FragmentActivity): ActivityStatusConductor =
        DemoActivityStatusConductor()
}