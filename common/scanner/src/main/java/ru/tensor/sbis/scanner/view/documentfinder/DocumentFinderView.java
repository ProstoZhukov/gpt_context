package ru.tensor.sbis.scanner.view.documentfinder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.tensor.sbis.scanner.R;
import ru.tensor.sbis.scanner.data.model.CornerPoint;

/**
 * @author am.boldinov
 */
public final class DocumentFinderView extends View {

    private static final int VERTEX_COUNT = 4;
    private static final float DEFAULT_POINT_RADIUS = 20.0f;
    private static final float DEFAULT_LINE_WIDTH = 7.0f;

    private static final int MODE_DISPLAY = 0;
    private static final int MODE_CROP = 1;

    @Nullable
    private FinderResizeListener mFinderResizeListener;
    private final float[] mMatrixValues = new float[9];
    private final float[] mPointBuffer = new float[2];
    @NonNull
    private final Matrix mPointInverseMatrix = new Matrix();
    private int mMode = MODE_DISPLAY;

    private Paint mLinePaint;
    private Paint mCornerPaint;
    private Path mLinePath;
    private Path mCornerPath;

    private Matrix mMatrix;

    @Nullable
    private List<CornerPoint> mCoordinates;
    private SparseArray<CornerPoint> mTouchCornerArray;

    public DocumentFinderView(Context context) {
        super(context);
        init(null);
    }

    public DocumentFinderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DocumentFinderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DocumentFinderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void setResizeListener(@Nullable FinderResizeListener finderResizeListener) {
        mFinderResizeListener = finderResizeListener;
    }

