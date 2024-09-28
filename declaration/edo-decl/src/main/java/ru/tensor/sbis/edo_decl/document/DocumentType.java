package ru.tensor.sbis.edo_decl.document;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

public enum DocumentType {

    /**
     * # Типы документов, нужны для определения, каким окном открывать документ в интерфейсе (этим рули Лобастов)
     * DOC_TYPE_DOCUMENT = 0 # Документ из документооборота
     * DOC_TYPE_LETTER = 1 # Корреспонденция (запрашивается как обычный документ)
     * DOC_TYPE_DISC = 2 # Файл на СБис.Диске
     * DOCUMENT_DISCUSSION = 3 # Переписка по файлу, материалу группы
     * DOC_TYPE_TRADES = 4 # Торги (запрашивается как обычный документ)
     * DOC_TYPE_NEWS = 5 # Новость с главной страници                          # /index.html#newsid=3dd60c24-5499-46b5-835c-a779e9bd90a6
     * DOC_TYPE_SOCNET_NEWS = 6 # Новость социальной сети
     * DOC_TYPE_SOCNET_NEWS_REPOST = 7 # Репост новости из социальной сети     # /shared/disk/231418d3-a481-4057-b74c-edffd6ddbdca
     * DOC_TYPE_WI = 8 # Форум на wi.sbis.ru
     * ...
     * DOC_TYPE_SOCNET_GROUP = 14 Чат по группе
     * DOC_TYPE_WEBINAR = 15 # Вебинар
     * DOC_TYPE_MEETING = 16 # Совещания
     * ...
     * DOC_TYPE_PROJECT = 2058 Чат по проекту
     * DOC_TYPES_WITH_PHASE = (DOC_TYPE_DOCUMENT, DOC_TYPE_LETTER, DOC_TYPE_TRADES) # Документы, у которых есть Фаза
     * <p>
     * DOC_TYPE_NOT_FOUND_AND_SAVE = -1
     */

    UNKNOWN("unknown"),
    UNSUPPORTED("unsupported"),
    DISC_FOLDER("shared_folder"),
    VIOLATION("4902"),
    GROUP_DISCUSSION_TOPIC("group_discussion_topic"),
    TASK("0"),
    DOCUMENT("1"),
    SHARED_FILE("2"),

    NEWS("5"),
    SOCNET_NEWS("6"),
    NEWS_REPOST("7"),
    ACCEPTED_APPLICATION_GROUP("9"),
    DISCUSSION("10"),
    DOCUMENT_DISCUSSION("3"),
    QUESTION("11"),
    SOCNET_GROUP("14"),
    PROJECT("2058"),
    WEBINAR("15") {
        protected String[] getTypeNames() {
            return new String[]{"Вебинар"};
        }
    },
    MEETING("16") {
        protected String[] getTypeNames() {
            return new String[]{"СовещаниеСервис", "Видеосовещание"};
        }
    },
    OFFER("20"),
    SUBSCRIPTION("30"),
    ORDER("0") {
        protected String[] getTypeNames() {
            return new String[]{"Наряд"};
        }
    };

    private final String mValue;

    @Nullable
    protected String[] getTypeNames() {
        return null;
    }

    DocumentType(String value) {
        mValue = value;
    }

    @NonNull
    public static DocumentType fromValue(final int value) {
        for (DocumentType s : DocumentType.values()) {
            if (s.ordinal() == value) {
                return s;
            }
        }
        return UNKNOWN;
    }

    @NonNull
    public static DocumentType parse(@Nullable String value) {
        return parse(value, UNSUPPORTED);
    }

    @NonNull
    public static DocumentType parse(@Nullable String value,
                                     @NonNull DocumentType defaultValue) {
        return parse(value, null, defaultValue);
    }

    @NonNull
    public static DocumentType parse(@Nullable String value, @Nullable String typeName,
                                     @NonNull DocumentType defaultValue) {
        // Пытаемся получить специфичный тип, например ORDER или MEETING
        if (typeName != null) {
            DocumentType specificType = getTypeByName(typeName);
            if (specificType != null) {
                return specificType;
            }
        }
        // Вычисляем тип по значению
        for (DocumentType s : DocumentType.values()) {
            if (s.mValue.equals(value)) {
                return s;
            }
        }
        return defaultValue;
    }

    @Nullable
    public static DocumentType getTypeByName(String typeName) {
        for (DocumentType type : values()) {
            String[] typeNames = type.getTypeNames();
            if (typeNames != null) {
                for (String name : typeNames) {
                    if (TextUtils.equals(name, typeName)) {
                        return type;
                    }
                }
            }
        }
        return null;
    }

    public static boolean isNews(@NonNull DocumentType documentType) {
        return documentType == NEWS ||
                documentType == SOCNET_NEWS ||
                documentType == NEWS_REPOST;
    }

    public static boolean isAcceptedApplicationGroup(@NonNull DocumentType documentType) {
        return documentType == ACCEPTED_APPLICATION_GROUP;
    }

    public static boolean isDiscussion(@NonNull DocumentType documentType) {
        return documentType == DISCUSSION ||
                documentType == QUESTION ||
                documentType == OFFER ||
                documentType == DOCUMENT_DISCUSSION;
    }

    public static boolean isSubscription(@NonNull DocumentType documentType) {
        return documentType == SUBSCRIPTION;
    }

    public static boolean isTask(@NonNull DocumentType documentType) {
        return documentType == TASK;
    }

    public static boolean isViolation(@Nullable String subType) {
        if (subType != null) {
            return subType.equals(VIOLATION.mValue);
        } else {
            return false;
        }
    }
}
