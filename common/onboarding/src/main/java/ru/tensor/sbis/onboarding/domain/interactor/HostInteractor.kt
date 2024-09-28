package ru.tensor.sbis.onboarding.domain.interactor

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.Subject
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.verification_decl.permission.PermissionLevel
import ru.tensor.sbis.onboarding.contract.providers.content.BasePage
import ru.tensor.sbis.onboarding.contract.providers.content.Button
import ru.tensor.sbis.onboarding.contract.providers.content.CustomPage
import ru.tensor.sbis.onboarding.contract.providers.content.FeaturePage
import ru.tensor.sbis.onboarding.contract.providers.content.NoPermissionPage
import ru.tensor.sbis.onboarding.contract.providers.content.Onboarding
import ru.tensor.sbis.onboarding.contract.providers.content.Page
import ru.tensor.sbis.onboarding.contract.providers.content.SuppressBehaviour.BUTTON
import ru.tensor.sbis.onboarding.contract.providers.content.SuppressBehaviour.NOTHING
import ru.tensor.sbis.onboarding.contract.providers.content.SuppressBehaviour.PERMISSION
import ru.tensor.sbis.onboarding.contract.providers.content.SuppressBehaviour.SCREEN
import ru.tensor.sbis.onboarding.contract.providers.content.SystemPermissions
import ru.tensor.sbis.onboarding.di.HostScope
import ru.tensor.sbis.onboarding.di.ui.host.ARG_PAGE_COUNTER
import ru.tensor.sbis.onboarding.domain.OnboardingRepository
import ru.tensor.sbis.onboarding.domain.holder.PermissionFeatureHolder
import ru.tensor.sbis.onboarding.domain.interactor.usecase.HostState
import ru.tensor.sbis.onboarding.domain.provider.StringProvider
import ru.tensor.sbis.onboarding.domain.util.OnboardingIssue.CALL_NON_EXIST_PAGE
import ru.tensor.sbis.onboarding.domain.util.reportIssue
import ru.tensor.sbis.onboarding.ui.host.adapter.FeaturePageCreator
import ru.tensor.sbis.onboarding.ui.host.adapter.PageListHolder
import ru.tensor.sbis.onboarding.ui.host.adapter.PageParams
import ru.tensor.sbis.onboarding.ui.page.OnboardingFeatureFragment
import ru.tensor.sbis.onboarding.ui.utils.ThemeProvider
import ru.tensor.sbis.onboarding.ui.utils.plusAssign
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

/**
 * Интерактор экрана приветствия
 *
 * @author as.chadov
 *
 * @param repository репозиторий содержимого комопнента
 * @param intentProvider провайдер интента главной активности
 * @param permissionFeatureHolder холдер функционального интерфейса модуля "полномочий"
 * @param pageCounterChannel [Subject] событие изменения счетчика общего кол-ва экранов
 *
 * @property usedPages список отображаемых страниц
 */
