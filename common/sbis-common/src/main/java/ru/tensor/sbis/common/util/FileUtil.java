package ru.tensor.sbis.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by Alexander Sosnin
 *
 * @author sa.nikitin
 */
public class FileUtil {

    private static final HashMap<String, String> imagesMap = new HashMap<>();
    @NonNull
    private static final List<String> imageFileExt;
    @NonNull
    private static final Set<String> lockedFileList = Collections.synchronizedSet(new HashSet<>());
    @NonNull
    private static final String[] convertibleToPDFFormats = new String[]{".vsd", ".vsdx", ".rtf", ".xml", ".sabydoc"};

    private static final HashMap<String, String> fileExtToMimeTypes = new HashMap<>();
    private static final int COPY_FILE_BLOCK_SIZE = 4096;
    /**
     * number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;

    public static final long ONE_MB = ONE_KB * ONE_KB;

    public static final long ONE_GB = ONE_KB * ONE_MB;

    public static final long ONE_TB = ONE_KB * ONE_GB;

    public static final String APK_EXTENSION = ".apk";
    public static final String GIF_EXTENSION = ".gif";
    public static final String TIF_EXTENSION = ".tif";
    public static final String TIFF_EXTENSION = ".tiff";
    public static final String PSD_EXTENSION = ".psd";
    public static final String SABYDOC_EXTENSION = ".sabydoc";

    static {
        imagesMap.put(GIF_EXTENSION, "image/gif");
        imagesMap.put(".jpg", "image/jpeg");
        imagesMap.put(".jpeg", "image/jpeg");
        imagesMap.put(".png", "image/png");
        imagesMap.put(".bmp", "image/bmp");
        imagesMap.put(".psd", "image/psd");
        imagesMap.put(TIF_EXTENSION, "image/tiff");
        imagesMap.put(TIFF_EXTENSION, "image/tiff");
        imagesMap.put(".webp", "image/webp");
        imagesMap.put(".jfif", "image/webp");
        imagesMap.put(".heif", "image/heif");
        imagesMap.put(".heic", "image/heic");

        fileExtToMimeTypes.putAll(imagesMap);

        fileExtToMimeTypes.put(".doc", "application/msword");
        fileExtToMimeTypes.put(".docx", "application/msword");
        fileExtToMimeTypes.put(".pdf", "application/pdf");
        fileExtToMimeTypes.put(".ppt", "application/vnd.ms-powerpoint");
        fileExtToMimeTypes.put(".pptx", "application/vnd.ms-powerpoint");
        fileExtToMimeTypes.put(".xls", "application/vnd.ms-excel");
        fileExtToMimeTypes.put(".xlsx", "application/vnd.ms-excel");
        fileExtToMimeTypes.put(".zip", "application/zip");
        fileExtToMimeTypes.put(".rar", "application/zip");
        fileExtToMimeTypes.put(".rtf", "application/rtf");
        fileExtToMimeTypes.put(".wav", "audio/x-wav");
        fileExtToMimeTypes.put(".mp3", "audio/x-wav");
        fileExtToMimeTypes.put(".txt", "text/plain");
        fileExtToMimeTypes.put(".3gp", "video*//*");
        fileExtToMimeTypes.put(".mpg", "video*//*");
        fileExtToMimeTypes.put(".mpeg", "video*//*");
        fileExtToMimeTypes.put(".mpe", "video*//*");
        fileExtToMimeTypes.put(".mp4", "video*//*");
        fileExtToMimeTypes.put(".avi", "video*//*");
        fileExtToMimeTypes.put(".mov", "video*//*");

        imageFileExt = new ArrayList<>(imagesMap.keySet());
    }

    /**
     * FileType store different types of file.
     * works with {@see AttachmentResourcesHolder} in common module.
     * When you add another file type - make sure to add text/color for file in AttachmentResourceHolder
     */
    public enum FileType {
        AUDIO,
        VIDEO,
        PDF,
        PPT,
        XML,
        XLS,
        ARCHIVE,
        DOC,
        ODT,
        TXT,
        DOT,
        DOTH,
        DOTM,
        DJVU,
        HTM,
        HTML,
        MHT,
        MHTML,
        ODC,
        ODF,
        ODG,
        ODI,
        ODM,
        ODP,
        ODS,
        OTC,
        OTF,
        OTG,
        OTH,
        OTI,
        OTP,
        OTS,
        OTT,
        RTF,
        XHTML,
        URL,
        UNKNOWN,
        CSV,
        PY,
        SQL,
        FOLDER,
        IMAGE,
        LINK,
        PHOTOSHOP,
        VSD,
        ALERT,
        SSB,
        SSBX,
        ISB,
        SABYDOC
    }

