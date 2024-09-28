package ru.tensor.sbis.base_components.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.base_components.R;
import ru.tensor.sbis.base_components.checkablefiles.CheckableFile;

public class RecentMediaListAdapter extends AbstractListAdapter<CheckableFile, RecentMediaListAdapter.RecentMediaViewHolder> {

    public static final int UNCHECKABLE_TYPE = 0;
    public static final int CHECKABLE_TYPE = 1;
    public static final int CHECKABLE_ITEM_VIEW_TYPE = -1;

    private List<CheckableFile> mItemsList;
    private final LayoutInflater mLayoutInflater;
    private final int mAdapterType;

    private final PipelineDraweeControllerBuilder mDraweeControllerBuilder = Fresco.newDraweeControllerBuilder();
    @Nullable
    private final OnRecentMediaClickListener mOnRecentMediaClickListener;

    @Nullable
    private final OnFileCheckedStateChangeListener mOnFileCheckedStateChangeListener;

    public RecentMediaListAdapter(@NonNull Context context,
                                  @NonNull List<File> itemsList,
                                  @NonNull OnRecentMediaClickListener onRecentMediaClickListener) {
        this(context, UNCHECKABLE_TYPE, onRecentMediaClickListener, null);
        List<CheckableFile> checkableFiles = new ArrayList<>(itemsList.size());
        for (File file : itemsList) {
            checkableFiles.add(new CheckableFile(file));
        }
        setCheckableFiles(checkableFiles);
    }

    @SuppressWarnings("unused")
    public RecentMediaListAdapter(@NonNull Context context,
                                  @NonNull List<CheckableFile> itemsList,
                                  int adapterType) {
        this(context, adapterType, null, null);
        setCheckableFiles(itemsList);
    }

    public RecentMediaListAdapter(@NonNull Context context,
                                  int adapterType,
                                  @Nullable OnRecentMediaClickListener onRecentMediaClickListener,
                                  @Nullable OnFileCheckedStateChangeListener onFileCheckedStateChangeListener) {
        mLayoutInflater = LayoutInflater.from(context);
        mOnRecentMediaClickListener = onRecentMediaClickListener;
        mAdapterType = adapterType;
        mOnFileCheckedStateChangeListener = onFileCheckedStateChangeListener;
    }

    public void setCheckableFiles(@NonNull List<CheckableFile> checkableFiles) {
        mItemsList = checkableFiles;
    }

    @NonNull
    @Override
    public RecentMediaViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int position) {
        if (mAdapterType == CHECKABLE_TYPE) {
            return new RecentMediaViewHolder(
                    mLayoutInflater.inflate(R.layout.base_components_recent_media_list_selectable_item,
                            viewGroup, false), mDraweeControllerBuilder,
                    mOnRecentMediaClickListener, mOnFileCheckedStateChangeListener);
        } else {
            return new RecentMediaViewHolder(
                    mLayoutInflater.inflate(R.layout.base_components_recent_media_list_item,
                            viewGroup, false), mDraweeControllerBuilder,
                    mOnRecentMediaClickListener, mOnFileCheckedStateChangeListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecentMediaViewHolder itemHolder, final int position) {
        itemHolder.setContent(mItemsList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapterType == CHECKABLE_TYPE ? CHECKABLE_ITEM_VIEW_TYPE : UNCHECKABLE_TYPE;
    }

    @Override
    public int getItemCount() {
        return mItemsList != null ? mItemsList.size() : 0;
    }

    public static class RecentMediaViewHolder extends RecyclerView.ViewHolder {

        private final String mIconCheck;
        @NonNull
        final SimpleDraweeView mDraweeView;
        @NonNull
        final View mPlaceholderView;
        @NonNull
        private final TextView mPictureChecker;
        private boolean mChecked;
        @NonNull
        private final FrameLayout mPictureCheckerFrame;
        @NonNull
        final PipelineDraweeControllerBuilder mDraweeControllerBuilder;
        @Nullable
        private CheckableFile mCheckableFile;

        final ControllerListener<ImageInfo> mControllerListener;

        public RecentMediaViewHolder(@NonNull View itemView, @NonNull PipelineDraweeControllerBuilder draweeControllerBuilder,
                                     @Nullable OnRecentMediaClickListener onRecentMediaClickListener,
                                     @Nullable OnFileCheckedStateChangeListener onFileCheckedStateChangeListener) {
            super(itemView);
            mIconCheck = itemView.getContext().getResources().getString(ru.tensor.sbis.design.R.string.design_mobile_icon_check);
            mDraweeView = itemView.findViewById(R.id.base_components_preview_file);
            mPictureChecker = itemView.findViewById(R.id.base_components_picture_check);
            mPlaceholderView = itemView.findViewById(R.id.base_components_preview_placeholder);
            mPictureCheckerFrame = itemView.findViewById(R.id.base_components_picture_check_frame);
            mDraweeControllerBuilder = draweeControllerBuilder;
            final int maxWidth = itemView.getResources().getDimensionPixelSize(R.dimen.base_components_recent_media_attachment_preview_max_width);
            itemView.setOnClickListener(view -> {
                    if (mPictureChecker != null) {
                        if(mCheckableFile != null && onFileCheckedStateChangeListener != null) {
                            if(onFileCheckedStateChangeListener.onFileCheckClicked(mCheckableFile, getAdapterPosition())) {
                                setChecked(mPictureChecker, !mChecked);
                            }
                        }
                    }
                    if (onRecentMediaClickListener != null) {
                        onRecentMediaClickListener.onRecentMediaClick(mCheckableFile.getFile());
                    }
                });

            mControllerListener = new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    ViewGroup.LayoutParams params = mDraweeView.getLayoutParams();
                    float aspectRatio = (float) imageInfo.getWidth() / (float) imageInfo.getHeight();
                    params.width = (int) (aspectRatio * params.height);
                    if (params.width > maxWidth) {
                        params.width = maxWidth;
                    }
                    mDraweeView.setLayoutParams(params);
                    mPlaceholderView.setVisibility(View.GONE);
                }
            };
        }

        public void setContent(@NonNull CheckableFile checkableFile) {
            mCheckableFile = checkableFile;
            mPlaceholderView.setVisibility(View.VISIBLE);
            mDraweeView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            DraweeController controller = mDraweeControllerBuilder
                    .setAutoPlayAnimations(true)
                    .setUri(Uri.fromFile(checkableFile.getFile()))
                    .setOldController(mDraweeView.getController())
                    .setControllerListener(mControllerListener)
                    .build();
            mDraweeView.setController(controller);
            setChecked(mPictureChecker, checkableFile.isChecked());
        }

        @NonNull
        public View getPictureCheckerFrame() {
            return mPictureCheckerFrame;
        }

        private void setChecked(@NonNull TextView pictureChecker, boolean isChecked) {
            mChecked = isChecked;
            if (isChecked) {
                pictureChecker.setText(mIconCheck);
            } else {
                pictureChecker.setText("");
            }
        }
    }

    public interface OnRecentMediaClickListener {
        void onRecentMediaClick(@NonNull File file);
    }

    public interface OnFileCheckedStateChangeListener {
        /**
         * Действие при клике на выбор файла из адаптера
         * @param file - файл которые в данный момент отмечаем
         * @param position - позиция файла в adapter
         * @return true - если удалось сменить состояние отмеченности файла на противоположное
         */
        boolean onFileCheckClicked(@NonNull CheckableFile file, int position);
    }
}