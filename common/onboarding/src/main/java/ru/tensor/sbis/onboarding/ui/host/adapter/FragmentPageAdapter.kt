package ru.tensor.sbis.onboarding.ui.host.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Адаптер вью-пейджера экрана приветствия
 *
 * @author as.chadov
 */
internal class FragmentPageAdapter constructor(
    fragmentManager: FragmentManager,
    private val creator: FeaturePageCreator,
    private val paramHolder: PageListHolder,
) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    override fun getItem(position: Int): Fragment {
        val params = paramHolder.getPageParams(position)
        return creator.createFeaturePage(params)
    }

    override fun getCount(): Int {
        val count = paramHolder.getPageCount()
        if (lastItemCount != count) {
            lastItemCount = count
            notifyDataSetChanged()
        }
        if (count != 0 && deferredRestoreAction != null) {
            performItemAddedAction()
        }
        return count
    }

    fun addOnRestoreItemAction(action: Runnable) {
        deferredRestoreAction = action
    }

    private fun performItemAddedAction() {
        val tempAction = deferredRestoreAction
        deferredRestoreAction = null
        tempAction?.run()
    }

    private var lastItemCount = 0
    private var deferredRestoreAction: Runnable? = null
}