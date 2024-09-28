package ru.tensor.sbis.link_opener.domain.parser

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import androidx.tracing.trace
import dagger.Lazy
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.common.util.AppConfig
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.link_opener.contract.LinkOpenerFeatureConfiguration
import ru.tensor.sbis.link_opener.data.InnerLinkPreview
import ru.tensor.sbis.link_opener.data.UriContainer
import ru.tensor.sbis.link_opener.data.map
import ru.tensor.sbis.link_opener.ui.LinkOpenerProgressDispatcher
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.service.LinkDecoratorServiceRepository
import ru.tensor.sbis.toolbox_decl.linkopener.service.Subscription
import timber.log.Timber
import javax.inject.Inject

/**
 * Парсер намерения интент-фильтр [Intent] в превью ссылки на документ [InnerLinkPreview].
 * Работает через микросервис контроллера 'LinkDecoratorService' обернутый в [LinkDecoratorServiceRepository].
 *
 * @param networkUtils сетевой помощник для проверки доступности соединения.
 * @param configuration конфигурация использования компонента.
 * @param progressDispatcher диспетчер UI прогресса открытия ссылки.
 *
 * @property lastRequest последний запрашиваемый url документа.
 * @property subject шлюз уведомления о получении превью данных на ссылку из интента.
 * @property channel канал уведомлений активируемый по подпискам.
 * @property serviceSubscription подписка на модуль работы с событиями, при разрушении данного объекта происходит отписка.
 *
 * @author as.chadov
 */
