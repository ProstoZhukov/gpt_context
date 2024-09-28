package ru.tensor.sbis.scanner.data;

import android.graphics.ImageFormat;
import android.hardware.Camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import ru.tensor.sbis.camera.service.AbstractCameraService;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.exceptions.LoadDataException;
import ru.tensor.sbis.scanner.data.mapper.CornerPointListMapper;
import ru.tensor.sbis.scanner.data.model.CornerPoint;
import ru.tensor.sbis.scanner.data.model.Rotation;
import ru.tensor.sbis.scanner.generated.CornerCoordinates;
import ru.tensor.sbis.scanner.generated.ScannerApi;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;
import ru.tensor.sbis.scanner.generated.ScannerRotateAngle;

/**
 * @author am.boldinov
 */
@SuppressWarnings("deprecation")
public final class DocumentScannerServiceImpl extends AbstractCameraService implements DocumentScannerService {

    private static final String TAG = DocumentScannerServiceImpl.class.getCanonicalName();

    private static final int PREVIEW_DEFAULT_WIDTH = 1280;
    private static final int PREVIEW_DEFAULT_HEIGHT = 720;
    private static final int PICTURE_DEFAULT_WIDTH = 2400;
    private static final float DESIRED_ASPECT_RATIO = (float) PREVIEW_DEFAULT_WIDTH / PREVIEW_DEFAULT_HEIGHT;
    private static final float DESIRED_ASPECT_RATIO_MAX_DELTA = 0.2f;

    private static final int COORDINATES_HANDLE_DELAY_MILLIS = 500;

    @NonNull
    private final String[] mDesiredFocusModes = new String[]{
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
    };

    @NonNull
    private final PublishSubject<byte[]> mCoordinatesSubject = PublishSubject.create();
    @NonNull
    private final DependencyProvider<ScannerApi> mScannerApi;
    @NonNull
    private final CornerPointListMapper mPointMapper;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private boolean mCoordinatesMustEmitted = true;
    private int mCameraOrientation;

    public DocumentScannerServiceImpl(@NonNull DependencyProvider<ScannerApi> scannerApi, @NonNull CornerPointListMapper pointMapper) {
        mScannerApi = scannerApi;
        mPointMapper = pointMapper;
    }

    @Override
    protected int getDefaultCameraId() {
        return Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    @Override
    protected void onPreStartPreview() {
        super.onPreStartPreview();
        mCoordinatesMustEmitted = true;
        if (mCamera != null && mSurfaceProvider != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            final String focusMode = getBestFocusMode(parameters, mDesiredFocusModes);
            if (focusMode != null) {
                parameters.setFocusMode(focusMode);
            }
            parameters.setPreviewFormat(ImageFormat.NV21);
            mCameraOrientation = determineSuitableCameraOrientation(mSurfaceProvider.getScreenOrientation());
            final Camera.Size bestPreviewSize = getBestCameraSize(parameters.getSupportedPreviewSizes(), PREVIEW_DEFAULT_WIDTH);
            mPreviewWidth = bestPreviewSize == null ? PREVIEW_DEFAULT_WIDTH : bestPreviewSize.width;
            mPreviewHeight = bestPreviewSize == null ? PREVIEW_DEFAULT_HEIGHT : bestPreviewSize.height;
            parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
            final Camera.Size bestPictureSize = getBestCameraSize(parameters.getSupportedPictureSizes(), PICTURE_DEFAULT_WIDTH);
            if (bestPictureSize != null) {
                parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);
            }
            mCamera.addCallbackBuffer(new byte[mPreviewWidth * mPreviewHeight * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / 8]);
            mCamera.setPreviewCallbackWithBuffer((data, camera) -> {
                if (camera != null) {
                    camera.addCallbackBuffer(data);
                }
                if (mCoordinatesMustEmitted) {
                    mCoordinatesSubject.onNext(data);
                }
            });
            mCamera.setParameters(parameters);
        }
    }

