package ru.tensor.sbis.main_screen_basic.widget

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.tensor.sbis.main_screen_common.interactor.MainScreenWidgetInteractor
import ru.tensor.sbis.main_screen_common.viewmodel.MainScreenViewModel
import ru.tensor.sbis.main_screen_decl.MainScreenLifecycleDelegate
import ru.tensor.sbis.main_screen_decl.basic.BasicContentController
import ru.tensor.sbis.main_screen_decl.basic.BasicMainScreenViewApi
import ru.tensor.sbis.main_screen_decl.basic.ProvideContentAction
import ru.tensor.sbis.main_screen_decl.basic.data.ContentHost
import ru.tensor.sbis.main_screen_decl.basic.data.ContentPlacement
import ru.tensor.sbis.main_screen_decl.basic.data.FragmentContainer
import ru.tensor.sbis.main_screen_decl.basic.data.InOverlayContainer
import ru.tensor.sbis.main_screen_decl.basic.data.Inside
import ru.tensor.sbis.main_screen_decl.basic.data.OnTop
import ru.tensor.sbis.main_screen_decl.basic.data.ScreenId
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.env.MainScreenHost
import ru.tensor.sbis.main_screen_decl.intent.IntentHandleExtension
import ru.tensor.sbis.verification_decl.permission.PermissionChecker
import ru.tensor.sbis.verification_decl.permission.PermissionLevel
import ru.tensor.sbis.verification_decl.permission.PermissionScope

/**
 * Реализует функционал главного экрана без аккордеона и ННП.
 *
 * @author us.bessonov
 */
