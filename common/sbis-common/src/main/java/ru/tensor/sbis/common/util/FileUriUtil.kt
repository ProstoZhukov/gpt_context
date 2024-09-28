package ru.tensor.sbis.common.util

import android.annotation.SuppressLint
import android.content.*
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.common.BuildConfig
import timber.log.Timber
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ng.sharipov on 26/11/15.
 *
 * @author sa.nikitin
 */
class FileUriUtil(context: Context) {
    class FileInfo(val name: String?, val size: Long, val mimeType: String?)

    private val mContext: Context
    fun isFileScheme(uriString: String): Boolean {
        return checkFileScheme(uriString)
    }

    fun getPath(uriString: String): String? {
        val uri = Uri.parse(uriString)
        return getPath(mContext, uri)
    }

    fun generateSnapshotUri(): String {
        return Objects.requireNonNull(generateSnapshotUri(mContext)).toString()
    }

    fun getFileName(uriString: String): String? {
        val uri = Uri.parse(uriString)
        return uri?.let { getFileName(it) }
    }

    fun getFileName(uri: Uri): String? {
        return getFileName(mContext, uri)
    }

    fun getFileInfo(
        uriString: String,
        requestName: Boolean,
        requestSize: Boolean,
        requestMimeType: Boolean
    ): FileInfo? {
        return getFileInfo(mContext, uriString, requestName, requestSize, requestMimeType)
    }

    fun getFileInfo(
        uri: Uri,
        requestName: Boolean,
        requestSize: Boolean,
        requestMimeType: Boolean = true
    ): FileInfo? {
        return getFileInfo(mContext, uri, requestName, requestSize, requestMimeType)
    }

    fun getUriForExternalFile(file: File): Uri {
        return getUriForExternalFile(mContext, file)
    }

    fun getUriForInternalFile(file: File): Uri {
        return getUriForInternalFile(mContext, file)
    }

    fun getUriForInternalFile(filePath: String): Uri {
        return getUriForInternalFile(File(filePath))
    }

    fun getFile(uri: Uri): File? {
        return getFile(mContext, uri)
    }

    fun getFileAsync(uriString: String): Single<File> {
        return getFileAsync(mContext, Uri.parse(uriString))
    }

    fun getFileAsyncWithName(uriString: String): Single<Pair<File?, String?>> {
        return getFileAsyncWithName(uriString, AndroidSchedulers.mainThread())
    }

    fun getFileAsyncWithName(uriString: String, scheduler: Scheduler): Single<Pair<File?, String?>> {
        return getFileAsync(mContext, Uri.parse(uriString))
            .zipWith(Single.fromCallable { getFileName(uriString) }
                .subscribeOn(Schedulers.io()),
                BiFunction<File, String?, Pair<File?, String?>> { _, _ -> Pair<File?, String?>(null, null) })
            .observeOn(scheduler)
    }

    fun getFileType(uri: Uri): String? {
        return mContext.contentResolver.getType(uri)
    }

    fun getFileSize(uri: String): Long {
        return getFileSize(mContext, Uri.parse(uri))
    }

    fun openInputSteam(uri: Uri): InputStream? {
        return try {
            mContext.contentResolver.openInputStream(uri)
        } catch (e: FileNotFoundException) {
            null
        }
    }