    @NonNull
    public static FileType detectFileType(@NonNull File file) {
        return detectFileTypeByExtension(getFileExtension(file));
    }

    @NonNull
    public static FileType detectFileType(@NonNull String fileName) {
        return detectFileTypeByExtension(getFileExtension(fileName).toLowerCase(Locale.getDefault()));
    }

    @NonNull
    public static FileType detectFileTypeByExtension(@NonNull String extension) {
        if (!extension.startsWith(".")) {
            extension = ".".concat(extension);
        }
        extension = extension.toLowerCase();
        switch (extension) {
            case ".doc":
            case ".docx":
            case ".docm":
            case ".rtf":
                return FileType.DOC;
            case ".pdf":
                return FileType.PDF;
            case ".ppt":
            case ".pptx":
            case ".pot":
            case ".potx":
            case ".ppsx":
            case ".pps":
            case ".ppsm":
            case ".potm":
            case ".pptm":
                return FileType.PPT;
            case ".xls":
            case ".xlsx":
            case ".xlt":
            case ".xlsm":
            case ".xlsb":
                return FileType.XLS;
            case ".xml":
                return FileType.XML;
            case ".zip":
            case ".rar":
            case ".7z":
            case ".arj":
                return FileType.ARCHIVE;
            case ".txt":
            case ".plain866":
            case ".plain1251":
            case ".patch":
            case ".css":
                return FileType.TXT;
            case ".wav":
            case ".mp3":
            case ".m4a":
            case ".m4u":
            case ".wma":
                return FileType.AUDIO;
            case ".3gp":
            case ".mpg":
            case ".mpeg":
            case ".mpe":
            case ".mp4":
            case ".avi":
            case ".flv":
            case ".m4v":
            case ".mov":
            case ".wmv":
            case ".ogv":
            case ".webm":
            case ".mkv":
            case ".vob":
                return FileType.VIDEO;
            case ".odt":
                return FileType.ODT;
            case ".dot":
            case ".dotx":
                return FileType.DOT;
            case ".doth":
                return FileType.DOTH;
            case ".dotm":
                return FileType.DOTM;
            case ".djvu":
                return FileType.DJVU;
            case ".htm":
                return FileType.HTM;
            case ".html":
                return FileType.HTML;
            case ".mht":
                return FileType.MHT;
            case ".mhtml":
                return FileType.MHTML;
            case ".odc":
                return FileType.ODC;
            case ".odf":
                return FileType.ODF;
            case ".odg":
                return FileType.ODG;
            case ".odi":
                return FileType.ODI;
            case ".odm":
                return FileType.ODM;
            case ".odp":
                return FileType.ODP;
            case ".ods":
                return FileType.ODS;
            case ".otc":
                return FileType.OTC;
            case ".otf":
                return FileType.OTF;
            case ".otg":
                return FileType.OTG;
            case ".oth":
                return FileType.OTH;
            case ".oti":
                return FileType.OTI;
            case ".otp":
                return FileType.OTP;
            case ".ots":
                return FileType.OTS;
            case ".ott":
                return FileType.OTT;
            case ".xhtml":
                return FileType.XHTML;
            case ".url":
                return FileType.URL;
            case GIF_EXTENSION:
            case ".jpg":
            case ".jpeg":
            case ".png":
            case ".bmp":
            case TIFF_EXTENSION:
            case TIF_EXTENSION:
            case ".webp":
            case ".ico":
            case ".jfif":
            case ".heif":
            case ".heic":
                return FileType.IMAGE;
            case ".py":
                return FileType.PY;
            case ".sql":
                return FileType.SQL;
            case ".csv":
                return FileType.CSV;
            case ".lnk":
                return FileType.LINK;
            case ".psd":
                return FileType.PHOTOSHOP;
            case ".vsd":
            case ".vsdx":
                return FileType.VSD;
            case ".alert":
                return FileType.ALERT;
            case ".sabydoc":
                return FileType.SABYDOC;
            case ".isb":
                return FileType.ISB;
            case ".ssb":
                return FileType.SSB;
            case ".ssbx":
                return FileType.SSBX;
            default:
                return FileType.UNKNOWN;
        }
    }

