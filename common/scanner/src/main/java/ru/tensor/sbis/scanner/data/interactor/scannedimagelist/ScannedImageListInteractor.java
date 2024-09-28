package ru.tensor.sbis.scanner.data.interactor.scannedimagelist;

import androidx.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import ru.tensor.sbis.scanner.adapter.scannedimagelist.item.ScannedImageListItem;

/**
 * @author am.boldinov
 */
public interface ScannedImageListInteractor {

    @NonNull
    Observable<List<ScannedImageListItem>> getScannedImages();

    @NonNull
    Observable<List<ScannedImageListItem>> deleteScannedImage(int imageId);

    @NonNull
    Observable<String> saveToPdf(@NonNull List<ScannedImageListItem> scannedImagesToSaving, @NonNull String newPDFFileName);

    Completable saveToGallery(@NonNull List<ScannedImageListItem> scannedImagesToSaving);
}
