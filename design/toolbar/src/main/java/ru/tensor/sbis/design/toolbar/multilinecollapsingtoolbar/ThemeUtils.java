package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Утилиты для работы с темами
 *
 * @author us.bessonov
 */
class ThemeUtils {

    private static final int[] APPCOMPAT_CHECK_ATTRS = {
            com.google.android.material.R.attr.colorPrimary
    };

    static void checkAppCompatTheme(Context context) {
        TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        final boolean failed = !a.hasValue(0);
        a.recycle();
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme "
                    + "(or descendant) with the design library.");
        }
    }
}

