package ru.tensor.sbis.scanner.adapter.scannedimagelist;

import androidx.annotation.NonNull;
import android.view.ViewGroup;

import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingAdapter;
import ru.tensor.sbis.scanner.R;
import ru.tensor.sbis.scanner.adapter.scannedimagelist.clickhandler.ScannedImageListItemClickHandler;
import ru.tensor.sbis.scanner.adapter.scannedimagelist.item.ScannedImageListItem;
import ru.tensor.sbis.scanner.adapter.scannedimagelist.viewholder.ScannedImageListItemViewHolder;

/**
 * @author am.boldinov
 */
public class ScannedImageListAdapter extends UniversalBindingAdapter<ScannedImageListItem, ScannedImageListItemViewHolder> {

    @NonNull
    private final ScannedImageListItemClickHandler mClickHandler;

    public ScannedImageListAdapter(@NonNull ScannedImageListItemClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public ScannedImageListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScannedImageListItemViewHolder(
            createBinding(R.layout.scanned_image_list_item, parent), mClickHandler);
    }
}
