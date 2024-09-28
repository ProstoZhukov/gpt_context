package ru.tensor.sbis.richtext.converter.cfg;

import androidx.annotation.NonNull;

/**
 * Настройки рендера богатого текста
 * <p>
 * @author am.boldinov
 */
public class RenderOptions {

    private boolean mDrawLinkAsDecorated;
    private boolean mDrawWrappedImages;
    private int mParagraphLineSpacing = -1;

    /**
     * Устанавливает признак, что ссылки необходимо декорировать
     */
    @NonNull
    public RenderOptions drawLinkAsDecorated(boolean value) {
        mDrawLinkAsDecorated = value;
        return this;
    }

    /**
     * Устанавливает признак, что необходимо по умолчанию рендерить изображения, вложенные в текст
     */
    @NonNull
    public RenderOptions drawWrappedImages(boolean value) {
        mDrawWrappedImages = value;
        return this;
    }

    /**
     * Устанавливает расстояние между параграфами
     */
    @NonNull
    public RenderOptions paragraphLineSpacing(int size) {
        mParagraphLineSpacing = size;
        return this;
    }

    /**
     * Возвращает признак о необходимости декорации ссылок
     */
    public boolean isDrawLinkAsDecorated() {
        return mDrawLinkAsDecorated;
    }

    /**
     * Возвращает признак о необходимости рендерить изображения, вложенные в текст
     */
    public boolean isDrawWrappedImages() {
        return mDrawWrappedImages;
    }

    /**
     * Возвращает расстояние между параграфами
     */
    public int getParagraphLineSpacing() {
        return mParagraphLineSpacing;
    }
}
