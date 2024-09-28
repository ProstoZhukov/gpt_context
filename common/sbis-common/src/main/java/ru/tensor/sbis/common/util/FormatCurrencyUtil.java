package ru.tensor.sbis.common.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.common.R;

/**
 * Утилита для форматирования валюты.
 *
 * @author am.boldinov
 */
public class FormatCurrencyUtil {

    /**
     * Шаблоны форматирования для сумм
     */
    private enum CurrencyRoundingPattern {
        CURRENCY_STANDARD_PATTERN("###,###.##"),
        CURRENCY_INCLUDE_DECIMAL_PATTERN("###,##0.00"),
        CURRENCY_ROUNDING_PATTERN("###,###");

        private String mPattern;

        CurrencyRoundingPattern(@NonNull String pattern) {
            mPattern = pattern;
        }

        public String getPattern() {
            return mPattern;
        }
    }

    /**
     * Символ рубля.
     */
    public static final String RUB_CURRENCY = String.valueOf('\u20BD');

    /**
     * Отформатировать валюту в рублях.
     *
     * @param sum            сумма
     * @param pattern        шаблон, по которому необходимо форматировать сумму
     * @param concatCurrency нужно ли добавлять знак рубля
     * @return отформатированную строку с валютой
     */
    private static String formatCurrency(double sum, @NonNull CurrencyRoundingPattern pattern, boolean concatCurrency) {
        String result = createFormatter(pattern).format(sum);
        if (concatCurrency) {
            result += " " + RUB_CURRENCY;
        }
        return result;
    }

