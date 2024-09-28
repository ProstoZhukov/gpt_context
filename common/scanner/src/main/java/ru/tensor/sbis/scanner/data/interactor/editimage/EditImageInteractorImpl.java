package ru.tensor.sbis.scanner.data.interactor.editimage;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.util.uri.UriWrapper;
import ru.tensor.sbis.mvp.interactor.BaseInteractor;

import kotlin.Pair;
import ru.tensor.sbis.scanner.data.mapper.CornerCoordinatesListMapper;
import ru.tensor.sbis.scanner.data.model.CornerPoint;
import ru.tensor.sbis.scanner.generated.ScannerApi;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;
import ru.tensor.sbis.scanner.generated.ScannerRotateAngle;

/**
 * @author am.boldinov
 */
public class EditImageInteractorImpl extends BaseInteractor implements EditImageInteractor {

    @NonNull
    private final CornerCoordinatesListMapper mCoordinatesListMapper;
    @NonNull
    private final UriWrapper mUriWrapper;
    @NonNull
    private final DependencyProvider<ScannerApi> mScannerApi;

    public EditImageInteractorImpl(@NonNull CornerCoordinatesListMapper coordinatesListMapper,
                                   @NonNull UriWrapper uriWrapper, @NonNull DependencyProvider<ScannerApi> scannerApi) {
        super();
        mCoordinatesListMapper = coordinatesListMapper;
        mUriWrapper = uriWrapper;
        mScannerApi = scannerApi;
    }

    @NonNull
    @Override
    public Observable<List<ScannerPageInfo>> rotateImages(@NonNull List<Pair<Integer, ScannerRotateAngle>> rotateList) {
        return Observable.fromCallable(new Callable<List<ScannerPageInfo>>() {
            @Override
            public List<ScannerPageInfo> call() throws Exception {
                final List<ScannerPageInfo> result = new ArrayList<>();
                for (Pair<Integer, ScannerRotateAngle> pair : rotateList) {
                    result.add(rotateImageInternal(pair.getFirst(), pair.getSecond()));
                }
                return result;
            }
        })
                .compose(getObservableBackgroundSchedulers());
    }

    @NonNull
    @Override
    public Observable<ScannerPageInfo> rotateImage(int imageId, @NonNull ScannerRotateAngle rotateAngle) {
        return Observable.fromCallable(new Callable<ScannerPageInfo>() {
            @Override
            public ScannerPageInfo call() throws Exception {
                return rotateImageInternal(imageId, rotateAngle);
            }
        })
                .compose(getObservableBackgroundSchedulers());
    }

    @NonNull
    @Override
    public Observable<ScannerPageInfo> cropImage(int imageId, @NonNull List<CornerPoint> cornerPointList, @Nullable ScannerRotateAngle rotateAngle) {
        return Observable.fromCallable(new Callable<ScannerPageInfo>() {
            @Override
            public ScannerPageInfo call() throws Exception {
                ScannerPageInfo scannerPageInfo = mScannerApi.get().cropPage(imageId, new ArrayList<>(mCoordinatesListMapper.apply(cornerPointList)));
                if (rotateAngle != null) {
                    scannerPageInfo = mScannerApi.get().rotatePage(imageId, rotateAngle);
                }
                invalidateFrescoCache(scannerPageInfo);
                return scannerPageInfo;
            }
        })
                .compose(getObservableBackgroundSchedulers());
    }

    private ScannerPageInfo rotateImageInternal(int imageId, @NonNull ScannerRotateAngle rotateAngle) {
        final ScannerPageInfo scannerPageInfo = mScannerApi.get().rotatePage(imageId, rotateAngle);
        invalidateFrescoCache(scannerPageInfo);
        return scannerPageInfo;
    }

    private void invalidateFrescoCache(@NonNull ScannerPageInfo scannerPageInfo) {
        if (scannerPageInfo.getImageCroppedPath() != null) {
            final String croppedUri = mUriWrapper.getStringUriForFilePath(scannerPageInfo.getImageCroppedPath());
            Fresco.getImagePipeline().evictFromCache(Uri.parse(croppedUri));
            if (scannerPageInfo.getImagePath() != null) {
                final String originalUri = mUriWrapper.getStringUriForFilePath(scannerPageInfo.getImagePath());
                Fresco.getImagePipeline().evictFromCache(Uri.parse(originalUri));
            }
        }
    }
}
