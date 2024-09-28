package ru.tensor.sbis.scanner.data.interactor.editimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.reactivex.Observable;
import kotlin.Pair;
import ru.tensor.sbis.scanner.data.model.CornerPoint;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;
import ru.tensor.sbis.scanner.generated.ScannerRotateAngle;

/**
 * @author am.boldinov
 */
public interface EditImageInteractor {

    @NonNull
    Observable<List<ScannerPageInfo>> rotateImages(@NonNull List<Pair<Integer, ScannerRotateAngle>> rotateList);

    @NonNull
    Observable<ScannerPageInfo> rotateImage(int imageId, @NonNull ScannerRotateAngle rotateAngle);

    @NonNull
    Observable<ScannerPageInfo> cropImage(int imageId, @NonNull List<CornerPoint> cornerPointList, @Nullable ScannerRotateAngle rotateAngle);
}
