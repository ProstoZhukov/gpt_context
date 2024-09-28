package ru.tensor.sbis.communicator.sbis_conversation.conversation.utils;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;

import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.attachments.decl.AllowedActionResolver;
import ru.tensor.sbis.attachments.decl.attachment_list.AttachmentListViewerIntentFactory;
import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListComponentConfig;
import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListEntity;
import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListParams;
import ru.tensor.sbis.attachments.decl.viewer.RegularAttachmentParams;
import ru.tensor.sbis.attachments.decl.viewer.RegularAttachmentViewerArgs;
import ru.tensor.sbis.attachments.models.action.AttachmentActionType;
import ru.tensor.sbis.attachments.models.id.AttachmentId;
import ru.tensor.sbis.common.util.CommonUtils;
import ru.tensor.sbis.common.util.UrlUtils;
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin;
import ru.tensor.sbis.communicator.sbis_conversation.contract.CommunicatorSbisConversationDependency;
import ru.tensor.sbis.design_notification.SbisPopupNotification;
import ru.tensor.sbis.tasks.feature.AdditionalDocumentOpenArgs;
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature;
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController;
import ru.tensor.sbis.communication_decl.meeting.EventCardType;
import ru.tensor.sbis.communication_decl.meeting.MeetingActivityProvider;
import ru.tensor.sbis.edo_decl.document.Document;
import ru.tensor.sbis.edo_decl.document.DocumentType;
import ru.tensor.sbis.info_decl.news.ui.NewsActivityProvider;
import ru.tensor.sbis.tasks.feature.DocumentFeature;
import ru.tensor.sbis.tasks.feature.WithUuidAndEventUuidArgs;
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs;
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory;
import timber.log.Timber;

/** SelfDocumented */
public class DocumentOpenUtils {

    /** SelfDocumented */
    public static boolean showDocument(@NonNull Context context, @NonNull Document document) {
        switch (document.getType()) {
            case MEETING:
            case WEBINAR:
                return showEventCard(context, document);
            case ORDER:
            case DOCUMENT:
                return showIncomingDocument(document);
            case NEWS:
                return showNews(context, document);
            case SHARED_FILE:
                return showSharedFile(context, document);
            case DISC_FOLDER:
                return showDiscFolder(context, document);
            case TASK:
                return showTask(context, document);
            default:
                return showUnsupportedDocument(context, document);
        }
    }

    public static boolean showIncomingDocument(@NonNull Document document) {
        final String uuid = document.getUuid();
        if (!TextUtils.isEmpty(uuid)) {
            String documentLinkInfix = document.getLinkInfix();
            String url = TextUtils.isEmpty(documentLinkInfix)
                    ? CommonUtils.createLinkByUuid(CommonUtils.INFIX, uuid)
                    : UrlUtils.formatUrl(Objects.requireNonNull(documentLinkInfix));

            OpenLinkController.Provider provider = getDependency().getOpenLinkControllerProvider();
            if (url != null && provider != null) {
                provider.getOpenLinkController().processAndForget(url);
                return true;
            }
        }
        return false;
    }

    private static boolean showTask(@NonNull Context context, @NonNull Document document) {
        return showTask(context, document.getUuid());
    }

    /** SelfDocumented */
    @SuppressWarnings("WeakerAccess")
    public static boolean showTask(@NonNull Context context, @NonNull String uuid) {
        if (!TextUtils.isEmpty(uuid)) {
            final DocumentFeature provider = getDependency().getDocumentFeature();
            if (provider != null) {
                Intent intent = provider.createDocumentCardActivityIntent(
                        context,
                        new WithUuidAndEventUuidArgs(UUID.fromString(uuid), null, "", false, null),
                        new AdditionalDocumentOpenArgs.Regular()
                );
                if (context == context.getApplicationContext()) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                return true;
            }
        }
        return false;
    }

    /**
     * TODO https://online.sbis.ru/opendoc.html?guid=7f2d4270-66e3-4985-b8c9-468f8e2af044&client=3
     */
    public static boolean isWorkPlanDocument(Document document) {
        return document.getSubtype() != null && document.getSubtype().equals("ПланРабот");
    }

