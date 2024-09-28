package ru.tensor.sbis.scanner.ui.editimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.SerialDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import ru.tensor.sbis.common.util.CommonUtils;
import ru.tensor.sbis.common.util.uri.UriWrapper;
import ru.tensor.sbis.scanner.data.interactor.editimage.EditImageInteractor;
import ru.tensor.sbis.scanner.data.mapper.CornerPointListMapper;
import ru.tensor.sbis.scanner.data.model.CornerPoint;
import ru.tensor.sbis.scanner.data.model.Rotation;
import ru.tensor.sbis.scanner.generated.CornerCoordinates;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;
import ru.tensor.sbis.scanner.generated.ScannerRotateAngle;
import timber.log.Timber;

/**
 * @author am.boldinov
 */
public class EditImagePresenterImpl implements EditImageContract.Presenter {

    @NonNull
    private final EditImageInteractor mEditImageInteractor;
    @NonNull
    private final CornerPointListMapper mPointListMapper;
    @NonNull
    private final UriWrapper mUriWrapper;
    @Nullable
    private ScannerPageInfo mScannerPageInfo;
    @Nullable
    private EditImageContract.View mView;
    @NonNull
    private final SerialDisposable mEditImageDisposable = new SerialDisposable();

    public EditImagePresenterImpl(@NonNull EditImageInteractor editImageInteractor, @NonNull UriWrapper uriWrapper,
                                  @NonNull CornerPointListMapper pointListMapper) {
        mEditImageInteractor = editImageInteractor;
        mUriWrapper = uriWrapper;
        mPointListMapper = pointListMapper;
    }

    @Override
    public void attachView(@NonNull EditImageContract.View view) {
        mView = view;
        loadImage();
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {
        mEditImageDisposable.dispose();
    }

    @Override
    public void onCancelButtonClick() {
        if (mView != null) {
            mView.finishScreen();
        }
    }

    @Override
    public void onDoneButtonClick(@NonNull Rotation rotation, @Nullable List<CornerPoint> cornerPoints) {
        if (mScannerPageInfo != null) {
            final ScannerRotateAngle rotateAngle = rotation.toAngleRotate();
            if (cornerPoints != null && cornerPointsIsChanged(cornerPoints, mScannerPageInfo.getRectangle())) {
                mEditImageDisposable.set(
                    performEditAction(mEditImageInteractor.cropImage(mScannerPageInfo.getId(), cornerPoints, rotateAngle))
                );
            } else if (rotateAngle != ScannerRotateAngle.NOT_ROTATE) {
                mEditImageDisposable.set(
                    performEditAction(mEditImageInteractor.rotateImage(mScannerPageInfo.getId(), rotateAngle))
                );
            } else if (mView != null) {
                mView.finishScreen();
            }
        }
    }

    @Override
    public void onRotateButtonClick() {
        if (mView != null) {
            mView.rotateImage();
        }
    }

    @Override
    public void setScannerPageInfo(@Nullable ScannerPageInfo scannerPageInfo) {
        mScannerPageInfo = scannerPageInfo;
        loadImage();
    }

    private void loadImage() {
        if (mView != null && mScannerPageInfo != null && mScannerPageInfo.getImagePath() != null) {
            final String uri = mUriWrapper.getStringUriForFilePath(mScannerPageInfo.getImagePath());
            List<CornerPoint> cornerPointList = null;
            if (!CommonUtils.isEmpty(mScannerPageInfo.getRectangle())) {
                try {
                    cornerPointList = mPointListMapper.apply(mScannerPageInfo.getRectangle());
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
            mView.displayImage(uri, cornerPointList, mScannerPageInfo.getOriginalWidth(), mScannerPageInfo.getOriginalHeight());
        }
    }

    @NonNull
    private Disposable performEditAction(@NonNull Observable<ScannerPageInfo> upstream) {
        if (mView != null) {
            mView.showWaitProgress();
        }
        return upstream.doAfterTerminate(new Action() {
            @Override
            public void run() throws Exception {
                if (mView != null) {
                    mView.hideWaitProgress();
                }
            }
        }).subscribe(new Consumer<ScannerPageInfo>() {
            @Override
            public void accept(ScannerPageInfo scannerPageInfo) throws Exception {
                mScannerPageInfo = scannerPageInfo;
                if (mView != null && mScannerPageInfo != null) {
                    mView.finishScreenWithResult(mScannerPageInfo);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (mView != null) {
                    mView.showImageProcessingError();
                }
            }
        });
    }

    private static boolean cornerPointsIsChanged(@NonNull List<CornerPoint> cornerPoints, @NonNull List<CornerCoordinates> cornerCoordinates) {
        for (CornerPoint cornerPoint : cornerPoints) {
            boolean contains = false;
            for (CornerCoordinates coordinates : cornerCoordinates) {
                if (cornerPoint.x == coordinates.getX() && cornerPoint.y == coordinates.getY()) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                return true;
            }
        }
        return false;
    }
}
