package ru.tensor.sbis.base_components

import androidx.annotation.UiThread
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.android_ext_decl.AndroidComponent

/**
 * SelfDocumented
 * Created by kabramov on 03.10.16.
 */
@UiThread
abstract class BaseDialogFragment : DialogFragment(), AndroidComponent {

    override fun getFragment(): Fragment? {
        return this
    }

    //todo add here some base implementation if needed!

    override fun getSupportFragmentManager(): FragmentManager {
        @Suppress("DEPRECATION")
        return requireFragmentManager()
    }
}
