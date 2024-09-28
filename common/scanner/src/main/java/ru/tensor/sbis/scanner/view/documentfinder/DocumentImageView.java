package ru.tensor.sbis.scanner.view.documentfinder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import java.util.List;

import ru.tensor.sbis.scanner.R;
import ru.tensor.sbis.scanner.data.model.CornerPoint;
import ru.tensor.sbis.scanner.data.model.Rotation;

/**
 * @author am.boldinov
 */
public final class DocumentImageView extends FrameLayout implements FinderResizeListener, DraweeAttacher.OnMatrixChangeListener {

    private DocumentDraweeView mImageView;
    private DocumentFinderView mFinderView;
    @Nullable
    private RectF mOriginalImageBounds;
    private int mImageWidth;
    private int mImageHeight;

    private boolean mCoordinatesInImageOrientation;

    public DocumentImageView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public DocumentImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DocumentImageView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DocumentImageView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.document_image_view, this, true);
        mImageView = findViewById(R.id.image_view);
        mFinderView = findViewById(R.id.finder_view);
        mImageView.getAttacher().getScaleSettings().setMinimum(0.7f);
        if (attrs != null) {
            TypedArray attrArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.DocumentImageView, 0, 0);
            final boolean touchEnabled = attrArray.getBoolean(R.styleable.DocumentImageView_touch_enabled, true);
            attrArray.recycle();
            mImageView.getAttacher().setTouchEnabled(touchEnabled);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mImageView.getAttacher().setOnMatrixChangeListener(this);
        mFinderView.setResizeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFinderView.setResizeListener(null);
        mImageView.getAttacher().setOnMatrixChangeListener(null);
    }

    public void setImage(@NonNull String imageUri) {
        setImage(imageUri, null);
    }

    public void setImage(@NonNull String imageUri, @Nullable List<CornerPoint> finderCoordinates) {
        mFinderView.setVisibility(INVISIBLE);
        mImageView.setDraweeUri(imageUri, new DocumentDraweeView.OnLoadingListener() {
            @Override
            public void onComplete() {
                mCoordinatesInImageOrientation = false;
                mFinderView.setCoordinates(finderCoordinates);
            }
        });
    }

    public void setImage(@NonNull String imageUri, @Nullable List<CornerPoint> finderCoordinates, int imageWidth, int imageHeight) {
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
        setImage(imageUri, finderCoordinates);
    }

    @Override
    public void onFinderResize(@NonNull List<CornerPoint> cornerPointList) {
        // TODO make auto scale
    }

    @Override
    public void onMatrixChange(@NonNull RectF imageBounds, @NonNull Matrix matrix) {
        if (mOriginalImageBounds == null) {
            mOriginalImageBounds = new RectF(imageBounds);
        }
        if (!mCoordinatesInImageOrientation) {
            mCoordinatesInImageOrientation = mFinderView.mapCoordinatesToImage(imageBounds, mImageWidth, mImageHeight, false);
        }
        final float[] imageMatrixValues = mImageView.getAttacher().getDrawMatrixValues();
        final float[] finderMatrixValues = mFinderView.getDrawMatrixValues();
        for (int i = 0; i < imageMatrixValues.length; i++) {
            if (i == Matrix.MTRANS_X || i == Matrix.MTRANS_Y) {
                finderMatrixValues[i] = getTranslationByRotation(i, imageBounds, mImageView.getAttacher().getRotation());
            } else {
                finderMatrixValues[i] = imageMatrixValues[i];
            }
        }
        mFinderView.getDrawMatrix().setValues(finderMatrixValues);
        mFinderView.invalidate();
        mFinderView.setVisibility(VISIBLE);
    }

    public void rotate() {
        mImageView.rotateCcw();
    }

    public void setRotation(@Nullable Rotation rotation) {
        if (rotation != null) {
            mImageView.getAttacher().setRotation(rotation);
        } else {
            mImageView.getAttacher().resetRotation();
        }
    }

    @NonNull
    public Rotation getDraweeRotation() {
        return mImageView.getAttacher().getRotation();
    }

    @Nullable
    public List<CornerPoint> getFinderCoordinatesInOriginalOrientation() {
        if (mCoordinatesInImageOrientation && mOriginalImageBounds != null) {
            mFinderView.mapCoordinatesToImage(mOriginalImageBounds, mImageWidth, mImageHeight, true);
            mCoordinatesInImageOrientation = false;
        }
        return mFinderView.getCoordinates();
    }

    private static float getTranslationByRotation(int matrixIndex, @NonNull RectF displayRect, @NonNull Rotation rotation) {
        switch (rotation) {
            case HORIZONTAL:
                return matrixIndex == Matrix.MTRANS_X ? displayRect.left : displayRect.top;
            case HORIZONTAL_INVERSE:
                return matrixIndex == Matrix.MTRANS_X ? displayRect.right : displayRect.bottom;
            case VERTICAL:
                return matrixIndex == Matrix.MTRANS_X ? displayRect.right : displayRect.top;
            case VERTICAL_INVERSE:
                return matrixIndex == Matrix.MTRANS_X ? displayRect.left : displayRect.bottom;
            default:
                throw new UnsupportedOperationException("Unknown rotation type");
        }
    }
}
