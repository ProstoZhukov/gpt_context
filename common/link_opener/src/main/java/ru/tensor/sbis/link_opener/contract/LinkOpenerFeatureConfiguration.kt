package ru.tensor.sbis.link_opener.contract

import android.os.Build
import androidx.annotation.ArrayRes
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.ColorRes
import androidx.annotation.IntRange
import ru.tensor.sbis.link_opener.R
import ru.tensor.sbis.link_opener.domain.parser.ScalableParser
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType

/**
 * Конфигурация использования компонента для открытия контента/документов по ссылкам [LinkOpenerFeature].
 *
 * @property syncWindow окно ожидания синхронизации данных по ссылке, мс.
 * Коллбэк синхронизации данных по ссылке "LinkSyncEvent" не гарантирован (может быть не получен
 * по независящим от UI причинам), окно [syncWindow] гарантирует же нам что ожидание синхронизации
 * не будет превышать указанного лимита времени по достижении которого (если синхронизация не произошла)
 * будет обработан прогнозный/кэшированный результат от
 * [ru.tensor.sbis.linkdecorator.generated.LinkDecoratorService.LinkDecoratorService.getDecoratedLinkWithoutDetection].
 * Если корректность данных по ссылке для первого ее открытия приоритетнее времени отклика
 * на открытие [syncWindow] может быть изменен.
 * @property domainKeywords массив ключевых слов для проверки url ссылки на принадлежность домену СБИС.
 * По умолчанию "sbis.ru"
 * @property customDomainKeywords массив ключевых слов для проверки url ссылки на принадлежность домену приложения.
 * @property evaluateUnknownDocTypeAfterSync выполнить пост-обработку ссылок с [DocType.UNKNOWN],
 * т.е. компонент прогоняет неопределенные ссылки через доп. Парсер для определения [DocType].
 * Например, ссылка может быть не распаршена на облаке под гостевой сессией, но быть верно определена
 * на контроллере или UI [ScalableParser]. По умолчанию всегда включено.
 * @property areCustomTabsAllowed допускается ли использование Google Custom Tabs
 * @property customToolbarColor цвет тулбара, при [areCustomTabsAllowed]
 * @property useSabylinkAppRedirect true если компонент допускает перенаправление внешних (инициировавших запуск МП) уже
 * открытых ссылок в другие МП семейства СБИС.
 * По умолчанию опция улучшает пользовательский опыт на устройства с android 12+ где отсутствует предоставляемый
 * системой выбор используемого для открытия ссылки приложения и для общих цифровых ресурсов по
 * обрабатываемому доменному адресу запускается последнее установленное МП.
 * @property useInnerAppRedirect true если компонент допускает перенаправление внутренних (инициируемых в рамках работы МП)
 * ссылок для открытия в других МП семейства СБИС. Назначение свойства - разделить обработку ссылок стартующих МП и стартованных из МП,
 * т.к. в первом случае это требуется только для android 12+.
 * По умолчанию настройка включена тогда же когда и [useSabylinkAppRedirect].
 * @property showProgress true если следует отображать прогресс-диалог во время открытия ссылки
 * в т.ч. редиректа между МП [useSabylinkAppRedirect].
 */