    private void init(@Nullable AttributeSet attrs) {
        mTouchCornerArray = new SparseArray<>();
        mMatrix = new Matrix();
        initLines();
        if (attrs != null) {
            TypedArray attrArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.DocumentFinderView, 0, 0);
            mMode = attrArray.getInt(R.styleable.DocumentFinderView_finder_mode, MODE_DISPLAY);
            attrArray.recycle();
        }
        if (mMode == MODE_CROP) {
            initCorners();
        }
    }

    private void initLines() {
        mLinePath = new Path();
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(ContextCompat.getColor(getContext(), ru.tensor.sbis.design.R.color.palette_color_orange8));
        mLinePaint.setStrokeWidth(DEFAULT_LINE_WIDTH);
    }

    private void initCorners() {
        mCornerPath = new Path();
        mCornerPaint = new Paint();
        mCornerPaint.setAntiAlias(true);
        mCornerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCornerPaint.setColor(ContextCompat.getColor(getContext(), ru.tensor.sbis.design.R.color.palette_color_orange8));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final boolean handleResult;
        if (mMode == MODE_CROP) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    handleResult = handleStartGesture(ev);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    handleResult = handlePointerDown(ev);
                    break;
                case MotionEvent.ACTION_MOVE:
                    handleResult = handlePointerMove(ev);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    handleResult = handlePointerUp(ev);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    handleResult = handleEndGesture();
                    break;
                default:
                    handleResult = false;
            }
            if (touchCornersInProcess() && handleResult) {
                setCoordinates(mCoordinates);
                if (mFinderResizeListener != null && mCoordinates != null) {
                    mFinderResizeListener.onFinderResize(mCoordinates);
                }
            }
        } else {
            handleResult = false;
        }
        return handleResult;
    }

    private boolean handleEndGesture() {
        if (touchCornersInProcess()) {
            mTouchCornerArray.clear();
            return true;
        }
        return false;
    }

    private boolean handleStartGesture(@NonNull MotionEvent ev) {
        return checkCornerTouched(ev);
    }

    private boolean handlePointerMove(@NonNull MotionEvent ev) {
        boolean result = false;
        if (touchCornersInProcess()) {
            for (int i = 0; i < ev.getPointerCount(); i++) {
                int id = ev.getPointerId(i);
                CornerPoint point = mTouchCornerArray.get(id);
                if (point != null && mCoordinates != null) {
                    float coords[] = mapPointsToMatrix(ev.getX(i), ev.getY(i));
                    final float oldX = point.x;
                    final float oldY = point.y;
                    point.x = coords[0];
                    point.y = coords[1];
                    if (checkIntersectLines(mCoordinates.get(0).x, mCoordinates.get(0).y,
                            mCoordinates.get(2).x, mCoordinates.get(2).y,
                            mCoordinates.get(1).x, mCoordinates.get(1).y,
                            mCoordinates.get(3).x, mCoordinates.get(3).y)) {
                        result = true;
                    } else {
                        point.x = oldX;
                        point.y = oldY;
                    }
                }
            }
        }
        return result;
    }

    private boolean handlePointerDown(@NonNull MotionEvent ev) {
        return touchCornersInProcess() && checkCornerTouched(ev);
    }

    private boolean handlePointerUp(@NonNull MotionEvent ev) {
        final int id = ev.getPointerId(ev.getActionIndex());
        final int prevSize = mTouchCornerArray.size();
        mTouchCornerArray.remove(id);
        return prevSize > mTouchCornerArray.size();
    }

    private boolean checkCornerTouched(@NonNull MotionEvent ev) {
        int index = ev.getActionIndex();
        return checkCornerTouched(
                ev.getPointerId(index),
                ev.getX(index), ev.getY(index));
    }

    private boolean checkCornerTouched(int id, float x, float y) {
        if (mCoordinates != null) {
            final float[] points = mapPointsToMatrix(x, y);
            for (CornerPoint cornerPoint : mCoordinates) {
                if (isPointClicked(cornerPoint, points[0], points[1])) {
                    mTouchCornerArray.put(id, cornerPoint);
                    return true;
                }
            }
        }
        return false;
    }

    private float[] mapPointsToMatrix(float x, float y) {
        mPointBuffer[0] = x;
        mPointBuffer[1] = y;
        mMatrix.invert(mPointInverseMatrix);
        mPointInverseMatrix.mapPoints(mPointBuffer);
        return mPointBuffer;
    }

    private boolean touchCornersInProcess() {
        return mTouchCornerArray.size() != 0;
    }

    public Matrix getDrawMatrix() {
        return mMatrix;
    }

    public float[] getDrawMatrixValues() {
        getDrawMatrix().getValues(mMatrixValues);
        return mMatrixValues;
    }

    public boolean mapCoordinatesToImage(@NonNull RectF imageBounds, int imageWidth, int imageHeight, boolean original) {
        if (mCoordinates != null && !mCoordinates.isEmpty() && imageWidth > 0 && imageHeight > 0) {
            for (CornerPoint cornerPoint : mCoordinates) {
                mapPointToImage(cornerPoint, imageBounds, imageWidth, imageHeight, original);
            }
            if (!original) {
                setCoordinates(mCoordinates);
            }
            return true;
        }
        return false;
    }

    @Nullable
    public List<CornerPoint> getCoordinates() {
        return mCoordinates != null ? new ArrayList<>(mCoordinates) : null;
    }

    public void setCoordinates(@Nullable List<CornerPoint> cornerPointList) {
        mCoordinates = cornerPointList;
        mLinePath.rewind();
        if (mMode == MODE_CROP) {
            mCornerPath.rewind();
        }
        if (cornerPointList != null && cornerPointList.size() == VERTEX_COUNT) {
            final CornerPoint first = cornerPointList.get(0);
            mLinePath.moveTo(first.x, first.y);
            if (mMode == MODE_CROP) {
                mCornerPath.addCircle(first.x, first.y, DEFAULT_POINT_RADIUS, Path.Direction.CW);
            }
            for (int i = 1; i < cornerPointList.size(); i++) {
                final CornerPoint cornerPoint = cornerPointList.get(i);
                mLinePath.lineTo(cornerPoint.x, cornerPoint.y);
                if (mMode == MODE_CROP) {
                    mCornerPath.addCircle(cornerPoint.x, cornerPoint.y, DEFAULT_POINT_RADIUS, Path.Direction.CW);
                }
            }
            mLinePath.close();
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.concat(mMatrix);
        canvas.drawPath(mLinePath, mLinePaint);
        if (mMode == MODE_CROP) {
            canvas.drawPath(mCornerPath, mCornerPaint);
        }
    }

    private static void mapPointToImage(@NonNull CornerPoint cornerPoint, @NonNull RectF imageBounds, int imageWidth, int imageHeight, boolean original) {
        final float offset = Math.min(imageBounds.width() / imageWidth, imageBounds.height() / imageHeight);
        cornerPoint.x = original ? cornerPoint.x / offset : cornerPoint.x * offset;
        cornerPoint.y = original ? cornerPoint.y / offset : cornerPoint.y * offset;
    }

    private static boolean isPointClicked(@NonNull CornerPoint cornerPoint, float targetX, float targetY) {
        return Math.sqrt(Math.pow(cornerPoint.x - targetX, 2) + Math.pow(cornerPoint.y - targetY, 2)) <= DEFAULT_POINT_RADIUS * 4;
    }

    private static boolean checkIntersectLines(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        return signedArea(x1, y1, x2, y2, x3, y3) * signedArea(x1, y1, x2, y2, x4, y4) < 0.0f
                && signedArea(x3, y3, x4, y4, x1, y1) * signedArea(x3, y3, x4, y4, x2, y2) < 0.0f;
    }

    private static float signedArea(float x1, float y1, float x2, float y2, float x3, float y3) {
        return ((x2 - x1) * (y3 - y1)) - ((y2 - y1) * (x3 - x1));
    }
}
