package ru.tensor.sbis.common_views.sbisview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;
import ru.tensor.sbis.common_views.R;
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData;
import ru.tensor.sbis.design.sbis_text_view.SbisTextView;
import ru.tensor.sbis.person_decl.profile.model.SbisPersonViewData;
import ru.tensor.sbis.person_decl.profile.model.SbisPersonViewInitialsStubData;
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus;
import ru.tensor.sbis.design.TypefaceManager;
import ru.tensor.sbis.design.profile_decl.person.CompanyData;
import ru.tensor.sbis.design.profile_decl.person.DepartmentData;
import ru.tensor.sbis.design.profile_decl.person.GroupData;
import ru.tensor.sbis.design.profile_decl.person.PersonData;
import ru.tensor.sbis.design.profile_decl.person.PhotoData;
import ru.tensor.sbis.design.profile.personcollage.PersonCollageView;
import ru.tensor.sbis.design.profile.titleview.TitleTextView;
import ru.tensor.sbis.fresco_view.SuperEllipseDraweeView;
import timber.log.Timber;

/**
 * Реализация компонента содержимого тулбара: заголовок, подзаголовок, картинка (опционально).
 * При использовании в верстке указывается один из следующих типов изображения
 * в параметре {@link ru.tensor.sbis.design.text_span.R.styleable#SbisTitleView_imageViewType}:
 * {@link #IMAGE_VIEW_TYPE_NONE} - изображение отсутствует
 * {@link #IMAGE_VIEW_TYPE_SUPERELLIPSE} - изображение в формате суперэллипса
 * {@link #IMAGE_VIEW_TYPE_PERSONS_PHOTO_COLLECTION} - коллекция фото персон (по умолчанию)
 *
 * @deprecated удалить после миграции по поручению https://online.sbis.ru/opendoc.html?guid=44374009-2b8c-4390-bc6c-d4b6854c7e77
 */
@Deprecated
public class SbisTitleView extends FrameLayout {

    private static final int IMAGE_VIEW_TYPE_NONE = 0;
    private static final int IMAGE_VIEW_TYPE_SUPERELLIPSE = 1;
    private static final int IMAGE_VIEW_TYPE_PERSONS_PHOTO_COLLECTION = 2;

    private int imageViewType = IMAGE_VIEW_TYPE_PERSONS_PHOTO_COLLECTION;

    @NonNull
    private final TitleTextView mTitleTextView;
    @NonNull
    private final SbisTextView mSubTitleTextView;
    @NonNull
    private final View mImageView;
    @StyleRes
    private int mTitleStyle;

    @StyleRes
    private int defaultStandardTitleStyle;
    @StyleRes
    private int defaultDocumentTitleStyle;
    @StyleRes
    private int documentSubtitleStyle;
    @StyleRes
    private int defaultTitleWithSubtitleStyle;
    @StyleRes
    private int subtitleStyle;
    @ColorInt
    private int superEllipseBackgroundColor;


    public SbisTitleView(@NonNull Context context) {
        this(context, null);
    }

    public SbisTitleView(@NonNull Context context,
                         @Nullable AttributeSet attributeSet,
                         int defStyleAttr) {
        this(context, attributeSet);
    }

