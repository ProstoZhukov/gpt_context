package ru.tensor.sbis.scanner.data.mapper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import ru.tensor.sbis.common.util.uri.UriWrapper;
import ru.tensor.sbis.scanner.adapter.scannedimagelist.item.ScannedImageListItem;
import ru.tensor.sbis.scanner.data.model.ScannerPage;
import ru.tensor.sbis.scanner.generated.ScannerPageInfo;

/**
 * @author am.boldinov
 */
public class ScannedImageModelMapper implements Function<List<ScannerPageInfo>, List<ScannedImageListItem>> {

    @NonNull
    private final UriWrapper mUriWrapper;

    public ScannedImageModelMapper(@NonNull UriWrapper uriWrapper) {
        mUriWrapper = uriWrapper;
    }

    @Override
    public List<ScannedImageListItem> apply(List<ScannerPageInfo> scannerPageInfos) throws Exception {
        final List<ScannedImageListItem> result = new ArrayList<>(scannerPageInfos.size());
        for (int i = 0; i < scannerPageInfos.size(); i++) {
            final ScannerPageInfo scannerPageInfo = scannerPageInfos.get(i);
            final String url = mUriWrapper.getStringUriForFilePath(scannerPageInfo.getImageCroppedPath());
            final ScannerPage scannerPage = new ScannerPage(scannerPageInfo, i+1);
            result.add(new ScannedImageListItem(String.valueOf(scannerPageInfo.getId()), url, scannerPage));
        }
        return result;
    }
}
