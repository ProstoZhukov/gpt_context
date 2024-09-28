package ru.tensor.sbis.design.toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import ru.tensor.sbis.design.sbis_text_view.SbisTextView;
import ru.tensor.sbis.design.toolbar.databinding.ToolbarButtonViewBinding;

/**
 * Created by am.boldinov on 16.06.15.
 * Использовать {@link SbisBottomButtonsView}, внешний вид такой же, но в использовании удобнее
 */
@SuppressWarnings("unused")
@Deprecated
public class SbisButtonToolbarView extends FrameLayout {

    private static final String CLOSE_TAG = "close";
    private static final int DEFAULT_THEME = 2;

    CloseButtonListener mListener;
    protected LinearLayout mMainContainer;
    protected boolean hideForTablet;
    private Paint mLinePaint;

    private int mTheme;

    public SbisButtonToolbarView(@NonNull Context context) {
        this(context, null);
    }

    public SbisButtonToolbarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SbisButtonToolbarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    private void initialize(@Nullable AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        boolean btnCloseVisible = false;

        if (attrs != null) {
            TypedArray attributeArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SbisButtonToolbarView, 0, 0);
            try {
                btnCloseVisible = attributeArray.getBoolean(R.styleable.SbisButtonToolbarView_closeButtonVisible, false);
                hideForTablet = attributeArray.getBoolean(R.styleable.SbisButtonToolbarView_hideTextForTablet, false);
                mTheme = attributeArray.getInt(R.styleable.SbisButtonToolbarView_panelTheme, DEFAULT_THEME);
            } finally {
                attributeArray.recycle();
            }
        }

        int maxHeight = getResources().getDimensionPixelSize(R.dimen.toolbar_bottom_buttons_view_height);
        ToolbarButtonViewBinding viewBinding = ToolbarButtonViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
        View toolbar = viewBinding.toolbarRoot;

        setHeight(toolbar, maxHeight);
        setHeight(toolbar.findViewById(R.id.toolbar_button_container), maxHeight);
        setBackgroundColor(ContextCompat.getColor(getContext(), mTheme != DEFAULT_THEME ? R.color.toolbar_bottom_background_color : R.color.toolbar_sbis_bottom_toolbar_backgorund_color));

        mMainContainer = viewBinding.toolbarButtonContainer;

        if (btnCloseVisible) {
            addCloseButton();
        }

        if (mTheme != DEFAULT_THEME) {
            mLinePaint = new Paint();
            mLinePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
            mLinePaint.setStyle(Paint.Style.FILL);
            mLinePaint.setColor(ContextCompat.getColor(getContext(), ru.tensor.sbis.design.R.color.palette_color_gray6));
        }
    }

    @SuppressLint("CanvasSize")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLinePaint != null) {
            canvas.drawRect(0, 0, canvas.getWidth(), mLinePaint.getStrokeWidth(), mLinePaint);
        }
    }

    public void setListener(CloseButtonListener listener) {
        mListener = listener;
    }

    @SuppressWarnings("unused")
    public View addButtonInCenter(String icon, String text, OnClickListener onClickListener) {
        View button = generateButton(icon, text, true, onClickListener);
        return addButtonInCenter(button);
    }

    @SuppressWarnings("unused")
    private View addButtonInCenter(View button) {
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayoutParams.weight = 1;
        View closeButton = mMainContainer.findViewWithTag(CLOSE_TAG);
        if (closeButton != null) {
            mMainContainer.removeView(closeButton);
            mMainContainer.addView(button, linearLayoutParams);
            mMainContainer.addView(closeButton, linearLayoutParams);
        } else {
            mMainContainer.addView(button, linearLayoutParams);
        }
        return button;
    }

    @SuppressWarnings("unused")
    private void addCloseButton() {
        View button = generateButton(getResources().getString(ru.tensor.sbis.design.R.string.design_mobile_icon_close_circle), getResources().getString(R.string.toolbar_button_cancel), true, v -> {
            if (mListener != null) {
                mListener.onClickClose();
            }
        });
        button.setTag(CLOSE_TAG);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayoutParams.weight = 1;
        mMainContainer.addView(button, linearLayoutParams);
    }

    public View generateButton(String icon, String text, boolean vertical, OnClickListener onClickListener) {
        boolean isTablet = getResources().getBoolean(ru.tensor.sbis.design.R.bool.is_tablet);
        Context context = getContext();
        View button = inflateButton(vertical, context, R.drawable.toolbar_selectable_bottom_button);

        SbisTextView iconButton = button.findViewById(R.id.toolbar_panel_button_icon_text);
        iconButton.setTextColor(getColorStateListForTheme());
        iconButton.setText(icon);

        SbisTextView buttonText = button.findViewById(R.id.toolbar_panel_button_text);
        if (text != null && text.length() > 0 && (!vertical || !isTablet || !hideForTablet)) {
            buttonText.setText(text);
            buttonText.setVisibility(VISIBLE);
        } else {
            buttonText.setVisibility(GONE);
        }
        buttonText.setTextColor(getColorStateListForTheme());
        button.setOnClickListener(onClickListener);
        return button;
    }

    protected View inflateButton(boolean vertical, Context context, int backgroundDrawableId) {
        View button;
        if (vertical) {
            button = LayoutInflater.from(context).inflate(R.layout.toolbar_bottom_panel_button, this, false);
        } else {
            button = LayoutInflater.from(context).inflate(R.layout.toolbar_bottom_panel_button_horizontal, this, false);
        }
        button.findViewById(R.id.toolbar_main).setBackground(ResourcesCompat.getDrawable(getResources(), backgroundDrawableId, null));
        return button;
    }

    public void setButtonEnabled(@NonNull View button, boolean enabled) {
        button.setEnabled(enabled);
        View iconView = button.findViewById(R.id.toolbar_panel_button_icon_text);
        if (iconView != null) {
            iconView.setEnabled(enabled);
        }
        View textView = button.findViewById(R.id.toolbar_panel_button_text);
        if (textView != null) {
            textView.setEnabled(enabled);
        }
    }

    public interface CloseButtonListener {
        @SuppressWarnings("unused")
        void onClickClose();
    }

    public void setHeight(View v, int dp) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.height = dp;
        v.setLayoutParams(layoutParams);
    }

    private ColorStateList getColorStateListForTheme() {
        return ContextCompat.getColorStateList(
                getContext(), mTheme != DEFAULT_THEME ? R.color.toolbar_bottom_action_panel_button_light_theme : R.color.toolbar_white_and_disabled_grey_text_color
        );
    }

}