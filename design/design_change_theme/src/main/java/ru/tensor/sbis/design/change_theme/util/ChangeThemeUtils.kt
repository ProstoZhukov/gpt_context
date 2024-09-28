/**
 * Набор утилитных функций для смены темы приложения
 *
 * @author da.zolotarev
 */
package ru.tensor.sbis.design.change_theme.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.preference.PreferenceManager
import ru.tensor.sbis.design.change_theme.ChangeThemePlugin
import ru.tensor.sbis.design.change_theme.util.SystemThemeState.*
import timber.log.Timber
import kotlin.system.exitProcess

private const val PREFERENCES_THEME_INDEX_KEY = "PREFERENCES_THEME_INDEX_KEY"
private const val PREFERENCES_THEME_USE_SYSTEM_THEME = "PREFERENCES_THEME_USE_SYSTEM_THEME"

/**
 * Функция вызывает смену тему и перезапускает приложение для ее применения.
 *
 * @param newTheme Тема, которую хотим установить.
 */
fun changeTheme(context: Context, newTheme: Theme?) {
    if (newTheme != null) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        putThemeInPreferences(pref, newTheme)
    }
    restartApp(context)
}

/**
 * Функция достаёт пользовательский флаг использования системной темизации в приложении.
 */
fun getSystemThemeEnabledFlag(context: Context) =
    PreferenceManager
        .getDefaultSharedPreferences(context)
        .getBoolean(PREFERENCES_THEME_USE_SYSTEM_THEME, false)

/**
 * Функция сохраняет пользовательский флаг использования системной темизации в приложении.
 */
fun setSystemThemeEnabledFlag(context: Context, isSystemTheme: Boolean) =
    PreferenceManager
        .getDefaultSharedPreferences(context)
        .edit()
        .putBoolean(PREFERENCES_THEME_USE_SYSTEM_THEME, isSystemTheme)
        .commit()

/**
 * Достаем текущую тему из [pref], если там пусто получаем [defValue]
 *
 * ВАЖНО: в случае если мы пытаемся использовать этот метод до инициализации плагинной системы
 * (например внутри Application класса), НЕОБХОДИМО явно передать список тем приложения [appThemes]
 */
fun getThemeFromPreferences(pref: SharedPreferences, defValue: Theme, appThemes: List<Theme>? = null): Theme {
    val themesProvider = ChangeThemePlugin.themesProvider
    return (themesProvider?.get()?.getThemes() ?: appThemes)?.firstOrNull { theme ->
        theme.id == pref.getInt(PREFERENCES_THEME_INDEX_KEY, defValue.id)
    } ?: defValue
}

/**
 * Перегрузка метода с предварительной проверкой на темизацию с использованием системной дневной/ночной темы.
 *
 * Темизация на основе системного режима не работает в приложении если:
 * - невозможно определить какая системная тема установлена на устройстве: флаг isNightModeEnable == null,
 * - для приложения не заданы темы для применения с дневным и ночным режимом устройства: systemThemes == null,
 * - системная темизация отключена пользователем: isSystemThemeEnable == false.
 *
 * ВАЖНО: в случае если мы пытаемся использовать этот метод до инициализации плагинной системы
 * (например внутри Application класса), НЕОБХОДИМО явно передать список тем приложения [appThemes] и [systemThemes].
 *
 * @param systemThemes объект с темами для использования в дневной и ночной системных темах. Следует передавать в
 *                          местах, где вызов осуществляется до инициализации плагинной системы.
 */