data class LinkOpenerFeatureConfiguration(
    @IntRange(from = MIN_WINDOW, to = MAX_WINDOW) var syncWindow: Long,
    @ArrayRes var domainKeywords: Int = R.array.link_opener_keywords,
    @ArrayRes var customDomainKeywords: Int,
    var evaluateUnknownDocTypeAfterSync: Boolean,
    var areCustomTabsAllowed: Boolean,
    @ColorRes var customToolbarColor: Int,
    var useSabylinkAppRedirect: Boolean,
    var useInnerAppRedirect: Boolean,
    var showProgress: Boolean,
) {

    /**
     * Строитель кастомных конфигураций для компонента [LinkOpenerFeature].
     */
    @Suppress("unused")
    class Builder {

        private val blank = DEFAULT

        /**
         * [LinkOpenerFeatureConfiguration.syncWindow]
         */
        fun setSyncWindow(@IntRange(from = MIN_WINDOW, to = MAX_WINDOW) window: Long): Builder {
            blank.syncWindow = window
            return this
        }

        /**
         * [LinkOpenerFeatureConfiguration.domainKeywords]
         */
        fun setDomainKeywords(@ArrayRes keywords: Int): Builder {
            blank.domainKeywords = keywords
            return this
        }

        /**
         * [LinkOpenerFeatureConfiguration.customDomainKeywords]
         */
        fun setCustomDomainKeywords(@ArrayRes keywords: Int): Builder {
            blank.customDomainKeywords = keywords
            return this
        }

        /**
         * [LinkOpenerFeatureConfiguration.evaluateUnknownDocTypeAfterSync]
         */
        fun useEvaluateUnknownDocTypeAfterSync(enable: Boolean): Builder {
            blank.evaluateUnknownDocTypeAfterSync = enable
            return this
        }

        /**
         * [LinkOpenerFeatureConfiguration.areCustomTabsAllowed]
         */
        fun useCustomTabs(enable: Boolean): Builder {
            blank.areCustomTabsAllowed = enable
            return this
        }

        /**
         * [LinkOpenerFeatureConfiguration.customToolbarColor]
         */
        fun customToolbarColor(@ColorRes customToolbarColor: Int): Builder {
            blank.customToolbarColor = customToolbarColor
            return this
        }

        /**
         * [LinkOpenerFeatureConfiguration.useSabylinkAppRedirect]
         */
        fun useSbisAppRedirect(enable: Boolean): Builder {
            blank.useSabylinkAppRedirect = enable
            return this
        }

        /**
         * [LinkOpenerFeatureConfiguration.useInnerAppRedirect]
         */
        fun useInnerAppRedirectTypes(enable: Boolean): Builder {
            blank.useInnerAppRedirect = enable
            return this
        }

        /**
         * [LinkOpenerFeatureConfiguration.showProgress]
         */
        fun useProgress(enable: Boolean): Builder {
            blank.showProgress = enable
            return this
        }

        /**
         * Возвращает настроенную конфигурацию.
         */
        fun build() = blank
    }

    @Suppress("unused")
    companion object {

        private const val MIN_WINDOW = 0L
        private const val MAX_WINDOW = 4000L

        /**
         * Эмпирическая величина стандартной задержки, баланс смещен в сторону корректности
         * обрабатываемых данных чем скорости отклика.
         * Величина в 1500L с меньшей долей вероятности приведет к корректной обработке впервые открываемой ссылки.
         */
        private const val DEFAULT_SYNC_WINDOW = 2500L

        /** Доступность перенаправление открытых ссылок в другие МП семейства СБИС. */
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
        private val useAppRedirect = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        /** Доступность перенаправление ссылок открытых во время работы МП в другие МП семейства СБИС. */
        private val useInnerAppRedirect = useAppRedirect

        /**
         * Вариант использования [LinkOpenerFeatureConfiguration] по-умолчанию.
         */
        val DEFAULT = LinkOpenerFeatureConfiguration(
            syncWindow = DEFAULT_SYNC_WINDOW,
            customDomainKeywords = R.array.link_opener_keywords,
            evaluateUnknownDocTypeAfterSync = true,
            areCustomTabsAllowed = false,
            customToolbarColor = R.color.link_opener_toolbar_white,
            useSabylinkAppRedirect = useAppRedirect,
            useInnerAppRedirect = useInnerAppRedirect,
            showProgress = true
        )

        /**
         * Вариант использования [LinkOpenerFeatureConfiguration] в Communicator.
         * Отличается от стандартного только поддержкой домена для динамических ссылок.
         */
        val COMMUNICATOR = LinkOpenerFeatureConfiguration(
            syncWindow = DEFAULT_SYNC_WINDOW,
            customDomainKeywords = R.array.link_opener_communicator_keywords,
            evaluateUnknownDocTypeAfterSync = true,
            areCustomTabsAllowed = true,
            customToolbarColor = R.color.link_opener_toolbar_blue,
            useSabylinkAppRedirect = useAppRedirect,
            useInnerAppRedirect = true,
            showProgress = true
        )

        /**
         * Вариант использования [LinkOpenerFeatureConfiguration] в SabyMy.
         * Отличается от стандартного только поддержкой домена для динамических ссылок.
         */
        val SABYMY = LinkOpenerFeatureConfiguration(
            syncWindow = DEFAULT_SYNC_WINDOW,
            customDomainKeywords = R.array.link_opener_sabymy_keywords,
            evaluateUnknownDocTypeAfterSync = true,
            areCustomTabsAllowed = true,
            customToolbarColor = R.color.link_opener_toolbar_blue,
            useSabylinkAppRedirect = useAppRedirect,
            useInnerAppRedirect = useInnerAppRedirect,
            showProgress = true
        )

        /**
         * Вариант использования [LinkOpenerFeatureConfiguration] в SabyGet.
         */
        val SABYGET = LinkOpenerFeatureConfiguration(
            syncWindow = DEFAULT_SYNC_WINDOW,
            customDomainKeywords = R.array.link_opener_sabyget_keywords,
            evaluateUnknownDocTypeAfterSync = true,
            areCustomTabsAllowed = false,
            customToolbarColor = R.color.link_opener_toolbar_white,
            useSabylinkAppRedirect = useAppRedirect,
            useInnerAppRedirect = useInnerAppRedirect,
            showProgress = true
        )

        /**
         * Вариант использования [LinkOpenerFeatureConfiguration] в SabyLite.
         * Отличается от стандартного только поддержкой домена для динамических ссылок.
         */
        val SABYLITE = LinkOpenerFeatureConfiguration(
            syncWindow = DEFAULT_SYNC_WINDOW,
            customDomainKeywords = R.array.link_opener_sabylite_keywords,
            evaluateUnknownDocTypeAfterSync = true,
            areCustomTabsAllowed = true,
            customToolbarColor = R.color.link_opener_toolbar_blue,
            useSabylinkAppRedirect = useAppRedirect,
            useInnerAppRedirect = useInnerAppRedirect,
            showProgress = true
        )

        /**
         * Вариант использования [LinkOpenerFeatureConfiguration] в SabyClients.
         * Отличается от стандартного только поддержкой домена для динамических ссылок.
         */
        val SABYCLIENTS = LinkOpenerFeatureConfiguration(
            syncWindow = DEFAULT_SYNC_WINDOW,
            customDomainKeywords = R.array.link_opener_sabyclients_keywords,
            evaluateUnknownDocTypeAfterSync = true,
            areCustomTabsAllowed = true,
            customToolbarColor = R.color.link_opener_toolbar_blue,
            useSabylinkAppRedirect = useAppRedirect,
            useInnerAppRedirect = useInnerAppRedirect,
            showProgress = true
        )
    }
}
