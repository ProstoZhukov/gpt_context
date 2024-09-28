package ru.tensor.sbis.modalwindows.bottomsheet;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import ru.tensor.sbis.design.utils.ThemeUtil;
import ru.tensor.sbis.modalwindows.R;

/**
 * Абстрактный диалог-фрагмент со списком опций.
 *
 * @author sr.golovkin
 *
 * @deprecated TODO: Будет удалено по <a href="https://online.sbis.ru/opendoc.html?guid=4f5ff4ec-2c38-4e09-92e9-89c7809bb3c8&client=3"></a>
 */
@Deprecated
@SuppressWarnings("rawtypes")
public abstract class AbstractBottomOptionsSheet<
        V extends AbstractBottomOptionsSheetContract.View,
        P extends AbstractBottomOptionsSheetContract.Presenter<V, T>,
        T extends BottomSheetOption>
        extends AbstractBottomSheet<V, P> {

    /**
     * Настроить RecyclerView для отображения опций.
     * @param recyclerView  - recycler view
     * @param adapter       - адаптер с опциями
     */
    protected void configureOptionsListView(@NonNull RecyclerView recyclerView, @NonNull BottomSheetOptionsAdapter adapter) {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Workaround to remove "blinking" effect when notifyItemChanged gets called
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
    }

    /**
     * Создать адаптер с опциями для отображения списка опций в диалоге.
     */
    @NonNull
    protected abstract BottomSheetOptionsAdapter createOptionsAdapter(@NonNull List<T> options, boolean isLandscape, @NonNull BottomSheetOptionsAdapter.Listener<T> listener);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInflater themedInflater = inflater.cloneInContext(new ContextThemeWrapper(requireActivity(), getThemeRes()));
        View view = super.onCreateView(themedInflater, container, savedInstanceState);
        if (view != null) {
            final RecyclerView optionsListView = getOptionsListView(view);
            if (optionsListView != null) {
                boolean isLandscape = getResources().getBoolean(ru.tensor.sbis.common.R.bool.is_landscape);
                final List<T> options = mPresenter.createOptions(isLandscape);
                @SuppressWarnings("Convert2Lambda")
                BottomSheetOptionsAdapter.Listener<T> listener = new BottomSheetOptionsAdapter.Listener<T>() {
                    @Override
                    public void onOptionClick(@NonNull T option, int value, int position) {
                        mPresenter.onOptionClick(option);
                    }
                };
                final BottomSheetOptionsAdapter optionsAdapter = createOptionsAdapter(options, isLandscape, listener);
                configureOptionsListView(optionsListView, optionsAdapter);
            }
        }
        return view;
    }

    @Nullable
    @Override
    protected View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.modalwindows_default_options_bottom_sheet, container);
    }

    /**
     * Получить экземпляр RecyclerView для отображения опций. Переопределите этот метод,
     * если вы переопределили метод {@link #createView(LayoutInflater, ViewGroup, Bundle)}
     * и используете кастомную верстку.
     * @param root - корневой view
     * @return экземпляр RecyclerView
     */
    @Nullable
    protected RecyclerView getOptionsListView(@NonNull View root) {
        return root.findViewById(R.id.modalwindows_options_list);
    }

    @StyleRes
    private int getThemeRes() {
        Integer themeRes = ThemeUtil.getDataFromAttrOrNull(requireContext(), R.attr.optionSheetTheme, true);
        return themeRes != null ? themeRes : R.style.ModalWindowsOptionSheetTheme;
    }
}