    @SuppressWarnings("SameReturnValue")
    private static boolean showSharedFile(@NonNull Context context, @NonNull Document document) {
        UUID diskId = document.getDiskId();
        if (diskId != null) {
            ViewerSliderIntentFactory factory = getDependency().getViewerSliderIntentFactory();
            if (factory != null) {
                EnumSet<AttachmentActionType> allowedActions = EnumSet.of(
                    AttachmentActionType.VIEW_DETAILS,
                    AttachmentActionType.VIEW_REDACTIONS,
                    AttachmentActionType.VIEW_ACCESS_RIGHTS,
                    AttachmentActionType.VIEW_LINK,
                    AttachmentActionType.OPEN,
                    AttachmentActionType.SHARE,
                    AttachmentActionType.DOWNLOAD,
                    AttachmentActionType.DOWNLOAD_PDF_WITH_STAMP
                );
                RegularAttachmentViewerArgs viewerParams = new RegularAttachmentViewerArgs(
                    new RegularAttachmentParams(UrlUtils.FILE_SD_OBJECT, new AttachmentId(diskId)),
                    document.getTitle(),
                    new AllowedActionResolver.FromActions(allowedActions)
                );
                Intent intent = factory.createViewerSliderIntent(context, new ViewerSliderArgs(viewerParams));
                context.startActivity(intent);
                return true;
            } else {
                Timber.e("ViewerSliderIntentFactory id is null");
                return false;
            }
        } else {
            Timber.e("Disk id is null");
            return false;
        }
    }

    @SuppressWarnings("SameReturnValue")
    private static boolean showDiscFolder(@NonNull Context context, @NonNull Document document) {
        UUID diskId = document.getDiskId();
        if (diskId != null) {
            AttachmentListViewerIntentFactory intentFactory = getDependency().getAttachmentListViewerIntentFactory();
            AllowedActionResolver allowedActionResolver = new AllowedActionResolver.AlwaysTrue();
            if (intentFactory != null) {
                Intent intent = intentFactory.newAttachmentListViewerIntent(
                    context,
                    new DefAttachmentListComponentConfig(
                        allowedActionResolver,
                        new DefAttachmentListParams(
                            new DefAttachmentListEntity.CloudFolder(
                                diskId,
                                UrlUtils.FILE_SD_OBJECT,
                                document.getTitle(),
                                null
                            )
                        )
                    )
                );
                if (context == context.getApplicationContext()) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                return true;
            } else {
                Timber.e("AttachmentListViewerIntentFactory id is null");
                return false;
            }
        } else {
            Timber.e("Disk id is null");
            return false;
        }
    }

    private static boolean showEventCard(@NonNull Context context, @NonNull Document document){
        String documentUuid = document.getUuid();
        if (!TextUtils.isEmpty(documentUuid)) {
            MeetingActivityProvider provider = getDependency().getMeetingActivityProvider();
            EventCardType cardType =
                    (document.getType() == DocumentType.MEETING) ? new EventCardType.Meeting(documentUuid, null)
                    : (document.getType() == DocumentType.WEBINAR) ? new  EventCardType.Webinar(documentUuid, null)
                    : null;

            if(cardType != null && provider != null) {
                Intent intent = provider
                        .getEventCardActivityIntent(context, cardType);
                if (context == context.getApplicationContext()) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                return true;
            } else {
                showUnsupportedDocument(context, document);
            }
        }
        return false;
    }

    private static boolean showUnsupportedDocument(@NonNull Context context, @NonNull Document document) {
        return showDocument(context, document, CommonUtils.INFIX);
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean showDocument(@NonNull Context context, @NonNull Document document, @NonNull String urlInfix) {
        return showDocument(context, document, urlInfix, null);
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean showDocument(@NonNull Context context, @NonNull Document document, @NonNull String urlInfix, @Nullable String title) {
        final String uuid = document.getUuid();
        if (!TextUtils.isEmpty(uuid)) {
            String documentLinkInfix = document.getLinkInfix();
            String url =
                    TextUtils.isEmpty(documentLinkInfix)
                            ? CommonUtils.createLinkByUuid(urlInfix, uuid)
                            : UrlUtils.formatUrl(Objects.requireNonNull(documentLinkInfix));
            if (title == null) {
                title = document.getTitle();
            }
            if (title != null) {
                title = Html.fromHtml(title).toString();
            }
            DocWebViewerFeature featureProvider = getDependency().getDocWebViewerFeature();
            if (url != null && featureProvider != null) {
                featureProvider.showDocumentLink(context, title, url);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private static boolean showNews(@NonNull Context context, @NonNull Document document) {
        String documentUuid = document.getUuid();
        return showNewsByDocumentUuid(context, documentUuid);
    }

    private static boolean showNewsByDocumentUuid(@NonNull Context context,
                                                  @Nullable String newsUuid) {
        NewsActivityProvider newsActivityProvider = getDependency().getNewsActivityProvider();
        if (newsUuid != null && newsActivityProvider != null) {
            Intent intent = newsActivityProvider
                    .getNewsIntent(newsUuid);
            if (context == context.getApplicationContext()) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            return true;
        } else {
            SbisPopupNotification.pushToast(context,
                    ru.tensor.sbis.common.R.string.common_show_document_failed);
        }
        return false;
    }

    private static CommunicatorSbisConversationDependency getDependency() {
        return CommunicatorSbisConversationPlugin.communicatorSbisConversationDependency;
    }
}