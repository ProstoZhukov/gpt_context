package ru.tensor.sbis.appdesign.scrolltotop;

import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import ru.tensor.sbis.appdesign.R;
import ru.tensor.sbis.design.scroll_to_top.ScrollToTop;
import ru.tensor.sbis.design.scroll_to_top.ScrollToTopListener;

public class ScrollToTopActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_to_top);

        ScrollToTop scrollToTop = findViewById(R.id.scrollToTop);

        ScrollToTopListener scrollToTopListener = new ScrollToTopListener(scrollToTop);

        RecyclerView recyclerView = findViewById(R.id.list_item);
        recyclerView.addOnScrollListener(scrollToTopListener);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(new MyAdapter());

        scrollToTop.setOnClickListener(v -> {
            recyclerView.scrollToPosition(0);
        });
    }

    static class MyAdapter extends RecyclerView.Adapter<ScrollToTopActivity.MyViewHolder> {

        @Override
        public ScrollToTopActivity.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ScrollToTopActivity.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ScrollToTopActivity.MyViewHolder holder, int position) {
            holder.textView.setText(String.format(Locale.getDefault(), "Item %d", position));
        }

        @Override
        public int getItemCount() {
            return 1000;
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}