    @NonNull
    public static FileType detectFileTypeById(int typeId) {
        switch (typeId) {
            case 1: // xls
            case 2: // xlsx
            case 5: // xlsb
            case 6: // xlsm
                return FileType.XLS;
            case 3:
                return FileType.CSV;
            case 4:
                return FileType.ODS;
            case 7: // doc
            case 8: // docx
            case 13: //docm
                return FileType.DOC;
            case 9:
                return FileType.RTF;
            case 10: // dot
            case 11: // dotx
                return FileType.DOT;
            case 12:
                return FileType.ODT;
            case 14:
                return FileType.DOTM;
            case 15: // ppt
            case 16: // pptx
            case 17: // pot
            case 18: // potm
            case 19: // potx
            case 20: // pps
            case 21: // ppsm
            case 22: // ppsx
            case 23: // pptm
                return FileType.PPT;
            case 24:
                return FileType.PDF;
            case 25:
                return FileType.ODP;
            case 26:
                return FileType.XML;
            case 27:
                return FileType.TXT;
            case 28:
                return FileType.HTM;
            case 29:
                return FileType.HTML;
            case 33:
                return FileType.PY;
            case 34:
                return FileType.SQL;
            case 35: // zip
            case 36: // rar
            case 37: // 7zip
                return FileType.ARCHIVE;
            case 38: // bmp
            case 39: // png
            case 40: // jpg
            case 41: // jpeg
            case 42: // tiff
            case 43: // tif
            case 44: // gif
                return FileType.IMAGE;
            case 45: // mp4
            case 46: // mov
            case 47: // m4v
            case 48: // wmv
                return FileType.VIDEO;
            case 49: // mp3
            case 50: // wav
            case 51: // aiff
            case 52: // m4a
                return FileType.AUDIO;
            case 0:
            default:
                return FileType.UNKNOWN;
        }
    }

    /**
     * Определение расширения файла по идентификатору типа.
     * Использовать только в случае, когда расширение нельзя узнать из имени файла.
     *
     * @param typeId идентификатор типа
     * @return расширение файла
     */
    @NonNull
    public static String getExtensionByTypeId(int typeId) {
        switch (typeId) {
            case 1:
                return ".xls";
            case 2:
                return ".xlsx";
            case 5:
                return ".xlsb";
            case 6:
                return ".xlsm";
            case 3:
                return ".csv";
            case 4:
                return ".ods";
            case 7:
                return ".doc";
            case 8:
                return ".docx";
            case 13:
                return ".docm";
            case 9:
                return ".rtf";
            case 10:
                return ".dot";
            case 11:
                return ".dotx";
            case 12:
                return ".odt";
            case 14:
                return ".dotm";
            case 15:
                return ".ppt";
            case 16:
                return ".pptx";
            case 17:
                return ".pot";
            case 18:
                return ".potm";
            case 19:
                return ".potx";
            case 20:
                return ".pps";
            case 21:
                return ".ppsm";
            case 22:
                return ".ppsx";
            case 23:
                return ".pptm";
            case 24:
                return ".pdf";
            case 25:
                return ".odp";
            case 26:
                return ".xml";
            case 27: // тип соответствует нескольким расширениям, по умолчанию указываем txt
                return ".txt";
            case 28:
                return ".htm";
            case 29:
                return ".html";
            case 33:
                return ".py";
            case 34:
                return ".sql";
            case 35:
                return ".zip";
            case 36:
                return ".rar";
            case 37:
                return ".7zip";
            case 38:
                return ".bmp";
            case 39:
                return ".png";
            case 40:
                return ".jpg";
            case 41:
                return ".jpeg";
            case 42:
                return TIFF_EXTENSION;
            case 43:
                return TIF_EXTENSION;
            case 44:
                return GIF_EXTENSION;
            case 45:
                return ".mp4";
            case 46:
                return ".mov";
            case 47:
                return ".m4v";
            case 48:
                return ".wmv";
            case 49:
                return ".mp3";
            case 50:
                return ".wav";
            case 51:
                return ".aiff";
            case 52:
                return ".m4a";
            case 0:
            default:
                return StringUtils.EMPTY;
        }
    }

