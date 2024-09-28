package ru.tensor.sbis.modalwindows.bottomsheet;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.design.sbis_text_view.SbisTextView;
import ru.tensor.sbis.modalwindows.bottomsheet.resourceprovider.ModalWindowsOptionSheetItemViewProvider;
import ru.tensor.sbis.modalwindows.bottomsheet.resourceprovider.OptionSheetItemViewProvider;

/**
 * Холдер базовой опции из списка опций.
 * <p>
 * @author sr.golovkin
 */
@SuppressWarnings("WeakerAccess")
public class BaseBottomSheetOptionHolder extends BottomSheetOptionHolder {

    public final SbisTextView mNameView;
    public final SbisTextView mIconView;
    private final int mDefaultColor;
    private final int mNameGravity;

    /**
     * ViewHolder с использованием стандартной разметки
     * @param parent ссылка на родителя
     * @param listener слушатель кликов по ячейке
     */
    public BaseBottomSheetOptionHolder(@NonNull ViewGroup parent, @Nullable OnOptionClickListener listener) {
        this(parent, listener, new ModalWindowsOptionSheetItemViewProvider());
    }

    /**
     * ViewHolder с передачей собственной разметки и идентификаторов ключевых полей для работы.
     * @param parent ссылка на родителя
     * @param listener слушатель кликов по ячейке
     * @param resourceProvider поставщик ресуров, используемых для конструирования данного ViewHolder
     */
    public BaseBottomSheetOptionHolder(@NonNull ViewGroup parent, @Nullable OnOptionClickListener listener, @NonNull OptionSheetItemViewProvider resourceProvider) {
        super(LayoutInflater.from(parent.getContext()).inflate(resourceProvider.provideOptionLayoutRes(), parent, false), listener);
        mNameView = resourceProvider.provideTitleView(itemView);
        mIconView = resourceProvider.provideIconView(itemView);
        mDefaultColor = mIconView.getTextColor();
        mNameGravity = mNameView.getGravity();
    }


    public void bindOption(@NonNull BottomSheetOption option) {
        final String icon = option.getIcon();
        if (icon == null) {
            mIconView.setVisibility(View.GONE);
            mNameView.setGravity((mNameGravity & Gravity.VERTICAL_GRAVITY_MASK) | Gravity.CENTER_HORIZONTAL);
        } else {
            mIconView.setText(icon);
            mIconView.setVisibility(View.VISIBLE);
            mNameView.setGravity(mNameGravity);
        }
        mNameView.setText(option.getName());
        mNameView.setVisibility(option.getName() != null ? View.VISIBLE : View.GONE);

        final int iconColor = option.getIconColor();
        if (iconColor != BottomSheetOption.COLOR_UNDEFINED) {
            mIconView.setTextColor(iconColor);
        } else {
            mIconView.setTextColor(mDefaultColor);
        }
    }
}