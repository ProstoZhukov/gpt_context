package ru.tensor.sbis.design.toolbar.util;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ru.tensor.sbis.design.utils.ThemeUtil;

/**
 * Вспомогательный класс для работы со стилем отображения статус-бара.
 *
 * @author us.bessonov
 */
@SuppressWarnings({"JavaDoc"})
public class StatusBarHelper {

    /**
     * Получить цвет статус-бара.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int getStatusBarColor(@Nullable Window window) {
        if (window != null) {
            return window.getStatusBarColor();
        }
        return 0;
    }

    /**
     * Задать цвет статус-бара.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(@NonNull Activity activity, int color) {
        setStatusBarColor(activity, color, false);
    }

    /**
     * Задать прозрачность статус-бара
     */
    public static void setStatusBarAlpha(@NonNull Activity activity, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        final int currentColor = getStatusBarColor(activity);
        final int alphaColor = ColorUtils.setAlphaComponent(currentColor, (int) (alpha * 255));
        setStatusBarColor(activity, alphaColor);
    }

    /**
     * Задать цвет статус-бара.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(@NonNull Activity activity, int color, boolean isDarkText) {
        Window window = activity.getWindow();
        if (window != null) {
            window.setStatusBarColor(color);

            if (isDarkText) {
                setLightMode(activity);
            } else {
                setDarkMode(activity);
            }
        }
    }

    /**
     * Задать светлую тему статус-бару.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void setLightMode(@Nullable Window window) {
        if (window == null) return;
        setMIUIStatusBarDarkIcon(window, true);
        setMeizuStatusBarDarkIcon(window, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int visibility = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(visibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * Задать темную тему статус-бару.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void setDarkMode(@Nullable Window window) {
        if (window == null) return;
        setMIUIStatusBarDarkIcon(window, false);
        setMeizuStatusBarDarkIcon(window, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int visibility = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(visibility & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * Определяет текущий стиль статусбара.
     *
     * @return true, если используется светлая тема статусбара
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isStatusBarLightMode(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int visibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            return (visibility & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0;
        }
        return false;
    }

    // region Vendor Specific Utils

    private static boolean isXiaomi() {
        return Build.MANUFACTURER.equalsIgnoreCase("xiaomi");
    }

    /**
     * Задать темный стиль для иконок в статус баре на MIUI (Xiaomi).
     */
    private static void setMIUIStatusBarDarkIcon(@Nullable Window window, boolean darkIcon) {
        if (isXiaomi() && window != null) {
            Class<? extends Window> clazz = window.getClass();
            try {
                @SuppressLint("PrivateApi") Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                int darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(window, darkIcon ? darkModeFlag : 0, darkModeFlag);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * Задать темный стиль для иконок в статус баре на Flame OS (Meizu).
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    private static void setMeizuStatusBarDarkIcon(@Nullable Window window, boolean darkIcon) {
        if (window == null) return;
        try {
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (darkIcon) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    // endregion

    /**
     * Используйте метод {@link #setStatusBarColor(Activity, int)}
     * в связке с {@link #setDarkMode(Activity)} или {@link #setLightMode(Activity)}.
     */
    @Deprecated
    public static void applyLightStatusBar(Activity activity, boolean lightStatusBar, int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (lightStatusBar) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            activity.getWindow().setStatusBarColor(statusBarColor);
        }
    }

    /**
     * Используйте метод {@link #setStatusBarColor(Activity, int)}
     * в связке с {@link #setDarkMode(Activity)} или {@link #setLightMode(Activity)}.
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    public static void applyStatusBarForXiaomi(Activity activity, boolean lightStatusBar, int statusBarColor) {
        if (isXiaomi()) {
            if (setMiuiStatusBarDarkMode(activity, lightStatusBar)) {
                activity.getWindow().setStatusBarColor(statusBarColor);
            }
        }
    }

    /**
     * Необходимо удалить вместе с методами {@link #applyLightStatusBar(Activity, boolean, int)}
     * и {@link #applyStatusBarForXiaomi(Activity, boolean, int)}.
     */
    @Deprecated
    private static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag;
            @SuppressLint("PrivateApi") Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Получить высоту статус бара
     *
     * @param context контекст
     * @return высота статус бара
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources resources = context.getResources();
        int statusBarResId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarResId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(statusBarResId);
        }
        return statusBarHeight;
    }

    /**
     * Определяем цвет иконок статусбара, чтобы они с ним не сливались.
     *
     * @param activity
     */
    public static void updateStatusBarMode(Activity activity) {
        updateStatusBarMode(activity, activity.getWindow().getStatusBarColor());
    }

    /**
     * Установка цвета иконок статусбара на основе переданного,
     * чтобы они с ним не сливались.
     *
     * @param window
     * @param statusBarColor - цвет статус бара, на основе которого не должны сливаться иконки.
     */
    public static void updateStatusBarMode(Context context, @Nullable Window window, @ColorInt int statusBarColor) {
        /*
          Если цвет статусбара прозрачный и определить правильный цвет невозможно,
          опираемся на флаг windowLightStatusBar.
         */
        if (statusBarColor != Color.TRANSPARENT || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (useDarkStatusBarMode(statusBarColor)) {
                setDarkMode(window);
            } else {
                setLightMode(window);
            }
        } else {
            if (ThemeUtil.getThemeBoolean(context, android.R.attr.windowLightStatusBar))
                StatusBarHelper.setLightMode(window);
            else StatusBarHelper.setDarkMode(window);
        }
    }

    /**
     * Узнать нужно ли использовать DarkMode для статусбара.
     *
     * @param background - цвет статусбара
     */
    public static boolean useDarkStatusBarMode(@ColorInt int background) {
        return ColorUtils.calculateLuminance(background) < 0.5;
    }

    /**
     * Перегрузка для использования [Activity] в качестве аргумента.
     */
    public static void updateStatusBarMode(@Nullable Activity activity, @ColorInt int statusBarColor) {
        if (activity == null) return;
        updateStatusBarMode(activity, activity.getWindow(), statusBarColor);
    }

    /**
     * Перегрузка для использования [Activity] в аргументе.
     */
    public static int getStatusBarColor(@Nullable Activity activity) {
        if (activity == null) return 0;
        return getStatusBarColor(activity.getWindow());
    }

    /**
     * Перегрузка для использования [Activity] в аргументе.
     */
    public static void setLightMode(@Nullable Activity activity) {
        if (activity == null) return;
        setLightMode(activity.getWindow());
    }

    /**
     * Перегрузка для использования [Activity] в аргументе.
     */
    public static void setDarkMode(@Nullable Activity activity) {
        if (activity == null) return;
        setDarkMode(activity.getWindow());
    }
}
