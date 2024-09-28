package ru.tensor.sbis.base_components

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Класс активности-контейнера для одного фрагмента, который нужно отобразить при создании во весь экран
 *
 * @author sa.nikitin
 */
abstract class AloneFragmentContainerActivity : FragmentContainerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val fragment = createFragment()
            if (fragment != null) {
                replaceFragment(fragment)
            } else {
                onFragmentNotCreated()
            }
        }
    }

    /** SelfDocumented */
    abstract fun createFragment(): Fragment?

    protected open fun onFragmentNotCreated() {
        finish()
    }
}