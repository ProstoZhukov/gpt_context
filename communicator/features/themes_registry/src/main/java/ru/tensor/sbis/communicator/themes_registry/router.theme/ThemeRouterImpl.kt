package ru.tensor.sbis.communicator.themes_registry.router.theme

import io.reactivex.subjects.PublishSubject
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationFromRegistryParams
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationViewMode
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communication_decl.crm.CRMConsultationOpenParams
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communicator.common.data.ConversationDetailsParams
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.navigation.data.CommunicatorArticleDiscussionParams
import ru.tensor.sbis.communicator.generated.ChatType
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.*
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.BaseThemeRouterDelegate
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.conversation.ThemeConversationRouter
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.conversation.ThemeConversationRouterImpl
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.external.ThemeExternalScreenRouter
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.external.ThemeExternalScreenRouterImpl
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.navigation.ThemeNavigationDelegate
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.navigation.ThemeNavigationDelegateImpl
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.new_conversation.ThemeNewConversationRouter
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.new_conversation.ThemeNewConversationRouterImpl
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.participants.ThemeParticipantsRouter
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.participants.ThemeParticipantsRouterImpl
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.ThemeConversationParams
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.ThemeTypedRouting
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType.ARTICLE_DISCUSSION
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType.CONSULTATION
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType.CONVERSATION
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType.NEWS
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType.NOTIFICATION
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType.PROFILE
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType.QUESTION_DISCUSSION
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType.SOCNET_EVENT
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType.VIOLATION
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType.WEB_VIEW
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeFragment

/**
 * Реализация роутера реестра диалгов.
 * @see [ThemeRouter].
 * @see [BaseThemeRouterDelegate].
 *
 * @property conversationRouter    роутер экранов переписки.
 * @property newConversationRouter роутер создания новой переписки.
 * @property participantsRouter    роутер участиков диалога.
 * @property externalScreenRouter  роутер экранов внешних зависимостей.
 * @property navigationHelper      базовая навигация.
 * @property routeCallback         колбэк с типом открываемого экрана.
 * @property closeCallback         колбэк с типом закрытого экрана.
 *
 * @author vv.chekurda
 */
