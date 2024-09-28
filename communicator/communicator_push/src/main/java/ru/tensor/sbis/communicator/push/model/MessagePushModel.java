package ru.tensor.sbis.communicator.push.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.tensor.sbis.pushnotification.model.PushData;
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage;

/**
 * Created by aa.mironychev on 13.04.17.
 */
public class MessagePushModel extends PushData {
    private UUID dialogUuid;
    private UUID messageUuid;
    private String conversationTitle;
    private String conversationSubtitle;
    private String documentUrl;
    private String documentName;
    private boolean themeIsChat;

    private boolean messageFromTheTask;

    private boolean isSocnetEvent;
    private boolean isComment;
    private boolean isViolation;
    private boolean isSupport;
    private boolean isSabySupport;
    private boolean isOperatorsConsultationMessage;
    private boolean isSabygetOperatorsMessage;
    private boolean isCrmRateMessage;
    private boolean isSubscription;
    private boolean isArticleDiscussionMessage;
    private boolean isAcceptedApplicationGroup;
    private boolean isDiscussionMentioning;
    private boolean needShowRemoveDialogAction;
    private boolean isAuthorBlocked;
    private String documentUuid;
    private List<ServiceType> subtypes;
    private Sender sender;
    private int membersCount;

    public MessagePushModel(@NotNull PushNotificationMessage message) {
        super(message);
    }

    public UUID getDialogUuid() {
        return dialogUuid;
    }

    public void setDialogUuid(UUID dialogUuid) {
        this.dialogUuid = dialogUuid;
    }

    public UUID getMessageUuid() {
        return messageUuid;
    }

    public void setMessageUuid(UUID messageUuid) {
        this.messageUuid = messageUuid;
    }

    public String getConversationTitle() {
        return conversationTitle;
    }

    public void setConversationTitle(String conversationTitle) {
        this.conversationTitle = conversationTitle;
    }

    public String getConversationSubtitle() {
        return conversationSubtitle;
    }

    public void setConversationSubtitle(String conversationSubtitle) {
        this.conversationSubtitle = conversationSubtitle;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public boolean isThemeIsChat() {
        return themeIsChat;
    }

    public void setThemeIsChat(boolean themeIsChat) {
        this.themeIsChat = themeIsChat;
    }

    public boolean isMessageFromTheTask() {
        return messageFromTheTask;
    }

    public void setMessageFromTheTask(boolean messageFromTheTask) {
        this.messageFromTheTask = messageFromTheTask;
    }

    public boolean isSocnetEvent() {
        return isSocnetEvent;
    }

    public void setSocnetEvent(boolean socnetEvent) {
        isSocnetEvent = socnetEvent;
    }

    public boolean isComment() {
        return isComment;
    }

    public void setComment(boolean comment) {
        isComment = comment;
    }

    public boolean isViolation() {
        return isViolation;
    }

    public void setViolation(boolean violation) {
        isViolation = violation;
    }

    /**
     * Относится ли пуш к каналам службы поддержки
     * @return true если пуш - новое событие в канале службы поддержки
     */
    public boolean isSupport() {
        return isSupport;
    }

    public void setSupport(boolean support) {
        isSupport = support;
    }

    /**
     * Относится ли пуш к каналу службы поддержки "Поддержка СБИС"
     * "Поддержка СБИС" - отдельный канал, который находится вне реестра каналов поддержки,
     * а в разделе настроек
     * @return true если пуш - новое событие в канале "Поддержка СБИС"
     */
    public boolean isSabySupport() {
        return isSabySupport;
    }

    public void setSabySupport(boolean sabySupport) {
        isSabySupport = sabySupport;
    }

    public boolean isOperatorsConsultationMessage() {
        return isOperatorsConsultationMessage;
    }

    public void setOperatorsConsultationMessage(boolean operatorsConsultationMessage) {
        isOperatorsConsultationMessage = operatorsConsultationMessage;
    }

    public boolean isSabygetOperatorsMessage() {
        return isSabygetOperatorsMessage;
    }

    public void setSabygetOperatorsMessage(boolean operatorsConsultationMessage) {
        isSabygetOperatorsMessage = operatorsConsultationMessage;
    }

    public boolean isCrmRateMessage() {
        return isCrmRateMessage;
    }

    public void setCrmRateMessage(boolean isRateMessage) {
        isCrmRateMessage = isRateMessage;
    }

    public boolean isArticleDiscussionMessage() {
        return isArticleDiscussionMessage;
    }

    public void setIsArticleDiscussionMessage(boolean isArticleDiscussionMessage) {
        this.isArticleDiscussionMessage = isArticleDiscussionMessage;
    }

    public boolean isSubscription() {
        return isSubscription;
    }

    public void setSubscription(boolean subscription) {
        isSubscription = subscription;
    }

    public boolean isAcceptedApplicationGroup() {
        return isAcceptedApplicationGroup;
    }

    public void setAcceptedApplicationGroup(boolean acceptedApplicationGroup) {
        isAcceptedApplicationGroup = acceptedApplicationGroup;
    }

    public boolean isDiscussionMentioning() {
        return isDiscussionMentioning;
    }

    public void setDiscussionMentioning(boolean discussionMentioning) {
        isDiscussionMentioning = discussionMentioning;
    }

    public String getDocumentUuid() {
        return documentUuid;
    }

    public void setDocumentUuid(String documentUuid) {
        this.documentUuid = documentUuid;
    }

    public List<ServiceType> getSubtypes() {
        return subtypes;
    }

    public void setSubtypes(List<ServiceType> subtypes) {
        this.subtypes = subtypes;
    }

    @Nullable
    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public String getMessageWithSender(@Nullable Integer messagesCount) {
        String countArg = messagesCount != null ? "(" + messagesCount + ")" : StringUtils.EMPTY;
        return String.format("%s %s%s: %s", sender.surname, sender.name, countArg, getMessage().getMessage());
    }

    public void setNeedShowRemoveDialogAction(boolean needShowRemoveDialogAction) {
        this.needShowRemoveDialogAction = needShowRemoveDialogAction;
    }

    public boolean isNeedShowRemoveDialogAction() {
        return needShowRemoveDialogAction;
    }

    public boolean isAuthorBlocked() {
        return isAuthorBlocked;
    }

    public void setAuthorBlocked(boolean authorBlocked) {
        isAuthorBlocked = authorBlocked;
    }

    public static class Sender implements Serializable {
        private static final long serialVersionUID = 9165158796382646729L;
        @SerializedName("id")
        public UUID uuid;
        @SerializedName("firstName")
        public String name;
        @SerializedName("lastName")
        public String surname;
        @SerializedName("fatherName")
        public String patronymic;
        @SerializedName("photoId")
        public String photoId;
        @SuppressWarnings({"unused", "RedundantSuppression"})
        @SerializedName("position")
        public String position;
    }

    public enum ServiceType {
        HAS_TEXT(1),
        HAS_ONE_ATTACHMENT(2),
        HAS_SEVERAL_ATTACHMENTS(4),
        DOCUMENT_FOR_SIGNATURE(8),
        MESSAGE_WAS_READ(512),
        MESSAGE_WAS_DELETED(1024);

        private final int mask;

        ServiceType(int value) {
            this.mask = value;
        }

        @NonNull
        public static List<ServiceType> from(int serverValue) {
            List<ServiceType> types = new ArrayList<>();
            for (ServiceType type : values()) {
                int masked = serverValue & type.mask;
                if (masked == type.mask) {
                    types.add(type);
                }
            }
            return types;
        }
    }
}
