package ru.tensor.sbis.communicator.communicator_navigation.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.common.event.NavigationChangeEvent
import ru.tensor.sbis.common.navigation.MenuNavigationItemType.*
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.util.*
import ru.tensor.sbis.communication_decl.communicator.CommunicatorMasterDetailFragment
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationParams
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature
import ru.tensor.sbis.communicator.common.data.ConversationDetailsParams
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.navigation.contract.*
import ru.tensor.sbis.communicator.common.navigation.data.CommunicatorArticleDiscussionParams
import ru.tensor.sbis.communicator.common.themes_registry.ConversationOpener
import ru.tensor.sbis.communicator.common.themes_registry.ThemesRegistry
import ru.tensor.sbis.communicator.common.ui.hostfragment.contracts.RegistryTabSwitcher
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.common.util.share.ConversationUtils
import ru.tensor.sbis.communicator.common.util.share.ConversationUtils.DATA_INTENT_ACTION
import ru.tensor.sbis.communicator.communicator_navigation.CommunicatorNavigationFacade.communicatorNavigationDependency
import ru.tensor.sbis.communicator.communicator_navigation.CommunicatorNavigationPlugin.conversationPreviewFragmentFactoryProvider
import ru.tensor.sbis.communicator.communicator_navigation.CommunicatorNavigationPlugin.customizationOptions
import ru.tensor.sbis.communicator.communicator_navigation.contract.CommunicatorNavigationDependency
import ru.tensor.sbis.communicator.communicator_navigation.contract.CommunicatorNavigationFeature
import ru.tensor.sbis.communicator.communicator_navigation.navigation.utils.mapShareContent
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.*
import ru.tensor.sbis.communicator.declaration.ConversationPreviewMode
import ru.tensor.sbis.communicator.declaration.MasterFragment
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType.*
import ru.tensor.sbis.communicator.dialog_selection.DialogSelectionFeatureImpl
import ru.tensor.sbis.deeplink.*
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.info_decl.notification.NotificationListFragmentAsCardConfiguration
import ru.tensor.sbis.info_decl.notification.view.NotificationListViewConfiguration
import ru.tensor.sbis.tasks.feature.AdditionalDocumentOpenArgs
import ru.tensor.sbis.tasks.feature.WithUuidAndEventUuidArgs
import java.util.*
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.communicator.common.R as RCommunicatorCommon
import ru.tensor.sbis.communicator.core.R as RCommunicatorCore
import ru.tensor.sbis.design.R as RDesign

