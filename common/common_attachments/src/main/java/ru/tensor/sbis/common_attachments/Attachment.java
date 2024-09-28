package ru.tensor.sbis.common_attachments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.common.util.FileUtil;

/**
 * Враппер для вложений (сообщения, задачи и др.)
 */
public class Attachment {

    public static final int MAX_PROGRESS = 100;

    @NonNull
    private FileUtil.FileType mFileType;
    @NonNull
    private String mUri;
    @NonNull
    private String mName;
    @Nullable
    private Boolean mIsOffice;

    private int mSignaturesCount;

    private int mProgress = MAX_PROGRESS;

    /**
     * Конструктор.
     *
     * @param uri ссылка на вложение.
     * @param name название вложения.
     * @param fileType тип вложения.
     * @param isOffice возможность подписания документов.
     */
    public Attachment(@NonNull String uri, @NonNull String name, @NonNull FileUtil.FileType fileType, @Nullable Boolean isOffice) {
        mUri = uri;
        mName = name;
        mFileType = fileType;
        mIsOffice = isOffice;
    }

    /**
     * Конструктор.
     *
     * @param uri ссылка на вложение.
     * @param name название вложения.
     * @param signaturesCount число подписей.
     * @param progress прогресс.
     * @param fileType тип вложения.
     * @param isOffice возможность подписания документов.
     */
    public Attachment(@NonNull String uri, @NonNull String name, @NonNull FileUtil.FileType fileType, int signaturesCount, int progress, @Nullable Boolean isOffice) {
        this(uri, name, fileType, isOffice);
        mSignaturesCount = signaturesCount;
        mProgress = progress;
    }

    /*** @SelfDocumented */
    @NonNull
    public FileUtil.FileType getFileType() {
        return mFileType;
    }

    /*** @SelfDocumented */
    @NonNull
    public String getUri() {
        return mUri;
    }

    /*** @SelfDocumented */
    @NonNull
    public String getName() {
        return mName;
    }

    /*** @SelfDocumented */
    public int getSignaturesCount() {
        return mSignaturesCount;
    }

    /*** @SelfDocumented */
    public int getProgress() {
        return mProgress;
    }

    /*** @SelfDocumented */
    public void setProgress(int progress) {
        mProgress = progress;
    }

    /*** @SelfDocumented */
    public boolean canSign() {
        return (mIsOffice != null) ? mIsOffice : mFileType != FileUtil.FileType.IMAGE;
    }
}

