package ru.tensor.sbis.communicator.common.util.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.DeviceConfigurationUtils

/**
 * Реализация обертки [BaseFragmentWrapper] над базовым фрагментом
 * для получения доступа к контексту и основному функционалу компонента
 *
 * @author vv.chekurda
 */
class BaseFragmentWrapperImpl : BaseFragmentWrapper {

    override var fragment: BaseFragment? = null

    override val isTablet: Boolean
        get() = DeviceConfigurationUtils.isTablet(requireContext())

    override val view: View?
        get() = requireFragment().view

    override val resources: Resources
        get() = requireContext().resources

    override val childFragmentManager: FragmentManager
        get() = requireFragment().childFragmentManager

    override fun startActivity(intent: Intent) {
        fragment?.startActivity(intent)
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        fragment?.startActivityForResult(intent, requestCode)
    }

    override fun requireFragment(): BaseFragment =
        fragment!!

    override fun requireContext(): Context =
        requireFragment().requireContext()

    override fun requireActivity(): FragmentActivity =
        requireFragment().requireActivity()

    override fun requireFragmentManager(): FragmentManager =
        requireFragment().requireFragmentManager()

    override fun getString(@StringRes id: Int): String =
        resources.getString(id)

    override fun clearReferences() {
        fragment = null
    }
}