@HostScope
internal class HostInteractor private constructor(
    private val repository: OnboardingRepository,
    private val permissionFeatureHolder: PermissionFeatureHolder,
    private val intentProvider: MainActivityProvider,
    private val pageCounterChannel: Subject<Int>,
    private val stringProvider: StringProvider,
    private val themeProvider: ThemeProvider,
    private val disposable: CompositeDisposable,
) : PageListHolder,
    FeaturePageCreator,
    Disposable by disposable {

    @Inject
    internal constructor(
        repository: OnboardingRepository,
        permissionFeatureHolder: PermissionFeatureHolder,
        intentProvider: MainActivityProvider,
        @Named(ARG_PAGE_COUNTER) pageCounterChannel: Subject<Int>,
        stringProvider: StringProvider,
        themeProvider: ThemeProvider,
    ) : this(
        repository,
        permissionFeatureHolder,
        intentProvider,
        pageCounterChannel,
        stringProvider,
        themeProvider,
        CompositeDisposable()
    )

    private var usedPages: MutableList<Page> = mutableListOf()

    init {
        disposable += repository.observe()
            .map(::filterPersistentPages)
            .subscribe {
                usedPages = it.toMutableList()
                pageCounterChannel.onNext(getPageCount())
                checkUserPermissions()
                checkUserLicense()
            }
    }

    /**
     * @return состояние хоста
     */
    fun observeHostState(): Observable<HostState> =
        repository.observe().map {
            val longestText = stringProvider.findLongestString(it.pages)
            val firstPage = it.pages.find { page -> page is BasePage } as? BasePage
            HostState(
                isPreventBackSwipe = it.preventBackSwipe,
                isBackPressed = !it.preventBackSwipe && it.backPressedSwipe,
                isAutoSwitchable = it.getFlippingTimerState(),
                longestDescriptionText = longestText,
                firstImageResId = firstPage?.image?.imageResId ?: 0
            )
        }

    fun observeTargetIntent(): Observable<Intent> =
        repository.observe().map {
            it.targetIntent ?: intentProvider.getMainActivityIntent()
        }

    @DrawableRes
    fun getCurrentBroadsheet(position: Int): Int {
        val page = usedPages[position]
        return if (page is BasePage) {
            page.image.imageResId
        } else 0
    }

    // region PageListHolder, FeaturePageCreator
    override fun getPageCount() = usedPages.size

    override fun getPageId(position: Int) = usedPages[position].uuid

    override fun getPageParams(position: Int): PageParams {
        if (getPageCount() <= position) {
            reportIssue(
                CALL_NON_EXIST_PAGE,
                pagePosition = position,
                pageCount = getPageCount()
            )
        }
        val uuid = usedPages[position].uuid
        return PageParams(uuid, position, getPageCount())
    }

    override fun createFeaturePage(params: PageParams): Fragment {
        val uuid = params.uuid
        return if (isCustomPage(uuid)) {
            getCustomPage(uuid).creator()
        } else {
            OnboardingFeatureFragment.newInstance(params)
        }
    }
    // endregion

    private fun Onboarding.getFlippingTimerState(): Boolean =
        if (themeProvider.isTV) {
            true
        } else {
            useFlippingTimer
        }

    /**
     * @return возвращает [Observable] список [Page] отображаемых по-умолчанию
     */
    private fun filterPersistentPages(content: Onboarding): List<Page> =
        content.pages.filter { page -> page is FeaturePage || page is CustomPage }

    private fun isCustomPage(uuid: String): Boolean {
        return usedPages.find { it.uuid == uuid } is CustomPage
    }

    private fun getCustomPage(uuid: String): CustomPage {
        return usedPages.find { it.uuid == uuid } as? CustomPage
            ?: throw IllegalArgumentException("Not found ${CustomPage::class.java.canonicalName} for uuid $uuid")
    }

    private fun checkUserPermissions() {
        if (!repository.hasStubPages()) {
            return
        }
        for (it in repository.getStubPages()) {
            checkStubPage(it)
        }
    }

    /**
     * выделение в отдельный метод исправляет proguard ошибку release build
     * https://www.guardsquare.com/en/products/proguard/manual/troubleshooting#unresolvedclass
     */
    private fun checkStubPage(page: NoPermissionPage) {
        permissionFeatureHolder {
            disposable += Observable.fromCallable {
                permissionChecker.checkPermissionsNow(page.permissionScopes, PermissionLevel.READ)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ info ->
                               if (page.inclusiveStrategy.and(info.all { it.level == PermissionLevel.NONE }) ||
                                   page.inclusiveStrategy.not().and(info.any { it.level == PermissionLevel.NONE })) {
                                   applyRestriction(page)
                               }
                           }, Timber::e)
        }
    }

    private fun applyRestriction(page: NoPermissionPage) {
        if (usedPages.any { it is FeaturePage && it.suppressed != NOTHING }) {
            val iterator = usedPages.listIterator()
            for (item in iterator) {
                if (item !is FeaturePage) {
                    return
                }
                when (item.suppressed) {
                    SCREEN -> iterator.remove()
                    BUTTON -> item.button = Button.EMPTY
                    PERMISSION -> {
                        item.permissions = SystemPermissions.EMPTY
                    }
                    NOTHING -> Unit
                }
            }
        }
        usedPages.add(page)
        pageCounterChannel.onNext(getPageCount())
    }

    private fun checkUserLicense() {
        //TODO https://online.sbis.ru/opendoc.html?guid=9e54ce4a-0568-400c-b946-ca0fb9afdd3b
    }

    /**
     * Проверяет из настроек onboarding можно ли перейти на след страницу по свайпу
     */
    fun canSwipe(position: Int): Boolean {
        val uuid = getPageId(position)
        return if (isCustomPage(uuid)) getCustomPage(uuid).canSwipe.invoke() else true
    }
}