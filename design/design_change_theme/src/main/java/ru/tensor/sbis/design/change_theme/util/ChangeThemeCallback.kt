package ru.tensor.sbis.design.change_theme.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.change_theme.ChangeThemePlugin
import ru.tensor.sbis.design.change_theme.contract.IgnoreStatusBarAutoColor
import ru.tensor.sbis.design.change_theme.contract.SelfThemedActivity
import ru.tensor.sbis.design.toolbar.util.StatusBarHelper
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.utils.getThemeInteger
import ru.tensor.sbis.design.R as RD

/**
 * Коллбэк на применение темы для всех активити.
 * Необходимо зарегистрировать в [Application] с помощью [Application.registerActivityLifecycleCallbacks]
 *
 * @param myTheme Ресурс стиля применяемой темы.
 *
 * @author da.zolotarev
 */
class ChangeThemeCallback(
    private val myTheme: Theme,
    private val isNewToolbar: Boolean = false
) : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) {
        restartAppIfChangedSystemTheme(activity)

        activity.theme.applyStyle(myTheme.globalTheme, true)
        activity.theme.applyStyle(myTheme.appTheme, true)

        // Проверяем есть ли у активити своя тема и применяем ее, так как она затерлась
        (activity as? SelfThemedActivity)?.getThemeRes()?.let {
            activity.theme.applyStyle(it, true)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (activity is IgnoreStatusBarAutoColor) return
        val statusBarColor = if (isNewToolbar) {
            activity.getThemeColorInt(RD.attr.unaccentedAdaptiveBackgroundColor)
        } else {
            activity.getThemeColorInt(RD.attr.headerBackgroundColor)
        }
        val currStatusBarColor = activity.window.statusBarColor
        val globalToolbarStyleRes = activity.getThemeInteger(RD.attr.globalToolbarStyle)

        val isUsedBaseAppThemeStatusBarColor = activity.window.statusBarColor ==
            ResourcesCompat.getColor(activity.resources, RD.color.color_primary, null)

        /**
         * Если для покраски статус бара используется цвет headerBackgroundColor и тема тулбара синия,
         * то обрамляем headerBackgroundColor в тему синего туллбара. А для случаев когда цвет статусбара
         * берется из BaseAppTheme, перекрашиваем статуст бар
         */
        if (isUsedBaseAppThemeStatusBarColor || (
                statusBarColor == currStatusBarColor &&
                    globalToolbarStyleRes == RD.style.FeatureDefaultLightContrastTheme
                )
        ) {
            activity.window.statusBarColor = if (isNewToolbar) {
                statusBarColor
            } else {
                ThemeContextBuilder(activity, RD.attr.globalToolbarStyle)
                    .build()
                    .getThemeColorInt(RD.attr.headerBackgroundColor)
            }
        }
        // Обновляем цвет иконок статус бара(черный/белый)
        StatusBarHelper.updateStatusBarMode(activity)
    }

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit

    private fun restartAppIfChangedSystemTheme(activity: Activity) {
        if (!getSystemThemeEnabledFlag(activity)) return
        val systemThemes = ChangeThemePlugin.themesProvider?.get()?.getSystemThemes() ?: return
        val theme = when (getSystemThemeMode(activity)) {
            SystemThemeState.DAY -> systemThemes.dayTheme
            SystemThemeState.NIGHT -> systemThemes.nightTheme
            else -> return
        }
        if (myTheme.id != theme.id) {
            restartApp(activity)
        }
    }
}