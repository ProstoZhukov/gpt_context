package ru.tensor.sbis.common.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Browser;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.webkit.MimeTypeMap;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;

import ru.tensor.sbis.common.R;
import ru.tensor.sbis.design_notification.SbisPopupNotification;
import timber.log.Timber;

import static ru.tensor.sbis.common.util.BrowserUtilsKt.openInBrowserApp;

/**
 * @author sa.nikitin
 */
public class CommonUtils {

    public static final String INFIX = "/opendoc.html?guid=";

    private static final String TELEGRAM_PACKAGE = "org.telegram.messenger";
    private static final String TELEGRAM_PACKAGE_WEB = "org.telegram.messenger.web";
    private static final String TELEGRAM_X_PACKAGE = "org.thunderdog.challegram";
    private static final String WHATSAPP_PACKAGE = "com.whatsapp";
    private static final String VIBER_PACKAGE = "com.viber.voip";

    public static void callPhone(@NonNull Context context, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            callPhone(context, Uri.parse(UrlUtils.TEL_URL_PREFIX + phoneNumber));
        } else {
            SbisPopupNotification.pushToast(context, R.string.common_profile_has_no_phone_number);
        }
    }

    public static void callPhone(@NonNull Context context, @NonNull Uri phoneUri) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, phoneUri);
        try {
            startAction(context, dialIntent, R.string.common_profile_device_has_no_call_ability);
        } catch (ActivityNotFoundException error) {
            //специфичный сценарий: система Galaxy Note8 отвечает, что может обработать implicit intent, но кидает ошибку при попытке запуска
            SbisPopupNotification.pushToast(context, R.string.common_profile_device_has_no_call_ability);
        }
    }

    public static void callSkype(@NonNull Context context, String skypeLogin) {
        if (!TextUtils.isEmpty(skypeLogin)) {
            Intent skypeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("skype:" + skypeLogin + "?chat"));
            startAction(context, skypeIntent, R.string.common_profile_device_has_no_skype);
        } else {
            SbisPopupNotification.pushToast(context, R.string.common_profile_has_no_skype);
        }
    }

    public static void sendEmail(@NonNull Context context, String email) {
        if (!TextUtils.isEmpty(email)) {
            sendEmail(context, Uri.parse(UrlUtils.MAIL_URL_PREFIX + email));
        } else {
            SbisPopupNotification.pushToast(context, R.string.common_profile_has_no_email);
        }
    }

    public static void openTelegramChat(@NonNull Context context, String userName) {
        if (!TextUtils.isEmpty(userName)) {
            // Для одинакового поведения со скайп
            if (isAnyTelegramAppAvailable(context)) {
                Intent telegramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/" + userName));
                startAction(context, telegramIntent, R.string.common_profile_device_has_no_telegram);
            } else {
                SbisPopupNotification.pushToast(context, R.string.common_profile_device_has_no_telegram);
            }
        } else {
            SbisPopupNotification.pushToast(context, R.string.common_profile_has_no_telegram);
        }
    }

    private static Boolean isAnyTelegramAppAvailable(@NonNull Context context) {
        return isAppAvailable(context, TELEGRAM_PACKAGE) ||
                isAppAvailable(context, TELEGRAM_X_PACKAGE) ||
                isAppAvailable(context, TELEGRAM_PACKAGE_WEB);
    }

    public static void openChooserForMakeCallApp(@NonNull Context context, String phoneNumber,
                                                 boolean isWhatsAppBound,
                                                 @Nullable Intent sbisCallIntent) {
        if (sbisCallIntent == null) {
            openChooserForBoundToWhatsAppPhone(context, phoneNumber, isWhatsAppBound);
        } else {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(UrlUtils.TEL_URL_PREFIX + phoneNumber));
            Intent chooserIntent = Intent.createChooser(dialIntent, context.getResources().getText(R.string.common_select_application_to_phone_call));

            ArrayList<Intent> extraIntents = new ArrayList<>();
            if (isWhatsAppBound) {
                Intent whatsappIntent = createWhatsappCallIntent(context, phoneNumber);
                if (whatsappIntent != null) {
                    extraIntents.add(whatsappIntent);
                }
            }

            extraIntents.add(sbisCallIntent);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents.toArray(new Intent[extraIntents.size()]));
            startAction(context, chooserIntent, R.string.common_profile_device_has_no_call_ability);
        }
    }

    public static void openChooserForBoundToWhatsAppPhone(
            @NonNull Context context,
            String phoneNumber,
            boolean isWhatsAppBound
    ) {
        if (!isWhatsAppBound) {
            callPhone(context, phoneNumber);
            return;
        }

        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(UrlUtils.TEL_URL_PREFIX + phoneNumber));
        Intent chooserIntent = Intent.createChooser(dialIntent, context.getResources().getText(R.string.common_select_application_to_phone_call));

        Intent whatsappIntent = createWhatsappCallIntent(context, phoneNumber);
        if (whatsappIntent != null) {
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{whatsappIntent});
        }

        startAction(context, chooserIntent, R.string.common_profile_device_has_no_call_ability);
    }

    @Nullable
    private static Intent createWhatsappCallIntent(@NonNull Context context, String phoneNumber) {
        //для WhatsApp необходимо сформировать интент с номером телефона по международному стандарту (с iso кодом страны)
        //в противном случает WhatsApp будет открыт, но отобразится ошибка о том, что контакт не найден
        if (isAppAvailable(context, WHATSAPP_PACKAGE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + formatRUPhoneNumber(phoneNumber)));
        }

        return null;
    }

    private static String formatRUPhoneNumber(String number) {
        if (number.startsWith("8")) return number.replaceFirst("8", "+7");
        return number;
    }

    public static void sendEmail(@NonNull Context context, @NonNull Uri emailUri) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, emailUri);
        startAction(context, emailIntent, R.string.common_profile_device_has_mo_email_client);
    }

    public static void openLink(@NonNull Context context, String url) {
        openLink(context, url, false);
    }

    /**
     * Открыть ссылку в поддерживающем её приложении
     *
     * @param context           Контекст
     * @param url               Ссылка для открытия
     * @param directlyToSbisApp Открыть напрямую в текущем приложении СБИС,
     *                          если оно поддерживает эту ссылку, т.е. минуя шаг выбора приложения,
     *                          если их несколько
     */
    public static void openLink(@NonNull Context context, String url, boolean directlyToSbisApp) {
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //Согласно https://online.sbis.ru/opendoc.html?guid=75681307-8ed6-43b1-85df-a6fd7bb7dabe
            if (directlyToSbisApp) {
                intent.setPackage(context.getPackageName());
                if (intent.resolveActivity(context.getPackageManager()) == null) {
                    intent.setPackage(null);
                }
            }
            startAction(context, intent, R.string.common_profile_social_network_open_error);
        } else {
            SbisPopupNotification.pushToast(context, R.string.common_profile_social_network_open_error);
        }
    }

    public static void startAction(@NonNull Context context, @NonNull Intent actionIntent, @StringRes int errorMessage) {
        if (actionIntent.resolveActivity(context.getPackageManager()) != null) {
            if (!(context instanceof Activity)) {
                actionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(actionIntent);
        } else {
            SbisPopupNotification.pushToast(context, errorMessage);
        }
    }

    public static boolean isPhoneCallAbility(@NonNull Context context) {
        return new Intent(Intent.ACTION_DIAL).resolveActivity(context.getPackageManager()) != null;
    }

    /**
     * @deprecated Inject {@link ru.tensor.sbis.common.util.NetworkUtils} as dependency instead
     */
    @Deprecated
    public static NetworkInfo getActiveNetworkInfo(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * @deprecated Inject {@link ru.tensor.sbis.common.util.NetworkUtils} as dependency instead
     */
    @Deprecated
    public static boolean isOnline(@NonNull Context context) {
        NetworkInfo netInfo = getActiveNetworkInfo(context);
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * @deprecated Inject {@link ru.tensor.sbis.common.util.NetworkUtils} as dependency instead
     */
    @Deprecated
    public static boolean isOnlineOrConnecting(@NonNull Context context) {
        NetworkInfo netInfo = getActiveNetworkInfo(context);
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String createUnsupportedDocumentLinkByUuid(@NonNull String uuid) {
        return createLinkByUuid(INFIX, uuid);
    }

    public static String createLinkByUuid(@NonNull String infix, @NonNull String uuid) {
        return buildLink(infix, uuid);
    }

    public static String buildLink(@NonNull String... parts) {
        final StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            builder.append(part);
        }
        final String url = builder.toString();
        return UrlUtils.formatUrl(url);
    }

    public static void showUnsupportedDocumentInBrowser(@NonNull Context context, @NonNull String uuid) {
        openLinkInBrowser(context, createLinkByUuid(INFIX, uuid), R.string.common_open_link_browser_error);
    }

    /**
     * @deprecated Оставлен для обратной совместимости, использовать {@link ru.tensor.sbis.common.util.BrowserUtilsKt}
     */
    public static void openLinkInExternalApp(@NonNull Context context, @NonNull String url) {
        openLinkInExternalApp(context, url, R.string.common_open_link_browser_error);
    }

    /**
     * @deprecated Оставлен для обратной совместимости, использовать {@link ru.tensor.sbis.common.util.BrowserUtilsKt}
     */
    @Deprecated
    public static void openLinkInExternalApp(@NonNull Context context, @NonNull String url, @StringRes int errorMessageRes) {
        openInBrowserApp(context, url, errorMessageRes, null);
    }

    /**
     * Для андроид 13 селектор Action.MAIN может работать некорректно, поэтому подходит такое решение
     * Ссылка на issue https://issuetracker.google.com/issues/243678703
     */
    public static void openLinkInBrowser(@NonNull Context context, @NonNull String url, @StringRes int errorMessageRes) {
        final Intent openLinkIntent;
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            Intent emptyBrowserIntent = new Intent()
                    .setAction(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .setData(Uri.fromParts("http", "", null));

            Intent targetIntent = new Intent(Intent.ACTION_VIEW);
            targetIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            targetIntent.setData(Uri.parse(url).normalizeScheme());
            targetIntent.setSelector(emptyBrowserIntent);
            openLinkIntent = targetIntent;
        } else {
            Intent targetIntent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
            targetIntent.setData(Uri.parse(url).normalizeScheme());
            openLinkIntent = targetIntent;
        }
        openLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(openLinkIntent);
        } catch (ActivityNotFoundException e) {
            if (errorMessageRes != ResourcesCompat.ID_NULL) {
                SbisPopupNotification.pushToast(context, errorMessageRes);
            }
            Timber.d("Trying to open invalid link");
        }
    }

    public static void openLinkInBrowser(@NonNull Context context, @NonNull String url) {
        openLinkInBrowser(context, url, ResourcesCompat.ID_NULL);
    }

    /**
     * Формирование интента на отображение диалога "Поделиться" по ссылке
     *
     * @param link  ссылка для передачи в выбранное приложение
     * @param title Опциональный заголовок окна выбора приложения. По умолчанию null - используется системный заголовок окна
     */
    public static Intent getShareLinkDialogIntent(@NonNull String link, @Nullable String title) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, link);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return Intent.createChooser(shareIntent, title);
    }

    /**
     * Формирование интента на отображение диалога "Поделиться" по ссылке
     *
     * @param link ссылка для передачи в выбранное приложение
     */
    public static Intent getShareLinkDialogIntent(@NonNull String link) {
        return getShareLinkDialogIntent(link, null);
    }

    public static boolean hasActivitiesForProcessingIntent(@NonNull Context context, @NonNull Intent intent) {
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = manager.queryIntentActivities(intent, 0);
        return resolveInfoList.size() > 0;
    }

    @Nullable
    public static String getFileName(@Nullable String filePath) {
        String fileName = null;
        if (filePath != null) {
            int lastIndexOfSlash = filePath.lastIndexOf('/');
            if (lastIndexOfSlash >= 0 && lastIndexOfSlash < filePath.length() - 1) {
                fileName = filePath.substring(lastIndexOfSlash + 1);
            }
        }
        return fileName;
    }

    public static void shareFile(@NonNull Context context, @NonNull String filePath) {
        File file = new File(filePath);
        Uri uri = FileUriUtil.getUriForInternalFile(context, file);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getFileExtension(file, false));
        shareIntent.setDataAndType(uri, mimeType != null ? mimeType : "*/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            Intent chooserIntent = Intent.createChooser(shareIntent, context.getResources().getText(R.string.common_menu_share));
            if (!(context instanceof Activity)) {
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(chooserIntent);
        } catch (ActivityNotFoundException e) {
            SbisPopupNotification.pushToast(context, R.string.file_can_not_be_shared);
        }
    }

    @Nullable
    public static List<Long> convertPrimitiveArrayToLongList(@Nullable long[] list) {
        if (list == null) {
            return null;
        }
        List<Long> result = new ArrayList<>();
        for (long number : list) {
            result.add(number);
        }
        return result;
    }

    @NonNull
    public static long[] convertLongListToPrimitiveArray(@NonNull List<Long> list) {
        long[] result = new long[list.size()];
        Iterator<Long> iterator = list.iterator();
        for (int i = 0; i < result.length; i++) {
            result[i] = iterator.next();
        }
        return result;
    }

    @NonNull
    public static String getStringWithoutPrefix(@NonNull String source, @NonNull String separator) {
        int prefixIndex = source.lastIndexOf(separator);
        if (prefixIndex > 0) {
            source = source.substring(prefixIndex + 1);
        }
        return source;
    }

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isEmpty(@Nullable Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean hasNotEmpty(@NonNull String... strings) {
        for (String str : strings) {
            if (!isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(@Nullable Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    public static <T> boolean equal(@Nullable T a, @Nullable T b) {
        return Objects.equals(a, b);
    }

    public static <T> boolean equal(@Nullable List<T> a, @Nullable List<T> a2) {
        if (a == a2) {
            return true;
        }
        if (a == null || a2 == null) {
            return false;
        }

        int length = a.size();
        if (a2.size() != length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            T o1 = a.get(i);
            T o2 = a2.get(i);
            if (!equal(o1, o2)) {
                return false;
            }
        }

        return true;
    }

    public static <T> int hashCode(@Nullable Collection<T> a) {
        if (a == null) {
            return 0;
        }

        int result = 1;

        for (T element : a) {
            result = 31 * result + (element == null ? 0 : element.hashCode());
        }

        return result;
    }

    public static Integer tryParse(@NonNull Object obj) {
        int retVal;
        try {
            retVal = Integer.parseInt((String) obj);
        } catch (NumberFormatException nfe) {
            retVal = 0; // or null if that is your preference
        }
        return retVal;
    }

    //region CommonUtilsClasses
    public static class SimpleAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            //not implemented
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            //not implemented
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            //not implemented
        }

    }
    //endregion

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static boolean anyNotNull(Object... objects) {
        for (Object object : objects) {
            if (object != null) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    public static String emptyIfNull(@Nullable String source) {
        return source == null ? StringUtils.EMPTY : source;
    }

    public static void handleException(@NonNull Throwable e) {
        Timber.e(e);
        if (AppConfig.isDebug()) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isAppAvailable(Context context, String appName) {
        try {
            context.getPackageManager().getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}