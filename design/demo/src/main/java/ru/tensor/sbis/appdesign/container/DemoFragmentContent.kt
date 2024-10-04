package ru.tensor.sbis.appdesign.container

import android.widget.Toast
import androidx.fragment.app.Fragment
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.container.Content
import ru.tensor.sbis.design.container.ContentCreator
import ru.tensor.sbis.design.container.FragmentContent

/**
 * Реализация контента для демо экрана контейнера.
 *
 * @author ma.kolpakov
 */
class DemoFragmentContent:FragmentContent {
    override var closeContainer: (() -> Unit)? = null
    override var showNewContent: ((ContentCreator<Content>) -> Unit)? = null
    lateinit var testFragment: SbisContainerFragment
    override fun theme() = R.style.DemoContainerCustomTheme

    override fun getFragment(): Fragment {
        testFragment = SbisContainerFragment()
        return testFragment
    }

    override fun onRestoreFragment(fragment: Fragment) {
        testFragment = fragment as SbisContainerFragment
    }

    override fun onDismiss() {
        Toast.makeText(testFragment.requireContext(), "dismiss from testFragment", Toast.LENGTH_SHORT)
                .show()
    }
}