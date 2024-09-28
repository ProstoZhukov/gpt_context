package ru.tensor.sbis.richtext.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Parcel;

import androidx.annotation.NonNull;

import android.text.Layout;
import android.text.style.BulletSpan;

import ru.tensor.sbis.richtext.util.SpannableUtil;

/**
 * Кастомная реализация для поддержки задания отступа от начала абзаца
 * <p>
 *
 * @author am.boldinov
 */
public class CustomBulletSpan extends BulletSpan implements PrioritySpan {

    private final int mGapWidth;
    @NonNull
    private final BulletSpanStyle mStyle;

    /**
     * Создает {@link CustomBulletSpan}
     *
     * @param gapWidth размер отступа до основного текста
     * @param style    стиль отрисовки
     */
    public CustomBulletSpan(int gapWidth, @NonNull BulletSpanStyle style) {
        super(gapWidth);
        mGapWidth = gapWidth;
        mStyle = style;
    }

    public CustomBulletSpan(int gapWidth) {
        this(gapWidth, BulletSpanStyle.FILL_CIRCLE);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mGapWidth;
    }

    @Override
    public void drawLeadingMargin(@NonNull Canvas c, @NonNull Paint p, int x, int dir,
                                  int top, int baseline, int bottom,
                                  @NonNull CharSequence text, int start, int end,
                                  boolean first, Layout l) {
        if (mStyle != BulletSpanStyle.NONE) {
            super.drawLeadingMargin(c, p, x + SpannableUtil.LEADING_MARGIN_OFFSET_X, dir, top, baseline, bottom, text, start, end, first, l);
        }
    }

    /**
     * Реализация {@link android.os.Parcelable.Creator}, генерирует экземпляр {@link CustomBulletSpan} на основе {@link Parcel}
     */
    public static final Creator<CustomBulletSpan> CREATOR = new Creator<CustomBulletSpan>() {
        @Override
        public CustomBulletSpan createFromParcel(Parcel source) {
            return new CustomBulletSpan(source);
        }

        @Override
        public CustomBulletSpan[] newArray(int size) {
            return new CustomBulletSpan[size];
        }
    };

    /**
     * Записывает в {@link Parcel} значения полей класса
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mGapWidth);
        dest.writeSerializable(mStyle);
    }

    /**
     * Создает {@link CustomBulletSpan} на основе {@link Parcel}
     */
    @SuppressWarnings("WeakerAccess")
    public CustomBulletSpan(Parcel src) {
        super(src);
        mGapWidth = src.readInt();
        mStyle = (BulletSpanStyle) src.readSerializable();
    }

    /**
     * Возвращает идентификатор типа Span для корректного понимания системой при сериализации
     */
    @Override
    public int getSpanTypeId() {
        // для меньших версий система думает, что это ее спан, а по факту кастомный и падает при копировании текста
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return SpannableUtil.UNPARCEL_SPAN_TYPE_ID;
        }
        return super.getSpanTypeId();
    }

    @Override
    public int getPriority() {
        if (mStyle == BulletSpanStyle.NONE) {
            return LeadingMargin.ENUMERATION_NONE;
        }
        return LeadingMargin.ENUMERATION;
    }
}
