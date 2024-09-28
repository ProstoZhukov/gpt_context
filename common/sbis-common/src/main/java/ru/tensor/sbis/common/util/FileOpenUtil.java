package ru.tensor.sbis.common.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import ru.tensor.sbis.common.R;
import ru.tensor.sbis.design_notification.SbisPopupNotification;
import timber.log.Timber;

/**
 * Created by am.boldinov on 11.06.15.
 *
 * @author sa.nikitin
 */
public class FileOpenUtil {

    public static String PDF_FILE_EXT = ".pdf";
    public static List<String> sExternalDocumentFileExtensions = Arrays.asList(".vsd", ".vsdx", ".rtf", ".xml");

    public static void openExternalFile(@NonNull Context context, @NonNull String fileUri) {
        Uri uri = Uri.parse(fileUri);
        boolean isUriIncorrect = false;
        boolean startActivityFailed = false;
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            String filePath = uri.getPath();
            try {
                uri = FileUriUtil.getUriForInternalFile(context, new File(filePath));
            } catch (IllegalArgumentException exception) {
                Timber.e(exception);
                isUriIncorrect = true;
            }
        }
        if (!isUriIncorrect) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, context.getContentResolver().getType(uri));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    startActivityFailed = true;
                }
            } else {
                startActivityFailed = true;
            }
        }
        if (startActivityFailed || isUriIncorrect) {
            SbisPopupNotification.pushToast(context, R.string.common_failed_to_open_file);
        }
    }

}
