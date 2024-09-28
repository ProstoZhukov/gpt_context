package ru.tensor.sbis.communicator.common.util.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.base_components.BaseFragment

/**
 * Обертка базового фрагмента для доступа к контексту и основному функционалу компонента
 *
 * @author vv.chekurda
 */
interface BaseFragmentWrapper {

    /** @SelfDocumented */
    var fragment: BaseFragment?

    /** @SelfDocumented */
    val view: View?

    /** @SelfDocumented */
    val childFragmentManager: FragmentManager

    /** @SelfDocumented */
    val resources: Resources

    /** @SelfDocumented */
    val isTablet: Boolean

    /** @SelfDocumented */
    fun startActivity(intent: Intent)

    /** @SelfDocumented */
    fun startActivityForResult(intent: Intent, requestCode: Int)

    /** @SelfDocumented */
    fun requireFragment(): BaseFragment

    /** @SelfDocumented */
    fun requireContext(): Context

    /** @SelfDocumented */
    fun requireActivity(): FragmentActivity

    /** @SelfDocumented */
    fun requireFragmentManager(): FragmentManager

    /** @SelfDocumented */
    fun getString(@StringRes id: Int): String

    /** @SelfDocumented */
    fun clearReferences()
}