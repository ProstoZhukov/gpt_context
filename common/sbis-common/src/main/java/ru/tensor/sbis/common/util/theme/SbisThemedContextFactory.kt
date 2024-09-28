package ru.tensor.sbis.common.util.theme

import android.content.Context
import android.content.res.Resources
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

/**
 * Фабрика по созданию темизированного контекста [SbisThemedContext].
 *
 * @author am.boldinov
 */
object SbisThemedContextFactory {

    /**
     * Возвращает экземпляр [SbisThemedContext], содержащий applicationContext, с установленной темой экрана.
     * Необходимо использовать для передачи в di компонент.
     *
     * @param fragment фрагмент, тема которого будет помещена в результирующий контекст
     */
    @JvmStatic
    fun createFrom(fragment: Fragment): SbisThemedContext {
        return with(fragment.requireContext()) {
            val theme = resources.newTheme().also {
                it.setTo(theme)
            }
            SbisThemedContext(object : ContextThemeWrapper(applicationContext, theme) {
                override fun getResources(): Resources {
                    // На некоторых версиях Android (до 9 включительно) не применялись ресурсы темы фрагмента.
                    return theme.resources
                }
            })
        }
    }

    /**
     * Возвращает экземпляр [SbisThemedContext], содержащий applicationContext, с установленной темой приложения.
     *
     * @param appContext контекст приложения
     * @param appTheme ресурс темы, применяемой к контексту.
     * @param customTheme, дополнительная тема, которая применяется ПЕРЕД [appTheme].
     */
    @JvmStatic
    fun create(
        appContext: Context,
        @StyleRes appTheme: Int,
        @StyleRes customTheme: Int? = null
    ): SbisThemedContext {
        return with(appContext) {
            val compoundTheme = resources.newTheme().also {
                customTheme?.apply { it.applyStyle(this, true) }
                it.applyStyle(appTheme, true)
            }
            SbisThemedContext(ContextThemeWrapper(applicationContext, compoundTheme))
        }
    }
}