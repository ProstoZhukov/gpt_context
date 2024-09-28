package ru.tensor.sbis.communicator.sbis_conversation.data.mapper;

import android.content.Context;

import io.reactivex.annotations.NonNull;
import ru.tensor.sbis.common.modelmapper.BaseModelMapper;
import ru.tensor.sbis.communicator.generated.DialogDocument;
import ru.tensor.sbis.common.util.UUIDUtils;
import ru.tensor.sbis.edo_decl.document.Document;
import ru.tensor.sbis.edo_decl.document.DocumentType;

/**
 * Маппер для получения объекта класса {@link Document} из объекта класса {@link DialogDocument}
 */
public class DocumentMapper extends BaseModelMapper<DialogDocument, Document> {

    /** SelfDocumented */
    public DocumentMapper(Context context) {
        super(context);
    }

    /** SelfDocumented */
    @Override
    public Document apply(@NonNull DialogDocument dialogDocument) {
        Document document = new Document();
        document.setTitle(dialogDocument.getTitle());
        document.setName(dialogDocument.getName());
        document.setText(dialogDocument.getText());
        document.setIsAccessible(dialogDocument.isAccessible());
        document.setLinkInfix(dialogDocument.getLink());

        ru.tensor.sbis.communicator.generated.DocumentType dialogDocumentType = dialogDocument.getType();

        DocumentType documentType;
        String uuid = UUIDUtils.toString(dialogDocument.getUuid());

        switch (dialogDocumentType) {
            case DISK:
                documentType = DocumentType.SHARED_FILE;
                document.setDiskId(dialogDocument.getDiskFileId());
                break;
            case DISK_FOLDER:
                documentType = DocumentType.DISC_FOLDER;
                document.setDiskId(dialogDocument.getDiskFileId());
                break;
            case NEWS:
                documentType = DocumentType.NEWS;
                break;
            case TASK:
                documentType = DocumentType.TASK;
                break;
            case MEETING:
                documentType = DocumentType.MEETING;
                break;
            case WEBINAR:
                documentType = DocumentType.WEBINAR;
                break;
            case GROUP_DISCUSSION_TOPIC:
            case GROUP_SUGGESTIONS:
            case GROUP_DISCUSSION_QUESTION:
                documentType = DocumentType.GROUP_DISCUSSION_TOPIC;
                break;
            case ORDER:
                documentType = DocumentType.ORDER;
                break;
            case DOCUMENT:
                documentType = DocumentType.DOCUMENT;
                break;
            case SOCNET_GROUP:
                documentType = DocumentType.SOCNET_GROUP;
                break;
            case PROJECT:
                documentType = DocumentType.PROJECT;
                break;
            case SOCNET_NEWS:
            case SOCNET_NEWS_REPOST:
            case WI:
            case TRADES:
            case LETTER:
            case UNKNOWN:
            default:
                documentType = DocumentType.UNSUPPORTED;
                break;
        }

        document.setUuid(uuid);
        document.setType(documentType);
        document.setSubtype(dialogDocument.getSubTypeName());
        return document;
    }
}