    companion object {
        const val SCHEME_FILE = "file://"
        const val UNDEFINED_FILE_SIZE: Long = -1

        @JvmStatic
        fun checkFileScheme(uriString: String): Boolean {
            val uri = Uri.parse(uriString)
            return ContentResolver.SCHEME_FILE == uri.scheme
        }

        fun parseUri(uriString: String): Uri {
            //Парсим таким образом, т.к. обычный Uri.parse неправильно парсит путь с точками в имени
            //Например: tartila... _#$#$#((+-¢£=_π}{^°£=₽°{£}}°^∆.jpg
            return if (uriString.contains(SCHEME_FILE)) {
                Uri.fromFile(File(uriString.substringAfter(SCHEME_FILE, uriString)))
            } else {
                Uri.parse(uriString)
            }
        }

        fun getFileInfo(
            context: Context,
            uriString: String,
            requestName: Boolean,
            requestSize: Boolean,
            requestMimeType: Boolean,
        ): FileInfo? {
            val uri = Uri.parse(uriString)
            return getFileInfo(context, uri, requestName, requestSize, requestMimeType)
        }

        fun getFileInfo(
            context: Context,
            uri: Uri,
            requestName: Boolean,
            requestSize: Boolean,
            requestMimeType: Boolean,
        ): FileInfo? {
            if (ContentResolver.SCHEME_FILE == uri.scheme && uri.path != null) {
                val file = File(uri.path)
                return if (file.exists()) {
                    val mimeType: String? =
                        MimeTypeMap
                            .getSingleton()
                            .getMimeTypeFromExtension(FileUtil.getFileExtension(file.name, false))
                    FileInfo(file.name, file.length(), mimeType)
                } else {
                    null
                }
            }
            val cursor: Cursor?
            cursor = try {
                context.contentResolver.query(
                    uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
                    null,
                    null,
                    null
                )
            } catch (securityException: SecurityException) {
                /*
            Повторяется при шаринге из некоторых приложений при условии, что активность,
            которая изначально была запущена шарингом, уже закрыта, и идёт обращение по Uri
            Шарящее приложение отнимает доступ к Uri, если изначальная активность завершена
            Такое, например, в Google Photos или Google Chrome
            */
                Timber.e(securityException)
                return null
            }
            return if (cursor != null && cursor.moveToFirst()) {
                var name: String? = null
                var size = UNDEFINED_FILE_SIZE
                var mimeType: String? = null
                if (requestName) {
                    name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
                if (requestSize) {
                    size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
                }
                if (requestMimeType) {
                    mimeType = context.contentResolver.getType(uri)
                }
                cursor.close()
                FileInfo(name, size, mimeType)
            } else {
                null
            }
        }

        /**
         * Gets the extension of a file name, like ".png" or ".jpg".
         *
         * @param context
         * @param uri
         * @return Extension including the dot("."); "" if there is no extension;
         * null if uri was null.
         */
        fun getExtension(context: Context, uri: Uri?): String? {
            uri ?: return null
            val fileName = getFileName(context, uri) ?: return null
            val dot = fileName.lastIndexOf(".")
            return if (dot >= 0) {
                fileName.substring(dot)
            } else {
                // No extension.
                ""
            }
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context       The context.
         * @param uri           The Uri to query.
         * @param selection     (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
            val column = "_data"
            val projection = arrayOf(
                column
            )
            try {
                context.applicationContext.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                    .use { cursor ->
                        if (cursor != null && cursor.moveToFirst()) {
                            val column_index = cursor.getColumnIndexOrThrow(column)
                            val value = cursor.getString(column_index)
                            return if (value.startsWith("content://") || !value.startsWith("/") && !value.startsWith("file://")) {
                                null
                            } else value
                        }
                    }
            } catch (ignore: Exception) {
            }
            return null
        }

        /**
         * Get a file path from a Uri. This will get the the path for Storage Access
         * Framework Documents, as well as the _data field for the MediaStore and
         * other file-based ContentProviders.<br></br>
         * <br></br>
         * Callers should check whether the path is local before assuming it
         * represents a local file.
         *
         * @param context The context.
         * @param uri     The Uri to query.
         * @see .getFile
         */
        @SuppressLint("NewApi")
        fun getPath(context: Context, uri: Uri): String? {
            try {
                if ("file".equals(uri.scheme, ignoreCase = true)) {
                    return uri.path
                } else if (DocumentsContract.isDocumentUri(context, uri)) {
                    if (isExternalStorageDocument(uri)) {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        if ("primary".equals(type, ignoreCase = true)) {
                            return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                        }
                    } else if (isDownloadsDocument(uri)) {
                        val id = DocumentsContract.getDocumentId(uri) ?: return null
                        //После raw: идёт абсолютный путь
                        if (id.startsWith("raw:")) {
                            return id.substring(4)
                        }
                        val contentUriPrefixesToTry = arrayOf(
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads"
                        )
                        for (contentUriPrefix in contentUriPrefixesToTry) {
                            val contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), id.toLong())
                            try {
                                val path = getDataColumn(context, contentUri, null, null)
                                if (path != null) {
                                    return path
                                }
                            } catch (e: Exception) {
                                //do nothing
                            }
                        }
                    } else if (isMediaDocument(uri)) {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        var contentUri: Uri? = null
                        when (type) {
                            "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                        val selection = "_id=?"
                        val selectionArgs = arrayOf(
                            split[1]
                        )
                        return getDataColumn(context, contentUri, selection, selectionArgs)
                    }
                } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                    return getDataColumn(context, uri, null, null)
                }
            } catch (e: Exception) {
                Timber.w(e, "Correct exception")
            }
            return null
        }

        /**
         * Checks if local file exists on device.
         *
         * @param context The context
         * @param uri     File Uri to be checked
         * @return true if exists and accessible, false otherwise
         */
        @JvmStatic
        fun isFileExists(context: Context, uri: Uri): Boolean {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                return if (inputStream != null) {
                    inputStream.close()
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Timber.d(e, "Failed to open file")
            }
            val localFilePath = getPath(context, uri)
            val localFile = if (localFilePath != null) File(localFilePath) else null
            return localFile != null && localFile.exists()
        }

        /**
         * @param context where we are looking for
         * @param uri     A Uri identifying content
         * @return file to source or `null` if failed
         */
        @JvmStatic
        fun getFile(context: Context, uri: Uri): File? {
            val filePath = getPath(context, uri)
            val file = if (filePath != null) File(filePath) else null
            return if (file == null || !file.exists()) {
                copyFileToCache(context, uri)
            } else {
                file
            }
        }

        /**
         * @param context where we are looking for
         * @param uri     A Uri identifying content
         * @return Single with file to source
         */
        private fun getFileAsync(context: Context, uri: Uri): Single<File> {
            return Single.create { emmiter: SingleEmitter<File> ->
                if (canGetInputStream(context, uri)) {
                    val cachedFile = generateTempFile(context, uri)
                    if (cachedFile != null) {
                        var inputStream: InputStream? = null
                        try {
                            inputStream = context.contentResolver.openInputStream(uri)
                        } catch (ex: FileNotFoundException) {
                            Timber.e(ex, uri.toString())
                            emmiter.onError(ex)
                        }
                        if (inputStream != null) {
                            var outStream: OutputStream? = null
                            try {
                                outStream = FileOutputStream(cachedFile)
                                val buffer = ByteArray(8 * 1024)
                                var bytesRead: Int
                                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                    outStream.write(buffer, 0, bytesRead)
                                }
                            } catch (ex: Exception) {
                                Timber.e(ex, "Error in file output stream writing")
                                emmiter.onError(ex)
                            } finally {
                                if (outStream != null) {
                                    try {
                                        outStream.close()
                                    } catch (ex: IOException) {
                                        Timber.e(ex, "Failed to close output stream")
                                        emmiter.onError(ex)
                                    }
                                }
                            }
                            try {
                                inputStream.close()
                            } catch (ex: IOException) {
                                Timber.e(ex, "Failed to close input stream")
                                emmiter.onError(ex)
                            }
                        }
                        emmiter.onSuccess(cachedFile)
                    } else {
                        emmiter.onError(IOException("Failed to get file"))
                    }
                } else {
                    emmiter.onError(IOException("Failed to get input stream for uri $uri"))
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }

        /**
         *
         * @param context where we are looking for
         * @param uri A Uri identifying content
         * @return true if can get InputStream else false
         */
        private fun canGetInputStream(context: Context, uri: Uri): Boolean {
            return if (uri.scheme == SCHEME_FILE) {
                val filePath = getPath(context, uri)
                val file = if (filePath != null) File(filePath) else null
                file != null && file.exists()
            } else {
                true
            }
        }

        fun copyFileToCache(context: Context, uri: Uri): File? {
            var cachedFile = generateTempFile(context, uri)
            if (cachedFile != null) {
                if (!writeToFile(context, cachedFile, uri)) {
                    cachedFile = null
                }
            }
            return cachedFile
        }

        /**
         * Метод для доступа к URI, содержащихся в [Intent]
         *
         * @param data экземпляр [Intent]
         * @return список [Uri]
         */
        @JvmStatic
        fun getUrisFromIntent(data: Intent): List<Uri> {
            val uris = getFilesByClipData(data.clipData)
            if (uris.isEmpty()) {
                val uri = data.data
                if (uri != null) {
                    uris.add(uri)
                }
            }
            return uris
        }

        /**
         * Метод для доступа к URI, содержащихся в [ClipData]
         *
         * @param clipData экземпляр [ClipData]
         * @return [Uri]
         */
        private fun getFilesByClipData(clipData: ClipData?): MutableList<Uri> {
            val result: MutableList<Uri> = ArrayList()
            if (clipData != null) {
                val itemCount = clipData.itemCount
                for (i in 0 until itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    if (uri != null) {
                        result.add(uri)
                    }
                }
            }
            return result
        }

        fun getFilesByClipData(context: Context, clipData: ClipData?): ArrayList<File> {
            val result = ArrayList<File>()
            if (clipData != null) {
                val itemCount = clipData.itemCount
                for (i in 0 until itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    if (uri != null) {
                        val f = getFile(context, uri)
                        if (f != null) {
                            result.add(f)
                        }
                    }
                }
            }
            return result
        }

        /**
         * @param context where we are looking for
         * @param uri     A Uri identifying content
         * @return generated file object with name depend on uri or `null` if failed
         */
        private fun generateTempFile(context: Context, uri: Uri): File? {
            var fileName = getFileName(context, uri)
            if (fileName == null) {
                fileName = createNameByMD5Hash(uri)
                if (fileName == null) {
                    return null
                }
                val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                fileName += "." + (extension ?: "tmp")
            }
            return File(context.cacheDir, fileName)
        }

        @JvmStatic
        fun getFileName(context: Context, uri: Uri): String? {
            val fileInfo = getFileInfo(context, uri, requestName = true, requestSize = false, requestMimeType = false)
            return fileInfo?.name
        }

        fun getFileSize(context: Context, uri: Uri): Long {
            val fileInfo = getFileInfo(context, uri, requestName = false, requestSize = true, requestMimeType = false)
            return fileInfo?.size ?: UNDEFINED_FILE_SIZE
        }

        fun createNameByMD5Hash(uri: Uri): String? {
            return try {
                val digest = MessageDigest.getInstance("MD5")
                digest.update(uri.toString().toByteArray())
                UUID.nameUUIDFromBytes(digest.digest()).toString()
            } catch (ex: NoSuchAlgorithmException) {
                Timber.e(ex)
                null
            }
        }

        @JvmStatic
        fun generateSnapshotUri(context: Context): Uri? {
            return generateImageUri(context, generateSnapshotName())
        }

        @JvmStatic
        fun generateSnapshotName(): String {
            return "Snapshot_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".jpg"
        }

        fun generateCroppedImageUri(context: Context): Uri? {
            return generateImageUri(context, generateCroppedSnapshotName())
        }

        fun generateCroppedSnapshotName(): String {
            return "Cropped_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".jpg"
        }

        private fun generateImageUri(context: Context, fileName: String): Uri? {
            var storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            if (!storageDir.canWrite()) {
                storageDir = Environment.getExternalStorageDirectory()
            }
            val outputFile: File
            outputFile = try {
                File(storageDir, fileName)
            } catch (e: Exception) {
                Timber.e("Error on generating image Uri for filename = " + fileName + " " + e.message)
                return null
            }
            return getUriForExternalFile(context, outputFile)
        }

        fun getUriForExternalFile(context: Context, file: File): Uri {
            return getFileProviderUri(context, file)
        }

        @JvmStatic
        fun getUriForInternalFile(context: Context, file: File): Uri {
            return getFileProviderUri(context, file)
        }

        private fun getFileProviderUri(context: Context, file: File): Uri {
            return FileProvider.getUriForFile(context, BuildConfig.FILE_AUTHORITY, file)
        }

        /**
         * @param context  where we are looking for
         * @param uri      A Uri identifying content
         * @param fileName expected file name
         * @return generated file object or `null` if failed
         */
        private fun getGenerateTempFileByName(context: Context, uri: Uri, fileName: String): File? {
            val subFolderName = createNameByMD5Hash(uri)
            var outputFile: File? = null
            if (subFolderName != null) {
                val cacheDir = context.cacheDir
                val subFolder = File(cacheDir, subFolderName)
                outputFile = if (subFolder.exists() || subFolder.mkdir()) {
                    File(subFolder, fileName)
                } else {
                    null
                }
            }
            return outputFile
        }

        /**
         * @param context where we are looking for
         * @param file    destination file
         * @param uri     A Uri identifying content
         * @return `true` if succeeded `false` if failed
         */
        fun writeToFile(context: Context, file: File?, uri: Uri): Boolean {
            var inputStream: InputStream? = null
            var success = true
            try {
                inputStream = context.contentResolver.openInputStream(uri)
            } catch (ex: FileNotFoundException) {
                Timber.e(ex, uri.toString())
                success = false
            } catch (ex: SecurityException) {
                Timber.e(ex, uri.toString())
                success = false
            }
            if (inputStream != null) {
                if (file != null) {
                    var outStream: OutputStream? = null
                    try {
                        outStream = FileOutputStream(file)
                        val buffer = ByteArray(8 * 1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outStream.write(buffer, 0, bytesRead)
                        }
                    } catch (ex: Exception) {
                        Timber.e(ex, "Error in file output stream writing")
                        success = false
                    } finally {
                        if (outStream != null) {
                            try {
                                outStream.close()
                            } catch (ex: IOException) {
                                Timber.e(ex, "Failed to close output stream")
                                success = false
                            }
                        }
                    }
                }
                try {
                    inputStream.close()
                } catch (ex: IOException) {
                    Timber.e(ex, "Failed to close input stream")
                    success = false
                }
            }
            return success
        }

        /**
         * @param file where we are looking for
         * @return Image file extension like .png, .jpeg or null if it's not an image or `null` if failed
         */
        fun getImageExtension(file: File): String? {
            var inputStream: InputStream? = null
            try {
                inputStream = FileInputStream(file)
            } catch (ex: FileNotFoundException) {
                Timber.e(ex)
            }
            if (inputStream != null) {
                try {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeStream(inputStream, null, options)
                    val mimeType = options.outMimeType
                    if (mimeType != null) {
                        return FileUtil.getImageFileExtension(mimeType)
                    }
                } finally {
                    try {
                        inputStream.close()
                    } catch (ex: IOException) {
                        Timber.e(ex)
                    }
                }
            }
            return null
        }

        fun getImageExtension(filePath: String): String? {
            val file = File(filePath)
            return if (file.exists() && file.length() > 0) {
                getImageExtension(file)
            } else {
                null
            }
        }

        fun isImage(filePath: String): Boolean {
            return getImageExtension(filePath) != null
        }

        fun isImage(file: File): Boolean {
            return file.exists() && file.length() > 0 && getImageExtension(file) != null
        }

        @JvmStatic
        fun getRecentMediaFiles(context: Context): ArrayList<File> {
            val resultList = ArrayList<File>()
            val projection = arrayOf(MediaStore.Images.ImageColumns.DATA)
            val cursor = context.contentResolver
                .query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                    null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
                )
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        val imageLocation = cursor.getString(0) ?: continue
                        val imageFile = File(imageLocation)
                        if (imageFile.exists()) {
                            resultList.add(imageFile)
                        }
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            return resultList
        }

        fun convertMbToBytes(mbSize: Int): Int {
            return 1024 * 1024 * mbSize
        }
    }

    init {
        mContext = context.applicationContext
    }
}