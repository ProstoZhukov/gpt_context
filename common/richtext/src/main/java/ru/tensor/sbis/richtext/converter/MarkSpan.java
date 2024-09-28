package ru.tensor.sbis.richtext.converter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ParagraphStyle;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import ru.tensor.sbis.design.TypefaceManager;
import ru.tensor.sbis.design.text_span.span.CustomTypefaceSpan;
import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.converter.cfg.NumberSpanConfiguration;
import ru.tensor.sbis.richtext.span.BlockQuoteData;
import ru.tensor.sbis.richtext.span.BlockQuoteSpan;
import ru.tensor.sbis.richtext.span.BulletSpanStyle;
import ru.tensor.sbis.richtext.span.CustomBulletSpan;
import ru.tensor.sbis.richtext.span.LinkUrlSpan;
import ru.tensor.sbis.richtext.span.NumberSpan;
import ru.tensor.sbis.richtext.span.NumberSpanStyle;
import ru.tensor.sbis.richtext.span.background.TextBackgroundColorSpan;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkOpener;

/**
 * Класс для отметки позиции начала span
 *
 * @author am.boldinov
 */
public abstract class MarkSpan {

    private int mDomPosition;
    private Object mTag;
    private boolean mHandled;

    /**
     * Возвращает реальный span для рендера
     */
    @NonNull
    public abstract Object getRealSpan();

    /**
     * @return true если span поддерживает пустое содержимое, false иначе
     */
    public boolean isSupportEmptyContent() {
        return false;
    }

    /**
     * Устанавливает позицию span в dom дереве
     * @param tag необходим для поиска в тексте dom позиции
     * @param domPosition позиция в dom дереве
     */
    public void setDomPosition(@NonNull Object tag, int domPosition) {
        mTag = tag;
        mDomPosition = domPosition;
    }

    /**
     * Находится ли span на необходимо позиции dom дерева
     */
    public boolean containsDomPosition(@NonNull Object tag, int domPosition) {
        return mTag == tag && mDomPosition == domPosition;
    }

    /**
     * Помечает спан как обработанный.
     * Необходимо вызывать в случаях, когда сторонний хендлер использует спан в своих целях, переопределяя
     * стандартное поведение.
     */
    public void markAsHandled() {
        mHandled = true;
    }

    /**
     * @return true если спан был обработан сторонним хендлером
     */
    public boolean isHandled() {
        return mHandled;
    }

    /**
     * Класс для отметки маркированных спанов, которые реализуют {@link android.text.style.LeadingMarginSpan}
     */
    public static abstract class LeadingMargin extends MarkSpan {

    }

    /**
     * Span для стиля текста
     */
    public static class TypefaceStyle extends MarkSpan {

        private final int mStyle;

        public TypefaceStyle(int style) {
            mStyle = style;
        }

        @NonNull
        @Override
        public Object getRealSpan() {
            return new StyleSpan(mStyle);
        }
    }

    /**
     * Span для моно шрифта.
     */
    public static final class Mono extends MarkSpan {

        @NonNull
        private final Typeface mTypeface;

        public Mono(@NonNull Context context) {
            final Typeface typeface = TypefaceManager.getRobotoMonoRegularFont(context);
            mTypeface = typeface != null ? typeface : Typeface.MONOSPACE;
        }

        @NonNull
        @Override
        public Object getRealSpan() {
            return new CustomTypefaceSpan(mTypeface);
        }
    }

    /**
     * Span для жирного текста
     */
    public static final class Bold extends TypefaceStyle {

        public Bold() {
            super(Typeface.BOLD);
        }
    }

    /**
     * Span для курсива
     */
    public static final class Italic extends MarkSpan {

        @Nullable
        private final Typeface mTypeface;

        public Italic(@NonNull Context context) {
            mTypeface = TypefaceManager.getRobotoItalicFont(context);
        }

        @NonNull
        @Override
        public Object getRealSpan() {
            if (mTypeface != null) {
                return new CustomTypefaceSpan(mTypeface);
            } else {
                return new StyleSpan(Typeface.ITALIC);
            }
        }
    }

    /**
     * Span для подчеркивания
     */
    public static final class Underline extends MarkSpan {
        @NonNull
        @Override
        public Object getRealSpan() {
            return new UnderlineSpan();
        }
    }

    /**
     * Span для зачеркивания
     */
    public static final class Strikethrough extends MarkSpan {
        @NonNull
        @Override
        public Object getRealSpan() {
            return new StrikethroughSpan();
        }
    }

    /**
     * Span для установки цвета текста
     */
    public static final class ForegroundColor extends MarkSpan {

        @ColorInt
        private final int mColor;

        public ForegroundColor(@ColorInt int color) {
            mColor = color;
        }

