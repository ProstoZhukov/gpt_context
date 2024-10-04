package ru.tensor.sbis.design.view_ext.buttonwithstate;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.Color;
import android.util.AttributeSet;

import com.mikepenz.iconics.IconicsDrawable;
import ru.tensor.sbis.design.TypefaceManager;
import ru.tensor.sbis.design.view_ext.R;

/**
 * Кнопка c состоянием
 *
 * @author ev.grigoreva
 */
@SuppressWarnings({"JavaDoc"})
public class ButtonWithStateView extends AppCompatButton {

    private static final int UNDEFINED = -1;

    public static final int DEFAULT_ICON_COLOR = Color.BLACK;

    public static final int TYPE_REGULAR = 1;
    public static final int TYPE_PRIMARY = 2;
    public static final int TYPE_COMPOUND = 3;
    public static final int TYPE_DISABLED = 4;

    private IconicsDrawable mCompoundIcon;

    public ButtonWithStateView(Context context) {
        super(context);
        init(null);
    }

    public ButtonWithStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ButtonWithStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        int stateType = UNDEFINED;
        String compoundIcon = null;
        int compoundIconColor = DEFAULT_ICON_COLOR;
        if (attributeSet != null) {
            TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable.ButtonWithStateView);
            stateType = a.getInt(R.styleable.ButtonWithStateView_buttonStateType, stateType);
            compoundIcon = a.getString(R.styleable.ButtonWithStateView_compoundIcon);
            compoundIconColor = a.getColor(R.styleable.ButtonWithStateView_compoundIconColor, compoundIconColor);
            a.recycle();
        }
        setStateType(stateType);
        if (compoundIcon != null) {
            setCompoundIconInternal(compoundIcon, compoundIconColor);
        }
    }

    /**
     * Установить тип состояния кнопки
     *
     * @param stateType Одно из значений {@link #TYPE_REGULAR}, {@link #TYPE_PRIMARY}, {@link #TYPE_COMPOUND}, {@link #TYPE_DISABLED}
     */
    public void setStateType(int stateType) {
        int backgroundRes = UNDEFINED;
        switch (stateType) {
            case TYPE_REGULAR:
                backgroundRes = R.drawable.design_view_ext_button_with_state_default_bg_selector;
                break;
            case TYPE_PRIMARY:
                backgroundRes = R.drawable.design_view_ext_button_with_state_primary_bg_selector;
                break;
            case TYPE_COMPOUND:
                backgroundRes = R.drawable.design_view_ext_button_with_state_compound_bg_selector;
                break;
            case TYPE_DISABLED:
                backgroundRes = ru.tensor.sbis.design.R.color.palette_color_transparent;
                break;
        }
        if (backgroundRes != UNDEFINED) {
            setBackgroundResource(backgroundRes);
        }
    }

    /** @SelfDocumented */
    public void setCompoundIcon(@NonNull String icon) {
        setCompoundIconInternal(icon, DEFAULT_ICON_COLOR);
    }

    /** @SelfDocumented */
    public void clearCompoundIcon() {
        mCompoundIcon = null;
        setCompoundDrawables(null, null, null, null);
    }

    /** @SelfDocumented */
    public void setCompoundIconColor(@ColorInt int color) {
        setCompoundIconInternal(null, color);
    }

    private void setCompoundIconInternal(@Nullable String icon, @ColorInt int color) {
        if (mCompoundIcon == null) {
            mCompoundIcon = new IconicsDrawable(getContext());
            final int bound = getCompoundIconSize();
            mCompoundIcon.setBounds(0, 0, bound,  bound);
            setCompoundDrawablePadding(getContext().getResources().getDimensionPixelSize(R.dimen.design_view_ext_button_with_state_view_compound_padding));
        }
        if (icon != null) {
            mCompoundIcon = mCompoundIcon.iconText(icon);
            mCompoundIcon = mCompoundIcon.typeface(TypefaceManager.getSbisMobileIconTypeface(getContext()));
        }
        if (color != DEFAULT_ICON_COLOR) {
            mCompoundIcon = mCompoundIcon.color(color);
        }
        if (mCompoundIcon.getPlainIcon() != null) {
            setCompoundDrawables(mCompoundIcon, null, null, null);
        }
    }

    /** @SelfDocumented */
    protected int getCompoundIconSize() {
        return Math.round(getTextSize());
    }
}
