package ru.tensor.sbis.richtext.span.decoratedlink;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;

import java.util.Objects;

import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview;
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType;

/**
 * Вью модель с данными для декорации ссылки
 *
 * @author am.boldinov
 */
@SuppressWarnings("rawtypes")
public class DecoratedLinkData {

    @NonNull
    private final DraweeHolder mDraweeHolder;
    @NonNull
    private final String mTitle;
    @Nullable
    private final String mSubtitle;
    @Nullable
    private final String mDetails;
    @Nullable
    private final String mAdditionalInfo;
    @NonNull
    private final UrlType mUrlType;
    @Px
    private final int mImageWidth;
    @Px
    private final int mImageHeight;
    @NonNull
    private final LinkPreview mLinkPreview;
    private final boolean mIsFullyLoaded;

    /**
     * TODO документация
     * Создает {@link DecoratedLinkData} по параметрам
     *
     * @param linkPreview   модель с данными по ссылке
     * @param draweeHolder  холдер для рендера изображения
     * @param title         заголовок ссылки
     * @param subtitle      подзаголовок ссылки
     * @param imageWidth    ширина изображения
     * @param imageHeight   высота изображения
     * @param isFullyLoaded загружена ли инофрмация о декорировании полностью
     */
    @SuppressWarnings("WeakerAccess")
    public DecoratedLinkData(@NonNull LinkPreview linkPreview, @NonNull DraweeHolder draweeHolder,
                             @NonNull String title, @Nullable String subtitle,
                             @Nullable String details, @Nullable String additionalInfo,
                             @NonNull UrlType urlType, int imageWidth, int imageHeight, boolean isFullyLoaded) {
        mDraweeHolder = draweeHolder;
        mTitle = title;
        mSubtitle = subtitle;
        mDetails = details;
        mAdditionalInfo = additionalInfo;
        mUrlType = urlType;
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
        mIsFullyLoaded = isFullyLoaded;
        mLinkPreview = linkPreview;
    }

    /**
     * Возвращает подзаголовок ссылки
     */
    @Nullable
    public String getSubtitle() {
        return mSubtitle;
    }

    /**
     * Возвращает заголовок ссылки
     */
    @NonNull
    public String getTitle() {
        return mTitle;
    }

    /**
     * Возвращает детали содерижмого ссылки
     */
    @Nullable
    public String getDetails() {
        return mDetails;
    }

    /**
     * Возвращает дополнительную информацию для отображения в ссылке, к примеру дата документа.
     */
    @Nullable
    public String getAdditionalInfo() {
        return mAdditionalInfo;
    }

    /**
     * Возвращает тип декорированной ссылки
     */
    @NonNull
    public UrlType getUrlType() {
        return mUrlType;
    }

    /**
     * Возвращает DraweeHolder для рендера изображения
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public DraweeHolder getDraweeHolder() {
        return mDraweeHolder;
    }

    /**
     * Возвращает наличие изображения в ссылке
     */
    public boolean hasImage() {
        return mDraweeHolder.getController() != null;
    }

    /**
     * Возвращает ссылку на источник
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public String getSourceUrl() {
        return mLinkPreview.getHref();
    }

    /**
     * Возвращает ширину изображения
     */
    @SuppressWarnings("WeakerAccess")
    public int getImageWidth() {
        return mImageWidth;
    }

    /**
     * Возвращает высоту изображения
     */
    @SuppressWarnings("WeakerAccess")
    public int getImageHeight() {
        return mImageHeight;
    }

    /**
     * Возвращает тип документа ссылки
     */
    @NonNull
    public DocType getDocType() {
        return mLinkPreview.getDocType();
    }

    /**
     * Загружена ли инофрмация о декорировании полностью
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isFullyLoaded() {
        return mIsFullyLoaded;
    }

    /**
     * Возвращает модель превью контроллера, необходима для открытия ссылок
     */
    @NonNull
    public LinkPreview getLinkPreview() {
        return mLinkPreview;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DecoratedLinkData that = (DecoratedLinkData) o;

        if (mImageWidth != that.mImageWidth) return false;
        if (mImageHeight != that.mImageHeight) return false;
        if (!compareDraweeHolders(mDraweeHolder, that.mDraweeHolder)) return false;
        if (!mTitle.equals(that.mTitle)) return false;
        if (!Objects.equals(mSubtitle, that.mSubtitle)) return false;
        if (!Objects.equals(mDetails, that.mDetails)) return false;
        if (!Objects.equals(mAdditionalInfo, that.mAdditionalInfo)) return false;
        if (mUrlType != that.mUrlType) return false;
        if (!getSourceUrl().equals(that.getSourceUrl())) return false;
        if (mIsFullyLoaded != that.mIsFullyLoaded) return false;
        return getDocType() == that.getDocType();
    }

    @Override
    public int hashCode() {
        int result = mTitle.hashCode();
        result = 31 * result + (mSubtitle != null ? mSubtitle.hashCode() : 0);
        result = 31 * result + (mDetails != null ? mDetails.hashCode() : 0);
        result = 31 * result + (mAdditionalInfo != null ? mAdditionalInfo.hashCode() : 0);
        result = 31 * result + mUrlType.hashCode();
        result = 31 * result + mImageWidth;
        result = 31 * result + mImageHeight;
        result = 31 * result + (mIsFullyLoaded ? 1 : 0);
        result = 31 * result + mLinkPreview.hashCode();
        return result;
    }

    private boolean compareDraweeHolders(@NonNull DraweeHolder holder1, @NonNull DraweeHolder holder2) {
        final DraweeController c1 = holder1.getController();
        final DraweeController c2 = holder2.getController();
        if (c1 == c2) {
            return true;
        }
        if (c1 != null && c2 != null) {
            return c1.isSameImageRequest(c2);
        }
        return false;
    }
}
