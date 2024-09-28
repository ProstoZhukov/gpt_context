package ru.tensor.sbis.richtext.span.view.image;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.span.view.ViewSizeType;

/**
 * Стиль обтекаемого изображения в тексте
 *
 * @author am.boldinov
 */
public final class ImageStyle {

    private final int mWidth;
    @NonNull
    private final ViewSizeType mWidthType;

    public ImageStyle(int width, @NonNull ViewSizeType widthType) {
        mWidth = width;
        mWidthType = widthType;
    }

    /**
     * Возвращает тип ширины изображения
     */
    @NonNull
    public ViewSizeType getWidthType() {
        return mWidthType;
    }

    /**
     * Возвращает ширину изображения
     */
    public int getWidth() {
        return mWidth;
    }
}
