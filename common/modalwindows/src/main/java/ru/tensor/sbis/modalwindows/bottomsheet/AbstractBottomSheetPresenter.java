package ru.tensor.sbis.modalwindows.bottomsheet;

import ru.tensor.sbis.mvp.presenter.AbstractBasePresenter;

/**
 * Абстрактный презентер для нижней панели с опциями.
 * @param <V> - тип view
 * @param <T> - тип опций
 *
 * @author sr.golovkin
 *
 * @deprecated TODO: Будет удалено по <a href="https://online.sbis.ru/opendoc.html?guid=4f5ff4ec-2c38-4e09-92e9-89c7809bb3c8&client=3"></a>
 */
@Deprecated
public abstract class AbstractBottomSheetPresenter<V extends AbstractBottomOptionsSheetContract.View, T extends BottomSheetOption>
        extends AbstractBasePresenter<V, Object>
        implements AbstractBottomOptionsSheetContract.Presenter<V, T> {

    protected AbstractBottomSheetPresenter() {
        super(null);
    }

    @Override
    public void onCancelClick() {
        if (mView != null) {
            mView.closeDialog();
        }
    }

}
