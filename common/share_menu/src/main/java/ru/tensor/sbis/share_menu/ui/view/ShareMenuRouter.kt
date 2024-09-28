package ru.tensor.sbis.share_menu.ui.view

import android.content.Context
import android.content.Intent
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.design.R
import ru.tensor.sbis.mvi_extension.router.Router
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter
import javax.inject.Inject

/**
 * Роутер компонента меню для "поделиться".
 *
 * @author vv.chekurda
 */
internal class ShareMenuRouter @Inject constructor(
    @IdRes private val contentContainerId: Int
) : FragmentRouter(),
    Router<Fragment> {

    /**
     * Установить фрагмент в контейнер меню.
     */
    fun setContent(fragment: Fragment) {
        execute {
            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, 0)
                .replace(contentContainerId, fragment, SHARE_CONTENT_FRAGMENT_TAG)
                .commitNow()
        }
    }

    /**
     * Открыть экран.
     */
    fun openScreen(intentCreator: (Context) -> Intent) {
        execute {
            val intent = intentCreator(requireContext())
            startActivity(intent)
        }
    }

    /**
     * Обработать нажатие кнопки назад.
     *
     * @return true, если было изменеие стека.
     */
    fun onBackPressed(): Boolean {
        var isHandled = false
        execute {
            isHandled = childFragmentManager.handleBackPressed()
        }
        return isHandled
    }

    /**
     * Завершить процесс, в котором происходит шаринг.
     */
    fun finishTask() {
        execute {
            requireActivity().finishAndRemoveTask()
        }
    }

    private fun FragmentManager.handleBackPressed(): Boolean =
        (fragments.lastOrNull() as? FragmentBackPress)?.onBackPressed() != true
            || popBackStackImmediate()
}

private const val SHARE_CONTENT_FRAGMENT_TAG = "SHARE_CONTENT_FRAGMENT_TAG"