    @NonNull
    public static String replaceFileExtension(@NonNull String fileName, @NonNull String newExtension) {
        final String realExtension = getFileExtension(fileName);
        final int lastIndex = fileName.toLowerCase().lastIndexOf(realExtension);
        if (lastIndex > 0) {
            newExtension = (newExtension.indexOf(".") != 0 ? ".".concat(newExtension) : newExtension).toLowerCase();
            return fileName.substring(0, lastIndex).concat(newExtension);
        }
        return fileName;
    }

    @NonNull
    public static Map<String, String> getFileExtToMimeTypes() {
        return Collections.unmodifiableMap(fileExtToMimeTypes);
    }

    @NonNull
    private static List<String> getImageFileExt() {
        return imageFileExt;
    }

    @Nullable
    public static String getImageFileExtension(@Nullable String mimeType) {
        for (final String key : imagesMap.keySet()) {
            if (Objects.requireNonNull(imagesMap.get(key)).equals(mimeType)) {
                return key;
            }
        }
        return null;
    }

    public static boolean isImage(@NonNull String fileName) {
        return getImageFileExt().contains(getFileExtension(fileName).toLowerCase());
    }

    public static boolean isGif(@Nullable String fileName) {
        return fileName != null && GIF_EXTENSION.equalsIgnoreCase(getFileExtension(fileName));
    }

    public static boolean isTif(@Nullable String fileName) {
        if (fileName == null) {
            return false;
        } else {
            String ext = getFileExtension(fileName);
            return TIF_EXTENSION.equals(ext) || TIFF_EXTENSION.equals(ext);
        }
    }

    public static boolean isImageByExtension(@Nullable String extension) {
        if (extension == null) {
            return false;
        }
        return detectFileTypeByExtension(extension) == FileType.IMAGE;
    }

    public static boolean isVideoByExtension(@Nullable String extension) {
        if (extension == null) {
            return false;
        }
        return detectFileTypeByExtension(extension) == FileType.VIDEO;
    }

    @NonNull
    public static String getFileExtension(@NonNull File file) {
        return getFileExtension(file.getName());
    }

    @NonNull
    public static String getFileExtension(@NonNull File file, boolean withDot) {
        return getFileExtension(file.getName(), withDot);
    }

    @NonNull
    public static String getFileExtension(@NonNull String fileName) {
        return getFileExtension(fileName, true);
    }

