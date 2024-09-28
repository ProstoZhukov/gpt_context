package ru.tensor.sbis.base_components.adapter.universal.pager;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.viewpager.widget.PagerAdapter;
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem;

/**
 * SelfDocumented
 * @author am.boldinov
 */
public abstract class UniversalViewPagerAdapter<T extends UniversalBindingItem> extends PagerAdapter {

    @NonNull
    private final SparseArray<PagerBindingItem> mBindingItems = new SparseArray<>();

    @Nullable
    protected List<T> mContent = Collections.emptyList();

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final LayoutInflater inflater = LayoutInflater.from(container.getContext());
        final ViewDataBinding viewHolder = DataBindingUtil.inflate(inflater, getItemLayout(), container, false);
        if (mContent != null) {
            final UniversalBindingItem bindingItem = mContent.get(position);
            setBindingVariables(viewHolder, bindingItem.getBindingVariables());
            mBindingItems.put(position, new PagerBindingItem(viewHolder, bindingItem));
        }
        final SparseArray<Object> externalVariables = createExternalBingingVariables();
        if (externalVariables != null) {
            setBindingVariables(viewHolder, externalVariables);
        }
        container.addView(viewHolder.getRoot());
        viewHolder.executePendingBindings();
        return viewHolder.getRoot();
    }

    @Override
    public int getCount() {
        return mContent != null ? mContent.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        mBindingItems.remove(position);
    }

    public void setContent(@Nullable List<T> content) {
        mContent = content;
        notifyDataSetChanged();
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        PagerBindingItem objectBindingItem = null;
        Integer objectDataPosition = null;
        // находим uuid-соответствие текущей вьюхе
        for (int j = 0; j < mBindingItems.size(); j++) {
            final PagerBindingItem bindingItem = mBindingItems.valueAt(j);
            if (bindingItem.getViewHolder().getRoot().equals(object)) {
                objectBindingItem = bindingItem;
                objectDataPosition = j; // записываем сразу позицию на случай удаления из кеша
                break;
            }
        }
        if (objectBindingItem != null && mContent != null) {
            // ищем этот uuid в списке с данным, если нашли то возвращаем позицию
            for (int i = 0; i < mContent.size(); i++) {
                final UniversalBindingItem contentItem = mContent.get(i);
                if (contentItem.getItemTypeId().equals(objectBindingItem.getDataBindingItem().getItemTypeId())) {
                    rebindVariables(objectBindingItem, contentItem);
                    return i;
                }
            }
        }
        // если позиция вьюхи в новом списке не найдена, то удаляем ее из кеша
        if (objectDataPosition != null) {
            mBindingItems.removeAt(objectDataPosition);
        }
        return POSITION_NONE;
    }

    @SuppressWarnings("SameReturnValue")
    @Nullable
    protected SparseArray<Object> createExternalBingingVariables() {
        return null;
    }

    @LayoutRes
    protected abstract int getItemLayout();

    private static final class PagerBindingItem {
        @NonNull
        private final ViewDataBinding mViewHolder;
        @NonNull
        private UniversalBindingItem mDataBindingItem;

        private PagerBindingItem(@NonNull ViewDataBinding viewHolder, @NonNull UniversalBindingItem dataBindingItem) {
            mViewHolder = viewHolder;
            mDataBindingItem = dataBindingItem;
        }

        @NonNull
        public ViewDataBinding getViewHolder() {
            return mViewHolder;
        }

        public void setDataBindingItem(@NonNull UniversalBindingItem dataBindingItem) {
            mDataBindingItem = dataBindingItem;
        }

        @NonNull
        public UniversalBindingItem getDataBindingItem() {
            return mDataBindingItem;
        }
    }

    private static void setBindingVariables(@NonNull ViewDataBinding viewHolder, @NonNull SparseArray<Object> variables) {
        for (int i = 0; i < variables.size(); i++) {
            final int variableId = variables.keyAt(i);
            viewHolder.setVariable(variableId, variables.get(variableId));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void rebindVariables(@NonNull PagerBindingItem pagerBindingItem, @NonNull UniversalBindingItem contentItem) {
        if (contentItem instanceof ru.tensor.sbis.base_components.adapter.universal.pager.Rebindable) {
            ((Rebindable) contentItem).rebind(pagerBindingItem.getDataBindingItem()); // перепривязываем необходимые значения из старой модели в новую
        }
        pagerBindingItem.setDataBindingItem(contentItem); // очищаем ссылку на старую модель
        setBindingVariables(pagerBindingItem.getViewHolder(), contentItem.getBindingVariables());
        pagerBindingItem.getViewHolder().executePendingBindings();
    }

}
