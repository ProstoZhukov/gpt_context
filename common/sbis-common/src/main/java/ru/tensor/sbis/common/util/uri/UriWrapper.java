package ru.tensor.sbis.common.util.uri;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

/**
 * @author sa.nikitin
 */
public interface UriWrapper {

    @NonNull
    String getStringUriForFilePath(@NonNull String filePath);

    @NonNull
    Uri getUriForBitmap(@NonNull Bitmap image);

    @Nullable
    String generateSnapshotStringUri();

    @NonNull
    String generateInternalSnapshotStringUri(@NonNull File parentDir);

    @Nullable
    File getFileByUriString(@NonNull String uriString);

    byte[] getByteArrayForUrl(String photoUrl);
}
