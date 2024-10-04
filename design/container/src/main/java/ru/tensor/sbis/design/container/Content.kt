package ru.tensor.sbis.design.container

import android.content.Context
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat.ID_NULL

/**
 * Интерфейс создания контента для контейнера.
 *
 * ```
 * Пример связывания шапки и контента
 *class MyContent : FragmentContent {
 *     lateinit var fragment: MyFragment
 *     override fun getFragment(containerFragment: SbisContainerImpl): Fragment {
 *         return MyFragment().also { fragment = it }
 *     }
 *
 *     override fun onRestoreFragment(containerFragment: SbisContainerImpl, fragment: Fragment) = Unit
 *
 *     Создаем шапку и связываем наш фрагмент с событием нажатие на кнопку закрыть в ней. Шапка инициализируется первой,
 *     и фрагмента а этот момент еще не существует, поэтому мы не передаем ссылку на метод фрагмента, а используем метод из MyContent.
 *
 *     override fun getHeaderView(context: Context, container: SbisContainer): View {
 *         return createContainerHeaderTitled(context, ru.tensor.sbis.design.R.string.calendar_title, null, ::close)
 *     }
 *
 *     private fun close(){
 *         fragment.closeWithAnimation()
 *     }
 *
 *     class MyFragment : Fragment() {
 *         fun closeWithAnimation() = Unit
 *    }
 *}
 * ```
 * @author ma.kolpakov
 */

interface Content {
    /**
     * Тема контейнера можно переопределить для кастомизации.
     */
    @StyleRes
    fun theme(): Int = R.style.SbisContainer

    /**
     * Фиксированная ширина контента, контейнер станет такой же ширины.
     */
    @DimenRes
    fun customWidth(): Int = ID_NULL

    /**
     * Фиксированная высота контента, контейнер станет такой же высоты.
     */
    @DimenRes
    fun customHeight(): Int = ID_NULL

    /**
     * Вернуть шапку для контейнера.
     */
    fun getHeaderView(context: Context, container: SbisContainer): View? = null

    /**
     * Использовать стандартные отступы для контента слева и справа.
     */
    fun useDefaultHorizontalOffset(): Boolean = true
}
