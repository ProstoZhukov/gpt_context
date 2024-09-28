package ru.tensor.sbis.richtext.converter.cfg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Конфигурация конвертера тегов богатого текста
 * <p>
 *
 * @author am.boldinov
 */
public class Configuration {

    /**
     * Возвращает конфигурацию по умолчанию
     */
    @NonNull
    public static Configuration getDefault() {
        return new Builder().build();
    }

    /**
     * Создает конфигурацию по настройкам рендера
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static Configuration fromRenderOptions(@NonNull RenderOptions renderOptions) {
        return new Builder(renderOptions).build();
    }

    /**
     * Возвращает конфигурацию с возможностью рендера декорированных ссылок
     */
    @NonNull
    public static Configuration withDecoratedLinks() {
        return fromRenderOptions(new RenderOptions().drawLinkAsDecorated(true));
    }

    @NonNull
    private final RenderOptions mRenderOptions;
    @NonNull
    private final BlockQuoteSpanConfiguration mBlockQuoteSpanConfiguration;
    @NonNull
    private final NumberSpanConfiguration mNumberSpanConfiguration;
    @NonNull
    private final BrConfiguration mBrConfiguration;
    @NonNull
    private final DecoratedLinkConfiguration mDecoratedLinkConfiguration;
    @NonNull
    private final CssConfiguration mCssConfiguration;
    @NonNull
    private final TableConfiguration mTableConfiguration;

    private Configuration(@NonNull RenderOptions renderOptions, @NonNull BlockQuoteSpanConfiguration blockQuoteSpanConfiguration,
                          @NonNull NumberSpanConfiguration numberSpanConfiguration, @NonNull BrConfiguration brConfiguration,
                          @NonNull DecoratedLinkConfiguration decoratedLinkConfiguration, @NonNull CssConfiguration cssConfiguration,
                          @NonNull TableConfiguration tableConfiguration) {
        mRenderOptions = renderOptions;
        mBlockQuoteSpanConfiguration = blockQuoteSpanConfiguration;
        mNumberSpanConfiguration = numberSpanConfiguration;
        mBrConfiguration = brConfiguration;
        mDecoratedLinkConfiguration = decoratedLinkConfiguration;
        mCssConfiguration = cssConfiguration;
        mTableConfiguration = tableConfiguration;
    }

    /**
     * Возвращает конфигурацию цитат
     */
    @NonNull
    public BlockQuoteSpanConfiguration getBlockQuoteSpanConfiguration() {
        return mBlockQuoteSpanConfiguration;
    }

    /**
     * Возвращает конфигурацию чисел для тегов перечисления
     */
    @NonNull
    public NumberSpanConfiguration getNumberSpanConfiguration() {
        return mNumberSpanConfiguration;
    }

    /**
     * Возвращает конфигурацию переноса строк
     */
    @NonNull
    public BrConfiguration getBrConfiguration() {
        return mBrConfiguration;
    }

    /**
     * Возвращает конфигурацию декорированных ссылок
     */
    @NonNull
    public DecoratedLinkConfiguration getDecoratedLinkConfiguration() {
        return mDecoratedLinkConfiguration;
    }

    /**
     * Возвращает конфигурацию css стилей
     */
    @NonNull
    public CssConfiguration getCssConfiguration() {
        return mCssConfiguration;
    }

    /**
     * Возвращает конфигурацию таблиц
     */
    @NonNull
    public TableConfiguration getTableConfiguration() {
        return mTableConfiguration;
    }

    /**
     * Возвращает настройки рендера богатого текста
     */
    @NonNull
    public RenderOptions getRenderOptions() {
        return mRenderOptions;
    }

    /**
     * Билдер для создания конфигурации
     */
    public static class Builder {

        @Nullable
        private BlockQuoteSpanConfiguration mBlockQuoteSpanConfiguration;
        @Nullable
        private NumberSpanConfiguration mNumberSpanConfiguration;
        @Nullable
        private BrConfiguration mBrConfiguration;
        @Nullable
        private DecoratedLinkConfiguration mDecoratedLinkConfiguration;
        @Nullable
        private CssConfiguration mCssConfiguration;
        @Nullable
        private TableConfiguration mTableConfiguration;

        @NonNull
        private final RenderOptions mRenderOptions;

        public Builder() {
            this(null);
        }

        public Builder(@Nullable RenderOptions renderOptions) {
            mRenderOptions = renderOptions != null ? renderOptions : new RenderOptions();
        }

        /**
         * Устанавливает конфигурацию цитат
         */
        @NonNull
        public Builder blockQuoteConfiguration(@Nullable BlockQuoteSpanConfiguration configuration) {
            mBlockQuoteSpanConfiguration = configuration;
            return this;
        }

        /**
         * Устанавливает конфигурацию чисел для тегов перечисления
         */
        @NonNull
        public Builder numberSpanConfiguration(@Nullable NumberSpanConfiguration configuration) {
            mNumberSpanConfiguration = configuration;
            return this;
        }

        /**
         * Устанавливает конфигурацию переноса строк
         */
        @NonNull
        public Builder brConfiguration(@Nullable BrConfiguration configuration) {
            mBrConfiguration = configuration;
            return this;
        }

        /**
         * Устанавливает конфигурацию декорированных ссылок
         */
        @NonNull
        public Builder decoratedLinkConfiguration(@Nullable DecoratedLinkConfiguration configuration) {
            if (!mRenderOptions.isDrawLinkAsDecorated()) {
                throw new IllegalStateException("Include support decorated links in RenderOptions for their configuration");
            }
            mDecoratedLinkConfiguration = configuration;
            return this;
        }

        /**
         * Устанавливает конфигурацию css стилей
         */
        @NonNull
        public Builder cssConfiguration(@Nullable CssConfiguration configuration) {
            mCssConfiguration = configuration;
            return this;
        }

        /**
         * Устанавливает конфигурацию таблиц
         */
        public Builder tableConfiguration(@Nullable TableConfiguration configuration) {
            if (!mRenderOptions.isDrawWrappedImages()) {
                throw new IllegalStateException("Include support wrapped images in RenderOptions for their configuration");
            }
            mTableConfiguration = configuration;
            return this;
        }

        /**
         * Собирает конфигурацию
         */
        @NonNull
        public Configuration build() {
            return new Configuration(mRenderOptions, mBlockQuoteSpanConfiguration != null ? mBlockQuoteSpanConfiguration : RichTextGlobalConfiguration.getBlockQuoteConfiguration(),
                    mNumberSpanConfiguration != null ? mNumberSpanConfiguration : RichTextGlobalConfiguration.getNumberConfiguration(),
                    mBrConfiguration != null ? mBrConfiguration : RichTextGlobalConfiguration.getBrConfiguration(),
                    mDecoratedLinkConfiguration != null ? mDecoratedLinkConfiguration : RichTextGlobalConfiguration.getDecoratedLinkConfiguration(),
                    mCssConfiguration != null ? mCssConfiguration : RichTextGlobalConfiguration.getCssConfiguration(),
                    mTableConfiguration != null ? mTableConfiguration : RichTextGlobalConfiguration.getTableConfiguration());
        }
    }
}