    public SbisTitleView(@NonNull Context context,
                         @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, ru.tensor.sbis.design.text_span.R.attr.SbisTitleView_theme, ru.tensor.sbis.design.toolbar.R.style.SbisTitleViewTheme);
    }

    public SbisTitleView(@NonNull Context context,
                         @Nullable AttributeSet attributeSet,
                         int defStyleAttr,
                         int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);

        boolean invertedBackground = true;

        if (attributeSet != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(
                    attributeSet,
                    ru.tensor.sbis.design.text_span.R.styleable.SbisTitleView,
                    defStyleAttr,
                    defStyleRes);

            try {
                invertedBackground = typedArray.getBoolean(ru.tensor.sbis.design.text_span.R.styleable.SbisTitleView_titleInvertedBackground, true);
                imageViewType = typedArray.getInt(ru.tensor.sbis.design.text_span.R.styleable.SbisTitleView_imageViewType, imageViewType);
                defaultStandardTitleStyle = typedArray.getResourceId(ru.tensor.sbis.design.text_span.R.styleable.SbisTitleView_SbisTitleView_standardTitleStyle, ru.tensor.sbis.design.R.style.StandardTitle);
                defaultDocumentTitleStyle = typedArray.getResourceId(ru.tensor.sbis.design.text_span.R.styleable.SbisTitleView_SbisTitleView_documentTitleStyle, ru.tensor.sbis.design.R.style.DocumentTitle);
                documentSubtitleStyle = typedArray.getResourceId(ru.tensor.sbis.design.text_span.R.styleable.SbisTitleView_SbisTitleView_documentSubtitleStyle, ru.tensor.sbis.design.R.style.DocumentSubtitle);
                defaultTitleWithSubtitleStyle = typedArray.getResourceId(ru.tensor.sbis.design.text_span.R.styleable.SbisTitleView_SbisTitleView_titleWithSubtitleStyle, ru.tensor.sbis.design.toolbar.R.style.ToolbarTitleWithSubtitleText);
                subtitleStyle = typedArray.getResourceId(ru.tensor.sbis.design.text_span.R.styleable.SbisTitleView_SbisTitleView_subtitleStyle, ru.tensor.sbis.design.toolbar.R.style.ToolbarTitleText);
                superEllipseBackgroundColor = typedArray.getColor(ru.tensor.sbis.design.text_span.R.styleable.SbisTitleView_SbisTitleView_superEllipseBackgroundColor, Color.TRANSPARENT);
            } finally {
                typedArray.recycle();
            }
        }

        ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(ru.tensor.sbis.design.toolbar.R.layout.toolbar_sbis_title_layout, this, true);

        mTitleTextView = findViewById(ru.tensor.sbis.design.toolbar.R.id.toolbar_view_sbis_title);
        configureTitleTextView(null, defaultStandardTitleStyle, false);
        mSubTitleTextView = findViewById(ru.tensor.sbis.design.toolbar.R.id.toolbar_view_sbis_sub_title);

        mImageView = inflateImageView(invertedBackground);
    }

    @NonNull
    private View inflateImageView(boolean invertedBackground) {
        switch (imageViewType) {
            case IMAGE_VIEW_TYPE_NONE: {
                return findViewById(ru.tensor.sbis.design.toolbar.R.id.toolbar_view_image_stub);
            }
            case IMAGE_VIEW_TYPE_SUPERELLIPSE: {
                ViewStub iconStub = findViewById(ru.tensor.sbis.design.toolbar.R.id.toolbar_view_image_stub);
                iconStub.setLayoutResource(R.layout.sbis_title_superellipse_image_view);
                SuperEllipseDraweeView view = (SuperEllipseDraweeView) iconStub.inflate();
                view.setShapeBackgroundColor(superEllipseBackgroundColor);
                return view;
            }
            case IMAGE_VIEW_TYPE_PERSONS_PHOTO_COLLECTION: {
                ViewStub iconStub = findViewById(ru.tensor.sbis.design.toolbar.R.id.toolbar_view_image_stub);
                if (invertedBackground) {
                    ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(
                            getContext(), ru.tensor.sbis.design.profile.R.style.DesignProfileActivityStatusStyleBlueTheme);
                    iconStub.setLayoutInflater(LayoutInflater.from(contextThemeWrapper));
                }
                iconStub.setLayoutResource(R.layout.common_views_sbis_title_person_collage_view);
                return iconStub.inflate();
            }
            default:
                throw new IllegalArgumentException("Unknown image view type " + imageViewType);
        }
    }

    /**
     * Включить отображение статуса активности персоны.
     */
    public void setHasActivityStatus() {
        if (imageViewType == IMAGE_VIEW_TYPE_PERSONS_PHOTO_COLLECTION) {
            ((PersonCollageView) mImageView).setHasActivityStatus(true, false);
        } else {
            throw new IllegalArgumentException("Illegal image view type " + imageViewType);
        }
    }

    @SuppressLint("ResourceType")
    private void configureTitleTextView(@Nullable String titleText, @StyleRes int titleStyle, boolean hasSubtitle) {
        mTitleTextView.setSimpleTitle(titleText);
        float textSize = getResources().getDimension(chooseTitleTextSize(hasSubtitle));
        float smallerTextSize = getResources().getDimension(chooseSmallerTitleTextSize(hasSubtitle));
        mTitleTextView.setDefaultTextSize(textSize);
        mTitleTextView.setSmallerNamesTextSize(smallerTextSize);
        Context context = mTitleTextView.getContext();
        mTitleTextView.setTextAppearance(context, titleStyle > 0 ? titleStyle : defaultStandardTitleStyle);
        mTitleTextView.setTypeface(Objects.requireNonNull(TypefaceManager.getRobotoRegularFont(context)));
    }

    private void configureSubtitleTextView(@Nullable String subTitleText, @StyleRes int subTitleStyle) {
        if (!TextUtils.isEmpty(subTitleText)) {
            mSubTitleTextView.setVisibility(VISIBLE);
            mSubTitleTextView.setText(subTitleText);
            mSubTitleTextView.setTextAppearance(subTitleStyle);
            Context context = mSubTitleTextView.getContext();
            mSubTitleTextView.getPaint().setTypeface(TypefaceManager.getRobotoRegularFont(context));
        } else {
            mSubTitleTextView.setVisibility(GONE);
        }
    }

    private void hideImageView() {
        mImageView.setVisibility(GONE);
    }

    /**
     * @param viewData данные о персонах для отображения
     * @return удалось ли установить во вью изображения данные о персонах
     */
    private boolean configurePhotoCollectionView(@Nullable ArrayList<SbisPersonViewData> viewData) {
        if (imageViewType == IMAGE_VIEW_TYPE_PERSONS_PHOTO_COLLECTION) {
            if (viewData != null) {
                mImageView.setVisibility(VISIBLE);
                ((PersonCollageView) mImageView).setDataList(toPhotoDataList(viewData));
                if (viewData.size() == 1) {
                    ActivityStatus singleActivityStatus = viewData.get(0).activityStatus;
                    if (singleActivityStatus != null) {
                        ((PersonCollageView) mImageView).setHasActivityStatus(true, false);
                    }
                }
            } else {
                mImageView.setVisibility(GONE);
            }
            return true;
        }
        return false;
    }

    private void configureSuperEllipseImageView(@Nullable String imageUrl) {
        if (imageViewType == IMAGE_VIEW_TYPE_SUPERELLIPSE) {
            if (!TextUtils.isEmpty(imageUrl)) {
                mImageView.setVisibility(View.VISIBLE);
                ((SimpleDraweeView) mImageView).setImageURI(Uri.parse(imageUrl));
            } else {
                mImageView.setVisibility(View.GONE);
            }
        }
    }

    private void configureSuperEllipseImageView(@Nullable Drawable drawable) {
        if (imageViewType == IMAGE_VIEW_TYPE_SUPERELLIPSE) {
            if (drawable != null) {
                ((SimpleDraweeView) mImageView).getHierarchy().setPlaceholderImage(drawable);
                mImageView.setVisibility(VISIBLE);
            } else {
                mImageView.setVisibility(GONE);
            }
        }
    }

    /**
     * Установить стиль для заголовока.
     *
     * @param titleStyle стиль заголовка.
     */
    public void setTitleStyle(@StyleRes int titleStyle) {
        mTitleStyle = titleStyle;
    }

    /**
     * Показать заголовок документа.
     *
     * @param title    заголовок.
     * @param subtitle подзаголовок.
     */
    public void displayDocumentTitle(@Nullable String title, @Nullable String subtitle) {
        configureTitleTextView(title, mTitleStyle != 0 ? mTitleStyle : defaultDocumentTitleStyle, subtitle != null && !subtitle.isEmpty());
        configureSubtitleTextView(subtitle, documentSubtitleStyle);
        hideImageView();
    }

    /**
     * Показать только заголовок.
     *
     * @param titleString заголовок.
     */
    public void showOnlyString(@Nullable String titleString) {
        configureTitleTextView(titleString, mTitleStyle != 0 ? mTitleStyle : defaultStandardTitleStyle, false);
        configureSubtitleTextView(null, 0);
        hideImageView();
    }

    /**
     * Показать только заголовок.
     *
     * @param titleString заголовок.
     * @param textStyle   стиль заголовка.
     */
    public void showOnlyString(@Nullable String titleString, int textStyle) {
        configureTitleTextView(titleString, textStyle, false);
        configureSubtitleTextView(null, 0);
        hideImageView();
    }

    /**
     * Показать заголовок и подзаголовок.
     *
     * @param titleString    заголовок.
     * @param titleStyle     стиль заголовка.
     * @param subtitleString подзаголовок.
     * @param subtitleStyle  стиль подзаголовока.
     * @param viewData       список данных для отображения {@link SbisPersonViewData}.
     */
    public void showTitleAndSubtitle(@Nullable String titleString, int titleStyle, @Nullable String subtitleString, int subtitleStyle, @Nullable ArrayList<SbisPersonViewData> viewData) {
        configureTitleTextView(titleString, titleStyle, subtitleString != null && !subtitleString.isEmpty());
        configureSubtitleTextView(subtitleString, subtitleStyle);
        if (!configurePhotoCollectionView(viewData)) {
            throw new IllegalArgumentException("Error setting list of SbisPersonViewData for current image view type " + imageViewType);
        }
    }

    /**
     * Показать заголовок и подзаголовок.
     *
     * @param titleString    заголовок.
     * @param titleStyle     стиль заголовка.
     * @param subtitleString подзаголовок.
     * @param subtitleStyle  стиль подзаголовока.
     */
    public void showTitleAndSubtitle(@Nullable String titleString, int titleStyle, @Nullable String subtitleString, int subtitleStyle) {
        configureTitleTextView(titleString, titleStyle, subtitleString != null && !subtitleString.isEmpty());
        configureSubtitleTextView(subtitleString, subtitleStyle);
        hideImageView();
    }

    /**
     * Показать заголовок и подзаголовок.
     *
     * @param titleString    заголовок.
     * @param subtitleString подзаголовок.
     */
    public void showTitleAndSubtitle(@Nullable String titleString, @Nullable String subtitleString) {
        configureTitleTextView(titleString,
                mTitleStyle != 0 ? mTitleStyle : defaultTitleWithSubtitleStyle,
                subtitleString != null && !subtitleString.isEmpty());
        configureSubtitleTextView(subtitleString, subtitleStyle);
        hideImageView();
    }

    /**
     * Показать заголовок и подзаголовок в "одну линию". Выставляемые настройки:
     * "ellipsize=end"
     * "singleLine=true"
     * "maxLines=1"
     *
     * @param titleString    заголовок.
     * @param subtitleString подзаголовок.
     * */
    public void showTitleAndSubtitleEachInOneLine(@Nullable String titleString, @Nullable String subtitleString) {
        configureTitleTextView(
                titleString,
                mTitleStyle != 0 ? mTitleStyle : defaultTitleWithSubtitleStyle,
                subtitleString != null && !subtitleString.isEmpty());
        mTitleTextView.setMaxLines(1);
        mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        configureSubtitleTextView(subtitleString, subtitleStyle);
        mSubTitleTextView.setMaxLines(1);
        mSubTitleTextView.setSingleLine(true);
        mSubTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        hideImageView();
    }

    /**
     * Установить заголовок для диалога.
     *
     * @param viewData список данных для отображения {@link SbisPersonViewData}.
     * @param participantName имя получателя.
     * @param subTitle текст подзаголовка.
     * @param subtitleStyle стиль подзаголовка.
     * */
    public void setDialogTitle(@Nullable ArrayList<SbisPersonViewData> viewData, @Nullable String participantName, @Nullable String subTitle, @StyleRes int subtitleStyle) {
        mTitleTextView.setSingleParticipantName(participantName);
        configureSubtitleTextView(subTitle, subtitleStyle);
        configurePhotoCollectionView(viewData);
    }

    /**
     * Установить заголовок для диалога (с параметром "ellipsize=end").
     *
     * @param viewData список данных для отображения {@link SbisPersonViewData}.
     * @param participantName имя получателя.
     * @param subTitle текст подзаголовка.
     * @param subtitleStyle стиль подзаголовка.
     * */
    public void setDialogTitleEllipsizedTitle(@Nullable ArrayList<SbisPersonViewData> viewData, @Nullable String participantName, @Nullable String subTitle, @StyleRes int subtitleStyle) {
        mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        mTitleTextView.setSingleParticipantName(participantName);
        configureSubtitleTextView(subTitle, subtitleStyle);
        configurePhotoCollectionView(viewData);
    }

    /*** @SelfDocumented */
    public void setViolationTitle(@Nullable ArrayList<SbisPersonViewData> viewData, @Nullable String participantName, @StyleRes int titleStyle, @Nullable String subTitle, @StyleRes int subtitleStyle) {
        configureTitleTextView(participantName, titleStyle, subTitle != null && !subTitle.isEmpty());
        mTitleTextView.setMaxLines(1);
        configureSubtitleTextView(subTitle, subtitleStyle);
        configurePhotoCollectionView(viewData);
    }

    /**
     * Установить заголовок для диалога.
     *
     * @param viewData список данных для отображения {@link SbisPersonViewData}.
     * @param participantNames имя получателей.
     * @param hiddenRecipientsCount скрыть счетчик кол-ва получателей.
     * */
    public void setGroupDialogTitle(@Nullable ArrayList<SbisPersonViewData> viewData, @Nullable List<String> participantNames, int hiddenRecipientsCount) {
        mTitleTextView.setParticipantsNames(participantNames, hiddenRecipientsCount);
        configureSubtitleTextView(null, 0);
        configurePhotoCollectionView(viewData);
    }

    /**
     * Установить текст, который будет отодражен в одну строчку.
     *
     * @param viewData список данных для отображения {@link SbisPersonViewData}.
     * @param participantName имя получателя.
     * @param subTitle текст подзаголовка.
     * @param subtitleStyle стиль подзаголовка.
     * */
    public void setTitleAndSubtitleInOneLine(@Nullable ArrayList<SbisPersonViewData> viewData, @Nullable String participantName, @Nullable String subTitle, @StyleRes int subtitleStyle) {
        setDialogTitle(viewData, participantName, subTitle, subtitleStyle);
        makeSingleAndEllipsized(mTitleTextView);
        makeSingleAndEllipsized(mSubTitleTextView);
    }

    /**
     * Показать текст, который будет иметь настройки:
     * "ellipsize=end"
     * "singleLine=true"
     * "maxLines=1"
     *
     * @param titleString текст заголовка.
     * @param subTitle текст подзаголовка.
     * @param subtitleStyle стиль подзаголовка.
     * */
    public void showEllipsizedTitleAndSubtitle(@Nullable String titleString, @Nullable String subTitle, @StyleRes int subtitleStyle) {
        configureTitleTextView(
                titleString,
                mTitleStyle != 0 ? mTitleStyle : defaultTitleWithSubtitleStyle,
                subTitle != null && !subTitle.isEmpty());
        makeSingleAndEllipsized(mTitleTextView);
        configureSubtitleTextView(subTitle, subtitleStyle);
        makeSingleAndEllipsized(mSubTitleTextView);
        hideImageView();
    }

    private static void makeSingleAndEllipsized(SbisTextView textView) {
        textView.setSingleLine(true);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
    }

    private static void makeSingleAndEllipsized(TitleTextView textView) {
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
    }

    /**
     * Установка данных для тулбара по формату шапки уведомления (формат без картинки)
     *
     * @param titleString    заголовок
     * @param subtitleString подзаголовок
     */
    public void setNotificationTitle(@Nullable String titleString,
                                     @Nullable String subtitleString) {
        setNotificationTitle(titleString, subtitleString, null);
    }

    /**
     * Установка данных для тулбара по формату шапки уведомления (формат с картинкой)
     *
     * @param titleString    заголовок
     * @param subtitleString подзаголовок
     * @param imageUrl       url картинки
     */
    public void setNotificationTitle(@Nullable String titleString,
                                     @Nullable String subtitleString,
                                     @Nullable String imageUrl) {
        setNotificationTitle(titleString, subtitleString, imageUrl, null);
    }

    /**
     * Установка данных для тулбара по формату шапки уведомления (формат с картинкой)
     *
     * @param titleString      заголовок
     * @param subtitleString   подзаголовок
     * @param imageUrl         url картинки
     * @param iconScaleType    тип масштабирования картинки
     */
    public void setNotificationTitle(@Nullable String titleString,
                                     @Nullable String subtitleString,
                                     @Nullable String imageUrl,
                                     @Nullable ScalingUtils.ScaleType iconScaleType) {
        if (TextUtils.isEmpty(subtitleString)) {
            configureTitleTextView(
                    titleString,
                    mTitleStyle != 0 ? mTitleStyle : ru.tensor.sbis.design.toolbar.R.style.ToolbarTitleText,
                    subtitleString != null && !subtitleString.isEmpty());
            mTitleTextView.setMaxLines(2);
        } else {
            configureTitleTextView(
                    titleString,
                    ru.tensor.sbis.design.toolbar.R.style.ToolbarTitleWithSubtitleText,
                    subtitleString != null && !subtitleString.isEmpty());
            mTitleTextView.setMaxLines(1);
        }
        mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        configureSubtitleTextView(subtitleString, ru.tensor.sbis.design.toolbar.R.style.ToolbarSubtitle);
        mSubTitleTextView.setMaxLines(1);
        mSubTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        boolean drawableImage = false;
        if (imageUrl != null) {
            final Uri uri = Uri.parse(imageUrl);
            if (UriUtil.isLocalResourceUri(uri) && uri.getPath() != null) {
                try {
                    final Drawable drawable = ContextCompat.getDrawable(getContext(), Integer.parseInt(uri.getPath().substring(1)));
                    configureSuperEllipseImageView(drawable);
                    drawableImage = true;
                } catch (NumberFormatException e) {
                    Timber.e(e);
                }
            }
        }
        if (!drawableImage) {
            configureSuperEllipseImageView(imageUrl);
            if (imageViewType == IMAGE_VIEW_TYPE_SUPERELLIPSE && iconScaleType != null) {
                ((SimpleDraweeView) mImageView).getHierarchy().setActualImageScaleType(iconScaleType);
            }
        }
    }

    /**
     * Установить заголовок для диалога. (с задержкой)
     *
     * @param viewData список данных для отображения {@link SbisPersonViewData}.
     * @param participantNames имя получателей.
     * @param hiddenRecipientsCount скрыть счетчик кол-ва получателей.
     * */
    public void setGroupDialogTitleDelayed(@Nullable ArrayList<SbisPersonViewData> viewData, @Nullable List<String> participantNames, int hiddenRecipientsCount) {
        if (participantNames == null) {
            return;
        }

        final long delayBeforeCollageUpdate = 100L;
        // Для задержки обновления списка участников пока коллаж загружается, для эффекта одновременного обновления. Фикс https://online.sbis.ru/opendoc.html?guid=2363fd1b-476f-4cf5-a74b-2c85d419af44
        mTitleTextView.postDelayed(() -> mTitleTextView.setParticipantsNames(participantNames, hiddenRecipientsCount), delayBeforeCollageUpdate);
        configureSubtitleTextView(null, 0);
        configurePhotoCollectionView(viewData);
    }

    /**
     * Установка данных для тулбара по формату шапки новости
     *
     * @param imageUrl url картинки
     * @param title    заголовок
     * @param subTitle подзаголовок
     */
    public void setNewsTitle(@Nullable String imageUrl, @NonNull String title, @Nullable String subTitle) {
        configureTitleTextView(
                title,
                mTitleStyle != 0 ? mTitleStyle : ru.tensor.sbis.design.R.style.StandardTitle_Large,
                subTitle != null && !subTitle.isEmpty());
        mTitleTextView.setMaxLines(1);
        mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        configureSubtitleTextView(subTitle, ru.tensor.sbis.design.toolbar.R.style.ToolbarSubtitle);
        mSubTitleTextView.setMaxLines(1);
        mSubTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        configureSuperEllipseImageView(imageUrl);
    }

    /**
     * Установка данных для тулбара по формату шапки новости
     *
     * @param imageUrl url картинки
     * @param participantNames список имен получателей
     * @param hiddenRecipientsCount счетчик скрытых получателей
     * @param subTitle подзаголовок
     * @param counterTextRes ресурс шаблона для отображения счетчика
     */
    public void setNewsTitle(
            @Nullable String imageUrl,
            @NonNull List<String> participantNames,
            int hiddenRecipientsCount,
            @Nullable String subTitle,
            @Nullable Integer counterTextRes
    ) {
        if (counterTextRes != null) {
            mTitleTextView.setCounterTextRes(counterTextRes);
        }
        configureTitleTextView(
                null,
                mTitleStyle != 0 ? mTitleStyle : ru.tensor.sbis.design.R.style.StandardTitle_Large,
                subTitle != null && !subTitle.isEmpty());
        if (subTitle != null) {
            mTitleTextView.setSupportNamesFullWidth(true);
            mTitleTextView.setParticipantsNamesSingleLine(participantNames, hiddenRecipientsCount, true);
        } else {
            mTitleTextView.setParticipantsNames(participantNames, hiddenRecipientsCount);
        }
        configureSubtitleTextView(subTitle, ru.tensor.sbis.design.toolbar.R.style.ToolbarSubtitle);
        mSubTitleTextView.setMaxLines(1);
        mSubTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        configureSuperEllipseImageView(imageUrl);
    }

    /**
     * Установка цвета заглавного текста.
     *
     * @param color идентификатор ресурса цвета.
     * */
    public void setTitleTextColor(@ColorInt int color) {
        mTitleTextView.setTextColor(color);
    }

    /**
     * Установка размера текста подстроки.
     *
     * @param sizeRes идентификатор ресурса размера.
     * */
    public void setTitleTextSizeRes(@DimenRes int sizeRes) {
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(sizeRes));
    }

    /**
     * Установка цвета текста подстроки.
     *
     * @param color идентификатор ресурса цвета.
     * */
    public void setSubTitleTextColor(@ColorInt int color) {
        mSubTitleTextView.setTextColor(color);
        mSubTitleTextView.setAlpha(Color.alpha(color) / 255f);
    }

    /**
     * Установка заголовка для экрана "настройки чата".
     *
     * @param title заголовок.
     * @param subTitle подзаголовок.
     * */
    public void setChatSettingsTitle(@Nullable String title, @Nullable String subTitle) {
        configureTitleTextView(
                title,
                mTitleStyle != 0 ? mTitleStyle : ru.tensor.sbis.design.R.style.StandardTitle_Large,
                subTitle != null && !subTitle.isEmpty());
        configureSubtitleTextView(subTitle, ru.tensor.sbis.design.toolbar.R.style.ToolbarSubtitle);
        // установка однострочности в ресурсах не работает, пришлось в коде
        mSubTitleTextView.setSingleLine(true);
        mSubTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        if (imageViewType == IMAGE_VIEW_TYPE_PERSONS_PHOTO_COLLECTION && l != null) {
            mImageView.setOnClickListener(view -> l.onClick(this));
        }
    }

    /**
     * Установка слушателя нажатия на заголовок
     *
     * @param listener слушатель
     */
    public void setTitleOnClickListener(@Nullable OnClickListener listener) {
        mTitleTextView.setOnClickListener(listener);
    }

    /**
     * @return вью заголовка
     */
    @NonNull
    public TitleTextView getTitleTextView() {
        return mTitleTextView;
    }

    @NonNull
    public View getImageView() {
        return mImageView;
    }

    @NonNull
    private List<PhotoData> toPhotoDataList(@NonNull List<SbisPersonViewData> sbisPersonViewData) {
        ArrayList<PhotoData> photoDataList = new ArrayList<>(sbisPersonViewData.size());
        PhotoData photoData;
        for (SbisPersonViewData data : sbisPersonViewData) {
            switch (data.photoDataType) {
                case DEPARTMENT:
                    photoData = new DepartmentData(data.personUuid, Collections.singletonList(
                            new PersonData(data.personUuid, data.photoUrl,
                                    toInitialsStubData(data.initialsStubData))));
                    break;
                case COMPANY:
                    photoData = new CompanyData(data.personUuid, data.photoUrl);
                    break;
                case GROUP:
                    photoData = new GroupData(data.personUuid, data.photoUrl);
                    break;
                case PERSON:
                default:
                    photoData = new PersonData(data.personUuid, data.photoUrl,
                            toInitialsStubData(data.initialsStubData));
            }
            photoDataList.add(photoData);
        }
        return photoDataList;
    }

    @Nullable
    private InitialsStubData toInitialsStubData(@Nullable SbisPersonViewInitialsStubData sbisPersonViewInitialsStubData) {
        if (sbisPersonViewInitialsStubData == null) {
            return null;
        }
        return new InitialsStubData(
                sbisPersonViewInitialsStubData.getInitials(),
                sbisPersonViewInitialsStubData.getInitialsBackgroundColor(),
                sbisPersonViewInitialsStubData.getInitialsBackgroundColorRes()
        );
    }

    /**
     * Получить размер для заголовка, когда он помещается на одной строке:
     * - есть подзаголовок - *MEDIUM*
     * - иначе - *LARGE*
     */
    private int chooseTitleTextSize(boolean hasSubtitle) {
        return hasSubtitle ? R.dimen.common_views_sbis_title_view_title_text_size_medium :
                R.dimen.common_views_sbis_title_view_title_text_size_large;
    }

    /**
     * Получить размер для заголовка, когда он не помещается на одной строке:
     * - есть подзаголовок - *MEDIUM*
     * - иначе - *SMALL*
     */
    private int chooseSmallerTitleTextSize(boolean hasSubtitle) {
        return hasSubtitle ? R.dimen.common_views_sbis_title_view_title_text_size_medium
                : R.dimen.common_views_sbis_title_view_title_text_size_small;
    }
}