package ru.tensor.sbis.scanner.adapter.scannedimagelist.viewholder;

import androidx.databinding.ViewDataBinding;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageView;

import ru.tensor.sbis.base_components.adapter.universal.UniversalViewHolder;
import ru.tensor.sbis.scanner.BR;
import ru.tensor.sbis.scanner.R;
import ru.tensor.sbis.scanner.adapter.scannedimagelist.clickhandler.ScannedImageListItemClickHandler;

/**
 * @author am.boldinov
 */
public class ScannedImageListItemViewHolder extends UniversalViewHolder {

    @NonNull
    private final ImageView mCheckStateView;

    public ScannedImageListItemViewHolder(@NonNull ViewDataBinding binding,
                                          @Nullable ScannedImageListItemClickHandler clickHandler) {
        super(binding, BR.ScannedImageListItemClickHandler, clickHandler);
        mCheckStateView = itemView.findViewById(R.id.scanned_image_check_state);
    }

    @Override
    public void updateCheckState(boolean checked, boolean animate) {
        mCheckStateView.setImageResource(checked ? ru.tensor.sbis.design.R.drawable.checkbox_full_icon : ru.tensor.sbis.design.R.drawable.checkbox_empty_icon);
    }
}
