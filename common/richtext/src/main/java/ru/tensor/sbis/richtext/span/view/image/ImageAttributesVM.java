package ru.tensor.sbis.richtext.span.view.image;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import ru.tensor.sbis.richtext.span.view.BaseAttributesVM;
import ru.tensor.sbis.richtext.span.view.ViewSizeType;
import ru.tensor.sbis.richtext.view.RichViewLayout;
import ru.tensor.sbis.richtext.view.ViewTemplate;

/**
 * Вью-модель атрибутов обтекаемого изображения в тексте
 *
 * @author am.boldinov
 */
public final class ImageAttributesVM extends BaseAttributesVM {

    @Px
    private int mWidth;
    @Px
    private int mHeight;
    @Px
    private final int mInitialWidth;
    @Px
    private final int mInitialHeight;
    @Nullable
    private final ViewTemplate mTemplate;
    @Nullable
    private ImageStyle mStyle;
    @Nullable
    private String mPreviewUrl;
    @Nullable
    private String mUuid;
    private boolean mSingleImageInText;

    public ImageAttributesVM(@NonNull String tag, @Px int width, @Px int height, @Nullable ViewTemplate template) {
        super(tag);
        mWidth = width;
        mHeight = height;
        mInitialWidth = width;
        mInitialHeight = height;
        mTemplate = template;
    }

    /**
     * Возвращает ссылку на превью изображения
     */
    @Nullable
    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    /**
     * Устанавливает ссылку на превью изображения
     */
    public void setPreviewUrl(@Nullable String previewUrl) {
        mPreviewUrl = previewUrl;
    }

    /**
     * Возвращает идентификатор изображения
     */
    @Nullable
    public String getUuid() {
        return mUuid;
    }

    /**
     * Устанавливает идентификатор изображения
     */
    public void setUuid(@Nullable String uuid) {
        mUuid = uuid;
    }

    /**
     * Возвращает ширину изображения для отрисовки
     */
    @Px
    public int getWidth() {
        return mWidth;
    }

    /**
     * Возвращает высоту изображения для отрисовки
     */
    @Px
    public int getHeight() {
        return mHeight;
    }

    /**
     * Возвращает оригинальную ширину изображения
     */
    @Px
    public int getInitialWidth() {
        return mInitialWidth;
    }

    /**
     * Возвращает оригинальную высоту изображения
     */
    @Px
    public int getInitialHeight() {
        return mInitialHeight;
    }

    /**
     * Изменяет соотношение сторон (высоту и ширину для отрисовки) на основе переданной ширины
     */
    public void applyWidthRatio(@Px int width) {
        final float factor = (float) width / mWidth;
        mHeight = Math.round(mHeight * factor);
        mWidth = width;
    }

    /**
     * Изменяет соотношение сторон (высоту и ширину для отрисовки) на основе переданной высоты
     */
    public void applyHeightRatio(@Px int height) {
        final float factor = (float) height / mHeight;
        mWidth = Math.round(mWidth * factor);
        mHeight = height;
    }

    /**
     * Изменяет соотношение сторон (высоту и ширину для отрисовки) на основе стилей изображения
     * и максимально возможных размеров
     */
    public void applyStyleRatio(@Px int maxWidth, @Px int maxHeight) {
        if (mStyle != null) {
            final int desiredWidth;
            switch (mStyle.getWidthType()) {
                case PERCENT:
                    desiredWidth = Math.round(((float) mStyle.getWidth() / 100) * maxWidth);
                    break;
                case PIXEL:
                    desiredWidth = Math.min(mStyle.getWidth(), maxWidth);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown image style");
            }
            applyWidthRatio(desiredWidth);
        } else if (getInitialWidth() > maxWidth) {
            applyWidthRatio(maxWidth);
        }
        if (mHeight > maxHeight) {
            applyHeightRatio(maxHeight);
        }
    }

    /**
     * Возвращает стиль обтекаемого изображения
     */
    @Nullable
    public ImageStyle getStyle() {
        return mStyle;
    }

    /**
     * Устанавливает стиль обтекаемого изображения
     */
    public void setStyle(@Nullable ImageStyle style) {
        mStyle = style;
    }

    /**
     * Возвращает соотношение сторон изображения
     */
    public float getRatio() {
        return (float) getWidth() / getHeight();
    }

    /**
     * Является ли изображение кандидатом для включения в коллекцию изображений
     */
    public boolean isCollectionCandidate() {
        // Можно включать в коллецию:
        // Если изображение не является иконкой
        // Если без обтекания справа
        // Если изображение не отцентрировано
        // Если отцентрировано и его ширина в процентах меньше 100
        return mTemplate != ViewTemplate.INLINE_SIZE && mTemplate != ViewTemplate.RIGHT
                && (mTemplate != ViewTemplate.CENTER || isPercentStyle(mStyle) && mStyle.getWidth() < 100);
    }

    /**
     * Сравнивает вью модели для определения возможности присутствовать в одной коллекции.
     */
    public boolean isMatchToCollection(ImageAttributesVM other) {
        if (mTemplate == ViewTemplate.CENTER) {
            return false; // Если в коллекции лежит отцентрированный, то остальные к нему не добавляются
        }
        // В коллекции лежат изображения с одинаковыми шаблонами
        if (mTemplate == other.mTemplate) {
            return true;
        }
        // Исключительный кейс, в котором изображения могут содержаться в коллекции
        return mTemplate == ViewTemplate.LEFT && other.mTemplate == ViewTemplate.CENTER
                && isPercentStyle(mStyle) && isPercentStyle(other.mStyle)
                && mStyle.getWidth() + other.mStyle.getWidth() <= 100;
    }

    @NonNull
    @Override
    public ViewTemplate getTemplate() {
        return mTemplate == null ? ViewTemplate.CENTER : mTemplate;
    }

    /**
     * Является ли изображение одиночным без текста
     */
    public boolean isSingleImageInText() {
        return mSingleImageInText;
    }

    /**
     * Устанавливает признак является ли изображение одиночным без текста
     */
    public void setSingleImageInText(boolean singleImageInText) {
        mSingleImageInText = singleImageInText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ImageAttributesVM that = (ImageAttributesVM) o;
        return mWidth == that.mWidth &&
                mHeight == that.mHeight &&
                mTemplate == that.mTemplate &&
                Objects.equals(mStyle, that.mStyle) &&
                Objects.equals(mPreviewUrl, that.mPreviewUrl) &&
                Objects.equals(mUuid, that.mUuid);
    }


    @NonNull
    @Override
    public RichViewLayout.ViewHolderFactory createViewHolderFactory() {
        return (parent) -> {
            final RichImageView view = new RichImageView(parent.getContext());
            view.setLayoutParams(new RichViewLayout.LayoutParams(RichViewLayout.LayoutParams.WRAP_CONTENT, RichViewLayout.LayoutParams.WRAP_CONTENT));
            return new ImageSingleViewHolder(view);
        };
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mWidth, mHeight, mTemplate, mStyle, mPreviewUrl, mUuid);
    }

    private static boolean isPercentStyle(@Nullable ImageStyle style) {
        return style != null && style.getWidthType() == ViewSizeType.PERCENT;
    }
}
