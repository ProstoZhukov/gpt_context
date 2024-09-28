package ru.tensor.sbis.design;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import timber.log.Timber;

/**
 * Класс представляет собой объект для доступа к файлам шрифта.
 *
 * @author du.bykov
 */
public class TypefaceManager {

    /**
     * Метод для получения шрифта 'RobotoLight'.
     *
     * @return {@link Typeface}
     */
    @Nullable
    public static Typeface getRobotoLightFont(@NonNull Context context) {
        return light.getTypeface(context);
    }

    /**
     * Метод для получения шрифта 'RobotoRegular'.
     *
     * @return {@link Typeface}
     */
    @Nullable
    public static Typeface getRobotoRegularFont(@NonNull Context context) {
        return regular.getTypeface(context);
    }

    /**
     * Метод для получения шрифта 'RobotoItalic'.
     *
     * @return {@link Typeface}
     */
    @Nullable
    public static Typeface getRobotoItalicFont(@NonNull Context context) {
        return italic.getTypeface(context);
    }

    /**
     * Метод для получения шрифта 'RobotoMedium'.
     *
     * @return {@link Typeface}
     */
    @Nullable
    public static Typeface getRobotoMediumFont(@NonNull Context context) {
        return medium.getTypeface(context);
    }

    /**
     * Метод для получения шрифта 'RobotoBold'.
     *
     * @return {@link Typeface}
     */
    @Nullable
    public static Typeface getRobotoBoldFont(@NonNull Context context) {
        return bold.getTypeface(context);
    }

    /**
     * Метод для получения шрифта 'RobotoMonoRegular'.
     *
     * @return {@link Typeface}
     */
    @Nullable
    public static Typeface getRobotoMonoRegularFont(@NonNull Context context) {
        return monoRegular.getTypeface(context);
    }

    /**
     * Метод для получения шрифта 'CbucIcon'.
     *
     * @return {@link Typeface}
     */
    @Nullable
    public static Typeface getCbucIconTypeface(@NonNull Context context) {
        return cbucIcons.getTypeface(context);
    }

    /**
     * Метод для получения шрифта 'SbisMobileIcon'.
     *
     * @return {@link Typeface}
     */
    public static Typeface getSbisMobileIconTypeface(@NonNull Context context) {
        return sbisMobileIcons.getTypeface(context);
    }

    /**
     * Метод для получения шрифта 'sbis_navigation_icons'.
     *
     * @return {@link Typeface}
     */
    public static Typeface getSbisNavigationIconTypeface(@NonNull Context context) {
        return sbisNavigationIcons.getTypeface(context);
    }

    @Nullable
    public static Typeface getFont(@NonNull Context context, @FontRes int font) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                return context.getResources().getFont(font);
            } catch (Resources.NotFoundException exception) {
                return ResourcesCompat.getFont(context, font);
            }
        } else {
            return ResourcesCompat.getFont(context, font);
        }

    }

    private static final FontCache light = new FontCache(R.font.roboto_light);
    private static final FontCache regular = new FontCache(R.font.roboto_regular);
    private static final FontCache italic = new FontCache(R.font.roboto_italic);
    private static final FontCache medium = new FontCache(R.font.roboto_medium);
    private static final FontCache bold = new FontCache(R.font.roboto_bold);
    private static final FontCache monoRegular = new FontCache(R.font.roboto_mono_regular);
    private static final FontCache cbucIcons = new FontCache(R.font.cbuc_icons);
    private static final FontCache sbisMobileIcons = new FontCache(R.font.sbis_mobile_icons);
    private static final FontCache sbisNavigationIcons = new FontCache(R.font.sbis_navigation_icons);
}