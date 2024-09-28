package ru.tensor.sbis.richtext.converter.cfg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Глобальная конфигурация богатого текста.
 * Рекомендуется задавать в {@link android.app.Application}.
 * Если требуется кастомизировать конфигурацию на конкретных экранах, то
 * необходимо использовать {@link Configuration.Builder} при настройке {@link ru.tensor.sbis.richtext.converter.RichTextConverter}
 *
 * @author am.boldinov
 */
public class RichTextGlobalConfiguration {

    @Nullable
    private static BrConfiguration sBrConfiguration;
    @Nullable
    private static BlockQuoteSpanConfiguration sBlockQuoteConfiguration;
    @Nullable
    private static NumberSpanConfiguration sNumberConfiguration;
    @Nullable
    private static DecoratedLinkConfiguration sDecoratedLinkConfiguration;
    @Nullable
    private static CssConfiguration sCssConfiguration;
    @Nullable
    private static TableConfiguration sTableConfiguration;

    /**
     * Возвращает конфигурацию переноса строк
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static BrConfiguration getBrConfiguration() {
        if (sBrConfiguration == null) {
            sBrConfiguration = new DefaultBrConfiguration();
        }
        return sBrConfiguration;
    }

    /**
     * Устанавливает конфигурацию переноса строк
     */
    public static void setBrConfiguration(@NonNull BrConfiguration brConfiguration) {
        sBrConfiguration = brConfiguration;
    }

    /**
     * Возвращает конфигурацию цитат
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static BlockQuoteSpanConfiguration getBlockQuoteConfiguration() {
        if (sBlockQuoteConfiguration == null) {
            sBlockQuoteConfiguration = new DefaultBlockQuoteSpanConfiguration();
        }
        return sBlockQuoteConfiguration;
    }

    /**
     * Устанавливает конфигурацию цитат
     */
    public static void setBlockQuoteConfiguration(@NonNull BlockQuoteSpanConfiguration blockQuoteConfiguration) {
        sBlockQuoteConfiguration = blockQuoteConfiguration;
    }

    /**
     * Возвращает конфигурацию чисел для тегов перечисления
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static NumberSpanConfiguration getNumberConfiguration() {
        if (sNumberConfiguration == null) {
            sNumberConfiguration = new DefaultNumberSpanConfiguration();
        }
        return sNumberConfiguration;
    }

    /**
     * Устанавливает конфигурацию чисел для тегов перечисления
     */
    public static void setNumberConfiguration(@NonNull NumberSpanConfiguration numberConfiguration) {
        sNumberConfiguration = numberConfiguration;
    }

    /**
     * Возвращает конфигурацию декорированных ссылок
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static DecoratedLinkConfiguration getDecoratedLinkConfiguration() {
        if (sDecoratedLinkConfiguration == null) {
            sDecoratedLinkConfiguration = new DefaultDecoratedLinkConfiguration(null);
        }
        return sDecoratedLinkConfiguration;
    }

    /**
     * Устанавливает конфигурацию декорированных ссылок
     */
    public static void setDecoratedLinkConfiguration(@NonNull DecoratedLinkConfiguration decoratedLinkConfiguration) {
        sDecoratedLinkConfiguration = decoratedLinkConfiguration;
    }

    /**
     * Возвращает конфигурацию css стилей
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static CssConfiguration getCssConfiguration() {
        if (sCssConfiguration == null) {
            sCssConfiguration = new DefaultCssConfiguration();
        }
        return sCssConfiguration;
    }

    /**
     * Устанавливает конфигурацию css стилей
     */
    public static void setCssConfiguration(@Nullable CssConfiguration cssConfiguration) {
        sCssConfiguration = cssConfiguration;
    }

    /**
     * Возвращает конфигурацию таблиц
     */
    @NonNull
    public static TableConfiguration getTableConfiguration() {
        if (sTableConfiguration == null) {
            sTableConfiguration = new DefaultTableConfiguration();
        }
        return sTableConfiguration;
    }

    /**
     * Устанавливает конфигурацию таблиц
     */
    public static void setTableConfiguration(@Nullable TableConfiguration tableConfiguration) {
        sTableConfiguration = tableConfiguration;
    }
}
