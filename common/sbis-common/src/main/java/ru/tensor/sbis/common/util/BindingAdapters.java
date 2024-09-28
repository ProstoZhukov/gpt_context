package ru.tensor.sbis.common.util;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.view.IconicsImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import kotlin.jvm.functions.Function1;
import ru.tensor.sbis.design.SbisMobileIcon;
import ru.tensor.sbis.design.utils.FormatUtils;
import ru.tensor.sbis.design.utils.iconics.IconicUtils;

public class BindingAdapters {

    @BindingAdapter("onClick")
    public static void onClick(@NonNull View view, @Nullable Runnable runnable) {
        view.setOnClickListener(v -> {
            if (runnable != null) {
                runnable.run();
            }
        });
    }

    @BindingAdapter("onLongClick")
    public static void onLongClick(@NonNull View view, @NonNull Runnable runnable) {
        view.setOnLongClickListener(v -> {
            runnable.run();
            return true;
        });
    }

    @BindingAdapter("textColorRes")
    public static void setTextColorRes(@NonNull TextView view,
                                       @ColorRes int colorResId) {
        if (colorResId != 0) {
            view.setTextColor(ContextCompat.getColor(view.getContext(), colorResId));
        }
    }

    @BindingAdapter("textSizeRes")
    public static void setTextSizeRes(@NonNull TextView view,
                                      @DimenRes int sizeResId) {
        if (sizeResId != 0) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.getContext().getResources().getDimension(sizeResId));
        }
    }

    @BindingAdapter({"highlightedText", "linkColor"})
    public static void setHighlightedText(@NonNull TextView view, @NonNull String text, @ColorInt int linkColor) {
        view.setText(TextFormatUtils.highlightLinks(text, linkColor));
    }

    @BindingAdapter({"highlightedText", "linkColor", "onUrlClick"})
    public static void setHighlightedText(@NonNull TextView view, @NonNull String text, @ColorInt int linkColor, Function1<String, Boolean> urlClickHandler) {
        view.setText(TextFormatUtils.highlightLinks(text, linkColor, urlClickHandler));
    }

    @BindingAdapter("layout_marginLeft")
    public static void setMarginLeft(@NonNull View view,
                                     @DimenRes int marginLeftResId) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.leftMargin = (int)view.getContext().getResources().getDimension(marginLeftResId);
        view.setLayoutParams(lp);
    }

    @BindingAdapter("layout_marginRight")
    public static void setMarginRight(@NonNull View view,
                                      @DimenRes int marginRightResId) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.rightMargin = (int)view.getContext().getResources().getDimension(marginRightResId);
        view.setLayoutParams(lp);
    }

    @BindingAdapter("layout_marginTop")
    public static void setMarginTop(@NonNull View view,
                                    @DimenRes int marginTopResId) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.topMargin = (int) view.getContext().getResources().getDimension(marginTopResId);
        view.setLayoutParams(lp);
    }

    @BindingAdapter("layout_marginBottom")
    public static void setMarginBottom(@NonNull View view,
                                       @DimenRes int marginBottomResId) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.bottomMargin = (int) view.getContext().getResources().getDimension(marginBottomResId);
        view.setLayoutParams(lp);
    }

    @BindingAdapter("layout_marginHorizontal")
    public static void setMarginHorizontal(@NonNull View view,
                                           @DimenRes int marginHorizontalResId) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int horizontalMargin = (int) view.getContext().getResources().getDimension(marginHorizontalResId);
        lp.leftMargin = horizontalMargin;
        lp.rightMargin = horizontalMargin;
        view.setLayoutParams(lp);
    }

    @BindingAdapter("iconicsImageViewColor")
    public static void photoCollectionData(@NonNull IconicsImageView iconicsImageView, @ColorRes int color) {
        IconicUtils.setColorRes(iconicsImageView, color);
    }

    @BindingAdapter("iconicsImageViewIcon")
    public static void iconicsImageViewIconString(@NonNull IconicsImageView iconicsImageView, @NonNull String icon) {
        IconicUtils.setIcon(iconicsImageView, icon);
    }

    @BindingAdapter("iconicsImageViewIcon")
    public static void iconicsImageViewIIcon(@NonNull IconicsImageView iconicsImageView, @NonNull IIcon icon) {
        IconicUtils.setIcon(iconicsImageView, icon);
    }

    @BindingAdapter("visibilityEqualsGone")
    public static void isVisibilityOrGone(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter({"isVisible", "delay"})
    public static void isVisibleDelayed(View view, boolean visible, int delay) {
        final Runnable hideCallback = () -> view.setVisibility(View.INVISIBLE);
        view.removeCallbacks(hideCallback);
        if (visible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.postDelayed(hideCallback, delay);
        }
    }

    @BindingAdapter("invisibleOrGone")
    public static void isInvisibleOrGone(View view, boolean invisible) {
        view.setVisibility(invisible ? View.INVISIBLE : View.GONE);
    }

    @BindingAdapter("mobileIcon")
    public static void setMobileIcon(@NonNull TextView textView, @NonNull SbisMobileIcon.Icon icon) {
        textView.setText(String.valueOf(icon.getCharacter()));
    }

    @BindingAdapter("resId")
    public static void setTextRes(@NonNull TextView view,
                                  @StringRes int text) {
        if (text != 0) {
            view.setText(text);
        } else {
            view.setText("");
        }
    }

    @BindingAdapter("fontStyle")
    public static void setFontStyle(@NonNull TextView textView, int fontStyle) {
        textView.setTypeface(null, fontStyle);
    }

    @BindingAdapter("textVisibility")
    public static void setTextVisibility(@NonNull TextView view, String text) {
        view.setVisibility((text == null || text.isEmpty()) ? View.GONE : View.VISIBLE);
    }

    @BindingAdapter("formattedCount")
    public static void setFormattedCount(@NonNull TextView view, int count) {
        view.setText(FormatUtils.formatCount(count));
    }

    @BindingAdapter("selected")
    public static void setSelected(@NonNull View view,
                                   boolean selected) {
        view.setSelected(selected);
    }

    @BindingAdapter({"folderLevel", "paddingForLevel"})
    public static void paddingForLevel(@NonNull View view,
                                       int level,
                                       float paddingForLevel) {
        view.setPadding((int) (level * paddingForLevel),
                view.getPaddingTop(),
                view.getPaddingRight(),
                view.getPaddingBottom());
    }
}