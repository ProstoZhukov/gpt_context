package ru.tensor.sbis.design.moneyview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.design.sbis_text_view.SbisTextView;
import ru.tensor.sbis.design.theme.global_variables.FontSize;
import ru.tensor.sbis.design.theme.global_variables.Offset;
import ru.tensor.sbis.design.theme.global_variables.StyleColor;

/**
 * Вью для отображения стоимости с указанием валюты
 *
 * @author ev.grigoreva
 */
public class MoneyView extends LinearLayout {

    private SbisTextView mCost;
    private SbisTextView mCurrency;
    private SimpleDraweeView mVirtualCurrency;

    public MoneyView(Context context) {
        super(context);
        init();
    }

    public MoneyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoneyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MoneyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.design_moneyview, this, true);
        mCost = root.findViewById(R.id.design_moneyview_cost);
        mCost.setTextColor(StyleColor.SECONDARY.getTextColor(getContext()));
        mCost.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSize.XL.getScaleOffDimen(getContext()));

        int horizontalPadding = Offset.S.getDimenPx(getContext());
        int verticalPadding = Offset.XS.getDimenPx(getContext());
        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        setBackgroundResource(R.drawable.design_moneyview_background);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    @NonNull
    private View getCurrencyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.design_moneyview_text_currency, this, false);
    }

    @NonNull
    private View getVirtualCurrencyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.design_moneyview_image_currency, this, false);
    }

    /**
     * Установка значения стоимости
     *
     * @param cost стоимость
     */
    public void setCost(@Nullable CharSequence cost) {
        mCost.setText(cost);
        setVisibility(cost != null ? VISIBLE : GONE);
    }

    /**
     * Установка цвета отображения суммы
     *
     * @param color цвет суммы
     */
    @SuppressWarnings("unused")
    public void setCostColor(@Nullable @ColorInt Integer color) {
        if (color != null) {
            mCost.setTextColor(color);
        }
    }

    /**
     * Установка значения валюты
     *
     * @param currency валюта
     */
    public void setCurrency(@Nullable String currency) {
        prepareCurrencyViewForDisplaying(currency, false);
    }

    /**
     * Установка значения виртуальной валюты
     *
     * @param currencyUrl виртуальная валюта
     */
    public void setVirtualCurrency(@Nullable String currencyUrl) {
        prepareCurrencyViewForDisplaying(currencyUrl, true);
    }

    private void prepareCurrencyViewForDisplaying(@Nullable String value,
                                                  boolean isVirtualCurrency) {
        // скрываем вью для другого типа валюты при необходимости
        View anotherCurrencyView = getViewByType(!isVirtualCurrency);
        if (anotherCurrencyView != null) {
            anotherCurrencyView.setVisibility(View.GONE);
        }

        // проверяем вью для текущего типа валюты
        View currencyView = getViewByType(isVirtualCurrency);
        if (value != null) {
            if (currencyView != null) {
                currencyView.setVisibility(VISIBLE);
            } else {
                addViewByType(isVirtualCurrency);
            }
            setValueByType(value, isVirtualCurrency);
        } else if (currencyView != null) {
            currencyView.setVisibility(GONE);
        }
    }

    @Nullable
    private View getViewByType(boolean isVirtualCurrency) {
        return isVirtualCurrency ? mVirtualCurrency : mCurrency;
    }

    private void addViewByType(boolean isVirtualCurrency) {
        if (isVirtualCurrency) {
            mVirtualCurrency = (SimpleDraweeView) getVirtualCurrencyView();
            addView(mVirtualCurrency);
        } else {
            mCurrency = (SbisTextView) getCurrencyView();
            addView(mCurrency);
        }
    }

    private void setValueByType(@NonNull String value, boolean isVirtualCurrency) {
        if (isVirtualCurrency) {
            mVirtualCurrency.setImageURI(value);
        } else {
            mCurrency.setText(value);
        }
    }
}