    private static DecimalFormat createFormatter(@NonNull CurrencyRoundingPattern roundingPattern) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("en", "UK"));
        symbols.setGroupingSeparator(' ');
        DecimalFormat decimalFormat;
        decimalFormat = new DecimalFormat(roundingPattern.getPattern(), symbols);
        if (roundingPattern == CurrencyRoundingPattern.CURRENCY_ROUNDING_PATTERN) {
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            symbols.setDecimalSeparator('.');
        }
        return decimalFormat;
    }

    /**
     * Отформатировать сумму для ячейки уведомления с постоянным отображением дробной части
     * Спецификацией не регламентируется. Требуется по макету.
     * <p>
     * Задача, по которой это требуется - <a href="https://online.sbis.ru/opendoc.html?guid=0bb5224e-9639-48ba-a85e-1985a8e132e8">online.sbis.ru</a>
     *
     * @param context        контекст
     * @param sum            сумма
     * @param concatCurrency нужно ли добавлять знак рубля
     * @return отформатированный Spannable суммы
     */
    public static Spannable formatCurrencyAsSpannable(@NonNull Context context, double sum, boolean concatCurrency) {
        return formatCurrencyAsSpannable(context, sum, concatCurrency, null, null);
    }

    /**
     * Отформатировать сумму для ячейки уведомления с постоянным отображением дробной части
     * Спецификацией не регламентируется. Требуется по макету.
     * <p>
     * Задача, по которой это требуется - <a href="https://online.sbis.ru/opendoc.html?guid=0bb5224e-9639-48ba-a85e-1985a8e132e8">online.sbis.ru</a>
     *
     * @param context        контекст
     * @param sum            сумма
     * @param concatCurrency нужно ли добавлять знак рубля
     * @param mainTextColor  цвет текста суммы
     * @param mainTextSize   размер текста суммы
     * @return отформатированный Spannable суммы
     */
    public static Spannable formatCurrencyAsSpannable(@NonNull Context context,
                                                      double sum,
                                                      boolean concatCurrency,
                                                      @Nullable @ColorInt Integer mainTextColor,
                                                      @Nullable @Dimension Integer mainTextSize) {
        return formatCurrencyAsSpannable(context, sum, concatCurrency, mainTextColor, mainTextSize, null, null);
    }

    /**
     * Отформатировать сумму для ячейки уведомления с постоянным отображением дробной части
     * Спецификацией не регламентируется. Требуется по макету.
     *
     * @param context          контекст
     * @param sum              сумма
     * @param concatCurrency   нужно ли добавлять знак рубля
     * @param mainTextColor    цвет текста суммы
     * @param mainTextSize     размер текста суммы
     * @param decimalTextColor цвет текста дробной части
     * @param decimalTextSize  размер текста дробной части
     * @return отформатированный Spannable суммы
     */
    public static Spannable formatCurrencyAsSpannable(@NonNull Context context,
                                                      double sum,
                                                      boolean concatCurrency,
                                                      @Nullable @ColorInt Integer mainTextColor,
                                                      @Nullable @Dimension Integer mainTextSize,
                                                      @Nullable @ColorInt Integer decimalTextColor,
                                                      @Nullable @Dimension Integer decimalTextSize) {
        String currency = formatCurrencyAlwaysIncludeDecimal(sum, concatCurrency);
        SpannableString spannable = new SpannableString(currency);
        int dotPosition = currency.indexOf(".");
        spannable.setSpan(new TextAppearanceSpan(context, R.style.MoneyViewDecimalPartStyle), dotPosition, currency.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (mainTextColor != null) {
            spannable.setSpan(new ForegroundColorSpan(mainTextColor), 0, dotPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (mainTextSize != null) {
            spannable.setSpan(new AbsoluteSizeSpan(mainTextSize), 0, dotPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (decimalTextColor == null && decimalTextSize == null) {
            return spannable;
        }
        int endDecimalPosition = concatCurrency ? currency.indexOf(RUB_CURRENCY) : currency.length();
        if (decimalTextColor != null) {
            spannable.setSpan(new ForegroundColorSpan(decimalTextColor), dotPosition, endDecimalPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (decimalTextSize != null) {
            spannable.setSpan(new AbsoluteSizeSpan(decimalTextSize), dotPosition, endDecimalPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * Отформатировать валюту в рублях.
     *
     * @param sum            сумма
     * @param concatCurrency нужно ли добавлять знак рубля
     * @return отформатированную строку с валютой
     */
    public static String formatCurrency(double sum, boolean concatCurrency) {
        return formatCurrency(sum, CurrencyRoundingPattern.CURRENCY_STANDARD_PATTERN, concatCurrency);
    }

    /**
     * Отформатировать валюту в рублях (с округлением до ближайшего целого).
     *
     * @param sum            сумма
     * @param concatCurrency нужно ли добавлять знак рубля
     * @return отформатированную строку с валютой
     */
    public static String formatRoundingCurrency(double sum, boolean concatCurrency) {
        return formatCurrency(sum, CurrencyRoundingPattern.CURRENCY_ROUNDING_PATTERN, concatCurrency);
    }

    /**
     * Отформатировать валюту в рублях (с округлением до ближайшего целого).
     *
     * @param sum            сумма
     * @param concatCurrency нужно ли добавлять знак рубля
     * @param sumColor       цвет текста суммы
     * @return отформатированный Spannable суммы
     */
    public static Spannable formatRoundingCurrencyAsSpannable(double sum,
                                                              boolean concatCurrency,
                                                              @Nullable @ColorInt Integer sumColor) {
        String currency = formatRoundingCurrency(sum, concatCurrency);
        SpannableString spannable = new SpannableString(currency);
        if (sumColor != null) {
            int endSumPosition = concatCurrency ? currency.indexOf(RUB_CURRENCY) : currency.length();
            spannable.setSpan(new ForegroundColorSpan(sumColor), 0, endSumPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * Отформатировать валюту в рублях с постоянным отображением дробной части (2 знака после точки)
     *
     * @param sum            сумма
     * @param concatCurrency нужно ли добавлять знак рубля
     * @return отформатированную строку с валютой
     */
    public static String formatCurrencyAlwaysIncludeDecimal(double sum, boolean concatCurrency) {
        return formatCurrency(sum, CurrencyRoundingPattern.CURRENCY_INCLUDE_DECIMAL_PATTERN, concatCurrency);
    }
}
