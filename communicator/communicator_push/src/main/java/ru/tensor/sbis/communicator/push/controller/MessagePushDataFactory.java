package ru.tensor.sbis.communicator.push.controller;

import static ru.tensor.sbis.communicator.push.SupportSabyConversationCategory.SABY_SUPPORT_TITLE;
import static ru.tensor.sbis.communicator.push.SupportSabyConversationCategory.SETTY_KZ_SUPPORT_TITLE;
import static ru.tensor.sbis.communicator.push.SupportSabyConversationCategory.SETTY_SUPPORT_TITLE;
import static ru.tensor.sbis.edo_decl.document.DocumentType.DISCUSSION;
import static ru.tensor.sbis.edo_decl.document.DocumentType.DOCUMENT_DISCUSSION;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.tensor.sbis.common.util.UUIDUtils;
import ru.tensor.sbis.common.util.UrlUtils;
import ru.tensor.sbis.communication_decl.complain.ComplainService;
import ru.tensor.sbis.communicator.di.CommunicatorPushComponent;
import ru.tensor.sbis.communicator.push.model.MessagePushModel;
import ru.tensor.sbis.edo_decl.document.DocumentType;
import ru.tensor.sbis.pushnotification.PushType;
import ru.tensor.sbis.pushnotification.model.factory.PushDataFactory;
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage;

/**
 * @author am.boldinov
 */
class MessagePushDataFactory implements PushDataFactory<MessagePushModel> {

    MessagePushDataFactory(Context context) {
        ComplainService.Provider complainServiceProvider = CommunicatorPushComponent.getInstance(context)
                .getDependency()
                .getComplainServiceProvider();
        if (complainServiceProvider != null) {
            complainService = complainServiceProvider.getComplainService();
        } else {
            complainService = null;
        }
    }

    @Nullable
    private final ComplainService complainService;

    // data keys
    private static final String KEY_MESSAGE_UUID = "msgId";
    private static final String KEY_DIALOG_UUID = "dlgId";
    private static final String KEY_THEME_IS_CHAT = "themeIsChat";
    private static final String KEY_SERVICE_TYPE = "serviceType";
    private static final String KEY_DOCUMENT = "document";
    private static final String KEY_DOCUMENT_TYPE = "Type";
    private static final String KEY_DOCUMENT_SUBTYPE = "subType";
    private static final String KEY_DOCUMENT_NAME = "Name";
    private static final String KEY_DOCUMENT_UUID = "Id";
    private static final String KEY_SUBTYPE = "subtype";
    private static final String KEY_MEMBERS_COUNT = "dlgMembersCount";
    private static final String KEY_PERSON = "personModel";
    private static final String KEY_CHAT_NAME = "chatName";
    private static final String KEY_CHAT_SUBTITLE = "subtitle";
    private static final String NULL_STRING = "null";
    private static final String KEY_DOCUMENT_LINK = "Link";
    private static final String KEY_ACTION_CATEGORY = "action_category";