@SuppressLint("BinaryOperationInTimber")
fun getThemeFromPreferences(
    context: Context,
    defValue: Theme,
    appThemes: List<Theme>? = null,
    systemThemes: SystemThemes? = null
): Theme {
    val themesProvider = ChangeThemePlugin.themesProvider
    val appSystemThemes = themesProvider?.get()?.getSystemThemes() ?: systemThemes
    val systemThemeMode = getSystemThemeMode(context)
    val isSystemThemeEnable = getSystemThemeEnabledFlag(context)

    if (appSystemThemes != null && isSystemThemeEnable) {
        when (systemThemeMode) {
            DAY -> return appSystemThemes.dayTheme
            NIGHT -> return appSystemThemes.nightTheme
            NOT_SUPPORTED -> Timber.e(
                "Ошибка в считывании флага дневной/ночной (светлой/тёмной) темы устройства. " +
                    "Темизация на основе системной темы устройства не активна."
            )
        }
    }
    val pref = PreferenceManager.getDefaultSharedPreferences(context)
    return (themesProvider?.get()?.getThemes() ?: appThemes)?.firstOrNull { theme ->
        theme.id == pref.getInt(PREFERENCES_THEME_INDEX_KEY, defValue.id)
    } ?: defValue
}

/**
 * Кладем тему [theme] в [pref]
 */
fun putThemeInPreferences(pref: SharedPreferences, theme: Theme) {
    pref.edit().putInt(PREFERENCES_THEME_INDEX_KEY, theme.id).commit()
}

/**
 * Возвращает экземпляр [Context] с текущей темой приложения, хранимой в [pref],
 * если там пусто/не передано значение, вернет [defValue].
 *
 * Возвращаемый экземпляр [Context] создается на основе [Context.getApplicationContext],
 * поэтому не будет утекать при использовании вне жизненного цикла view.
 *
 * @param defValue тема, которая будет применена в случае отсутствия [pref].
 * @param pref [SharedPreferences] хранящие текущую тему приложения.
 * @param customTheme дополнительная тема, которая применяется ПЕРЕД остальными темами.
 * @param appThemes список всех тем приложения, используется если плагинная система не инициализирована.
 * @param systemThemes объект с темами для использования в дневной и ночной системных темах. Следует передавать в
 * местах, где вызов осуществляется до инициализации плагинной системы.
 */
fun Context.createThemeAppContext(
    defValue: Theme,
    pref: SharedPreferences? = null,
    @StyleRes customTheme: Int? = null,
    appThemes: List<Theme>? = null,
    systemThemes: SystemThemes? = null
): Context {
    val appTheme: Theme = pref?.let {
        getThemeFromPreferences(this, defValue, appThemes, systemThemes)
    } ?: defValue
    val compoundTheme: Resources.Theme = resources.newTheme().also {
        customTheme?.apply { it.applyStyle(this, true) }
        it.applyStyle(appTheme.globalTheme, true)
        it.applyStyle(appTheme.appTheme, true)
    }
    return ContextThemeWrapper(applicationContext, compoundTheme)
}

/**
 * Отличается от [Context.createThemeAppContext], тем что использует явно переданный ресурс темы, не привязывается к
 * механизму темизации.
 *
 * @param appTheme ресурс темы, применяемой к контексту.
 * @param customTheme, дополнительная тема, которая применяется ПЕРЕД остальными темами.
 */
fun Context.createThemeAppContext(@StyleRes appTheme: Int, @StyleRes customTheme: Int? = null): Context {
    // Так как тема не привязана к темизации, сохранять ее не надо.
    val theme = Theme(NO_ID, appTheme, appTheme, 0)
    return createThemeAppContext(theme, customTheme = customTheme)
}

/**
 * Проверить, установлена ли системой темная тема.
 */
fun getSystemThemeMode(context: Context): SystemThemeState =
    when (context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
        Configuration.UI_MODE_NIGHT_NO -> DAY
        Configuration.UI_MODE_NIGHT_YES -> NIGHT
        else -> NOT_SUPPORTED
    }

/**
 * Перезапустить приложение.
 */
internal fun restartApp(context: Context) {
    val packageManager = context.packageManager
    val intent: Intent? = packageManager.getLaunchIntentForPackage(context.packageName)
    intent?.component?.let {
        val mainIntent: Intent = Intent.makeRestartActivityTask(it)
        context.startActivity(mainIntent)
        exitProcess(0)
    }
}

const val NO_ID = -1
