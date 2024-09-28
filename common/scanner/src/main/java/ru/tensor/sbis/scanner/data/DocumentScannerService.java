package ru.tensor.sbis.scanner.data;

import androidx.annotation.NonNull;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import ru.tensor.sbis.camera.service.CameraService;
import ru.tensor.sbis.scanner.data.model.CornerPoint;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;

/**
 * @author am.boldinov
 */
public interface DocumentScannerService extends CameraService {
    Observable<List<CornerPoint>> getCoordinatesObservable();
    Single<ScannerPageInfo> takePicture(@NonNull Action onPictureTakenAction);
}
