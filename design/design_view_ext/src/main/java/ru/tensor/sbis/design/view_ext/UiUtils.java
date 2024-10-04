package ru.tensor.sbis.design.view_ext;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.util.TypedValue;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

/**
 * @author sa.nikitin
 */
@SuppressWarnings("JavaDoc")
public class UiUtils {

    /**
     * Удаление внешних отступов у view
     *
     * @param view вью для обработки
     */
    public static void removeMargins(@NonNull View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(0, 0, 0, 0);
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * Установка верхнего отступа у view
     *
     * @param view      вью для обработки
     * @param topMargin значение отступа
     */
    public static void setTopMargin(@NonNull View view, @Px int topMargin) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(0, topMargin, 0, 0);
            view.setLayoutParams(layoutParams);
        }
    }

    /** @SelfDocumented */
    @SuppressWarnings("JavaDoc")
    public interface ViewAction {
        /** @SelfDocumented */
        void apply(@NonNull View view);
    }

    /** @SelfDocumented */
    public static void applyRecursively(View view, @NonNull ViewAction action) {
        applyRecursively(view, action, View.class);
    }

    /** @SelfDocumented */
    public static void applyRecursively(View view, @NonNull ViewAction action, Class<?>... types) {
        if (view != null) {
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                for (int i = 0; i < group.getChildCount(); ++i) {
                    applyRecursively(group.getChildAt(i), action);
                }
            }
            for (Class<?> type : types) {
                if (type.isInstance(view)) {
                    action.apply(view);
                    break;
                }
            }
        }
    }

    /** @SelfDocumented */
    public static void setEnabledControls(View view, boolean enabled) {
        setEnabledControls(view, enabled, View.class);
    }

    /** @SelfDocumented */
    @SuppressWarnings("unused")
    public static void removeMaxLinesConstraints(View view) {
        removeMaxLinesConstraints(view, false);
    }

    /**
     * Удаление ограничений на кол-во строк
     *
     * @param view             вью для рекурсивного обхода
     * @param exceptSingleLine нужно ли пропускать однострочные поля
     */
    public static void removeMaxLinesConstraints(View view, boolean exceptSingleLine) {
        setMaxLinesConstraints(view, exceptSingleLine, Integer.MAX_VALUE);
    }

    /**
     * Установка ограничений на кол-во строк
     *
     * @param view             вью для рекурсивного обхода
     * @param exceptSingleLine нужно ли пропускать однострочные поля
     * @param maxLines         ограничение на кол-во строк
     */
    public static void setMaxLinesConstraints(
        @NonNull View view,
        boolean exceptSingleLine,
        @IntRange(from = 1) int maxLines
    ) {
        applyRecursively(view, view1 -> {
            if (view1 instanceof TextView) {
                TextView textView = (TextView) view1;
                if (!exceptSingleLine || textView.getMaxLines() > 1) {
                    textView.setMaxLines(maxLines);
                }
            }
        }, TextView.class);
    }

    /** @SelfDocumented */
    @SuppressWarnings("Convert2Lambda")
    public static void setEnabledControls(View view, boolean enabled, Class<?>... types) {
        applyRecursively(view, new ViewAction() {
            @Override
            public void apply(@NonNull View view) {
                if (!(view instanceof ViewGroup)) {
                    view.setEnabled(enabled);
                }
            }
        }, types);
    }

    /** @SelfDocumented */
    public static int getToolBarHeight(Context context) {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, context.getResources().getDisplayMetrics()
            );
        }
        return actionBarHeight;
    }

    /** @SelfDocumented */
    public static void disableActivityRotation(@NonNull Activity activity) {
        final int currentOrientation = activity.getWindowManager().getDefaultDisplay().getRotation();
        if (currentOrientation == Surface.ROTATION_0 || currentOrientation == Surface.ROTATION_180) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (currentOrientation == Surface.ROTATION_90) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
    }

    /**
     * Добавление для currentView выравнивания относительно targetView
     * Если целевая вью @Nullable то ранее установленное выравнивание удаляется
     *
     * @param currentView вью для добавления правила отрисовки
     * @param targetView  целевая вью, по которой осуществляется выравнивание
     * @param rule        код правила
     */
    private static void setRule(@NonNull View currentView, @Nullable View targetView, int rule) {
        if (currentView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) currentView.getLayoutParams();
            if (targetView != null) {
                lp.addRule(rule, targetView.getId());
            } else {
                lp.addRule(rule, 0);
            }
            currentView.setLayoutParams(lp);
        }
    }

    /**
     * Добавление для currentView выравнивания типа alignBaseline по targetView
     * Если целевая вью @Nullable то ранее установленное выравнивание удаляется
     *
     * @param currentView вью для добавления правила отрисовки
     * @param targetView  целевая вью, по которой осуществляется выравнивание
     */
    public static void setBaselineRule(@NonNull View currentView, @Nullable View targetView) {
        setRule(currentView, targetView, RelativeLayout.ALIGN_BASELINE);
    }

    /**
     * Добавление для currentView выравнивания типа toLeftOf по targetView
     * Если целевая вью @Nullable то ранее установленное выравнивание удаляется
     *
     * @param currentView вью для добавления правила отрисовки
     * @param targetView  целевая вью, по которой осуществляется выравнивание
     */
    public static void setLeftOfRule(@NonNull View currentView, @Nullable View targetView) {
        setRule(currentView, targetView, RelativeLayout.LEFT_OF);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setRule(currentView, targetView, RelativeLayout.START_OF);
        }
    }
}
