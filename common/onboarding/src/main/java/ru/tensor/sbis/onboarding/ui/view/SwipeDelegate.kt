package ru.tensor.sbis.onboarding.ui.view

import androidx.viewpager.widget.ViewPager
import ru.tensor.sbis.onboarding.domain.util.PermissionHelper
import ru.tensor.sbis.onboarding.ui.host.OnboardingHostFragmentImpl
import ru.tensor.sbis.onboarding.ui.host.adapter.PageListHolder
import javax.inject.Inject

internal class SwipeDelegate @Inject constructor(
    private val fragment: OnboardingHostFragmentImpl,
    private val holder: PageListHolder,
    private val permissionInteractor: PermissionHelper
) : OnSwipeListener {

    private fun shouldPostponeForward(leavePosition: Int): Boolean {
        val pageUuid = holder.getPageId(leavePosition)
        return permissionInteractor.hasUnresolvedPermissions(pageUuid)
    }

    override fun onSwipeForward(
        leavePosition: Int,
        deferredSwipeAction: ViewPager.() -> Unit
    ): Boolean = if (shouldPostponeForward(leavePosition)) {
        val featureUuid = holder.getPageId(leavePosition)
        permissionInteractor.askPermissionsAndAction(featureUuid) {
            fragment.viewPager()?.deferredSwipeAction()
        }
        true
    } else {
        false
    }

    override fun onSwipeOutAtEnd(deferredSwipeAction: () -> Unit) {
        val lastPageCount = holder.getPageCount() - 1
        if (shouldPostponeForward(lastPageCount)) {
            val featureUuid = holder.getPageId(lastPageCount)
            permissionInteractor.askPermissionsAndAction(featureUuid) {
                deferredSwipeAction()
            }
        } else deferredSwipeAction()
    }

    override fun onSwipeBack() {
        /**
         * Событие свайпа назад, в текущей реализации не используется
         */
    }

    override fun onSwipeOutAtStart() {
        /**
         * Событие свайпа назад при нахождении в области первой фичи, в текущей реализации не используется
         */
    }
}