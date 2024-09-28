package ru.tensor.sbis.base_components.adapter;


import android.view.View;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.base_components.adapter.checkable.CheckableViewHolder;
import ru.tensor.sbis.base_components.adapter.selectable.AbstractSelectableViewHolder;

/**
 * Legacy-код
 * @param <DM>
 */
public class AbstractViewHolder<DM> extends AbstractSelectableViewHolder
        implements CheckableViewHolder {

    private boolean mNeedToBeRecycled = false;

    public AbstractViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * Recycle VH if it was not recycled before (ex. notifyItemChanged was executed).
     * Then bind views with data.
     *
     * @param dataModel - model with data to be shown
     */
    @CallSuper
    public void bind(DM dataModel) {
        if (mNeedToBeRecycled && isNeedToRecycleOnBind()) {
            recycle();
        }
        mNeedToBeRecycled = true;
    }

    /**
     * Override it if you need to free something when ViewHolder gets recycled.
     * Ex. Disposable
     * 
     */
    @CallSuper
    public void recycle() {
        mNeedToBeRecycled = false;
    }

    //region CheckableViewHolder interface implementation
    @Override
    public void updateCheckState(boolean checked, boolean animate) {
        //ignore
        //override this method to implement checking logic
    }
    //endregion CheckableViewHolder interface implementation

    /**
     * Нужно ли производить операцию отправки дочерних холдеров на переиспользование в момент вызова
     * {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int, List)}
     *
     * @return true - нужно
     */
    protected boolean isNeedToRecycleOnBind() {
        return true;
    }

}