    @NotNull
    @Override
    public MessagePushModel create(@NotNull PushNotificationMessage message) {
        final MessagePushModel model = new MessagePushModel(message);
        final JSONObject data = message.getData();
        String dialogUuid = data.optString(KEY_DIALOG_UUID);
        String messageUuid = data.optString(KEY_MESSAGE_UUID);
        model.setDialogUuid(!dialogUuid.isEmpty() ? UUIDUtils.fromString(dialogUuid) : null);
        model.setMessageUuid(!messageUuid.isEmpty() ? UUIDUtils.fromString(messageUuid) : null);

        parseSender(model, data);
        parseConversationTitle(model, data);
        parseConversationSubtitle(model, data);
        parseActionCategory(model, data);


        final boolean isNewDialogMessage = model.getMessage().getType() == PushType.NEW_MESSAGE;
        final boolean isCrmRateMessage = model.getMessage().getType() == PushType.OPERATORS_RATE;
        model.setCrmRateMessage(isCrmRateMessage);
        final int subtypes = data.optInt(KEY_SUBTYPE);
        model.setSubtypes(MessagePushModel.ServiceType.from(subtypes));
        final int membersCount = data.optInt(KEY_MEMBERS_COUNT);
        model.setMembersCount(membersCount);
        final JSONObject document = data.optJSONObject(KEY_DOCUMENT);
        if (document != null) {
            String documentType = document.optString(KEY_DOCUMENT_TYPE);
            String documentSubtype = document.optString(KEY_DOCUMENT_SUBTYPE);
            final DocumentType type = DocumentType.parse(documentType);
            model.setComment(DocumentType.isNews(type));
            model.setDocumentUuid(document.optString(KEY_DOCUMENT_UUID));
            model.setDocumentUrl(UrlUtils.formatUrl(document.optString(KEY_DOCUMENT_LINK)));
            model.setViolation(DocumentType.isViolation(documentSubtype));
            model.setMessageFromTheTask(DocumentType.isTask(type));
            model.setSubscription(DocumentType.isSubscription(type));
            model.setDiscussionMentioning(DocumentType.isDiscussion(type));
            model.setDocumentName(document.optString(KEY_DOCUMENT_NAME));
            if (message.getType() != PushType.NEW_CHAT_MESSAGE) {
                model.setIsArticleDiscussionMessage(isNewDialogMessage && (type == DISCUSSION || type == DOCUMENT_DISCUSSION));
            }
        }

        final JSONObject extraData = message.getExtraData();
        model.setThemeIsChat(extraData.optBoolean(KEY_THEME_IS_CHAT));
        model.setNeedShowRemoveDialogAction(!model.isThemeIsChat() || model.isArticleDiscussionMessage());
        model.setSocnetEvent(extraData.optString(KEY_SERVICE_TYPE).equals("socnet_event"));
        if (complainService != null && model.getSender() != null
                && model.getSender().uuid != null
                && complainService.isPersonBlocked(model.getSender().uuid)
        ) {
            model.setAuthorBlocked(true);
        }
        return model;
    }

    private void parseSender(@NonNull MessagePushModel model, @NonNull JSONObject data) {
        String senderPayload = data.optString(KEY_PERSON);
        MessagePushModel.Sender sender = new Gson()
                .fromJson(senderPayload, MessagePushModel.Sender.class);
        model.setSender(sender);
    }

    private void parseConversationTitle(@NonNull MessagePushModel model, @NonNull JSONObject data) {
        String chatTitle = data.optString(KEY_CHAT_NAME);
        if (!chatTitle.isEmpty() && !chatTitle.equals(NULL_STRING)) {
            model.setConversationTitle(chatTitle);
        } else {
            model.setConversationTitle(model.getMessage().getTitle());
        }
    }

    private void parseConversationSubtitle(MessagePushModel model, JSONObject data) {
        String chatSubtitle = data.optString(KEY_CHAT_SUBTITLE);
        if (!chatSubtitle.isEmpty() && !chatSubtitle.equals(NULL_STRING)) {
            model.setConversationSubtitle(chatSubtitle);
        }
    }

    private void parseActionCategory(MessagePushModel model, JSONObject data) {
        String category = data.optString(KEY_ACTION_CATEGORY);
        List<String> supportTitles = new ArrayList<>();
        supportTitles.add(SABY_SUPPORT_TITLE);
        supportTitles.add(SETTY_SUPPORT_TITLE);
        supportTitles.add(SETTY_KZ_SUPPORT_TITLE);

        if (!category.isEmpty() && !category.equals(NULL_STRING)) {
            if (category.equals("client_consultation")) {
                boolean isSabySupport = supportTitles.contains(model.getConversationTitle());
                model.setSabySupport(isSabySupport);
                model.setSupport(!isSabySupport);
            }
            model.setOperatorsConsultationMessage(category.equals("operator_consultation"));
            model.setSabygetOperatorsMessage(category.equals("operator_sabyget"));
        }
    }
}
