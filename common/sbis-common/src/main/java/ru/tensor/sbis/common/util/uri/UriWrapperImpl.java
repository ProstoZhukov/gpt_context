package ru.tensor.sbis.common.util.uri;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import ru.tensor.sbis.common.util.FileUriUtil;
import timber.log.Timber;

/**
 * @author sa.nikitin
 */
public class UriWrapperImpl implements UriWrapper {

    @NonNull
    private final Context mContext;

    public UriWrapperImpl(@NonNull Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public String getStringUriForFilePath(@NonNull String filePath) {
        File file = new File(filePath);
        Uri uri = FileUriUtil.getUriForInternalFile(mContext, file);
        return uri.toString();
    }

    @NonNull
    @Override
    public Uri getUriForBitmap(@NonNull Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), image, "Title", null);
        return Uri.parse(path);
    }

    @Nullable
    @Override
    public String generateSnapshotStringUri() {
        Uri uri = FileUriUtil.generateSnapshotUri(mContext);
        return uri == null ? null : uri.toString();
    }

    @NonNull
    @Override
    public String generateInternalSnapshotStringUri(@NonNull File parentDir) {
        return FileUriUtil.getUriForInternalFile(
                mContext,
                new File(
                        parentDir,
                        FileUriUtil.generateSnapshotName()
                )
        )
                .toString();
    }

    @Override
    public File getFileByUriString(@NonNull String uriString) {
        return FileUriUtil.getFile(mContext, Uri.parse(uriString));
    }

    @Override
    @Nullable
    public byte[] getByteArrayForUrl(@Nullable String photoUrl) {
        if (photoUrl == null) {
            return null;
        }
        File avatarFile = getFileByUriString(photoUrl);
        if (avatarFile != null && avatarFile.exists()) {
            ByteBuffer fileBytesBuffer = ByteBuffer.allocate((int) avatarFile.length());
            try (FileChannel channel = new RandomAccessFile(avatarFile, "r").getChannel()) {
                if (channel.read(fileBytesBuffer) > 0 && fileBytesBuffer.hasArray()) {
                    return fileBytesBuffer.array();
                }
            } catch (Exception e) {
                Timber.e(e, "Error on reading or saving avatar photo file.");
            }
        }
        return null;
    }

}