    @NonNull
    public static String getFileExtension(@NonNull String fileName, boolean withDot) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return StringUtils.EMPTY;
        } else {
            int substringBeginIndex = withDot ? lastDotIndex : (lastDotIndex + 1);
            return substringBeginIndex > fileName.length()
                    ? StringUtils.EMPTY
                    : fileName.substring(substringBeginIndex).toLowerCase();
        }
    }

    public static long getFolderSize(@NonNull File startingDir) {
        long size = 0;
        if (startingDir != null) {
            if (startingDir.isDirectory()) {
                File[] files = startingDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        size += getFolderSize(file);
                    }
                }
            } else {
                size += startingDir.length();
            }
        }
        return size;
    }

    public static void removeCachedFiles(@NonNull File cacheDir) {
        List<File> cachedFiles = getCachedFiles(cacheDir);
        for (File file : cachedFiles) {
            if (file.exists() && !isFileLocked(file)) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }

    @NonNull
    private static List<File> getCachedFiles(@NonNull File startingDir) {
        List<File> result = new ArrayList<>();
        File[] files = startingDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(getCachedFiles(file));
                } else {
                    result.add(file);
                }
            }
        }
        return result;
    }

    public static void lockFile(@NonNull File file) {
        lockedFileList.add(file.getAbsolutePath());
    }

    public static void unlockFile(@NonNull File file) {
        lockedFileList.remove(file.getAbsolutePath());
    }

    public static boolean isFileLocked(@NonNull File file) {
        return lockedFileList.contains(file.getAbsolutePath());
    }

    @WorkerThread
    @Nullable
    public static File copyFile(@NonNull File src, @NonNull File dstDir) throws IOException {
        if (!src.exists() || !dstDir.exists() && !dstDir.mkdirs()) {
            return null;
        }
        File newFile = new File(dstDir, src.getName());
        if (!newFile.exists() && !newFile.createNewFile()) {
            return null;
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(src).getChannel();
            destination = new FileOutputStream(newFile).getChannel();
            long size = source.size();
            for (long block = 0; block < size; block += COPY_FILE_BLOCK_SIZE) {
                destination.transferFrom(source, block, Math.min(COPY_FILE_BLOCK_SIZE, size - block));
            }
        } finally {
            close(source);
            close(destination);
        }
        return newFile;
    }

    private static void close(@Nullable Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            Timber.d(e);
        }
    }

    public static File getDownloadsDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    @Nullable
    public static String getApplicationDir(@NonNull Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }
        return null;
    }

    /**
     * Получение полного пути до папки в кэше
     *
     * @param context
     * @param folderName название папки
     * @return полный путь до папки в кэше
     */
    @NonNull
    public static String getCachedFolderPath(@NonNull Context context, @NonNull String folderName) {
        File subDir = new File(context.getCacheDir() + String.format(Locale.getDefault(), "/%s", folderName));

        if (subDir.exists() && !subDir.isDirectory()) {
            subDir.delete();
        }

        if (!subDir.exists()) {
            subDir.mkdir();
        }

        return String.format("/%s/", subDir.getAbsolutePath());
    }

    /**
     * Получение полного пути до файла в кэше
     *
     * @param context
     * @param folderName название папки, в которой находится файл
     * @param fileName   имя файла
     * @return полный путь до файла в кэше
     */
    @NonNull
    public static String getCachedFilePath(@NonNull Context context, @NonNull String folderName, @NonNull String fileName) {
        return getFilePath(getCachedFolderPath(context, folderName), fileName);
    }

    /**
     * Получение полного пути до файла
     *
     * @param folderPath полный путь до папки, в которой находится файл
     * @param fileName   имя файла
     * @return полный путь до файла
     */
    @NonNull
    public static String getFilePath(@NonNull String folderPath, @NonNull String fileName) {
        return folderPath.concat(fileName);
    }

    /**
     * Получение имени файла, загружаемого по ссылке
     *
     * @param url                ссылка на файл
     * @param contentDisposition информация о загружаемом файле из http заголовка
     * @param mimetype           тип содержимого, сообщаемый сервером
     * @return имя загружаемого файла
     */
    public static String parseFileName(@NonNull String url, @NonNull String contentDisposition, @NonNull String mimetype) {
        // Apk файлы имеют mimeType = application/octet-stream, которые ошибочно
        // распознаются утилитой URLUtil.guessFileName() как неизвестные бинарные файлы (bin)
        if (isApk(url)) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        String[] nameSplit = Uri.decode(contentDisposition).split("\'");
        if (nameSplit.length > 1) {
            fileName = nameSplit[nameSplit.length - 1];
        }
        return fileName;
    }

    public static boolean isApk(@NonNull String fileName) {
        return APK_EXTENSION.equals(getFileExtension(fileName));
    }

    public static boolean isSabyDoc(@NonNull String fileName) {
        return SABYDOC_EXTENSION.equals(getFileExtension(fileName));
    }

    public static boolean isConvertibleToPDF(@NonNull String fileName) {
        String extension = getFileExtension(fileName);
        for (String convertibleToPDFFormat : convertibleToPDFFormats) {
            if (convertibleToPDFFormat.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
