package ru.tensor.sbis.richtext.span.view.youtube;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

import ru.tensor.sbis.design.theme.ThemeTokensProvider;
import ru.tensor.sbis.richtext.R;
import timber.log.Timber;

/**
 * Превьюер ютуб видео
 * <p>
 * Created by am.boldinov on 01.03.17.
 */
public class YouTubePreviewer extends SimpleDraweeView {

    /**
     * Слушатель превьюера
     */
    public interface Listener {
        /**
         * Вызывается когда происходит клик по вью
         *
         * @param videoId идентификатор видео
         */
        void onYouTubePreviewClick(@NonNull String videoId);
    }

    private static final int QUALITY_UNSPECIFIED = -1;
    private static final int QUALITY_NORMAL = 0;
    private static final int QUALITY_MEDIUM = 1;
    private static final int QUALITY_HIGH = 2;

    private static final int ASPECT_RATIO_UNSPECIFIED = -1;
    private static final int ASPECT_RATIO_16_9 = 0;

    private static final float DEFAULT_PLAY_SIZE_DP = 50;

    @NonNull
    private YouTubeUtil.PreviewType mPreviewType = YouTubeUtil.PreviewType.HIGH_QUALITY;
    private final int mAspectRatioMode;
    private int mButtonSize;
    @Nullable
    private String mVideoId;
    @Nullable // Note: Possible exception while decoding
    private Bitmap mButtonBmp;
    private Rect mSourceRect;
    private final Rect mDestRect = new Rect();
    private final Paint mPaint;
    private int mMaxHeightInPx = Integer.MAX_VALUE;

    public YouTubePreviewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray previewerAttrs = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RichTextYouTubePreviewer,
                0, 0);
        try {
            int defaultSize = Math.round(dp2px(DEFAULT_PLAY_SIZE_DP));
            mButtonSize = previewerAttrs.getDimensionPixelSize(R.styleable.RichTextYouTubePreviewer_playSize, defaultSize);
            int quality = previewerAttrs.getInt(R.styleable.RichTextYouTubePreviewer_quality, QUALITY_UNSPECIFIED);
            if (quality != QUALITY_UNSPECIFIED) {
                switch (quality) {
                    case QUALITY_NORMAL:
                        mPreviewType = YouTubeUtil.PreviewType.NORMAL_QUALITY;
                        break;
                    case QUALITY_MEDIUM:
                        mPreviewType = YouTubeUtil.PreviewType.MEDIUM_QUALITY;
                        break;
                    case QUALITY_HIGH:
                        mPreviewType = YouTubeUtil.PreviewType.HIGH_QUALITY;
                        break;
                }
            }
            mAspectRatioMode = previewerAttrs.getInt(R.styleable.RichTextYouTubePreviewer_aspectRatio, ASPECT_RATIO_UNSPECIFIED);
        } finally {
            previewerAttrs.recycle();
        }
        getHierarchy().setPlaceholderImage(
                new ColorDrawable(
                        ThemeTokensProvider.INSTANCE.getColorInt(
                                getContext(),
                                ru.tensor.sbis.design.R.attr.paleColor
                        )
                )
        );

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        initPlayButton();
    }

    private void initPlayButton() {
        try {
            if (mButtonBmp != null) {
                mButtonBmp.recycle();
            }
            mButtonBmp = BitmapFactory.decodeResource(getResources(), R.drawable.richtext_youtube_play_image);
            mSourceRect = new Rect(0, 0, mButtonBmp.getWidth(), mButtonBmp.getHeight());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Устанавливает обработчик клика на превью
     */
    public void setOnPreviewClickListener(@Nullable Listener listener) {
        super.setOnClickListener(v -> {
            if (listener != null && mVideoId != null) {
                listener.onYouTubePreviewClick(mVideoId);
            }
        });
    }

    /**
     * Use {@link YouTubePreviewer#setOnPreviewClickListener(Listener)} to listen click event
     */
    @Deprecated
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {

    }

    public void setVideoId(@Nullable String videoId) {
        mVideoId = videoId;
        setImageURI(videoId != null && !TextUtils.isDigitsOnly(videoId)
                ? YouTubeUtil.getPreviewUrl(videoId, mPreviewType)
                : null);
    }

    /**
     * Устанавливает максимальную высоту вью в dp
     */
    @SuppressWarnings("unused")
    public void setMaxPreviewHeightInDp(float dp) {
        mMaxHeightInPx = Math.round(dp2px(dp));
    }

    /**
     * Устанавливает максимальную высоту вью в px
     */
    public void setMaxPreviewHeightInPx(@Px int px) {
        mMaxHeightInPx = px;
    }

    /**
     * Устанавливает размер кнопки в px
     */
    @SuppressWarnings("unused")
    public void setButtonSize(@Px int size) {
        if (mButtonSize != size) {
            mButtonSize = size;
            initPlayButton();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mButtonBmp != null) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();

            float offset = mButtonSize / 2.0f;
            mDestRect.left = Math.round(width / 2.0f - offset);
            mDestRect.top = Math.round(height / 2.0f - offset);
            mDestRect.right = Math.round(width / 2.0f + offset);
            mDestRect.bottom = Math.round(height / 2.0f + offset);

            canvas.drawBitmap(mButtonBmp, mSourceRect, mDestRect, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight = mMaxHeightInPx;
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);

        int previewWidth = maxWidth - getPaddingLeft() - getPaddingRight();
        // Рассчитываем высоту, отталкиваясь от ширины
        float aspectRatio = getPreviewAspectRatio();
        int previewHeight = (int) (previewWidth / aspectRatio);
        if (previewHeight + getPaddingTop() + getPaddingBottom() > maxHeight) {
            // Если высота превышена - пересчитываем, отталкиваясь от высоты
            previewHeight = maxHeight - getPaddingTop() - getPaddingBottom();
            previewWidth = (int) (previewHeight * aspectRatio);
        }
        setMeasuredDimension(previewWidth + getPaddingLeft() + getPaddingRight(), previewHeight + getPaddingTop() + getPaddingBottom());
    }

    private float getPreviewAspectRatio() {
        switch (mAspectRatioMode) {

            case ASPECT_RATIO_UNSPECIFIED:
                return (float) mPreviewType.getWidth() / mPreviewType.getHeight();

            case ASPECT_RATIO_16_9:
                return 16.05f / 9;

            default:
                throw new IllegalStateException("Unknown aspect ratio mode " + mAspectRatioMode + "!");
        }
    }

    private float dp2px(final float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }
}
