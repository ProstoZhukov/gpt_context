package ru.tensor.sbis.scanner.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.tensor.sbis.scanner.R;

/**
 * @author am.boldinov
 */
public final class FlashSwitchView extends LinearLayout {

    private TextView mFlashTextView;
    private TextView mFlashIconView;

    public FlashSwitchView(Context context) {
        super(context);
        init();
    }

    public FlashSwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlashSwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlashSwitchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.flash_switch_view, this, true);
        mFlashTextView = findViewById(R.id.flash_text);
        mFlashIconView = findViewById(R.id.flash_icon);
        mFlashTextView.setMinWidth((int) Math.ceil(mFlashTextView.getPaint().measureText(getMaxFlashText())));
    }

    public void switchToAuto() {
        switchInternal(R.string.scanner_flash_state_auto, ru.tensor.sbis.design.R.string.design_mobile_icon_flash_on, android.R.color.white);
    }

    public void switchToOff() {
        switchInternal(R.string.scanner_flash_state_off, ru.tensor.sbis.design.R.string.design_mobile_icon_flash_off, android.R.color.white);
    }

    public void switchToOn() {
        switchInternal(R.string.scanner_flash_state_on, ru.tensor.sbis.design.R.string.design_mobile_icon_flash_on, ru.tensor.sbis.design.R.color.palette_color_orange8);
    }

    private void switchInternal(@StringRes int textRes, @StringRes int iconRes, @ColorRes int iconColorRes) {
        mFlashTextView.setText(textRes);
        mFlashIconView.setText(iconRes);
        mFlashIconView.setTextColor(ContextCompat.getColor(getContext(), iconColorRes));
    }

    @NonNull
    private String getMaxFlashText() {
        return getResources().getString(R.string.scanner_flash_state_off);
    }
}
