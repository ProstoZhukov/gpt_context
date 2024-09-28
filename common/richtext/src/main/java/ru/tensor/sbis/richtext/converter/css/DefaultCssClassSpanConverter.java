package ru.tensor.sbis.richtext.converter.css;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.util.ColorIntArray;
import ru.tensor.sbis.richtext.R;
import timber.log.Timber;

/**
 * Стандартный конвертер css класса в span
 *
 * @author am.boldinov
 */
public class DefaultCssClassSpanConverter implements CssClassSpanConverter {

    @NonNull
    private final Context mContext;
    @NonNull
    private final ColorIntArray mTextColorPalette;
    @NonNull
    private final ColorIntArray mBackgroundColorPalette;

    public DefaultCssClassSpanConverter(@NonNull Context context, @ArrayRes int textColorPalette,
                                        @ArrayRes int backgroundColorPalette) {
        mContext = context;
        mTextColorPalette = new ColorIntArray(context, textColorPalette);
        mBackgroundColorPalette = new ColorIntArray(context, backgroundColorPalette);
    }

    @Nullable
    @Override
    public List<MarkSpan> convert(@NonNull String cssClass) {
        final Integer style = CssAppearanceClassFactory.create(cssClass);
        if (style != null) {
            return convert(style);
        }
        final Integer color = CssColorClassFactory.create(mContext, cssClass);
        if (color != null) {
            return createTextColorList(color);
        }
        final Integer textColor = parseColorPalette(cssClass, "richEditor_Base_color_", mTextColorPalette);
        if (textColor != null) {
            return createTextColorList(textColor);
        }
        final Integer backgroundColor = parseColorPalette(cssClass, "richEditor_Base_background_", mBackgroundColorPalette);
        if (backgroundColor != null) {
            return createBackgroundColorList(backgroundColor);
        }
        return null;
    }

    @NonNull
    @Override
    public List<MarkSpan> convert(int style) {
        return createAppearanceList(style);
    }

    @NonNull
    private List<MarkSpan> createTextColorList(@ColorInt int color) {
        final MarkSpan span = new MarkSpan.ForegroundColor(color);
        return Collections.singletonList(span);
    }

    @NonNull
    private List<MarkSpan> createBackgroundColorList(@ColorInt int color) {
        final MarkSpan span = new MarkSpan.BackgroundColor(mContext, color);
        return Collections.singletonList(span);
    }

    @NonNull
    private List<MarkSpan> createAppearanceList(@StyleRes int style) {
        final List<MarkSpan> result = new ArrayList<>(3);
        final TypedArray array = mContext.obtainStyledAttributes(style, R.styleable.RichTextAppearance);
        final int textColorType = array.getType(R.styleable.RichTextAppearance_android_textColor);
        final int textColor;
        if (textColorType == TypedValue.TYPE_ATTRIBUTE) {
            final TypedValue typedValue = new TypedValue();
            array.getValue(R.styleable.RichTextAppearance_android_textColor, typedValue);
            mContext.getTheme().resolveAttribute(typedValue.data, typedValue, true);
            textColor = typedValue.data;
        } else {
            textColor = array.getInt(R.styleable.RichTextAppearance_android_textColor, ResourcesCompat.ID_NULL);
        }
        if (textColor != ResourcesCompat.ID_NULL) {
            result.add(new MarkSpan.ForegroundColor(textColor));
        }
        final int textSize = array.getDimensionPixelSize(R.styleable.RichTextAppearance_android_textSize, ResourcesCompat.ID_NULL);
        if (textSize != ResourcesCompat.ID_NULL) {
            result.add(new MarkSpan.FontSize(textSize));
        }
        final int textStyle = array.getInt(R.styleable.RichTextAppearance_android_textStyle, ResourcesCompat.ID_NULL);
        if (textStyle != ResourcesCompat.ID_NULL) {
            result.add(new MarkSpan.TypefaceStyle(textStyle));
        }
        array.recycle();
        return result;
    }

    @ColorInt
    private Integer parseColorPalette(@NonNull String cssClass, @NonNull String name,
                                      @NonNull ColorIntArray colorPalette) {
        if (cssClass.startsWith(name)) {
            try {
                final int colorNumber = Integer.parseInt(cssClass.replaceAll("[^0-9]", "")) - 1; // на вебе счет от единицы
                return colorPalette.getColor(colorNumber);
            } catch (NumberFormatException e) {
                Timber.e(e);
            }
        }
        return null;
    }
}