internal class ThemeRouterImpl private constructor(
    private val conversationRouter: ThemeConversationRouter,
    private val newConversationRouter: ThemeNewConversationRouter,
    private val participantsRouter: ThemeParticipantsRouter,
    private val externalScreenRouter: ThemeExternalScreenRouter,
    private val navigationHelper: ThemeNavigationDelegate,
    override val routeCallback: PublishSubject<ThemeRouteType> = PublishSubject.create(),
    override val closeCallback: PublishSubject<ThemeRouteType> = PublishSubject.create()
) : BaseThemeRouterDelegate(),
    ThemeRouter,
    ThemeConversationRouter by conversationRouter,
    ThemeNewConversationRouter by newConversationRouter,
    ThemeParticipantsRouter by participantsRouter,
    ThemeExternalScreenRouter by externalScreenRouter,
    ThemeNavigationDelegate by navigationHelper {

    private val routing: ThemeTypedRouting by lazy(::initTypedRouting)

    constructor() : this(
        ThemeConversationRouterImpl(),
        ThemeNewConversationRouterImpl(),
        ThemeParticipantsRouterImpl(),
        ThemeExternalScreenRouterImpl(),
        ThemeNavigationDelegateImpl()
    )

    override fun initRouter(fragment: ThemeFragment) {
        super.initRouter(fragment)
        conversationRouter.initRouter(fragment)
        newConversationRouter.initRouter(fragment)
        participantsRouter.initRouter(fragment)
        externalScreenRouter.initRouter(fragment)
        navigationHelper.initRouter(fragment)
    }

    /**
     * Инициализации навигационных маршрутов реестра по типам [ThemeRouteType].
     */
    private fun initTypedRouting() = ThemeTypedRouting(routeCallback, closeCallback) {
        registerRoute {
            type(VIOLATION)
            action { showViolationDetails(documentUuid) }
        }

        registerRoute {
            type(PROFILE)
            action { showProfile(documentUuid!!) }
        }

        registerRoute {
            type(ARTICLE_DISCUSSION)
            action {
                showArticleDiscussion(
                    CommunicatorArticleDiscussionParams(
                        documentUuid = documentUuid!!,
                        dialogUuid = uuid,
                        messageUuid = if (isSingleIncoming) {
                            socnetServiceObject?.relevantMessageUUID ?: messageUuid
                        } else {
                            null
                        },
                        documentUrl = documentUrl ?: EMPTY,
                        documentTitle = externalEntityTitle?.toString(),
                        isSocnetEvent = isSocnetEvent
                    )
                )
            }
        }

        registerRoute {
            type(QUESTION_DISCUSSION)
            action {
                showLinkInWebView(
                    UrlUtils.formatUrl(documentUrl!!),
                    externalEntityTitle?.toString()
                )
            }
        }

        registerRoute {
            type(NEWS)
            action { showNewsDetails(this) }
        }

        registerRoute {
            type(CONVERSATION)
            conversationAction { isChatTab: Boolean, isSearchEmpty: Boolean, isArchived: Boolean, closeAction ->
                showConversationDetailsScreen(
                    params = ConversationDetailsParams(
                        dialogUuid = uuid,
                        messageUuid = getFromMessageUuid(isChatTab, isSearchEmpty),
                        isChat = isChatForOperations,
                        viewData = participantsCollage,
                        title = title,
                        dialogTitle = dialogTitle.toString(),
                        photoId = participantsCollage.takeIf { it.size == 1 }?.first()?.photoUrl,
                        fromChatTab = isChatTab,
                        archivedDialog = isArchived,
                        isGroupConversation = chatType != ChatType.PRIVATE && (isGroupConversation || isChatForOperations),
                        type = if (chatType == ChatType.CONSULTATION) ConversationType.CONSULTATION else ConversationType.REGULAR
                    ),
                    onCloseCallback = closeAction
                )
            }
        }

        registerRoute {
            type(CONSULTATION)
            conversationAction { isChatTab: Boolean, isSearchEmpty: Boolean, _: Boolean, closeAction ->
                showConsultationDetailsScreen(
                    CRMConsultationOpenParams(
                        relevantMessageUuid = getFromMessageUuid(isChatTab, isSearchEmpty),
                        crmConsultationCase = CRMConsultationCase.Client(this.uuid)
                    ),
                    closeAction
                )
            }
        }

        registerRoute {
            type(SOCNET_EVENT)
            action {
                showLinkInWebView(
                    UrlUtils.formatUrl(documentUrl!!),
                    externalEntityTitle?.toString()
                )
            }
        }

        registerRoute {
            type(WEB_VIEW)
            action {
                showLinkInWebView(
                    UrlUtils.formatUrl(documentUrl!!),
                    externalEntityTitle?.toString()
                )
            }
        }

        registerRoute {
            type(NOTIFICATION)
            action {
                val noticeData = requireNotNull(noticeData)
                showNotificationListScreen(
                    conversationUuid = noticeData.uuid,
                    toolbarTitle = noticeData.toolbarTitle,
                    photoUrl = noticeData.photoUrl,
                    configuration = noticeData.configuration
                )
            }
        }
    }

    override fun openContentScreen(routeParams: ThemeConversationParams) {
        routing.openContentScreen(routeParams)
    }

    override fun openConversationPreview(routeParams: ThemeConversationParams, list: List<ThemeConversationPreviewMenuAction>) {
        openConversationPreview(
            params = ConversationFromRegistryParams(
                conversationUuid = routeParams.model.uuid,
                messageUuid = routeParams.model.getFromMessageUuid(routeParams.isChatTab, routeParams.isSearchEmpty),
                isChat = routeParams.isChatTab,
                conversationViewMode = ConversationViewMode.PREVIEW
            ),
            list = list
        )
    }

    override fun openLinkInWebView(link: String) {
        showLinkInWebView(
            UrlUtils.formatUrl(link),
            null
        )
    }
}

/**
 * Входящее сообщение переписки с одним участником.
 */
internal val ConversationModel.isSingleIncoming
    get() = participantsUuids.size == 1 && !isOutgoing

/**
 * Условие открытия переписки на релевантном сообщении:
 * На релевантном сообщении открываются только диалоги и непрочитанные каналы,
 * в которых сообщения адресованы текущему пользователю.
 */
internal fun ConversationModel.getFromMessageUuid(isChatTab: Boolean, isSearchEmpty: Boolean) =
    if (!(isChatTab || isChatForOperations) || unreadCount > 0 || !isSearchEmpty) messageUuid else null