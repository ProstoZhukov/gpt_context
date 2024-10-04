package ru.tensor.sbis.design.view_ext;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.tensor.sbis.design.view_ext.databinding.DesignViewExtFloatingButtonsPanelBinding;

/**
 * Floating buttons panel for SBIS 2.
 * See design guidelines at http://axure.tensor.ru/MobileAPP/#p=кнопки_описание
 */
@SuppressWarnings({"unused", "ConstantConditions", "JavaDoc"})
public class FloatingButtonsBar extends FrameLayout {

    // Main container for buttons
    /** @SelfDocumented */
    protected LinearLayout mMainContainer;

    private List<FloatingButton> mButtons;

    public FloatingButtonsBar(@NonNull Context context) {
        this(context, null);
    }

    public FloatingButtonsBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingButtonsBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        mButtons = new ArrayList<>();
        DesignViewExtFloatingButtonsPanelBinding viewBinding = DesignViewExtFloatingButtonsPanelBinding.inflate(LayoutInflater.from(context), this,true);
        mMainContainer = viewBinding.buttonsContainer;
    }

    @Nullable
    public FloatingButton getButton(int id) {
        for (int i = 0; i < mButtons.size(); i++) {
            FloatingButton button = mButtons.get(i);
            if (button.id == id) {
                return button;
            }
        }
        return null;
    }

    /**
     * Adds a floating button to the panel.
     *
     * @param button subclassed FloatingButton object.
     */
    public void addButton(@NonNull final FloatingButton button) {
        generateButton(button, -1);
    }

    /**
     * Inserts a text button to the panel at specified position.
     *
     * @param button   subclassed FloatingButton object.
     * @param position the position at which to add the button.
     */
    public void insertButton(@NonNull final FloatingButton button, final int position) {
        generateButton(button, position);
    }

    /**
     * Removes the button from the panel.
     *
     * @param button the button to remove.
     */
    public void removeButton(@NonNull final FloatingButton button) {
        mButtons.remove(button);
        refresh();
    }

    /**
     * Removes the button at specified position from the panel.
     *
     * @param position the position of the removing button.
     */
    public void removeButton(final int position) {
        mButtons.remove(position);
        refresh();
    }

    /**
     * Changes button visibility.
     *
     * @param button  the button to change visibility.
     * @param visible boolean
     */
    public void setButtonVisible(@NonNull final FloatingButton button, boolean visible) {
        button.visible = visible;
        refresh();
    }

    /**
     * Changes button enabled.
     *
     * @param button  the button to change enable.
     * @param enabled boolean
     */
    public void setButtonEnabled(@NonNull final FloatingButton button, boolean enabled) {
        button.enabled = enabled;
        if (button.inProgress && enabled) {
            button.inProgress = false;
        }
        refresh();
    }

    /**
     * Changes button progress state.
     *
     * @param button     the button to change enable.
     * @param inProgress boolean
     */
    public void setButtonInProgress(@NonNull final FloatingButton button, boolean inProgress) {
        button.inProgress = inProgress;
        button.enabled = !inProgress;
        refresh();
    }

    private void generateButton(@NonNull final FloatingButton button, final int position) {
        if (mButtons.size() >= 3) {
            Log.w("FloatingButtonsBar", "FloatingButtonsBar should contain 1 to 3 buttons!");
        }
        mButtons.add(position == -1 ? mButtons.size() : position, button);
        refresh();
    }

    private void refresh() {
        mMainContainer.removeAllViews();

        if (mButtons.size() > 0) {
            int visibleButtonsCount = getVisibleButtonsCount();
            boolean oneButton = getVisibleButtonsCount() == 1;
            /*
             * Special case when we have only one button.
             * If it is an iconic button, place it at left.
             * If it is a text button, place it horizontally centered.
             */
            if (oneButton && !getFirstVisibleButton().canStretch()) {
                mMainContainer.setGravity(Gravity.NO_GRAVITY);
                mMainContainer.setWeightSum(0);
            } else {
                mMainContainer.setGravity(Gravity.CENTER_HORIZONTAL);
                mMainContainer.setWeightSum(oneButton ? 1 : 0);
            }

            int margin = getContext().getResources().getDimensionPixelSize(R.dimen.design_view_ext_floating_button_margin);
            LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(margin, ViewGroup.LayoutParams.MATCH_PARENT);

            int buttonSize = getContext().getResources().getDimensionPixelSize(R.dimen.design_view_ext_floating_button_height);

            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

            for (int i = 0, inflatedButtons = 0; i < mButtons.size(); i++) {
                FloatingButton button = mButtons.get(i);
                if (button.isVisible()) {
                    View buttonView = button.getView(getContext());
                    LinearLayout.LayoutParams layoutParams =
                            new LinearLayout.LayoutParams(
                                    button.canStretch() ?
                                            oneButton ? LayoutParams.WRAP_CONTENT : 0
                                            : buttonSize,
                                    buttonSize);
                    layoutParams.bottomMargin = margin;
                    if (button.canStretch()) {
                        layoutParams.weight = oneButton ? 0 : 1;
                        if (oneButton) {
                            buttonView.setMinimumWidth(screenWidth / 2);
                        }
                    }
                    mMainContainer.addView(buttonView, layoutParams);

                    if (!oneButton && inflatedButtons < visibleButtonsCount - 1) {
                        mMainContainer.addView(new View(getContext()), separatorParams);
                    }

                    inflatedButtons++;
                }
            }
        }
    }

    private int getVisibleButtonsCount() {
        int count = 0;
        for (FloatingButton button : mButtons) {
            count += button.isVisible() ? 1 : 0;
        }
        return count;
    }

    @Nullable
    private FloatingButton getFirstVisibleButton() {
        for (FloatingButton button : mButtons) {
            if (button.isVisible()) {
                return button;
            }
        }
        return null;
    }


    /**
     * Abstract Floating button class
     */
    public static abstract class FloatingButton {
        int id;
        boolean priority;
        boolean visible;
        boolean enabled;
        boolean inProgress;
        View view;
        OnClickListener onClickListener;

        public FloatingButton(int id, boolean priority, @Nullable final OnClickListener onClickListener) {
            this.id = id;
            this.priority = priority;
            this.onClickListener = onClickListener;
            this.visible = true;
            this.enabled = true;
            this.inProgress = false;
        }

        /**
         * @return true if this button has priority (orange button)
         */
        public boolean isPriority() {
            return priority;
        }

        /**
         * Sets the priority to the button. If true, button becomes orange.
         *
         * @param priority boolean
         */
        public void setPriority(boolean priority) {
            if (this.priority != priority) {
                this.priority = priority;
                if (view != null) {
                    view.setBackgroundResource(priority ? R.drawable.design_view_ext_background_floating_button_priority_selector : R.drawable.design_view_ext_background_floating_button_usual_selector);
                }
            }
        }

        /**
         * Set whether this view can receive the focus.
         *
         * @param focusable If true, this view can receive the focus.
         */
        public void setFocusable(boolean focusable) {
            if (view != null) {
                view.setFocusable(focusable);
            }
        }

        /**
         * @return true if this button is visible to user.
         */
        public boolean isVisible() {
            return visible;
        }

        /**
         * Sets button visibility.
         *
         * @param visible boolean
         */
        void setVisible(boolean visible) {
            this.visible = visible;
        }

        /**
         * @return true if this button is enabled for user.
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Sets button enabled.
         *
         * @param enabled boolean
         */
        void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Sets the OnClickListener to the button.
         *
         * @param onClickListener the listener to set
         */
        public void setOnClickListener(@Nullable final OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            if (view != null) {
                view.setOnClickListener(onClickListener);
            }
        }

        /**
         * @return true if this button is in progress.
         */
        public boolean isInProgress() {
            return inProgress;
        }

        /**
         * Sets button in progress.
         *
         * @param inProgress boolean
         */
        void setInProgress(boolean inProgress) {
            this.inProgress = inProgress;
        }

        @NonNull
        public View getView(@NonNull Context context) {
            if (inProgress) {
                FrameLayout container = new FrameLayout(context);
                ProgressBar progressBar = new ProgressBar(context);
                progressBar.setIndeterminate(true);
                int size = context.getResources().getDimensionPixelSize(R.dimen.design_view_ext_floating_button_height);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size, Gravity.CENTER);
                container.addView(progressBar, params);

                view = container;
            } else {
                view = generateView(context);
            }
            view.setBackgroundResource(priority ? R.drawable.design_view_ext_background_floating_button_priority_selector : R.drawable.design_view_ext_background_floating_button_usual_selector);
            view.setClickable(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setElevation(context.getResources().getDimensionPixelSize(R.dimen.design_view_ext_floating_button_elevation));
            }
            view.setEnabled(enabled);
            view.setOnClickListener(onClickListener);
            return view;
        }

        void setView(@NonNull View view) {
            this.view = view;
        }

        public abstract boolean canStretch();

        @NonNull
        public abstract View generateView(@NonNull Context context);
    }


    /**
     * Floating button with text label
     */
    public static class FloatingTextButton extends FloatingButton {
        private String text;
        @Nullable
        private Drawable leftIcon;

        public FloatingTextButton(@Nullable String text,
                                  final boolean priority,
                                  @Nullable final OnClickListener onClickListener) {
            this(View.generateViewId(), text, priority, onClickListener);
        }

        public FloatingTextButton(int id,
                                  @Nullable String text,
                                  final boolean priority,
                                  @Nullable final OnClickListener onClickListener) {
            super(id, priority, onClickListener);
            this.text = text;
        }

        /**
         * @return button text as String.
         */
        public String getText() {
            return text;
        }

        /**
         * Sets the text label.
         *
         * @param text String
         */
        public void setText(@Nullable final String text) {
            this.text = text;
            LinearLayout container = (LinearLayout) view;
            if (container != null) {
                updateText(container);
            }
        }

        public void setLeftIcon(@Nullable Drawable icon) {
            leftIcon = icon;
            LinearLayout container = (LinearLayout) view;
            if (container != null) {
                updateLeftIcon(container);
            }
        }

        @Override
        public boolean canStretch() {
            return true;
        }

        @NonNull
        @Override
        public View generateView(@NonNull Context context) {
            LinearLayout container = new LinearLayout(context);
            container.setPadding(
                    context.getResources().getDimensionPixelSize(R.dimen.design_view_ext_floating_button_margin),
                    0,
                    context.getResources().getDimensionPixelSize(R.dimen.design_view_ext_floating_button_margin),
                    0);
            container.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams leftIconLP =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
            leftIconLP.setMarginEnd(
                    context.getResources().getDimensionPixelSize(R.dimen.design_view_ext_floating_button_text_left_icon_padding)
            );
            container.addView(generateLeftIconView(context), leftIconLP);
            container.addView(
                    generateTextView(context),
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );
            updateLeftIcon(container);
            return container;
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        private ImageView generateLeftIconView(@NonNull Context context) {
            ImageView leftIconView =new AppCompatImageView(context);
            return leftIconView;
        }

        private TextView generateTextView(@NonNull Context context) {
            TextView textView = new TextView(context);
            textView.setTypeface(ResourcesCompat.getFont(context, ru.tensor.sbis.design.R.font.roboto_regular));
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.design_view_ext_floating_button_text_size));
            textView.setTextColor(ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.palette_alpha_color_black8));
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            return textView;
        }

        private void updateText(@NonNull LinearLayout container) {
            ((TextView) container.getChildAt(1)).setText(text);
        }

        private void updateLeftIcon(@NonNull LinearLayout container) {
            ImageView leftIconView = (ImageView) container.getChildAt(0);
            if (leftIcon == null) {
                leftIconView.setVisibility(View.GONE);
            } else {
                leftIconView.setImageDrawable(leftIcon);
                leftIconView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Floating button with text icon
     */
    public static class FloatingIconicButton extends FloatingButton {
        private String icon;

        public FloatingIconicButton(@NonNull String icon,
                                    final boolean priority,
                                    @Nullable final OnClickListener onClickListener) {
            this(View.generateViewId(), icon, priority, onClickListener);
        }

        public FloatingIconicButton(int id,
                                    @NonNull String icon,
                                    final boolean priority,
                                    @Nullable final OnClickListener onClickListener) {
            super(id, priority, onClickListener);
            if (icon.length() != 1) {
                throw new IllegalArgumentException("Icon must be 1-length string!");
            }
            this.icon = icon;
        }

        /**
         * @return button icon as String.
         */
        public String getIcon() {
            return icon;
        }

        /**
         * Sets the icon.
         * Icon must be from sbis-mobile-icons.ttf font.
         *
         * @param icon String
         */
        public void setIcon(@NonNull final String icon) {
            if (icon.length() != 1) {
                throw new IllegalArgumentException("Icon must be 1-length string!");
            }
            this.icon = icon;
            if (view != null) {
                ((TextView) view).setText(icon);
            }
        }

        @Override
        public boolean canStretch() {
            return false;
        }

        @NonNull
        @Override
        public View generateView(@NonNull Context context) {
            TextView textView = new TextView(context);
            textView.setTypeface(ResourcesCompat.getFont(context, ru.tensor.sbis.design.R.font.sbis_mobile_icons));
            textView.setText(icon);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.design_view_ext_floating_button_icon_size));
            textView.setTextColor(ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.palette_alpha_color_black8));
            textView.setGravity(Gravity.CENTER);

            return textView;
        }
    }

    /**
     * Floating button with image icon
     */
    public static class FloatingImageButton extends FloatingButton {
        private Drawable image;

        public FloatingImageButton(@NonNull Drawable image,
                                   final boolean priority,
                                   @Nullable final OnClickListener onClickListener) {
            this(View.generateViewId(), image, priority, onClickListener);
        }

        public FloatingImageButton(int id,
                                   @NonNull Drawable image,
                                   final boolean priority,
                                   @Nullable final OnClickListener onClickListener) {
            super(id, priority, onClickListener);
            this.image = image;
        }

        /**
         * @return button icon as Drawable.
         */
        public Drawable getImage() {
            return image;
        }

        /**
         * Sets the icon.
         *
         * @param image Drawable
         */
        public void setImage(@NonNull final Drawable image) {
            this.image = image;
            if (view != null) {
                ((ImageView) ((FrameLayout) view).getChildAt(0)).setImageDrawable(image);
            }
        }

        @Override
        public boolean canStretch() {
            return false;
        }

        @NonNull
        @Override
        public View generateView(@NonNull Context context) {
            FrameLayout container = new FrameLayout(context);
            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(image);
            int size = context.getResources().getDimensionPixelSize(R.dimen.design_view_ext_floating_button_height);
            LayoutParams params = new LayoutParams(size, size, Gravity.CENTER);
            container.addView(imageView, params);

            return container;
        }
    }
}
