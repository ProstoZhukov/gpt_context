/**
 * Инструменты для получения пути до SearchInput в иерархии фрагментов
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.utils

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.utils.extentions.getActivity

private const val PATH_SEPARATOR = " | "

/**
 * Сформировать строку, представляющую собой путь в древовидной иерархии фрагментов,
 * ведущий к данному [View]. Данная строка может использоваться для идентификации места в приложении
 * где используется данный [View].
 *
 * @return строка вида *Activity* | *fragment* | *fragment* ...
 */
fun provideViewLocation(view: View): String? {
    return startFromRoot(view) { pathBuilderWithRoot, fragments, parent ->
        fragments.formNameRecursive(view, parent, pathBuilderWithRoot).toString()
    }
}

/**
 * Функция поиска данной вью в иерархии. Первично проверяет id, после - ссылку.
 * Если не указан id - ищет рекурсивно по иерархии вью от указанного родителя
 */
private fun ViewGroup.findView(view: View): Boolean {
    if (view.id != View.NO_ID) {
        findViewById<View>(view.id)?.let {
            return it === view
        }
    } else {
        return findViewParent<View>(view) { it === this } != null
    }
    return false
}

/**
 * Рекурсивный поиск фрагмента, содержащего данную вью. После нахождения вернет полный путь в иерархии фрагментов
 * к данной вью. Поиск производится изнутри-наружу, к вершине иерархии
 */
private fun List<Fragment>.formNameRecursive(
    view: View,
    parent: Fragment?,
    currentPath: StringBuilder
): StringBuilder? {
    forEach { fragment ->
        val fragmentName = fragment.getName()!!
        fragment.childFragmentManager
            .fragments
            .filter { it.isAdded && !it.isDetached }
            .takeIf { isNotEmpty() }
            ?.formNameRecursive(view, fragment, currentPath.append(PATH_SEPARATOR).append(fragmentName))
            ?.let {
                return it
            }
        //Нашли самого глубокого чайлда, проверяем наличие вью
            ?: run {
                ((parent?.view as ViewGroup?)?.findView(view)
                    ?: (fragment.view as ViewGroup?)?.findView(view))
                    ?.let { return currentPath } //вью найдена
                    ?: run {
                        //вью не найдена, удаляем добавленную часть пути и переходим на следующую итерацию
                        val from = currentPath.length - (PATH_SEPARATOR.length + fragmentName.length)
                        currentPath.delete(from, currentPath.length)
                    }
            }
    }
    return null
}

/**
 * Начать процедуру поиска строки внутри Activity с формированием строкового пути в иерархии вложенных фрагментов,
 * используя StringBuilder. После подтверждения факта наличия фрагментов внутри активити, начинает рекурсивный поиск
 */
private fun startFromRoot(
    view: View,
    action: (pathBuilderWithRoot: StringBuilder, List<Fragment>, Fragment?) -> String?
): String? {
    val activity = view.getActivity() as? FragmentActivity
    val fragments = activity
        ?.supportFragmentManager
        ?.fragments
        ?.filter { it.isAdded && !it.isDetached }

    fragments?.let {
        return action(StringBuilder(activity.localClassName), it, null)
    }
    return null
}

private fun Fragment.getName() = this::class.java.canonicalName