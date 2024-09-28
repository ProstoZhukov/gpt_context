package ru.tensor.sbis.richtext.util;

import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.BuildConfig;
import timber.log.Timber;

/**
 * Обертка для более простого создания экземпляров {@link StaticLayout} в зависимости
 * от переданных параметров
 *
 * @author am.boldinov
 */
public final class StaticLayoutProxy {

    public static final int TEXT_ELLIPSIZE_OFFSET = 2; // Погрешность для рендера StaticLayout

    /**
     * Создает новый экземпляр {@link StaticLayout}
     *
     * @param text   размещаемый текст, может быть стилизованным
     * @param start  позиция начала текста
     * @param end    позиция + 1 конца текста
     * @param width  ширина текста в пикселях
     * @param layout готовый макет для текста, используется для клонирования атрибутов текста в новый объект
     */
    @NonNull
    public static StaticLayout create(@NonNull CharSequence text, int start, int end, int width, @NonNull Layout layout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return StaticLayout.Builder.obtain(text, start, end, layout.getPaint(), width)
                    .setAlignment(layout.getAlignment())
                    .setLineSpacing(layout.getSpacingAdd(), layout.getSpacingMultiplier())
                    .build();
        } else {
            //noinspection deprecation
            return new StaticLayout(text, start, end, layout.getPaint(), width, layout.getAlignment(),
                    layout.getSpacingMultiplier(), layout.getSpacingAdd(), true);
        }
    }

    /**
     * Создает новый экземпляр {@link StaticLayout}
     *
     * @param text      размещаемый текст, может быть стилизованным
     * @param paint     информация о стиле отрисовки текста
     * @param alignment выравнивание текста по горизонтали
     * @param width     ширина текста в пикселях
     */
    @NonNull
    public static StaticLayout create(@NonNull CharSequence text, @NonNull TextPaint paint,
                                      @NonNull Layout.Alignment alignment, int width) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return StaticLayout.Builder.obtain(text, 0, text.length(), paint, width)
                    .setAlignment(alignment)
                    .setIncludePad(false)
                    .build();
        } else {
            //noinspection deprecation
            return new StaticLayout(text, paint, width, alignment, 1, 0, false);
        }
    }

    /**
     * Создает новый экземпляр {@link StaticLayout}
     *
     * @param text  размещаемый текст, может быть стилизованным
     * @param paint информация о стиле отрисовки текста
     * @param width ширина текста в пикселях
     */
    @NonNull
    public static StaticLayout create(@NonNull CharSequence text, @NonNull TextPaint paint, int width) {
        return create(text, paint, Layout.Alignment.ALIGN_NORMAL, width);
    }

    /**
     * Создает новый экземпляр однострочного {@link StaticLayout} с троеточием в конце
     *
     * @param text      размещаемый текст, может быть стилизованным
     * @param paint     информация о стиле отрисовки текста
     * @param alignment выравнивание текста по горизонтали
     * @param width     ширина текста в пикселях
     */
    @NonNull
    public static StaticLayout createEllipsize(@NonNull CharSequence text, @NonNull TextPaint paint,
                                               @NonNull Layout.Alignment alignment, int width) {
        final CharSequence result = TextUtils.ellipsize(text, paint, width - TEXT_ELLIPSIZE_OFFSET, TextUtils.TruncateAt.END);
        final StaticLayout layout = create(result, paint, alignment, width);
        if (!BuildConfig.DEBUG && layout.getLineCount() > 1) {
            Timber.e("StaticLayoutProxy created invalid ellipsize layout");
            return create(result, paint, Integer.MAX_VALUE);
        }
        return layout;
    }

    /**
     * Создает новый экземпляр однострочного {@link StaticLayout} с троеточием в конце
     *
     * @param text  размещаемый текст, может быть стилизованным
     * @param paint информация о стиле отрисовки текста
     * @param width ширина текста в пикселях
     */
    @NonNull
    public static StaticLayout createEllipsize(@NonNull CharSequence text, @NonNull TextPaint paint, int width) {
        return createEllipsize(text, paint, Layout.Alignment.ALIGN_NORMAL, width);
    }
}