internal open class DeeplinkParser @Inject constructor(
    private val linkDecoratorServiceRepository: Lazy<LinkDecoratorServiceRepository>,
    private val scalableParser: ScalableParser,
    private val linkTypeDetector: LinkTypeDetector,
    private val mapper: LinkUriMapper,
    private val networkUtils: NetworkUtils,
    private val progressDispatcher: LinkOpenerProgressDispatcher,
    private val configuration: LinkOpenerFeatureConfiguration,
) {

    private val serviceRepository: LinkDecoratorServiceRepository by lazy {
        linkDecoratorServiceRepository.get()
    }

    private var subject: Subject<InnerLinkPreview> = PublishSubject.create()
    private var channel: Observable<InnerLinkPreview>
    private var serviceSubscription: Subscription? = null
    private var lastRequest = Request()
    private val reserveEmitter = ReserveEmitter()

    init {
        channel = subject
            .doOnSubscribe {
                getServiceSubscription().enable()
            }
            .doOnDispose {
                getServiceSubscription().disable()
                serviceSubscription = null
                reserveEmitter.dispose()
            }
            .compose(getObservableBackgroundSchedulers())
            .publish()
            .refCount()
    }

    /**
     * Возвращает подписку на получение данных о ссылке.
     *
     * @param args [UriContainer].
     * @return подписка на получение данных о ссылке.
     */
    fun observeParsing(
        args: UriContainer,
    ): Observable<InnerLinkPreview> {
        val linkType = linkTypeDetector.getType(args)
        return when {
            linkType.isSabylink -> Observable.just(mapper.unmarshal(args.intent?.data))
            linkType.isSbis     -> Observable.merge(channel, requestParseSbisUrl(args.uriString))
            linkType.isForeign  -> Observable.just(scalableParser.parseNoSbisUrlToLinkPreview(args.uriString))
            else                -> Observable.just(InnerLinkPreview.EMPTY)
        }.doOnNext { it.setOriginFields(args.isIntentSource, args.isOuterLink) }
    }

    /**
     * Разобрать намерение/uri и обработать его результат.
     *
     * @param args [UriContainer].
     * @param onParse коллбэк действия по результатам разбора намерения.
     *
     * @return false если интент НЕ будет обработан надлежащим образом.
     * Например, если [Intent] не содержит адрес ссылки.
     */
    @SuppressLint("CheckResult")
    fun executeOnParsing(
        args: UriContainer,
        onParse: (InnerLinkPreview) -> Unit,
    ): Boolean {
        val linkType = linkTypeDetector.getType(args)
        if (!linkType.isValid) {
            return false
        }
        if (args.isWebViewVisitor) {
            InnerLinkPreview(
                url = args.uriString,
                isWebViewVisitor = true
            ).let(onParse)
            return true
        }
        progressDispatcher.register()
        if (linkType.isSabylink) {
            progressDispatcher.showProgress()
            onParse(mapper.unmarshal(args.intent?.data))
            return true
        }
        if (linkType.isSbis) {
            progressDispatcher.showProgress()
            Observable.merge(channel, requestParseSbisUrl(args.uriString))
                .filter(::isProper)
                .take(1)
                .subscribe { linkPreview ->
                    linkPreview.setOriginFields(args.isIntentSource, args.isOuterLink)
                    onParse(linkPreview)
                }
        } else {
            val preview = scalableParser.parseNoSbisUrlToLinkPreview(args.uriString)
                .apply { this@apply.setOriginFields(args.isIntentSource, args.isOuterLink) }
            onParse(preview)
        }
        return true
    }

    /**
     * Выполнить постобработку превью на ссылку [preview].
     * Постобработка включает только доп. разбор на UI без обращения к микросервису декорирования.
     *
     * @param onParse коллбэк действия по результатам постобработки.
     */
    fun executePostParsing(
        preview: LinkPreview,
        onParse: (LinkPreview) -> Unit,
    ) = onParse(scalableParser.postProcessUnknownLinkPreview(preview))

    /** @SelfDocumented */
    private fun isProper(preview: InnerLinkPreview): Boolean {
        val isProper = !preview.isPredictable || preview.isAfterSync || !networkUtils.isConnected
        if (!isProper) {
            progressDispatcher.unregister()
        }
        return isProper
    }

    /**
     * Выполнить запрос на чтение превью с данными о СБИС ссылке.
     * Получить превью ссылки из КЭША микро сервиса
     * - актуальное, если ранее кэшировалось
     * - спрогнозированное, если будет вызов к облаку
     *
     * @param data СБИС ссылка из интента
     *
     * Функция [LinkDecoratorServiceRepository.getDecoratedLinkWithDetection] осуществляет блокирующее обращение и использует синхронизатор одновременно,
     * но из-за особенностей разбора ссылок на задачи на облаке ее использование нежелательно.
     * Функция [LinkDecoratorServiceRepository.getDecoratedLinkWithoutDetection] использует только синхронизатор.
     */
    private fun requestParseSbisUrl(
        data: String,
    ): Observable<InnerLinkPreview> =
        Observable.create<InnerLinkPreview> { emitter ->
            lastRequest = Request(data)
            val linkPreview = requestLinkPreview(data)
            if (linkPreview != null && !linkPreview.isPredictable) {
                emitter.onNext(linkPreview)
            } else if (linkPreview != null) {
                reserveEmitter.schedule(linkPreview)
            }
            emitter.onComplete()
        }.compose(getObservableBackgroundSchedulers())

    private fun requestLinkPreview(data: String): InnerLinkPreview? {
        val preview = trace("LinkDecoratorServiceRepository.getDecoratedLinkWithoutDetection") {
            serviceRepository.getDecoratedLinkWithoutDetection(data)
        }
        reportLog("LinkDecoratorServiceRepository. Get decorated link preview result: $preview")
        val innerLinkPreview = preview?.map()
        return if (configuration.evaluateUnknownDocTypeAfterSync) {
            innerLinkPreview?.let { scalableParser.postProcessUnknownLinkPreview(it) }
        } else innerLinkPreview
    }

    /** Логировать состояние работы с микросервисом колбэка данных. */
    private fun reportLog(message: String) {
        if (AppConfig.isDebug()) {
            Timber.tag(javaClass.simpleName).d(message)
        }
    }

    private fun getServiceSubscription(): Subscription =
        serviceSubscription ?: serviceRepository.subscribe(LinkRefreshCallback())
            .also { serviceSubscription = it }

    private fun <T> getObservableBackgroundSchedulers(
        delayError: Boolean = false,
    ): ObservableTransformer<T, T> =
        ObservableTransformer { observable ->
            observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), delayError)
        }

    /**
     * Слушатель синхронизатора по событию [EVENT_NAME].
     * Доставка события не гарантировано поскольку имеется таймаут кэширования и т.п.
     */
    private inner class LinkRefreshCallback : LinkDecoratorServiceRepository.DataRefreshedCallback {
        override fun onEvent(data: LinkPreview) {
            if (lastRequest.url != data.href) {
                return
            }
            reserveEmitter.dispose()

            reportLog("LinkDecoratorServiceRepository. Get decorated link preview callback result: $data")
            val callbackPreview = data.map()
            callbackPreview.isAfterSync = true
            subject.onNext(callbackPreview)
        }
    }

    /**
     * Резервный излучатель "спрогнозированного" превью на ссылку.
     * Необходим, поскольку событие [EVENT_NAME] ничем не гарантировано.
     */
    private inner class ReserveEmitter {

        private var handler: Handler? = null
        private var token: InnerLinkPreview? = null

        /**
         * Инициирует отложенную отправку спрогнозированного превью.
         */
        fun schedule(rawPreview: InnerLinkPreview) {
            token = rawPreview
            handler = Handler(Looper.getMainLooper()).apply {
                postDelayed(configuration.syncWindow, token) {
                    rawPreview.isAfterSync = true
                    subject.onNext(rawPreview)
                }
            }
        }

        /** @SelfDocumented */
        fun dispose() {
            handler?.removeCallbacksAndMessages(token)
            token = null
            handler = null
        }
    }

    private inner class Request(val url: String = "")
}

private const val EVENT_NAME = "LinkSyncEvent"
