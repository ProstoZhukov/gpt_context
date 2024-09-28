package ru.tensor.sbis.share_menu.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuContent
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuDelegate

/**
 * Вспомогательная реализация для встраиваниям контрола меню [ShareMenuDelegate]
 * в отображаемые разделы реализаций [ShareMenuContent].
 *
 * @property shareMenuDelegate контрол для управления состоянием меню из отображаемого раздела.
 *
 * @author vv.chekurda
 */
internal class ShareMenuDelegateInjector(
    private val shareMenuDelegate: ShareMenuDelegate
) : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentCreated(fm: FragmentManager, fragment: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, fragment, savedInstanceState)
        if (fragment is ShareMenuContent) {
            fragment.setShareMenuDelegate(shareMenuDelegate)
        }
    }

    /**
     * Зарегистрировать фрагмент через который отображаются разделы в меню.
     */
    fun register(fragment: Fragment) {
        fragment.childFragmentManager.registerFragmentLifecycleCallbacks(this, false)
    }

    /**
     * Удалить регистрацию фрагмента, через который отображаются разделы в меню.
     */
    fun unregister(fragment: Fragment) {
        fragment.childFragmentManager.unregisterFragmentLifecycleCallbacks(this)
    }
}