    @Override
    protected void onPreStopPreview() {
        super.onPreStopPreview();
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(null);
        }
    }

    @Nullable
    private static Camera.Size getBestCameraSize(@Nullable List<Camera.Size> sizeList, int desiredWidth) {
        if (sizeList == null || sizeList.isEmpty()) {
            return null;
        }
        Camera.Size minHighSize = null;
        Camera.Size maxLowSize = null;
        for (Camera.Size size : sizeList) {
            if (size.width == size.height) { // квадратных быть не должно
                continue;
            }
            if (size.width == desiredWidth) {
                minHighSize = size;
                break;
            }
            if ((minHighSize == null || size.width < minHighSize.width) && size.width > desiredWidth) {
                minHighSize = size;
            }
            if ((maxLowSize == null || size.width > maxLowSize.width) && size.width < desiredWidth) {
                maxLowSize = size;
            }
        }
        Camera.Size resultSize = minHighSize != null ? minHighSize : maxLowSize;
        if (resultSize != null) {
            float minRatioDelta = Float.MAX_VALUE;
            // выбрали подходящий размер по ширине, теперь подбираем по соотношению сторон
            for (Camera.Size size : sizeList) {
                if (size.width == resultSize.width) {
                    final float ratio = (float) size.width / size.height;
                    final float delta = ratio - DESIRED_ASPECT_RATIO;
                    if (delta > DESIRED_ASPECT_RATIO_MAX_DELTA) { // не даем сильно сужать изображение
                        continue;
                    }
                    final float absDelta = Math.abs(delta);
                    if (absDelta < minRatioDelta) {
                        minRatioDelta = absDelta;
                        resultSize = size;
                    }
                }
            }
        }
        return resultSize;
    }

    @Nullable
    private static String getBestFocusMode(@NonNull Camera.Parameters parameters, @NonNull String[] desiredFocusModes) {
        final List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        for (String desired : desiredFocusModes) {
            if (supportedFocusModes.contains(desired)) {
                return desired;
            }
        }
        return null;
    }

    @Override
    public Observable<List<CornerPoint>> getCoordinatesObservable() {
        return mCoordinatesSubject
                .sample(COORDINATES_HANDLE_DELAY_MILLIS, TimeUnit.MILLISECONDS, Schedulers.io())
                .map((Function<byte[], List<CornerCoordinates>>) previewFrame -> {
                    if (mSurfaceProvider != null) {
                        return mScannerApi.get().findRectangleInPreview(mPreviewWidth, mPreviewHeight,
                                mSurfaceProvider.getSurfaceWidth(), mSurfaceProvider.getSurfaceHeight(),
                                previewFrame, ru.tensor.sbis.scanner.generated.ImageFormat.NV21, mCameraOrientation);
                    }
                    return Collections.emptyList();
                })
                .map(mPointMapper)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(cornerPoints -> mCoordinatesMustEmitted);
    }

    @Override
    public Single<ScannerPageInfo> takePicture(@NonNull Action onPictureTakenAction) {
        return Single.create((SingleOnSubscribe<byte[]>) emitter -> {
            if (mCamera != null) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    private boolean mTakePictureInProcess = false;

                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (emitter.isDisposed() || mTakePictureInProcess) {
                            return;
                        }
                        try {
                            mTakePictureInProcess = true;
                            camera.takePicture(null, null, (data, pictureCamera) -> {
                                stopPreview();
                                if (!emitter.isDisposed()) {
                                    if (data != null) {
                                        emitter.onSuccess(data);
                                    } else {
                                        emitter.onError(new LoadDataException(LoadDataException.Type.DEFAULT));
                                    }
                                }
                                mTakePictureInProcess = false;
                            });
                        } catch (Exception e) {
                            mTakePictureInProcess = false;
                            if (!emitter.isDisposed()) {
                                emitter.onError(new LoadDataException(LoadDataException.Type.DEFAULT));
                            }
                        }
                    }
                });
            } else {
                emitter.onError(new LoadDataException(LoadDataException.Type.DEFAULT));
            }
        })
                .doOnSuccess(bytes -> onPictureTakenAction.run())
                .observeOn(Schedulers.io())
                .map(bytes -> mScannerApi.get().addPage(bytes,
                        ru.tensor.sbis.scanner.generated.ImageFormat.JPEG,
                        getAngleRotateByOrientation(mCameraOrientation))
                )
                .doOnSubscribe(disposable -> mCoordinatesMustEmitted = false)
                .doAfterTerminate(() -> mCoordinatesMustEmitted = true)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private static ScannerRotateAngle getAngleRotateByOrientation(int cameraOrientation) {
        final Rotation rotation = Rotation.fromValue(cameraOrientation);
        if (rotation != null) {
            return rotation.toAngleRotate();
        }
        return ScannerRotateAngle.NOT_ROTATE;
    }
}
