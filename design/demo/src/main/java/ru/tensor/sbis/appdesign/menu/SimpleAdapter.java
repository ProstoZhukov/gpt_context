package ru.tensor.sbis.appdesign.menu;

import android.content.Context;
import android.content.res.Resources;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.tensor.sbis.appdesign.R;
import ru.tensor.sbis.appdesign.databinding.ItemDesignElementBinding;

/**
 * Created by da.pavlov1 on 15.11.2017.
 */

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {
    private List<DesignItem> items = new ArrayList<>();
    private OnItemClickListener onClickListener;

    public interface OnItemClickListener {
        void onItemClick(DesignItem item);
    }

    public void setItems(List<DesignItem> items) {
        this.items.clear();
        this.items.addAll(items);

        notifyDataSetChanged();
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_design_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        final DesignItem item = items.get(position);

        Resources res = context.getResources();
        holder.binding.txtName.setText(res.getString(item.getIdRes()));
        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemDesignElementBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
