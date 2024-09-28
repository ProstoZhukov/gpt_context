package ru.tensor.sbis.scanner.data.interactor.scannedimagelist;

import androidx.annotation.NonNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.Nullable;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.exceptions.LoadDataException;
import ru.tensor.sbis.common.util.FileUtil;
import ru.tensor.sbis.common.util.CommonUtils;
import ru.tensor.sbis.mvp.interactor.BaseInteractor;
import ru.tensor.sbis.mvp.interactor.FileInteractor;
import ru.tensor.sbis.scanner.adapter.scannedimagelist.item.ScannedImageListItem;
import ru.tensor.sbis.scanner.data.interactor.ScannerPageInteractor;
import ru.tensor.sbis.scanner.data.mapper.ScannedImageModelMapper;
import ru.tensor.sbis.scanner.generated.ScannerApi;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;
import ru.tensor.sbis.storage.external.ExternalDir;
import ru.tensor.sbis.storage.external.SbisExternalStorage;
import timber.log.Timber;

/**
 * @author am.boldinov
 */
public class ScannedImageListInteractorImpl extends BaseInteractor implements ScannedImageListInteractor {

    @NonNull
    private final DependencyProvider<ScannerApi> mScannerApi;
    @NonNull
    private final ScannedImageModelMapper mScannedImageModelMapper;
    @NonNull
    private final ScannerPageInteractor mPageInteractor;
    @NonNull
    private final FileInteractor mFileInteractor;
    @NonNull
    private final SbisExternalStorage mSbisExternalStorage;

    public ScannedImageListInteractorImpl(@NonNull DependencyProvider<ScannerApi> scannerApi,
                                          @NonNull ScannedImageModelMapper scannedImageModelMapper,
                                          @NonNull ScannerPageInteractor pageInteractor,
                                          @NonNull FileInteractor fileInteractor,
                                          @NonNull SbisExternalStorage sbisExternalStorage) {
        super();
        mScannerApi = scannerApi;
        mScannedImageModelMapper = scannedImageModelMapper;
        mPageInteractor = pageInteractor;
        mFileInteractor = fileInteractor;
        mSbisExternalStorage = sbisExternalStorage;
    }

    @NonNull
    @Override
    public Observable<List<ScannedImageListItem>> getScannedImages() {
        return getScannedImagesSync()
                .compose(getObservableBackgroundSchedulers());
    }

    @NonNull
    @Override
    public Observable<List<ScannedImageListItem>> deleteScannedImage(int imageId) {
        return mPageInteractor.deletePageSync(imageId)
                .andThen(getScannedImagesSync())
                .compose(getObservableBackgroundSchedulers());
    }

    @NonNull
    @Override
    public Observable<String> saveToPdf(@NonNull List<ScannedImageListItem> scannedImagesToSaving,
                                        @NonNull String newPDFFileName) {
        return Observable.fromCallable(() -> {
            ArrayList<ScannerPageInfo> scannedPages = new ArrayList<>(scannedImagesToSaving.size());
            for (ScannedImageListItem scannedImageListItem : scannedImagesToSaving) {
                scannedPages.add(scannedImageListItem.getPageInfo());
            }
            String pdfPath;
            try {
                pdfPath = mScannerApi.get().convertToPdf(scannedPages, newPDFFileName.concat(".pdf"));
            } catch (Exception e) {
                pdfPath = null;
            }
            if (CommonUtils.isEmpty(pdfPath)) {
                throw new LoadDataException(LoadDataException.Type.DEFAULT);
            }
            return pdfPath;
        })
                .compose(getObservableBackgroundSchedulers());
    }

    @Override
    public Completable saveToGallery(@NonNull List<ScannedImageListItem> scannedImagesToSaving) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                for (ScannedImageListItem imageListItem : scannedImagesToSaving) {
                    final boolean result = saveToSbisScannedImagesDir(new File(imageListItem.getPageInfo().getImageCroppedPath()));
                    if (!result) {
                        throw new LoadDataException(LoadDataException.Type.DEFAULT);
                    }
                }
            }
        })
                .compose(getCompletableBackgroundSchedulers());
    }

    private Observable<List<ScannedImageListItem>> getScannedImagesSync() {
        return mPageInteractor.getPageListSync()
                .map(mScannedImageModelMapper);
    }

    public boolean saveToSbisScannedImagesDir(@NonNull File image) {
        return saveImageToDir(mSbisExternalStorage.getSbisScansExternalDir(), image);
    }

    private boolean saveImageToDir(@NonNull ExternalDir dir, @NonNull File image) {
        File savingImage;
        if (dir.getStatus().isExists() && dir.getStatus().isWritable()) {
            savingImage = copyImageToDir(dir, image);
        } else {
            savingImage = null;
        }
        if (savingImage != null) {
            mFileInteractor.scanFile(savingImage);
        }
        return savingImage != null;
    }

    @Nullable
    private File copyImageToDir(@NonNull ExternalDir dir, @NonNull File image) {
        File newFile = null;
        try {
            newFile = FileUtil.copyFile(image, dir.getDir());
        } catch (IOException e) {
            Timber.d(e);
        }
        return newFile;
    }
}