internal class BasicMainScreenWidget(
    val host: MainScreenHost,
    private val mainScreenView: BasicMainScreenViewApi,
    private var fragmentContainer: FragmentContainer,
    @IdRes private val mainContainerId: Int,
    private val permissionChecker: PermissionChecker?,
    initialIntent: Intent?,
    private val intentHandleExtensions: List<IntentHandleExtension<out IntentHandleExtension.ExtensionKey>>,
    private val monitorPermissionOnLifecycle: Boolean = false
) : MainScreenLifecycleDelegate {
    private lateinit var mainScope: CoroutineScope

    private val controllers = mutableMapOf<String, BasicContentController>()

    private val controllerProviders = mutableMapOf<String, Pair<ProvideContentAction, ContentPlacement>>()

    private val backPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            if (!delegateBackPress()) {
                isEnabled = false
                host.backPressedDispatcherOwner.onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        }
    }

    private val viewModel: MainScreenViewModel by lazy {
        ViewModelProvider(host.viewModelStoreOwner, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
                    MainScreenViewModel(MainScreenWidgetInteractor(permissionChecker)) as T
                } else throw IllegalArgumentException("Unknown $modelClass")
            }
        })[MainScreenViewModel::class.java]
    }

    private val permissionData = mutableMapOf<PermissionScope, MutableLiveData<PermissionLevel>>()

    private var intent: Intent? = initialIntent

    private val contentHost = ContentHost(host.context, fragmentContainer, mainContainerId, host.overlayFragmentHolder)

    lateinit var defaultContentController: BasicContentController

    lateinit var defaultScreenId: ScreenId

    /**
     * Отобразить новый экран ([Fragment]).
     *
     * @param contentPlacement место размещения экрана.
     * @param provideContent предоставляет [BasicContentController] для создания и управления [Fragment]'ом.
     * @param id идентификатор контента.
     */
    fun showContent(
        contentPlacement: ContentPlacement,
        provideContent: ProvideContentAction,
        id: String,
        externalEntryPoint: ContentController.EntryPoint
    ) {
        when (contentPlacement) {
            is InOverlayContainer -> {
                host.overlayFragmentHolder?.setFragmentWithTag(
                    createScreen(provideContent, externalEntryPoint),
                    contentPlacement.swipeable,
                    id
                )
            }

            is Inside -> {
                fragmentContainer.fragmentManager
                    .beginTransaction()
                    .replace(
                        mainContainerId,
                        createScreen(provideContent, externalEntryPoint),
                        id
                    )
                    .commit()
            }

            is OnTop -> {
                fragmentContainer.fragmentManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(
                        fragmentContainer.containerId,
                        createScreen(provideContent, externalEntryPoint),
                        id
                    )
                    .commit()
            }
        }
    }

    /**
     * Отобразить контент произвольным образом.
     */
    fun performCustomShowAction(action: (ContentHost) -> Unit) {
        action(contentHost)
    }

    /** @SelfDocumented */
    fun handleNewIntent(intent: Intent) {
        val resolutionResult = resolveIntent(intent)

        resolutionResult.asSideEffect()?.let {
            it.action.invoke(host)
            return
        }

        val selectItem = resolutionResult.asSelectItem()
            ?: return

        val id = selectItem.getNavxId()
            ?: return

        fun Fragment.tryUpdate(): Boolean {
            getController(this)
                ?.let {
                    it.update(selectItem.entryPoint, contentHost)
                    return true
                }
            return false
        }

        val fm = fragmentContainer.fragmentManager
        listOfNotNull(
            host.overlayFragmentHolder?.getExistingFragment(id),
            fm.findFragmentById(fragmentContainer.containerId)?.takeIf { it.tag == id },
            fm.findFragmentById(mainContainerId)?.takeIf { it.tag == id }
        ).forEach {
            if (it.tryUpdate()) return
        }

        showContentFromIntent(id, selectItem)
    }

    /**
     * @see [BasicMainScreenViewApi.findDisplayedScreen]
     */
    fun findDisplayedScreen(id: ScreenId) = findExistingContent().find { it.tag == id.toString() }

    /**
     * Зарегистрировать провайдер контроллера содержимого.
     */
    fun registerControllerProvider(
        id: ScreenId,
        provider: ProvideContentAction,
        contentPlacement: ContentPlacement
    ) {
        controllerProviders[id.toString()] = provider to contentPlacement
    }

    /**
     * Метод для мониторинга прав по определенной зоне.
     *
     * @param permissionScope зона доступа.
     */
    fun monitorPermissionScope(permissionScope: PermissionScope): LiveData<PermissionLevel?> {
        return permissionData.getOrPut(permissionScope) { MutableLiveData<PermissionLevel>() }
    }

    /**
     * Метод для получения доступных расширений по обработке [Intent].
     *
     * @param key ключ расширения.
     */
    @Suppress("UNCHECKED_CAST")
    fun <K : IntentHandleExtension.ExtensionKey, E : IntentHandleExtension<K>> getIntentHandleExtension(key: K): E? {
        return intentHandleExtensions.firstOrNull { it.key == key } as? E
    }

    override fun setup() {
        viewModel.checkPermissions(permissionData.keys)

        host.backPressedDispatcherOwner.onBackPressedDispatcher.addCallback(backPressedCallback)

        viewModel.permissionsData.observe(host.viewLifecycleOwner) { permissionsInfo ->
            if (permissionsInfo == null) return@observe

            permissionsInfo.forEach {
                permissionData[it.scope]?.value = it.level
            }
        }

        refreshPermissionOnLifecycle()

        setupContent()
    }

    override fun activate() {
        mainScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        performOnExistingContent { _, _ -> start(contentHost) }
        refreshPermissionOnLifecycle()
    }

    override fun resume() {
        performOnExistingContent { _, _ -> resume(contentHost) }
    }

    override fun pause() {
        performOnExistingContent { _, _ -> pause(contentHost) }
    }

    override fun deactivate() {
        mainScope.cancel()
        performOnExistingContent { _, _ -> stop(contentHost) }
    }

    override fun reset() {
        backPressedCallback.remove()

        permissionData.values.forEach { liveData ->
            liveData.removeObservers(host.viewLifecycleOwner)
        }
        permissionData.clear()
    }

    private fun createScreen(
        provideContent: ProvideContentAction,
        externalEntryPoint: ContentController.EntryPoint
    ): Fragment {
        return provideContent(host.context, mainScreenView)
            .createScreen(externalEntryPoint, mainScreenView, contentHost)
    }

    private fun performOnExistingContent(
        action: BasicContentController.(id: String, content: Fragment) -> Unit
    ): Boolean {
        val existingContent = findExistingContent().takeUnless { it.isEmpty() }
            ?: return false
        existingContent.forEach {
            getController(it)?.action(it.tag.orEmpty(), it)
        }
        return true
    }

    private fun getController(fragment: Fragment) = controllerProviders[fragment.tag]?.let { (action, _) ->
        controllers.getOrPut(fragment.tag.orEmpty()) {
            action.invoke(host.context, mainScreenView)
        }
    }

    private fun findExistingContent(): List<Fragment> {
        val fm = fragmentContainer.fragmentManager
        return listOfNotNull(
            fm.findFragmentById(mainContainerId),
            fm.findFragmentById(fragmentContainer.containerId),
        ) + controllerProviders.keys.mapNotNull { host.overlayFragmentHolder?.getExistingFragment(it) }
    }

    private fun showDefaultContentImmediate(
        provideContent: ProvideContentAction,
        entryPoint: ContentController.EntryPoint?
    ) {
        fragmentContainer.fragmentManager
            .beginTransaction()
            .add(
                mainContainerId,
                createScreen(provideContent, entryPoint ?: ContentController.MenuClick()),
                defaultScreenId.toString()
            )
            .commitNowAllowingStateLoss()
    }

    /**
     * Обновляет актуальность прав на нужные области
     */
    private fun refreshPermissionOnLifecycle() {
        if (monitorPermissionOnLifecycle &&
            host.viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        ) {
            mainScope.launch {
                viewModel.refreshPermissions(permissionData.keys)
            }
        }
    }

    private fun setupContent() {
        val resolutionResult = intent?.let(::resolveIntent)

        val getDefaultScreenController: ProvideContentAction = { _, _ -> defaultContentController }
        registerControllerProvider(defaultScreenId, getDefaultScreenController, Inside)

        val restored = performOnExistingContent { id, fragment ->
            restore(mainScreenView, contentHost, fragment)
            resolutionResult.asSelectItem()?.let {
                if (it.getNavxId() == id) update(it.entryPoint, contentHost)
            }
        }
        if (!restored) {
            val selectItem = resolutionResult.asSelectItem()
            val id = selectItem?.getNavxId()
            val isDefaultEntryPoint = id == defaultScreenId.toString()
            val defaultEntryPoint = selectItem?.entryPoint
                ?.takeIf { isDefaultEntryPoint }

            showDefaultContentImmediate(getDefaultScreenController, defaultEntryPoint)

            selectItem?.takeUnless { isDefaultEntryPoint }
                ?.let { showContentFromIntent(id.orEmpty(), it) }
        }

        resolutionResult.asSideEffect()?.action?.invoke(host)
    }

    private fun resolveIntent(originalIntent: Intent): IntentHandleExtension.ResolutionResult? {
        val intent = Intent(originalIntent)
        originalIntent.data = null
        originalIntent.replaceExtras(null)
        this.intent = null
        host.fragmentActivity.intent = null // нужно сбросить, чтобы корректно обрабатывать новые.

        for (extension in intentHandleExtensions) {
            with(extension) { resolveIntent(intent) }
                ?.let { return it }
        }

        return null
    }

    private fun showContentFromIntent(id: String, selectItem: IntentHandleExtension.ResolutionResult.SelectItem) {
        controllerProviders[id]?.let { (action, placement) ->
            if (placement !is InOverlayContainer) {
                host.overlayFragmentHolder?.removeFragmentImmediate()
            }
            showContent(placement, action, id, selectItem.entryPoint)
        }
    }

    private fun IntentHandleExtension.ResolutionResult?.asSelectItem() =
        this as? IntentHandleExtension.ResolutionResult.SelectItem

    private fun IntentHandleExtension.ResolutionResult?.asSideEffect() =
        this as? IntentHandleExtension.ResolutionResult.SideEffect

    private fun IntentHandleExtension.ResolutionResult.SelectItem.getNavxId() =
        targetMenuItem.navxId?.ids?.first()

    private fun delegateBackPress(): Boolean {
        findExistingContent()
            .filter { it.tag != defaultScreenId.toString() }
            .forEach {
                if (getController(it)?.backPressed(mainScreenView, contentHost) == true) {
                    return true
                }
            }
        return findExistingContent()
            .find { it.tag == defaultScreenId.toString() }
            ?.let { getController(it)?.backPressed(mainScreenView, contentHost) }
            ?: false
    }

}