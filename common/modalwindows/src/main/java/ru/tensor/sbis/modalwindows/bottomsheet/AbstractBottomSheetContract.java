package ru.tensor.sbis.modalwindows.bottomsheet;

import ru.tensor.sbis.mvp.presenter.BasePresenter;

/**
 * @author sr.golovkin
 *
 * @deprecated TODO: Будет удалено по <a href="https://online.sbis.ru/opendoc.html?guid=4f5ff4ec-2c38-4e09-92e9-89c7809bb3c8&client=3"></a>
 */
@Deprecated
public interface AbstractBottomSheetContract {

    interface View {

        /**
         * Закрыть панель с опциями.
         */
        void closeDialog();

    }

    interface Presenter<V extends View> extends BasePresenter<V> {

        /**
         * Обработать клик на закрытие панели с опциями.
         */
        void onCancelClick();

    }

}
