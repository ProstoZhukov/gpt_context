package ru.tensor.sbis.communicator.common.util.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ConversationUtils {
    public static final String DATA_INTENT_ACTION = "data_intent_action_key";

    @Nullable
    public static String getTextToShare(@Nullable Intent intent) {
        String textToShare = null;
        final Bundle args = intent != null ? intent.getExtras() : null;
        if (args != null && args.containsKey(Intent.EXTRA_TEXT)) {
            textToShare = args.getString(Intent.EXTRA_TEXT);
            intent.removeExtra(Intent.EXTRA_TEXT);
        }
        return textToShare;
    }

    @Nullable
    public static ArrayList<Uri> getFilesToShare(@Nullable Intent intent) {
        ArrayList<Uri> filesToShareList = null;
        final Bundle args = intent != null ? intent.getExtras() : null;
        if (args != null && args.containsKey(Intent.EXTRA_STREAM)) {
            if (Intent.ACTION_SEND.equals(intent.getAction())
                    || Intent.ACTION_SEND.equals(args.getString(DATA_INTENT_ACTION))) {
                filesToShareList = new ArrayList<>(1);
                filesToShareList.add(args.getParcelable(Intent.EXTRA_STREAM));
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())
                    || Intent.ACTION_SEND_MULTIPLE.equals(args.getString(DATA_INTENT_ACTION))) {
                filesToShareList = args.getParcelableArrayList(Intent.EXTRA_STREAM);
            }
            if (args.containsKey(DATA_INTENT_ACTION)) {
                args.remove(DATA_INTENT_ACTION);
            }
            intent.removeExtra(Intent.EXTRA_STREAM);
        }
        return filesToShareList;
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public static boolean hasContentToShare(@Nullable Intent intent) {
        final Bundle args = intent != null ? intent.getExtras() : null;
        return args != null && (args.containsKey(Intent.EXTRA_STREAM) ||
                args.containsKey(Intent.EXTRA_TEXT));
    }

    public static void cleanSharingExtras(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            intent.removeExtra(Intent.EXTRA_TEXT);
            intent.removeExtra(Intent.EXTRA_STREAM);
        }
    }
}
