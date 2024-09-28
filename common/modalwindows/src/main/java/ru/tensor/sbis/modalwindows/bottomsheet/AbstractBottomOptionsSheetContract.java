package ru.tensor.sbis.modalwindows.bottomsheet;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * @author sr.golovkin
 *
 * @deprecated TODO: Будет удалено по <a href="https://online.sbis.ru/opendoc.html?guid=4f5ff4ec-2c38-4e09-92e9-89c7809bb3c8&client=3"></a>
 */
@Deprecated
public interface AbstractBottomOptionsSheetContract {

    interface View extends AbstractBottomSheetContract.View {

    }

    interface Presenter<V extends View, T extends BottomSheetOption> extends AbstractBottomSheetContract.Presenter<V> {

        /**
         * Создать список опций, которые должны быть отображены в панели.
         * @param isLandscape - флаг для landscape ориентации
         * @return список опций
         */
        @NonNull
        List<T> createOptions(boolean isLandscape);

        /**
         * Обработка нажатия на опцию в списке.
         * @param option - нажатая опция
         */
        void onOptionClick(@NonNull T option);

    }

}
