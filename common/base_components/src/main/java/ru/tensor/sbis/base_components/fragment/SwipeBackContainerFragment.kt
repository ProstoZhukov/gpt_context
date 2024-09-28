package ru.tensor.sbis.base_components.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import ru.tensor.sbis.common.R
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.design.swipeback.SwipeBackFragment
import ru.tensor.sbis.version_checker_decl.VersionedComponent

/**
 * Обертка над фрагментом [fragment] которая делает его удаляемым по свайпу. Поле [fragment] должно быть установлено
 * после создания [SwipeBackContainerFragment], перед его добавлением в Fragment Manager.
 *
 * @author du.bykov
 */
internal class SwipeBackContainerFragment : SwipeBackFragment(),
    AdjustResizeHelper.KeyboardEventListener,
    VersionedComponent {

    var fragment: Fragment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentContainerView = FragmentContainerView(requireContext())
        fragmentContainerView.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        fragmentContainerView.id = R.id.swipe_back_container
        return addToSwipeBackLayout(fragmentContainerView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) return

        childFragmentManager
            .beginTransaction()
            .replace(R.id.swipe_back_container, fragment!!)
            .commitNow()
        fragment = null
    }

    override fun swipeBackEnabled() = true

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        (findNestedFragment() as? AdjustResizeHelper.KeyboardEventListener)?.onKeyboardOpenMeasure(keyboardHeight)
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        (findNestedFragment() as? AdjustResizeHelper.KeyboardEventListener)?.onKeyboardCloseMeasure(keyboardHeight)
        return true
    }

    fun findNestedFragment() = childFragmentManager.findFragmentById(R.id.swipe_back_container)
}