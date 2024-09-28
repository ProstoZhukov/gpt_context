package ru.tensor.sbis.onboarding.domain

import androidx.annotation.VisibleForTesting
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.onboarding.contract.providers.content.BasePage
import ru.tensor.sbis.onboarding.contract.providers.content.FeaturePage
import ru.tensor.sbis.onboarding.contract.providers.content.NoPermissionPage
import ru.tensor.sbis.onboarding.contract.providers.content.Onboarding
import ru.tensor.sbis.onboarding.contract.providers.content.Page
import ru.tensor.sbis.onboarding.domain.util.OnboardingIssue.NOT_FOUND_PAGE
import ru.tensor.sbis.onboarding.domain.util.reportIssue
import ru.tensor.sbis.onboarding.ui.utils.OnboardingProviderMediator
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий содержимого приветственного экрана, единая точка входа для доступа к содержимому
 *
 * @author as.chadov
 *
 * @param onboardingMediator класс для работы с набором провайдеров onboarding
 * @param uiScheduler поток на котором выполняется обработка данных от провайдера. По умолчанию mainThread()
 * @param ioScheduler поток на котором выполняется получение данных от провайдера. По умолчанию io()
 *
 * @property contentSubject шлюз уведомления о получении содержимого приветственного экрана
 * @property contentChannel канал уведомлений активируемый по подпискам
 * @property content полученное содержимое компонента приветственного экрана
 * Должен перезапрашиваться поскольку конфигурация онбординга может измениться при повторном запуске
 */
@Singleton
class OnboardingRepository @VisibleForTesting internal constructor(
    private val onboardingMediator: OnboardingProviderMediator,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
) {
    private var content: Onboarding = Onboarding.EMPTY
    private var contentChannel: Observable<Onboarding>
    private val contentSubject = BehaviorSubject.create<Onboarding>()
    private var disposable = SerialDisposable()

    @Inject
    constructor(onboardingMediator: OnboardingProviderMediator) :
            this(onboardingMediator, AndroidSchedulers.mainThread(), Schedulers.io())

    init {
        contentChannel = contentSubject
            .doOnSubscribe { requestContent() }
            .doOnDispose { disposable.dispose() }
            .compose(getObservableBackgroundSchedulers())
            .replay(1)
            .refCount()
    }

    /**
     * Возвращает подписку на получение данных о содержимом приветственного экрана
     *
     * @return возвращает [Observable] содержимого компонента [Onboarding]
     */
    fun observe(): Observable<Onboarding> = contentChannel

    /**
     * @return содержимое компонента [Onboarding]
     */
    fun getCachedContent(): Onboarding = content

    /**
     * @return возвращает страницу [BasePage] с соответствующим uuid из числа базовых описанных в dsl
     */
    fun findDeclaredPage(uuid: String): BasePage =
        findAnyPage(uuid) as? BasePage
            ?: FeaturePage.emptyInstance()

    /**
     * @return возвращает список [NoPermissionPage] страниц заглушек если такие имеют место
     */
    fun getStubPages(): List<NoPermissionPage> =
        content.pages.filterIsInstance(NoPermissionPage::class.java)

    /**
     * @return возвращает страницу [Page] с соответствующим uuid из числа ранее полученных
     */
    fun findPageSafely(uuid: String): Page? =
        content.pages.find { it.uuid == uuid }

    /**
     * @return true если приветственный экран содержит страницы заглушек
     */
    fun hasStubPages(): Boolean = getStubPages().isNotEmpty()

    /**
     * @return возвращает страницу [Page] с соответствующим uuid
     * @exception IllegalArgumentException
     */
    private fun findAnyPage(uuid: String): Page? {
        val page = content.pages.find { it.uuid == uuid }
        if (page == null) {
            reportIssue(NOT_FOUND_PAGE, pageUuid = uuid)
        }
        return page
    }

    private fun requestContent() {
        if (disposable.isDisposed) {
            disposable = SerialDisposable()
        }
        disposable.set(
            Observable
                .fromCallable {
                    content = onboardingMediator.getActiveProvider()?.getOnboardingContent()
                        ?: Onboarding.EMPTY
                    content
                }
                .filter { it != Onboarding.EMPTY }
                .doOnError { Timber.e(it) }
                .subscribe { contentSubject.onNext(it) }
        )
    }

    private fun <T> getObservableBackgroundSchedulers(delayError: Boolean = false): ObservableTransformer<T, T> =
        ObservableTransformer { observable ->
            observable
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler, delayError)
        }
}