        @NonNull
        @Override
        public Object getRealSpan() {
            return new ForegroundColorSpan(mColor);
        }
    }

    /**
     * Span для установки фона у текста
     */
    public static final class BackgroundColor extends MarkSpan {

        @ColorInt
        private final int mColor;
        private final float mCornerRadius;

        public BackgroundColor(@ColorInt int color, float cornerRadius) {
            mColor = color;
            mCornerRadius = cornerRadius;
        }

        public BackgroundColor(@NonNull Context context, @ColorInt int color) {
            this(color, context.getResources().getDimension(R.dimen.richtext_background_corner_radius));
        }

        @NonNull
        @Override
        public Object getRealSpan() {
            return new TextBackgroundColorSpan(mColor, mCornerRadius);
        }
    }

    /**
     * Span для изменения размера текста
     */
    public static final class FontSize extends MarkSpan {

        private final int mSize;

        public FontSize(int size) {
            mSize = size;
        }

        public int getSize() {
            return mSize;
        }

        @NonNull
        @Override
        public Object getRealSpan() {
            return new AbsoluteSizeSpan(mSize);
        }
    }

    /**
     * Span для установки стиля ссылки
     */
    public static class Url extends MarkSpan {

        @NonNull
        private final String mHref;
        @NonNull
        private final DecoratedLinkOpener mLinkOpener;

        public Url(@NonNull String href, @NonNull DecoratedLinkOpener linkOpener) {
            mHref = href;
            mLinkOpener = linkOpener;
        }

        @NonNull
        public String getHref() {
            return mHref;
        }

        @NonNull
        @Override
        public URLSpan getRealSpan() {
            return new LinkUrlSpan(mHref, mLinkOpener);
        }
    }

    /**
     * Span для установки точки в начале абзаца
     */
    public static final class Bullet extends LeadingMargin {

        private static final int LEADING_MARGIN = 25;

        @NonNull
        private final BulletSpanStyle mStyle;

        public Bullet(@NonNull BulletSpanStyle style) {
            mStyle = style;
        }

        @NonNull
        @Override
        public Object getRealSpan() {
            return new CustomBulletSpan(LEADING_MARGIN, mStyle);
        }

        @Override
        public boolean isSupportEmptyContent() {
            return true;
        }
    }

    /**
     * Span для установки цифры в начале абзаца
     */
    public static final class Number extends LeadingMargin {

        private static final int LEADING_MARGIN = 15;

        @NonNull
        private final Context mContext;
        private final int mNumber;
        @Nullable
        private final NumberSpanConfiguration mConfiguration;
        @NonNull
        private final NumberSpanStyle mStyle;

        public Number(@NonNull Context context, int number, @NonNull NumberSpanStyle style) {
            this(context, number, null, style);
        }

        public Number(@NonNull Context context, int number,
                      @Nullable NumberSpanConfiguration configuration, @NonNull NumberSpanStyle style) {
            mContext = context;
            mNumber = number;
            mConfiguration = configuration;
            mStyle = style;
        }

        @Override
        public boolean isSupportEmptyContent() {
            return true;
        }

        @NonNull
        @Override
        public Object getRealSpan() {
            if (mConfiguration != null) {
                return new NumberSpan(mContext, LEADING_MARGIN, mNumber, mConfiguration.getTextSize(), mStyle);
            } else {
                return new NumberSpan(mContext, LEADING_MARGIN, mNumber, mStyle);
            }
        }
    }

    /**
     * Span для установки стиля цитаты
     */
    public static final class BlockQuote extends LeadingMargin {

        private static final int LEADING_MARGIN = 25;

        private final float mLineWidth;
        @ColorInt
        private final int mLineColor;
        @Nullable
        private final BlockQuoteData mData;

        public BlockQuote(float lineWidth, @ColorInt int lineColor) {
            this(lineWidth, lineColor, null);
        }

        public BlockQuote(float lineWidth, @ColorInt int lineColor, @Nullable BlockQuoteData data) {
            mLineWidth = lineWidth;
            mLineColor = lineColor;
            mData = data;
        }

        @NonNull
        @Override
        public Object getRealSpan() {
            return new BlockQuoteSpan(LEADING_MARGIN, mLineWidth, mLineColor, mData);
        }
    }

    public static final class Paragraph extends MarkSpan implements ParagraphStyle {

        @NonNull
        @Override
        public Object getRealSpan() {
            return new Paragraph();
        }
    }

    /**
     * Span для маркировки тегов h в тексте и дальнейшего их поиска по идентификатору.
     */
    public static final class Header extends MarkSpan {

        @Nullable
        private final String mId;

        public Header(@Nullable String id) {
            mId = id;
        }

        @Nullable
        public String getId() {
            return mId;
        }

        @NonNull
        @Override
        public Object getRealSpan() {
            return new Header(mId);
        }
    }
}
