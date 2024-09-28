package ru.tensor.sbis.design.utils

import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import ru.tensor.sbis.design.utils.extentions.getActivity

/**
 * Вспомогательный класс с методами для работы с клавиатурой.
 *
 * @author du.bykov
 */
object KeyboardUtils {

    @Deprecated(
        message = "Use showKeyboard with one parameter",
        replaceWith = ReplaceWith("KeyboardUtils.showKeyboard(view)")
    )
    fun showKeyboard(@Suppress("UNUSED_PARAMETER") context: Context, view: View) {
        showKeyboard(view)
    }

    /**
     * Отобразить клавиатуру для [view]. В некоторых случаях может не сработать, если вью еще не пришла в готовность,
     * тогда стоит попробовать [showKeyboardPost].
     */
    @JvmStatic
    fun showKeyboard(view: View) {
        /**
         * По каким-то странным обстоятельствам, для разных версий работает разная
         * последовательность, выведено экспериментальным путем.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindowInsetsController(view)?.show(WindowInsetsCompat.Type.ime())
            view.requestFocus()
        } else {
            view.requestFocus()
            WindowInsetsControllerCompat(view.getActivity().window, view).show(WindowInsetsCompat.Type.ime())
        }
    }

    /**
     * Отобразить клавиатуру для [view] отложенно. В некоторых случаях обычный [showKeyboard] может не сработать, если
     * вью еще не пришла в готовность.
     */
    @JvmStatic
    fun showKeyboardPost(view: View) {
        view.post { showKeyboard(view) }
    }

    /**
     * Скрыть клавиатуру для [view].
     */
    @JvmStatic
    fun hideKeyboard(view: View) {
        /**
         * Для версий андроид ниже 30 WindowInsetsController работает плохо поэтому используется InputMethodManager напрямую
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindowInsetsController(view)?.hide(WindowInsetsCompat.Type.ime())
        } else {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /** @SelfDocumented */
    @JvmStatic
    fun isKeyboardVisible(root: View): Boolean {
        return ViewCompat.getRootWindowInsets(root)?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
    }

    /** @SelfDocumented */
    fun isActiveInput(view: View): Boolean =
        (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).isActive(view)

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getWindowInsetsController(view: View) = with(view) {
        windowInsetsController ?: getActivity().window.insetsController
    }
}