/** @SelfDocumented */
internal class CommunicatorRouterImpl : CommunicatorRouter,
    CommunicatorThemesRouter,
    CommunicatorHostRouter,
    CommunicatorConversationRouter,
    CommunicatorDialogInformationRouter,
    LifecycleObserver {

    private var navigatedFragment: Fragment? = null
    private var hostFragmentManager: FragmentManager? = null
    private var supportFragmentManager: FragmentManager? = null
    private val isMasterTransaction: Boolean by lazy {
        navigatedFragment is MasterFragment
    }
    private val backStackRule: Boolean
        get() = !isMasterTransaction && hostFragmentManager?.fragments?.size != 0

    private val detailsContainerId: Int? by lazy {
        if (isTablet && hostFragmentManager != null) {
            RCommunicatorCore.id.communicator_details_fragment_container
        } else {
            null
        }
    }
    private var overlayDetailContainerId: Int? = null

    private var dependency: CommunicatorNavigationDependency? = null
    private var navigationBus: RxBus? = null
    private var requiredDeeplinkAction: DeeplinkAction? = null
    private var disposer = CompositeDisposable()

    private val context get() = navigatedFragment!!.requireContext()
    private var isTablet = false

    /** @SelfDocumented */
    override fun initCommunicatorRouter(fragment: Fragment) {
        navigatedFragment = fragment
        initFragmentManagers()
        initOverlayDetailContainer()
        initDependencies()
        performSavedDeeplinkAction()
    }

    private fun initFragmentManagers() {
        supportFragmentManager = navigatedFragment!!.requireActivity().supportFragmentManager
        hostFragmentManager = findHostFragment()?.childFragmentManager
    }

    private fun initOverlayDetailContainer() {
        findRegistryFragment()?.let { registry ->
            overlayDetailContainerId =
                if (registry.getRegistryType()?.isSupportsOverlayDetailContainer(isTablet) == true) {
                    RCommunicatorCommon.id.communicator_overlay_detail_container_id
                } else null
        }
    }

    /**
     * Ищем фрагмент хоста не только в [FragmentManager]'е [Activity], но и в числе родительских.
     * Это позволит найти его в случае, когда он содержится во [FragmentManager]'е вложенного фрагмента.
     */
    private fun findHostFragment(): Fragment? {
        fun List<Fragment>.findCommunicatorMasterDetailFragment(): Fragment? =
            this.firstOrNull { it is CommunicatorMasterDetailFragment }

        val activityFragments = supportFragmentManager?.fragments
        return activityFragments?.findCommunicatorMasterDetailFragment()
            ?: activityFragments?.firstOrNull()?.childFragmentManager?.fragments?.findCommunicatorMasterDetailFragment()
            ?: navigatedFragment?.parentFragmentManager?.fragments?.findCommunicatorMasterDetailFragment()
            ?: if (navigatedFragment?.parentFragment is CommunicatorMasterDetailFragment) {
                navigatedFragment?.parentFragment
            } else {
                null
            }
    }

    private fun findRegistryFragment(): Fragment? =
        hostFragmentManager?.fragments
            ?.firstOrNull { it is MasterFragment }

    private fun initDependencies() {
        dependency = communicatorNavigationDependency
        navigationBus = CommunicatorCommonComponent.getInstance(context).appRxBus
        isTablet = DeviceConfigurationUtils.isTablet(context)
    }

    /** @SelfDocumented */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun detachCommunicatorRouter() {
        navigatedFragment?.lifecycle?.removeObserver(this)
        navigatedFragment = null
        hostFragmentManager = null
        supportFragmentManager = null
        dependency = null
        disposer.dispose()
    }

    /** @SelfDocumented */
    override fun startNewConversation(personUuid: UUID) {
        showConversationDetailsScreen(
            ConversationDetailsParams(
                null, null, null, arrayListOf(personUuid),
                null, null, null, ConversationType.REGULAR, false,
                archivedDialog = false, viewData = null, title = null, fromChatTab = false, needToShowKeyboard = true
            )
        )
    }

    /** @SelfDocumented */
    override fun showConversationDetailsScreen(params: ConversationDetailsParams) {
        showConversationDetailsScreen(params, null)
    }

    override fun showConsultationDetailsScreen(params: CRMConsultationParams, onCloseCallback: (() -> Unit)?) {
        val consultationProvider = dependency?.crmConversationProvider ?: return
        val consultationFragmentFactory =  dependency?.crmConversationFragmentFactory ?: return

        val consultationIntent = consultationProvider.getCRMConversationActivityIntent(params)

        openScreen(
            intent = consultationIntent,
            useOverlayDetailContainer = true,
            onCloseCallback = onCloseCallback
        ) {
            consultationFragmentFactory.createCRMConversationFragment(params)
        }
    }

    /** @SelfDocumented */
    override fun showConversationDetailsScreen(
        params: ConversationDetailsParams,
        onCloseCallback: (() -> Unit)?
    ) {
        val conversationProvider = dependency?.conversationProvider ?: return

        val incomingIntent = navigatedFragment?.activity?.intent
        val filesToShareList = params.files?.also { incomingIntent?.removeExtra(Intent.EXTRA_STREAM) }
            ?: ConversationUtils.getFilesToShare(incomingIntent)
        val textToShare = params.textToShare?.also { incomingIntent?.removeExtra(Intent.EXTRA_TEXT) }
            ?: ConversationUtils.getTextToShare(incomingIntent)
        val containsSharingContent = filesToShareList.isNullOrEmpty().not() || textToShare.isNullOrEmpty().not()

        checkShareContentToCloseConversation(params.dialogUuid, textToShare, filesToShareList)
        val conversationIntent = conversationProvider.getConversationActivityIntent(
            params.dialogUuid,
            params.messageUuid,
            null,
            params.participantsUuids,
            filesToShareList,
            textToShare,
            null,
            params.type,
            params.isChat,
            params.archivedDialog,
            params.isGroupConversation
        ).apply {
            putParcelableArrayListExtra(
                CommunicatorCommonFeature.CONVERSATION_ACTIVITY_TOOLBAR_VIEW_DATA,
                params.viewData?.asArrayList()
            )
            putExtra(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_TOOLBAR_TITLE, params.title)
            putExtra(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_TOOLBAR_DIALOG_TITLE, params.dialogTitle)
            putExtra(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_TOOLBAR_PHOTO_ID, params.photoId)
            putExtra(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_CHATS_REGISTRY_KEY, params.fromChatTab)
            putExtra(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_IS_SHARING, containsSharingContent)
            putExtra(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_THREAD_INFO, params.threadCreationInfo)
            putExtra(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_FROM_PARENT_THREAD, params.fromParentThread)
            putExtra(CommunicatorCommonFeature.CONVERSATION_ACTIVITY_HIGHLIGHT_MESSAGE, params.highlightMessage)
            putExtra(IntentAction.Extra.NEED_TO_SHOW_KEYBOARD, params.needToShowKeyboard)
        }
        navigatedFragment?.castTo<ConversationOpener>()?.resetStateForNewData(params.dialogUuid, params.messageUuid)

        openScreen(
            intent = conversationIntent,
            useOverlayDetailContainer = true,
            onCloseCallback = onCloseCallback
        ) {
            conversationProvider.getConversationFragment(conversationIntent.extras!!)
        }
    }

    override fun openConversationPreview(params: ConversationParams, list: List<ThemeConversationPreviewMenuAction>) {
        supportFragmentManager?.let {
            val fragment = conversationPreviewFragmentFactoryProvider.get().create(
                list,
                ConversationPreviewMode.EXPANDABLE_PREVIEW,
                params
            )
            fragment.show(it, fragment::class.java.simpleName)
        }
    }

    /**
     * Проверить наличие контента "поделиться" для потенциального закрытия переписки с тем же идентификатором.
     *
     * Проблема связана с колбэками контроллера вложений панели сообщений,
     * из-за чего на двух разных экрана с одним и тем же диалогам одновременно загружается новое вложение.
     * Пока что для этого сценария избыточно накручивать новую логику в панели сообщений.
     */
    private fun checkShareContentToCloseConversation(
        conversationUuid: UUID?,
        shareText: String?,
        shareFiles: List<Uri>?
    ) {
        if (!(shareText.isNullOrBlank() && shareFiles.isNullOrEmpty())) {
            conversationUuid?.also { uuid ->
                val fragmentManager = supportFragmentManager ?: return
                val currentConversation = fragmentManager.fragments.find {
                    it.castTo<ConversationScreen>()?.conversationUuid == uuid
                } ?: return
                fragmentManager.beginTransaction()
                    .remove(currentConversation)
                    .commit()
            }
        }
    }

    /** @SelfDocumented */
    override fun showNewsDetails(
        onNewScreen: Boolean,
        documentUuid: String?,
        messageUuid: UUID?,
        dialogUuid: UUID?,
        isReplay: Boolean,
        showComments: Boolean
    ) {
        if (documentUuid.isNullOrBlank()) {
            showNoDocumentError()
            return
        }
        val newsActivityProvider = dependency?.newsActivityProvider ?: run {
            openDocumentInWebView(documentUuid, NEWS_INFIX)
            return
        }

        val intent = with(newsActivityProvider) {
            when {
                showComments -> getNewsSectionCommentIntent(documentUuid)
                messageUuid == null -> getNewsIntent(documentUuid)
                isReplay -> getNewsReplyCommentIntent(documentUuid, messageUuid, dialogUuid!!)
                else -> getNewsShowCommentIntent(documentUuid, messageUuid)
            }
        }
        intent.mapShareContent(navigatedFragment?.activity?.intent)

        openScreen(intent) {
            newsActivityProvider.getNewsCardFragment(intent.extras)
        }
    }

    /** @SelfDocumented */
    override fun showTask(documentUuid: String?) {
        if (documentUuid.isNullOrBlank()) {
            showNoDocumentError()
            return
        }
        val args = WithUuidAndEventUuidArgs(
            documentUuid = UUID.fromString(documentUuid),
            eventUuid = null,
            docType = "",
        )
        val additionalArgs = AdditionalDocumentOpenArgs.Regular()
        val intent = dependency?.documentFeature?.createDocumentCardActivityIntent(context, args, additionalArgs) ?: run {
            openDocumentInWebView(documentUuid)
            return
        }
        openScreen(intent) {
            dependency?.documentFeature?.createDocumentCardFragment(args, additionalArgs)
        }
    }

    /** @SelfDocumented */
    override fun startVideoCall(profileUuid: UUID) {
        context.also { context ->
            if (!NetworkUtils.isConnected(context)) {
                SbisPopupNotification.pushToast(
                    context,
                    ru.tensor.sbis.common.R.string.common_video_call_no_internet
                )
                return
            }
            dependency?.callActivityProvider?.performOutgoingCall(
                profileUuid.toString(),
                false
            )
        }
    }

    private fun openDocumentInWebView(documentUuid: String, infix: String? = null) {
        dependency?.openLinkControllerProvider?.openLinkController?.let {
            val url = if (infix.isNullOrBlank()) CommonUtils.createUnsupportedDocumentLinkByUuid(documentUuid) else
                CommonUtils.createLinkByUuid(infix, documentUuid)
            it.processAndForget(url)
        }
    }

    /** @SelfDocumented */
    override fun showProfile(uuid: UUID) {
        val personCardProvider = dependency?.personCardProvider ?: return
        if (checkIsProfileAlreadyOpened(uuid)) return

        val personCardIntent = personCardProvider.createPersonCardIntent(context, uuid)
        if (navigatedFragment !is ThemesRegistry) {
            openScreen(personCardIntent) {
                dependency?.personCardFragmentFactory
                    ?.createPersonCardFragment(personCardIntent.extras!!)
                    ?.also { addProfileUuidTag(it, uuid) }
            }
        } else {
            themeSpecificProfileOpen(personCardIntent, uuid)
        }
    }

    private fun checkIsProfileAlreadyOpened(uuid: UUID): Boolean =
        detailsContainerId != null
                && hostFragmentManager?.findFragmentById(detailsContainerId!!)
            ?.arguments
            ?.getString(PROFILE_UUID_TAG) == uuid.toString()

    private fun addProfileUuidTag(fragment: Fragment, uuid: UUID) {
        fragment.arguments?.putString(PROFILE_UUID_TAG, uuid.toString())
    }

    /** @SelfDocumented */
    override fun showViolationDetails(documentUuid: UUID?) {
        if (documentUuid == null) {
            showNoDocumentError()
            return
        }
        val violationProvider = dependency?.violationActivityProvider ?: run {
            openDocumentInWebView(UUIDUtils.toString(documentUuid))
            return
        }

        val violationIntent = violationProvider.getViolationDetailsIntent(documentUuid, null)
        openScreen(violationIntent) {
            violationProvider.getViolationDetailsFragment(violationIntent.extras!!)
        }
    }

    /** @SelfDocumented */
    override fun showLinkInWebView(url: String, title: String?) {
        dependency?.docWebViewerFeature?.showDocumentLink(context, title, url)
    }

    /** @SelfDocumented */
    override fun showVerificationFragment(registryContainerId: Int) {
        val verificationFragmentProvider = dependency?.verificationFragmentProvider
        if (verificationFragmentProvider == null || hostFragmentManager == null) return

        val verificationFragment = verificationFragmentProvider.createVerificationWithAlertFragment()
        val containerId = detailsContainerId ?: registryContainerId
        val tag = verificationFragment.javaClass.simpleName

        hostFragmentManager!!.beginTransaction()
            .doIf(!isTablet) {
                setCustomAnimations(RDesign.anim.right_in, RDesign.anim.left_out, RDesign.anim.left_in, RDesign.anim.right_out)
            }
            .add(containerId, verificationFragment, tag)
            .addToBackStack(verificationFragment.javaClass.simpleName)
            .commit()
    }

    override fun showNotificationListScreen(
        registryContainerId: Int,
        conversationUuid: UUID,
        toolbarTitle: String,
        photoUrl: String,
        configuration: NotificationListViewConfiguration
    ) {
        val fragment: Fragment =
            dependency?.notificationFeature?.getNotificationListFragmentAsCard(
                configuration = NotificationListFragmentAsCardConfiguration(
                    title = toolbarTitle,
                    imageUrl = photoUrl,
                    listConfig = configuration,
                    navigateBack = !isTablet,
                    swipeBackEnabled = !isTablet
                )
            ) ?: return

        showDetailFragment(fragment)
    }

    /** @SelfDocumented */
    override fun changeRegistry(registryType: CommunicatorRegistryType) {
        if (navigatedFragment !is CommunicatorMasterDetailFragment) removeSubContent()

        val hostFragmentNotNull = hostFragmentManager != null

        when {
            hostFragmentNotNull && customizationOptions.navigationWithCachedFragment -> {
                changeRegistryWithCached(registryType)
            }

            hostFragmentNotNull -> {
                replaceRegistryInMaster(registryType)
            }

            else -> {
                navigatedFragment?.getParentFragmentAs<RegistryTabSwitcher>()?.switchTab(registryType)
            }
        }
    }

    private fun replaceRegistryInMaster(registryType: CommunicatorRegistryType) {
        val registryFragment = when (registryType) {
            is DialogsRegistry -> dependency?.themesRegistryFragmentFactory?.createThemeFragment(registryType)
            is ChatsRegistry -> dependency?.themesRegistryFragmentFactory?.createThemeFragment(registryType)
        }?.addRegistryType(registryType)
        registryFragment?.let {
            hostFragmentManager!!
                .beginTransaction()
                .replace(RCommunicatorCore.id.communicator_master_fragment_container, it)
                .commitNowAllowingStateLoss()
        }
    }

    // При возврате на вкладку сотрудники через ННП, если до переключения были контакты
    // Tab View не может найти нужный id вкладки при вызове commitNowAllowingStateLoss
    private fun FragmentTransaction.commitIfTabViewNotAvailable() = try {
        commitNow()
    } catch (ex: IllegalStateException) {
        commit()
    }

    private fun changeRegistryWithCached(registryType: CommunicatorRegistryType) {
        val currentFragment = findFragment()
        val newFragment = findFragment(true, registryType)
        hostFragmentManager!!
            .beginTransaction()
            .apply {
                when {
                    // если новый фрагмент еще не создавался, то создаём и добавляем, старый пытаемся скрыть
                    newFragment == null -> {
                        createRegistry(registryType)?.also {
                            add(RCommunicatorCore.id.communicator_master_fragment_container, it)
                        }
                        currentFragment?.also { hide(it) }
                    }
                    // если переключение идёт в рамках ThemesRegistry - меняем вкладку
                    currentFragment is ThemesRegistry && registryType is ThemeRegistryType -> {
                        changeThemesRegistry(currentFragment, registryType)
                    }
                    // иначе показываем новый фрагмент из кэша и старый пытаемся скрыть
                    else -> {
                        if (currentFragment?.getRegistryType() == registryType) return@apply
                        show(newFragment)
                        if (registryType is ThemeRegistryType) {
                            changeThemesRegistry(newFragment, registryType)
                        }
                        currentFragment?.also { hide(it) }
                    }
                }
            }
            .commitNowAllowingStateLoss()
    }

    private fun changeThemesRegistry(fragment: Fragment, registryType: ThemeRegistryType) {
        if (fragment !is ThemesRegistry) return
        fragment.apply {
            addRegistryType(registryType)
            changeThemesRegistry(registryType)
        }
    }

    private fun findFragment(fromCache: Boolean = false, registryType: CommunicatorRegistryType? = null): Fragment? =
        hostFragmentManager!!.fragments.find {
            if (fromCache) {
                (it.getRegistryType() == registryType) || (it.getRegistryType() is ThemeRegistryType && registryType is ThemeRegistryType)
            } else {
                it.id == RCommunicatorCore.id.communicator_master_fragment_container && !it.isHidden
            }
        }

    private fun createRegistry(registryType: CommunicatorRegistryType): Fragment? =
        dependency?.themesRegistryFragmentFactory?.createThemeFragment(registryType)?.addRegistryType(registryType)

    private fun prefetchFragment(@IdRes containerId: Int, fragment: Fragment, tag: String) {
        Looper.myQueue().addIdleHandler {
            hostFragmentManager!!.beginTransaction()
                .add(containerId, fragment, tag)
                .hide(fragment)
                .commit()
            false
        }
    }

    /** @SelfDocumented */
    override fun changeNavigationSelectedItem(registryType: CommunicatorRegistryType) {
        val selectedMenuItem =
            when (registryType) {
                is DialogsRegistry   -> DIALOGS
                is ChatsRegistry     -> CHATS
                else                 -> null
            }
        selectedMenuItem?.let {
            navigationBus?.post(NavigationChangeEvent(it))
        }
    }

    override fun changeRegistrySelectedItem(uuid: UUID) {
        findHostFragment()?.castTo<ConversationOpener>()?.resetStateForNewData(uuid, null)
    }

    /** @SelfDocumented */
    override fun openScreen(
        intent: Intent,
        useOverlayDetailContainer: Boolean,
        onCloseCallback: (() -> Unit)?,
        fragmentProvider: () -> Fragment?
    ) {
        val isShown = fragmentProvider()?.let { fragment ->
            showDetailFragment(fragment, useOverlayDetailContainer, onCloseCallback)
        } == true
        if (!isShown) {
            openNewActivity(intent)
        }
    }

    private fun showDetailFragment(
        fragment: Fragment,
        useOverlayDetailContainer: Boolean = true,
        onCloseCallback: (() -> Unit)? = null
    ): Boolean =
        when {
            detailsContainerId != null -> {
                fragment.arguments?.putBoolean(IntentAction.Extra.TABLET_ACTION, true)
                setSubContent(fragment)
                true
            }
            useOverlayDetailContainer && overlayDetailContainerId != null -> {
                placeTopDetailContent(fragment, onCloseCallback)
                true
            }
            else -> false
        }

    private fun openNewActivity(intent: Intent) {
        context.startActivity(intent)
    }

    /** @SelfDocumented */
    override fun removeSubContent() {
        clearBackStack()
        hostFragmentManager?.run {
            val subFragment = detailsContainerId?.let(::findFragmentById)
            subFragment?.let {
                beginTransaction()
                    .remove(it)
                    .commitAllowingStateLoss()
            }
        }
    }

    /** @SelfDocumented */
    override fun setSubContent(fragment: Fragment) {
        if (isMasterTransaction) clearBackStack()
        addFragmentToDetailsContainer(fragment)
    }

    private fun placeTopDetailContent(
        fragment: Fragment,
        onCloseCallback: (() -> Unit)? = null
    ) {
        val fragmentManager = supportFragmentManager ?: return
        val containerId = overlayDetailContainerId ?: return
        val fragmentTag = fragment.javaClass.simpleName + containerId

        // Пытаемся опустить интерфейс на текущем открытом фрагменте в контейнере
        fragmentManager.findFragmentById(containerId)
            ?.castTo<AdjustResizeHelper.KeyboardEventListener>()
            ?.onKeyboardCloseMeasure(0)

        fragmentManager
            .beginTransaction()
            .setCustomAnimations(RDesign.anim.right_in, RDesign.anim.nothing, RDesign.anim.nothing, RDesign.anim.right_out)
            .add(containerId, fragment, fragmentTag)
            .addToBackStack(fragment)
            // Для 5-6 версий android нельзя звать commit после onSaveInstanceState до onRestoreInstanceState Activity.
            .commitAllowingStateLoss()

        onCloseCallback?.let { callback ->
            fragmentManager.addOnBackStackChangedListener(object : OnBackStackChangedListener {
                override fun onBackStackChanged() {
                    if (fragmentManager.findFragmentByTag(fragmentTag) == null) {
                        fragmentManager.removeOnBackStackChangedListener(this)
                        callback()
                    }
                }
            })
        }
    }

    /**
     * Дополнительное правило обработки открытия профиля в реестре диалогов на планшете,
     * откроет в detail стеке подряд не более одного профиля
     * стрелка перехода назад будет дополнительно доступна при смене одного профиля на другой без добавления в стек,
     * и скрыта, если detail контейнер пуст
     */
    private fun themeSpecificProfileOpen(intent: Intent, uuid: UUID) {
        if (detailsContainerId != null) {
            dependency?.personCardProvider?.let { provider ->
                val profileFragment =
                    intent.extras?.let(provider::createPersonCardFragment)
                        ?.also { addProfileUuidTag(it, uuid) }
                        ?: return

                with(profileFragment.requireArguments()) {
                    putBoolean(IntentAction.Extra.TABLET_ACTION, true)
                    putBoolean(
                        IntentAction.Extra.NEED_TO_ADD_FRAGMENT_TO_BACKSTACK,
                        hostFragmentManager!!.backStackEntryCount > 0
                    )
                    putBoolean(THEME_PROFILE_FRAGMENT, true)
                }

                val needAddToBackStack =
                    hostFragmentManager!!.findFragmentById(detailsContainerId!!)?.arguments?.let {
                        !it.getBoolean(THEME_PROFILE_FRAGMENT, false)
                    } ?: false

                addFragmentToDetailsContainer(profileFragment, needAddToBackStack)
            }
        } else {
            openNewActivity(intent)
        }
    }

    /** @SelfDocumented */
    override fun onNewDeeplinkAction(args: DeeplinkAction) {
        handleDeeplinkAction(args)
    }

    /** @SelfDocumented */
    override fun handleDeeplinkAction(args: DeeplinkAction) {
        if (!getDeeplinkAvailability(args)) return
        when (args) {
            is OpenEntityDeeplinkAction -> showProfile(UUIDUtils.fromString(args.uuid) ?: return)
            is OpenProfileDeeplinkAction -> showProfile(args.profileUuid)
            is OpenWebViewDeeplinkAction -> showLinkInWebView(args.documentUrl, args.documentTitle)
            is OpenConversationDeeplinkAction -> {
                showConversationDetailsScreen(
                    ConversationDetailsParams(
                        dialogUuid = args.dialogUuid,
                        messageUuid = args.messageUuid,
                        participantsUuids = args.recipients,
                        isChat = args.isChat,
                        title = args.title,
                        photoId = args.photoId,
                        isGroupConversation = args.isGroupConversation,
                        files = args.filesToShare
                    )
                )
            }
            is OpenNewsDeepLinkAction ->
                showNewsDetails(
                    onNewScreen = isTablet,
                    documentUuid = args.docUuid,
                    messageUuid = null,
                    dialogUuid = args.commentUuid,
                    isReplay = false,
                    showComments = true
                )
            is OpenArticleDiscussionDeeplinkAction ->
                showArticleDiscussion(
                    CommunicatorArticleDiscussionParams(
                        documentUuid = args.documentUuid,
                        dialogUuid = args.dialogUuid,
                        messageUuid = args.messageUuid,
                        documentUrl = args.documentUrl ?: "",
                        documentTitle = args.documentTitle,
                        isSocnetEvent = args.isSocnetEvent
                    )
                )
            is ShareToMessagesDeeplinkAction -> {
                navigatedFragment?.requireActivity()?.let {
                    val dialogSelectionIntent = DialogSelectionFeatureImpl.getDialogSelectionActivityIntent(it)
                    copyExtrasFromDataIntent(args.dataIntent, dialogSelectionIntent)
                    it.startActivity(dialogSelectionIntent)
                }
            }
            is OpenViolationDeeplinkAction -> {
                showViolationDetails(documentUuid = args.uuid)
            }
            is OpenInstructionDeeplinkAction -> {
                showDocumentActivity(
                    uuid = args.uuid,
                    url = args.url,
                    title = args.title
                )
            }
            else -> Unit
        }
    }

    private fun copyExtrasFromDataIntent(dataIntent: Intent, targetIntent: Intent) {
        // Шарим файлы
        val filesToShare = ArrayList<Uri>()
        dataIntent.clipData?.let { clipData ->
            for (i in 0 until clipData.itemCount) {
                clipData.getItemAt(i).uri?.let { uri -> filesToShare.add(uri) }
            }
        }
        when {
            dataIntent.action == Intent.ACTION_SEND && filesToShare.size == 1         -> {
                targetIntent.putExtra(Intent.EXTRA_STREAM, filesToShare[0])
                targetIntent.putExtra(DATA_INTENT_ACTION, Intent.ACTION_SEND)
            }
            dataIntent.action == Intent.ACTION_SEND_MULTIPLE && filesToShare.size > 1 -> {
                targetIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, filesToShare)
                targetIntent.putExtra(DATA_INTENT_ACTION, Intent.ACTION_SEND_MULTIPLE)
            }
        }
        // Шарим текст
        val textToShare = dataIntent.clipData?.let {
            if (it.itemCount > 0) {
                it.getItemAt(0).text?.toString()
            } else {
                null
            }
        }
        if (!textToShare.isNullOrBlank()) {
            targetIntent.putExtra(Intent.EXTRA_TEXT, textToShare)
        }
    }

    /** @SelfDocumented */
    override fun showArticleDiscussion(params: CommunicatorArticleDiscussionParams) {
        with(params) { showLinkInWebView(UrlUtils.formatUrl(documentUrl), documentTitle) }
    }

    private fun showDocumentActivity(uuid: UUID, url: String, title: String?) {
        val intent = dependency?.docWebViewerFeature?.createDocumentActivityIntent(context, title, url, uuid.toString())
        intent?.also(::openScreen)
    }

    private fun getDeeplinkAvailability(args: DeeplinkAction): Boolean =
        (dependency != null).also {
            if (!it) requiredDeeplinkAction = args
        }

    private fun performSavedDeeplinkAction() {
        if (requiredDeeplinkAction != null && dependency != null) {
            handleDeeplinkAction(requiredDeeplinkAction!!)
            requiredDeeplinkAction = null
        }
    }

    private fun addFragmentToDetailsContainer(
        fragment: Fragment,
        needAddToBackStack: Boolean = backStackRule
    ) {
        hostFragmentManager?.beginTransaction()!!
            .replace(detailsContainerId!!, fragment)
            .doIf(needAddToBackStack) { addToBackStack(fragment) }
            .commit()
    }

    private fun clearBackStack() {
        var clearedBackStack: Boolean
        do { clearedBackStack = popBackStack() }
        while (clearedBackStack)
    }

    /** @SelfDocumented */
    override fun popBackStack(): Boolean =
        supportFragmentManager?.let { fragmentManager ->
            when {
                overlayDetailContainerId?.let(fragmentManager::findFragmentById) != null
                        && fragmentManager.backStackEntryCount > 0 -> {
                    fragmentManager.safePopBackStackImmediate()
                    true
                }
                hostFragmentManager?.backStackEntryCount?.let { it > 0 } == true -> {
                    hostFragmentManager?.safePopBackStackImmediate()
                }
                else -> false
            }
        } ?: false

    private fun FragmentManager.safePopBackStackImmediate(): Boolean {
        return if (!isStateSaved) {
            executePendingTransactions()
            popBackStackImmediate()
        } else {
            Handler(Looper.getMainLooper()).post {
                safePopBackStackImmediate()
            }
            false
        }
    }

    override fun canPopBackStack(): Boolean =
        supportFragmentManager?.let { fragmentManager ->
            val hasOverlayStack = overlayDetailContainerId?.let(fragmentManager::findFragmentById) != null
                && fragmentManager.backStackEntryCount > 0
            val hasHostStack = hostFragmentManager?.backStackEntryCount?.let { it > 0 } == true
            hasOverlayStack || hasHostStack
        } ?: false

    override fun getTopSubContent(): Fragment? =
        supportFragmentManager?.let { fragmentManager ->
            overlayDetailContainerId?.let(fragmentManager::findFragmentById)
                ?: if (detailsContainerId != null) {
                    hostFragmentManager?.findFragmentById(detailsContainerId!!)
                } else {
                    null
                }
        }

    private fun FragmentTransaction.addToBackStack(fragment: Fragment) = apply {
        addToBackStack(fragment::class.java.simpleName)
        fragment.arguments?.putBoolean(IntentAction.Extra.NEED_TO_ADD_FRAGMENT_TO_BACKSTACK, true)
    }

    private fun Fragment.addRegistryType(type: CommunicatorRegistryType) = apply {
        if (this is MasterFragment) {
            if (arguments == null) {
                arguments = Bundle().apply { putSerializable(REGISTRY_TYPE_ARG, type) }
            } else {
                arguments?.putSerializable(REGISTRY_TYPE_ARG, type)
            }
        }
    }

    private fun Fragment.getRegistryType(): CommunicatorRegistryType? =
        if (this is MasterFragment) {
            arguments?.getSerializable(REGISTRY_TYPE_ARG) as? CommunicatorRegistryType
        } else null

    private fun showNoDocumentError() =
        SbisPopupNotification.pushToast(context, RCommon.string.common_show_document_failed)

    companion object : CommunicatorNavigationFeature {

        override fun getCommunicatorRouter(): CommunicatorRouter = CommunicatorRouterImpl()

        override fun getCommunicatorHostRouter(): CommunicatorHostRouter =
            getCommunicatorRouter()

        override fun getCommunicatorConversationRouter(): CommunicatorConversationRouter =
            getCommunicatorRouter()

        override fun getCommunicatorThemesRouter(): CommunicatorThemesRouter =
            getCommunicatorRouter()

        override fun getCommunicatorDialogInformationRouter(): CommunicatorDialogInformationRouter =
            getCommunicatorRouter()
    }
}

private const val THEME_PROFILE_FRAGMENT = "theme_profile_fragment"
private const val NEWS_INFIX = "/news/"
private const val REGISTRY_TYPE_ARG = "RegistryTypeArg"
private const val PROFILE_UUID_TAG = "ProfileUuidTag"
private const val TAB_VIEW_NOT_AVAILABLE = "Unable to get tab view"