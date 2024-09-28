package ru.tensor.sbis.scanner.adapter.scannedimagelist.clickhandler;

import androidx.annotation.NonNull;

import ru.tensor.sbis.scanner.adapter.scannedimagelist.item.ScannedImageListItem;

/**
 * @author am.boldinov
 */
public interface ScannedImageListItemClickHandler {

    void onItemClick(@NonNull ScannedImageListItem scannedImageListItem);

    void onCheckboxClick(@NonNull ScannedImageListItem scannedImageListItem);
}
